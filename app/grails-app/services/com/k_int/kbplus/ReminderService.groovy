package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.transaction.Transactional
import groovy.text.SimpleTemplateEngine
import org.apache.commons.lang3.time.DateUtils
import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.LocalDate
import org.springframework.context.ApplicationContext
import javax.annotation.PostConstruct

/**
 * @author Ryan@k-int.com
 */
@Transactional
class ReminderService {

    ApplicationContext applicationContext
    def mailService
    def grailsApplication
    String  from
    String  replyTo
    Boolean generic

    @PostConstruct
    void init() {
        from    = grailsApplication.config.notifications.email.from
        replyTo = grailsApplication.config.notifications.email.replyTo
        generic = grailsApplication.config.notifications.email.genericTemplate
    }

    //i.e result[user id] = [Reminder 1,Reminder 2, etc]
    def getActiveEmailRemindersByUserID() {
        //Get active reminders and users who have email
        def result = [:]
        Reminder.executeQuery('select r from Reminder as r where r.active = ? and r.user.email != null and r.method.value = ? order by r.user.id',[true,'email']).each { r ->
            result[r.user.id] << [r]  //Group Reminders and organise via users ID
        }
        result
    }

    //todo Think about limiting the search to Sub data that renewal data that is year earlier than the present data, i.e. max user can enter is 12 months
    //Perform relevant searching/joins to return all subscriptions of interest
    def getAccessibleSubsForUser(User user) {
        def result

        result
    }

    //user -> [mail 1, mail 2]
    def generateMail(Subscription sub, ArrayList userRemindersList) {
        log.debug("Setting up mail generation requirements...");
        def emailTemplateFile = generic? applicationContext.getResource("WEB-INF/mail-templates/subscriptionManualRenewalDateGeneric.gsp").file : applicationContext.getResource("WEB-INF/mail-templates/subscriptionManualRenewalDate.gsp").file
        def engine            = new SimpleTemplateEngine()
        def baseTemplate      = engine.createTemplate(emailTemplateFile)
        def _template
        def _content
        userRemindersList.each {
            _template = generic? baseTemplate.make() : baseTemplate.make(val)
            _content  = _template.toString()
            mailReminder(val.setup, _content)
        }

    }

    def mailReminder(setup, content) {
        log.debug("About to send mail...")
        mailService.sendMail {
            to setup.email
            from from
            replyTo replyTo
            subject setup.subject
            html content
        }
    }

    /**
     * isSubInReminderRange - Run from a large list of possible subscriptions that user has access to!
     * @param reminders     - List of Reminders per user
     * @param sub           - Current iteration of a Subscription
     * @param today         - Today's date in Joda format
     */
    def isSubInReminderRange(ArrayList reminders, Subscription sub, LocalDate today)
    {
        def result = [:]
        result.found = false
        LocalDate renewalDate = new LocalDate(sub.manualRenewalDate)

        reminders.each { r ->
         def reminderDate = convertReminderToDate(r.unit.value, r.amount, subRenewal) //e.g. sub renewal 12.11.15  rem date 3 months before = 12:8:15
         if(isDateInReminderPeriod(reminderDate, renewalDate, today))
         {  
           result.found = true
           result.info.add(['user':u,'reminder':r])
           return //Break out of each, should only be one for user in this period!
         } 
        }
    }
    
    //Date has to be between start and end range
    def isDateInReminderPeriod(LocalDate start, LocalDate end, LocalDate today) {
          return !today.isBefore(start) && !today.isAfter(end);
    }

    /**
     * Returns the calculated date
     * @param  unit       - Week
     * @param amount      - 3
     * @param convertFrom - subRenewalDate
     * @return LocalDate
     */
    def convertReminderToDate(String unit, int amount, LocalDate convertFrom)
    {
        switch (unit)
        {
            case 'Day':
                return new LocalDate(convertFrom).minusDays(amount)
                break
            case 'Week':
                return new LocalDate(convertFrom).minusWeeks(amount)
                break
            case 'Month':
                return new LocalDate(convertFrom).minusMonths(amount)
                break
            default:
                return new LocalDate(convertFrom).minusDays(amount)
                break
        }
    }

    //todo Learn how to CACHE the Subscription data to avoid possible repeating lookups
    def runReminders() {
        log.debug("Running reminder service...")
        if (mailService.disabled)
        {
            log.error("Unable to send reminders, mail service is disabled!")
            return
        }

        def usersReminders = getActiveEmailRemindersByUserID()
        int userCounter    = usersReminders.size()
        LocalDate today    = new LocalDate();

        log.debug("Presently there is ${userCounter} Users with potentially 1..* reminders")

        //Will be for the final stage... A Subscription instance as key
        def masterSubscriptionList = [:]

        //Key = User ID Val = List of Reminders 1..* for that user!
        //Go through each reminder a user has, lookup subscriptions accessible to user, compare each subscription to the date range 
        usersReminders.each { k,v ->
            //All possible subscriptions for this user
            def availibleSubs  = getAccessibleSubsForUser(v.user)
            log.debug("Lookup up ${v.size()}:Reminders for dates **  ${v.collect { [it.unit.value,it.amount] }}   ** \nSubscriptions for user ID:${k} (Username:${v.user.username}) total has ${availibleSubs.size()} ")

            //List would not have anything past 12 months of present date for server load purposes!
            //todo upon finding relevant matching, would be more efficient to add a key as Subcription ID, then add a list of arrays[sub id/inst][[user, reminder],[]] instances as values
            availibleSubs.each { sub ->
                def reminderAndUserInst = isSubInReminderRange(v, sub, today, v.user)
                if(reminderAndUserInst.found) {
                    log.debug("Found Subscription: ${sub.name} for User: ${v.user.username} Reminder: ${v.id}")
                    //Add to the master list i.e. [Subscription inst][['reminder':reminder inst1, 'user': user inst1],['reminder':reminder inst2, 'user': user inst2],etc]
                    addToMasterList(masterSubscriptionList,reminderAndUserInst)
                }
            }


        }


    }

    private void addToMasterList(LinkedHashMap masterSubscriptionList, reminderAndUserInst) {

    }

    /**
     * After all the Reminders for each users has been checked and then added to a master list of Subscriptions
     * Now is the time for the final process, iterate through and each Subscription generating the emails
     * @param masterSubscriptionList - [Subscription][[reminder1, user1],[reminder2, user2]]
     */
    private void processMasterList(LinkedHashMap masterSubscriptionList) {
        log.debug("Processing master list, ready for emailing based on each Subscription")

        masterSubscriptionList.eachWithIndex { subscription, valueList, i ->
            log.debug()
            generateMail(subscription, valueList)
        }
    }
}

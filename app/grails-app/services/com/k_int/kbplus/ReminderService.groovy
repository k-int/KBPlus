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
@Transactional(readOnly = true)
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
        log.debug("Initialised Reminder Service...")
    }

    //[1:[com.k_int.kbplus.Reminder : 8, com.k_int.kbplus.Reminder : 9, com.k_int.kbplus.Reminder : 10]]
    def getActiveEmailRemindersByUserID() {
        //Get active reminders and users who have email
        def result = [:]
        Reminder.executeQuery('select r from Reminder as r where r.active = ? and r.user.email != null and r.reminderMethod.value = ? order by r.user.id',[Boolean.TRUE,'email']).each { r ->
            if (result.containsKey(r.user.id)) {
                ArrayList userReminders = result.get(r.user.id)  //Group Reminders and organise via users ID
                userReminders.add(r)
                result.put(r.user.id, userReminders)
            } else
            {
                result.put(r.user.id, [r])
            }
        }
        log.debug("Users and active reminders : ${result}")
        result
    }

    /**
     * Grabs all Subscriptions that this user can access (e.g. authorised orgs)
     * Performs relevant searching/joins to return all subscriptions of interest
     * @param user
     * @return List of Subscriptions
     */
    def getAuthorisedSubsciptionsByUser(User user) {
        def qry_params = [user.defaultDash, LocalDate.now().minusMonths(13).toDate()]
        log.debug("Looking up subscriptions for user : ${user.username} Restricting to Subscriptions with renewal date one year previous to today!")
        def base_qry = "select s from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) AND s.manualRenewalDate < ? order by s.manualRenewalDate asc "
        def results = Subscription.executeQuery(base_qry, qry_params);
        if (results.size() == 0)
            log.error("ReminderService :: getAuthorisedSubsciptionsByUser - Unable to retrieve any subscriptions for user ${user.username}")

        log.debug("Returned list of Subscriptions : ${results}")
        results
    }

    //Key Subscription -> [[user: u, reminder: r]]
    //Will need to add Subscription to the map sent to the template subscription: sub
    private def generateMail(Subscription sub, ArrayList userRemindersList) {
        log.debug("Setting up mail generation requirements for Subscription ${sub.name}...");
        def emailTemplateFile = generic? applicationContext.getResource("WEB-INF/mail-templates/subscriptionManualRenewalDateGeneric.gsp").file : applicationContext.getResource("WEB-INF/mail-templates/subscriptionManualRenewalDate.gsp").file
        def engine            = new SimpleTemplateEngine()
        def baseTemplate      = engine.createTemplate(emailTemplateFile)
        def _template
        def _content
        Date now = new Date()
        if (generic)
        {
            _template = baseTemplate.make([subscription: sub])
            _content  = _template.toString()
            def userEmailList = userRemindersList.collect {it.user.email}.toArray()
            mailReminder(userEmailList, "Renewal Reminder", _content)
            Reminder.withTransaction { status ->
                userRemindersList.each { it.reminder.lastRan = now }
            }
        }
        else
        {
            userRemindersList.each { inst ->
                _template =  baseTemplate.make(inst.put(subscription: sub))
                _content  = _template.toString()
                mailReminder(inst.user.email, inst.reminder.trigger.value, _content)
                Reminder.withTransaction { status ->
                    inst.reminder.lastRan = now //Update the Reminder instance
                }
            }
        }

    }

    def mailReminder(userAddress, subjectTrigger, content) {
        mailService.sendMail {
            async true
            to userAddress
            from from
            replyTo replyTo
            subject subjectTrigger
            html content
        }
    }

    /**
     * isSubInReminderRange - Run from a large list of possible subscriptions that user has access to!
     * @param reminders     - List of Reminders per user
     * @param sub           - Current iteration of a Subscription
     * @param today         - Today's date in Joda format
     */
    private def isSubInReminderRange(ArrayList reminders, Subscription sub, LocalDate today, User u)
    {
        def result = [:]
        result.isFound = false
        LocalDate renewalDate = new LocalDate(sub.manualRenewalDate) //std Java date

        reminders.each { r ->
         def reminderDate = convertReminderToDate(r.unit.value, r.amount, renewalDate) //e.g. sub renewal 12.11.15  rem date 3 months before = 12:8:15
         if(isDateInReminderPeriod(reminderDate, renewalDate, today))
         {  
           result.isFound    = true
           result.instance = (['reminder':r, 'user':u])
           return //Break out of each, should only be one for user in this period, and pointless reminder again!
         } 
        }
    }

    /**
     * Checks if a Joda Time LocalDate instance 'today' is between start and end range
     * @param today - Typically will be the day this code is executed, however, could be anything should you decide so.
     */
    private boolean isDateInReminderPeriod(LocalDate start, LocalDate end, LocalDate today) {
        log.debug("Converted Reminder Date: ${start}  +  Subscription Renewal: ${end}  +  Todays Date: ${today}")
        return !today.isBefore(start) && !today.isAfter(end);
    }

    /**
     * Returns the calculated date
     * @param  unit       - Week
     * @param amount      - 3
     * @param convertFrom - subRenewalDate
     * @return LocalDate
     */
    private LocalDate convertReminderToDate(String unit, int amount, LocalDate convertFrom)
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
    /**
     * Method executed in NotificationJob!
     * @return
     */
    def runReminders() {
        def start_time = System.currentTimeMillis();
        log.debug("Running reminder service... Started: ${start_time}")
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
            def user = v.first().user

            //All possible subscriptions for this user
            def availableSubs  = getAuthorisedSubsciptionsByUser(user)

            log.debug("Lookup up ${v.size()}:Reminders for dates **  ${v.collect { [it.unit.value,it.amount] }}   ** \nSubscriptions for user ID:${k} (Username:${user.username}) total has ${availableSubs.size()} ")

            //Iterate through available subscriptions (List would not have anything past 12 months of present date for server load purposes!)
            //Organise master list i.e. [Subscription inst][['reminder':reminder inst1, 'user': user inst1],['reminder':reminder inst2, 'user': user inst2],etc]
            availableSubs.each { sub ->
                if (sub.manualRenewalDate != null) {
                    def reminderAndUserInst = isSubInReminderRange(v, sub, today, user)

                    if (reminderAndUserInst.isFound) {
                        log.debug("Found Subscription: ${sub.name} for User: ${reminderAndUserInst.instance.user.username} Reminder: ${reminderAndUserInst.instance.reminder.id}")
                        addToMasterList(masterSubscriptionList, sub, reminderAndUserInst.instance)
                    }
                }
            }
        }

        //Now a master list has been created (after going through each reminder for a user), need to process for email now!
        processMasterList(masterSubscriptionList)

        def end_time = System.currentTimeMillis() - start_time
        log.debug("Finished time taken: ${end_time}")
    }

    private void addToMasterList(LinkedHashMap masterSubscriptionList, Subscription sub, reminderAndUserInst) {
        def val = [] as List
        if (masterSubscriptionList.containsKey(sub))
        {
            val = masterSubscriptionList.get(sub)
            val.add(reminderAndUserInst)
            masterSubscriptionList.put(sub,val)
        }
        else
        {
            val.add(reminderAndUserInst)
            masterSubscriptionList.put(sub,val)
        }
    }

    /**
     * After all the Reminders for each users has been checked and then added to a master list of Subscriptions
     * Now is the time for the final process, iterate through and each Subscription generating the emails
     * @param masterSubscriptionList - [Subscription][[reminder1, user1],[reminder2, user2]]
     */
    private void processMasterList(LinkedHashMap masterSubscriptionList) {
        log.debug("Processing master list, ready for emailing based on each Subscription")

        masterSubscriptionList.eachWithIndex { subscription, valueList, i ->
            if (i % 5000 == 0)
                log.debug("Proessed ${i} Subscriptions...")

            generateMail(subscription, valueList)
        }
    }
}

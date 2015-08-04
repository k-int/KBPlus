package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.transaction.Transactional
import groovy.text.SimpleTemplateEngine
import org.apache.commons.lang3.time.DateUtils
import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.joda.time.LocalDate
import org.springframework.context.ApplicationContext

import javax.annotation.PostConstruct

@Transactional
class ReminderService {

    ApplicationContext applicationContext
    def mailService
    def grailsApplication
    String from
    String replyTo

    @PostConstruct
    void init() {
        from    = grailsApplication.config.notifications.email.from
        replyTo = grailsApplication.config.notifications.email.replyTo
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
    def generateMail(List userMailList) {
        log.debug("Setting up mail requirements...");
        def emailTemplateFile = applicationContext.getResource("WEB-INF/mail-templates/subscriptionManualRenewalDate.gsp").file
        def engine            = new SimpleTemplateEngine()
        def baseTemplate      = engine.createTemplate(emailTemplateFile)
        userMailList.each { key,val ->
            def template = baseTemplate.make(val)
            def content  = template.toString()
            mailReminder(val.setup, content)
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

    
    def isSubInReminderRange(ArrayList reminders, Subscription sub, LocalDate today)
    {
        result = [:]
        result.found = 0
        LocalDate renewalDate = new LocalDate(sub.manualRenewalDate)
        reminders.each { r ->
         def reminderDate = dateconvertReminderToDate(r.unit.value, r.amount, subRenewal) //e.g. sub renewal 12.11.15  rem date 3 months before = 12:8:15
         if(isDateInReminderPeriod(reminderDate, renewalDate, today))
         {  
           result.found++
           //todo add to result 
         } 
        }
    }
    
    //Date has to be between start and end range
    def isDateInReminderPeriod(LocalDate start, LocalDate end, LocalDate today) {
          return !today.isBefore(start) && !today.isAfter(end);
    }
    
    //Returns the calculated date i.e. Unit = Week Amount = 3 convertFrom = subRenewalDate 
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
        if (mailService.disabled)
        {
            log.error("Unable to send reminders, mail service is disabled!")
            return
        }
        log.debug("Running reminder service...")
        def usersReminders = getActiveEmailRemindersByUserID()
        int userCounter    = usersReminders.size()
        LocalDate today    = new LocalDate();
        log.debug("Presently there is ${userCounter} Users with potentially 1..* reminders")
       
        def subcriptionKeyToUserAndReminder = [:]

        //Key = User ID Val = List of Reminders 1..* for that user!
        //Go through each reminder a user has, lookup subscriptions accessible to user, compare each subscription to the date range 
        usersReminders.each { k,v -> 
            log.debug("Lookup up ${v.size()}:Reminders Subscriptions for user ID:${k}  Username:${v.user.username}")

            //Dates of interest, more convenient
           // def reminderDates = v.collect { [it.unit.value,it.amount] }

            //All possible subscriptions for this user
            def availibleSubs  = getAccessibleSubsForUser(v.user)
            
            //List would not have anything past 12 months of present date for server load purposes!
            //todo upon finding relevant matching, would be more efficient to add a key as Subcription ID, then add a list of arrays[sub id][[user, reminder],[]] instances as values
            availibleSubs.each { sub ->
                isSubInReminderRange(v,sub.manualRenewalDate)
            }


        }
    }

}

package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.transaction.Transactional
import groovy.text.SimpleTemplateEngine
import org.elasticsearch.common.joda.time.DateTime
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

    def isSubInReminderRange(String unit, int amount, Date subRenewal)
    {
        DateTime datetime = new DateTime(subRenewal)
        switch (unit)
        {
            case 'Day':
                datetime.minusDays(amount)
                break
            case 'Week':
                datetime.minusWeeks(amount)
                break
            case 'Month':
                datetime.minusMonths(amount)
                break
            default:
                datetime.minusDays(amount)
                break
        }
    }

    def runReminders() {
        if (mailService.disabled)
        {
            log.error("Unable to send reminders, mail service is disabled!")
            return
        }
        log.debug("Running reminder service...")
        def usersReminders = getActiveEmailRemindersByUserID()
        int userCounter    = usersReminders.size()
        log.debug("Presently there is ${userCounter} Users with potentially 1..* reminders")

        usersReminders.each { k,v ->
            log.debug("Lookup up ${v.size()}:Reminders Subscriptions for user ID:${k}  Username:${v.user.username}")
            reminderDates = v.collect { it.}
            def availibleSubs  = getAccessibleSubsForUser(v.user)
        }
    }
}

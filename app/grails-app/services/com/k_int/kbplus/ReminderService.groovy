package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.transaction.Transactional
import groovy.text.SimpleTemplateEngine
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
    def getActiveRemindersByUserID() {
        //Get active reminders and users who have email
        def result = [:]
        Reminder.executeQuery('select r from Reminder as r where r.active = ? and r.user.email != null order by r.user.id',[true]).each { r ->
            result[r.user.id] << [r]  //Group Reminders and organise via users ID
        }
        result
    }

    //Perform relevant searching/joins to return all subscriptions of interest
    def getAccessibleSubsForUser(User user) {

    }


    def generateMail(List content) {

    }

    def mailReminders(result) {
        log.debug("sendEmail....");
        def emailTemplateFile = applicationContext.getResource("WEB-INF/mail-templates/subscriptionManualRenewalDate.gsp").file
        def engine = new SimpleTemplateEngine()
        def tmpl = engine.createTemplate(emailTemplateFile).make(result)
        def content = tmpl.toString()

        mailService.sendMail {
            to result.user.email
            from from
            replyTo replyTo
            subject 'KBPlus Housekeeping Results'
            html content
        }


    }

    def runReminders() {
        if (mailService.disabled)
        {
            log.error("Unable to send reminders, mail service is disabled!")
            return
        }
    }
}

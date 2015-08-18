package com.k_int.kbplus

import com.k_int.kbplus.auth.Role
import com.k_int.kbplus.auth.User
import com.k_int.kbplus.auth.UserOrg
import grails.plugin.mail.MailService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.text.SimpleTemplateEngine
import net.sf.cglib.core.Local
import org.codehaus.groovy.grails.io.support.ClassPathResource
import org.codehaus.groovy.grails.io.support.GrailsResourceUtils
import org.codehaus.groovy.grails.io.support.Resource
import org.elasticsearch.common.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Specification

//https://github.com/pschneider-manzell/grails-spock-examples/blob/grails_2_3_7/grails-app/services/grails/geb/spock/GuttenbergService.groovy
//  http://stackoverflow.com/questions/27615988/grails-unit-test-mock-service-vs-assign-service-instance
//      ////http://stackoverflow.com/questions/10715919/how-to-mock-domain-specific-closures-in-spock
/**
 * Created by Ryan@k-int.com
 */
@TestFor(ReminderService)
@Mock([Reminder, User, Subscription, Org, OrgRole, TitleInstance, RefdataValue, SubscriptionPackage, UserOrg, Role, RefdataCategory, TitleHistoryEventParticipant])
class ReminderServiceTests extends Specification {



    @Shared user, org, userOrg, roleUser, sub, orgRole, ti, refdataValues, reminder
    def mailService

    /**
     * Setup required instances, run before the first feature method
     */
    def setup() {
        def refSubStatus  = new RefdataValue(value: 'Current').save()
        def refSubPublic  = new RefdataValue(value: 'No').save()
        def refSubType    = new RefdataValue(value: 'Subscription Taken').save()
        def refRemUnit    = new RefdataValue(value: 'Month').save()
        def refOrgRole    = new RefdataValue(value: 'Subscriber').save()
        def refRemMethod  = new RefdataValue(value: 'email').save()
        def refRemTrigger = new RefdataValue(value: 'Subscription Manual Renewal Date').save()
        reminder          = new Reminder(user: user,  active: true, amount: 1, unit:refRemUnit, reminderMethod:refRemMethod, trigger: refRemTrigger, lastRan:null).save()
        refdataValues     = [refSubStatus,refSubPublic, refSubType, refRemUnit, refOrgRole, refRemMethod, refRemTrigger]
        roleUser          = new Role(authority: 'INST_USER', roleType:'global').save()

        ti                = new TitleInstance(title: "A random title....", impId: UUID.randomUUID().toString()).save()
        sub               = new Subscription(name:"A random subscription name",
                                             status:refSubStatus,
                                             identifier:UUID.randomUUID().toString(),
                                             impId:UUID.randomUUID().toString(),
                                             startDate:new LocalDate().minusYears(1).toDate(),
                                             endDate: new LocalDate().plusMonths(1).toDate(),
                                             isPublic: refSubPublic,
                                             type: refSubType,
                                             manualRenewalDate: new LocalDate().minusMonths(3).toDate(),
                                             isSlaved: refSubPublic).save()
        org               = new Org(name: "new org", impId: UUID.randomUUID().toString()).save()
        orgRole           = new OrgRole(sub: sub, roleType: refOrgRole, org: org).save()
        user              = new User(username: 'j_doe', firstname: "John", lastname: "Doe", email: 'ryan@k-int.com', defaultDash: org).save()
        userOrg           = new UserOrg(org: org, user: user, formalRole: roleUser, status: 1).save()
        mailService       = new MailService()
//        mockDomain(RefdataValue, refdataValues)
//        mockDomain(TitleInstance, ti)
//        mockDomain(OrgRole, orgRole)
//        mockDomain(Org, org)
//        mockDomain(User, user)
//        mockDomain(UserOrg, userOrg)
//        mockDomain(Reminder, reminder)
    }

    def "mailer service is actively working "() {
        setup: "HTML templates to be rendered in the email"
//        println GrailsResourceUtils.getPathFromRoot("/home/razdev/Dev/KBPlusWork/KBPlus/app/web-app/WEB-INF/mail-templates/subscriptionManualRenewalDate.gsp")
        println GrailsResourceUtils.getArtefactDirectory("/home/razdev/Dev/KBPlusWork/KBPlus/app/web-app/WEB-INF/mail-templates/subscriptionManualRenewalDate.gsp")
        Resource resource = new ClassPathResource("../web-app/WEB-INF/mail-templates/subscriptionManualRenewalDateGeneric.gsp")
        println(resource.path)
        def emailTemplateFile = new File("/home/razdev/Dev/KBPlusWork/KBPlus/app/web-app/WEB-INF/mail-templates/subscriptionManualRenewalDate.gsp")
        def engine            = new SimpleTemplateEngine()
        def baseTemplate      = engine.createTemplate(emailTemplateFile)
        def template          = baseTemplate.make([subscription: sub])
        def content           = template.toString()

        when: "Sending the email with the constructed html..."
        mailService.sendMail {
            async true
            to "ryan@k-int.com"
            from "noreply@k-int.com"
            subject "Test email"
            html content
        }

        then: "The html content should not be null and you should have an email in your inbox!"
        assertNotNull(content)
        content.startsWith("<h1>Subscription Renewals Reminder</h1>")
        println("You will need to check your email!")
    }

    def "Getting all the authorised subscriptions for a specific user"() {
        //Create Reminder Service ReminderService reminderService = new ReminderService()
        when:
        def subscriptions = service.getAuthorisedSubsciptionsByUser(user)

        then:
        subscriptions != null
        subscriptions.size() > 0
    }

    def "Getting all the active reminders via a valid user instance containing an email address"() {
        //Create Reminder Service ReminderService reminderService = new ReminderService()
        when: "retrieving instances..."
        def reminders = service.getActiveEmailRemindersByUserID()

        then: "Should return a valid LinkedHashMap ordered with key as UID and values as multiple reminders for that user"
        assertNotNull(reminders)
        reminders instanceof LinkedHashMap
        reminders.size() == 1
        reminders.containsValue(reminder)
    }


    def "check the reminder date is created properly e.g. 3 months before today"() {
        setup: "The date to check against..."
        def correctDate = new LocalDate(sub.manualRenewalDate).minusMonths(reminder.amount)
        def toCheck

        when: "We have the unit type e.g. Month"
        switch (reminder.unit.value)
        {
            case 'Day':
                toCheck = new LocalDate(sub.manualRenewalDate).minusDays(reminder.amount)
                break
            case 'Week':
                toCheck = new LocalDate(sub.manualRenewalDate).minusWeeks(amount)
                break
            case 'Month':
                toCheck =  new LocalDate(sub.manualRenewalDate).minusMonths(reminder.amount)
                break
            default:
                toCheck =  new LocalDate(sub.manualRenewalDate).minusDays(reminder.amount)
                break
        }

        then: "Assert that the dates are the same"
        assertNotNull(toCheck)
        correctDate.equals(toCheck)
    }


    def "Checking the a subscription date is in the appropriate range"() {
        //Create Reminder Service ReminderService reminderService = new ReminderService()
        when: "We have a range of dates, only one out of the three should be correct"
        LocalDate renewalDate    = new LocalDate(sub.manualRenewalDate)
        LocalDate reminderDate   = service.convertReminderToDate(reminder.unit.value, reminder.amount, renewalDate)
        LocalDate today          = new LocalDate()


        then: "Dates should be in the range and fail if they are not"
        assertNotNull(reminder)
        assertTrue(service.isDateInReminderPeriod(reminderDate, renewalDate, today))
    }
}

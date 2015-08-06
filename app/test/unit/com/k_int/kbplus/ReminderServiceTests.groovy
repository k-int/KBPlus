package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Ryan@k-int.com
 */
@TestFor(ReminderService)
@Mock([Reminder, User, Subscription])
class ReminderServiceTests extends Specification {


    def "Getting all the authorised subscriptions for a specific user"() {
        //2. Setup mocks
        setup:
        //todo setup other required instances to mock at least one valid subscription
        //Create Reminder Service
        ReminderService reminderService = new ReminderService()
        //Use the user object created in the "where" statement to create mocked domain objects.
        //In this step, the user instance get's the ID assigned
        mockDomain(User, [userInstance])
        //Mock domain class subscription (required by the reminder service)
        mockDomain(Subscription)

        //3. Call the method to test
        when:
        def subscriptions = reminderService.getAuthorisedSubsciptionsByUser(userInstance)

        then:
        //4. Make asserts on the result
        subscriptions != null
        subscriptions.size() > 0

        //1. Create a dummy user instance used for the test. This instance has NO id yet
        where:
        userInstance = new User(username: 'j_doe', firstname: "John", lastname: "Doe", email: 'ryan@k-int.com')
    }
}

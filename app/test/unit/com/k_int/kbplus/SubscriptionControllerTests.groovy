package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(SubscriptionController)
@Mock(Subscription)
class SubscriptionControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/subscription/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.subscriptionInstanceList.size() == 0
        assert model.subscriptionInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.subscriptionInstance != null
    }

    void testSave() {
        controller.save()

        assert model.subscriptionInstance != null
        assert view == '/subscription/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/subscription/show/1'
        assert controller.flash.message != null
        assert Subscription.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/subscription/list'


        populateValidParams(params)
        def subscription = new Subscription(params)

        assert subscription.save() != null

        params.id = subscription.id

        def model = controller.show()

        assert model.subscriptionInstance == subscription
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/subscription/list'


        populateValidParams(params)
        def subscription = new Subscription(params)

        assert subscription.save() != null

        params.id = subscription.id

        def model = controller.edit()

        assert model.subscriptionInstance == subscription
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/subscription/list'

        response.reset()


        populateValidParams(params)
        def subscription = new Subscription(params)

        assert subscription.save() != null

        // test invalid parameters in update
        params.id = subscription.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/subscription/edit"
        assert model.subscriptionInstance != null

        subscription.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/subscription/show/$subscription.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        subscription.clearErrors()

        populateValidParams(params)
        params.id = subscription.id
        params.version = -1
        controller.update()

        assert view == "/subscription/edit"
        assert model.subscriptionInstance != null
        assert model.subscriptionInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/subscription/list'

        response.reset()

        populateValidParams(params)
        def subscription = new Subscription(params)

        assert subscription.save() != null
        assert Subscription.count() == 1

        params.id = subscription.id

        controller.delete()

        assert Subscription.count() == 0
        assert Subscription.get(subscription.id) == null
        assert response.redirectedUrl == '/subscription/list'
    }
}

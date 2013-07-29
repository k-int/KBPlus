package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(OnixplUsageTermController)
@Mock(OnixplUsageTerm)
class OnixplUsageTermControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/onixplUsageTerm/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.onixplUsageTermInstanceList.size() == 0
        assert model.onixplUsageTermInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.onixplUsageTermInstance != null
    }

    void testSave() {
        controller.save()

        assert model.onixplUsageTermInstance != null
        assert view == '/onixplUsageTerm/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/onixplUsageTerm/show/1'
        assert controller.flash.message != null
        assert OnixplUsageTerm.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTerm/list'

        populateValidParams(params)
        def onixplUsageTerm = new OnixplUsageTerm(params)

        assert onixplUsageTerm.save() != null

        params.id = onixplUsageTerm.id

        def model = controller.show()

        assert model.onixplUsageTermInstance == onixplUsageTerm
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTerm/list'

        populateValidParams(params)
        def onixplUsageTerm = new OnixplUsageTerm(params)

        assert onixplUsageTerm.save() != null

        params.id = onixplUsageTerm.id

        def model = controller.edit()

        assert model.onixplUsageTermInstance == onixplUsageTerm
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTerm/list'

        response.reset()

        populateValidParams(params)
        def onixplUsageTerm = new OnixplUsageTerm(params)

        assert onixplUsageTerm.save() != null

        // test invalid parameters in update
        params.id = onixplUsageTerm.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/onixplUsageTerm/edit"
        assert model.onixplUsageTermInstance != null

        onixplUsageTerm.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/onixplUsageTerm/show/$onixplUsageTerm.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        onixplUsageTerm.clearErrors()

        populateValidParams(params)
        params.id = onixplUsageTerm.id
        params.version = -1
        controller.update()

        assert view == "/onixplUsageTerm/edit"
        assert model.onixplUsageTermInstance != null
        assert model.onixplUsageTermInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTerm/list'

        response.reset()

        populateValidParams(params)
        def onixplUsageTerm = new OnixplUsageTerm(params)

        assert onixplUsageTerm.save() != null
        assert OnixplUsageTerm.count() == 1

        params.id = onixplUsageTerm.id

        controller.delete()

        assert OnixplUsageTerm.count() == 0
        assert OnixplUsageTerm.get(onixplUsageTerm.id) == null
        assert response.redirectedUrl == '/onixplUsageTerm/list'
    }
}

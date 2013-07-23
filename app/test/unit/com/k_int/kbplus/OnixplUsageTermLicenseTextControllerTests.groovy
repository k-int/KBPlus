package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(OnixplUsageTermLicenseTextController)
@Mock(OnixplUsageTermLicenseText)
class OnixplUsageTermLicenseTextControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/onixplUsageTermLicenseText/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.onixplUsageTermLicenseTextInstanceList.size() == 0
        assert model.onixplUsageTermLicenseTextInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.onixplUsageTermLicenseTextInstance != null
    }

    void testSave() {
        controller.save()

        assert model.onixplUsageTermLicenseTextInstance != null
        assert view == '/onixplUsageTermLicenseText/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/onixplUsageTermLicenseText/show/1'
        assert controller.flash.message != null
        assert OnixplUsageTermLicenseText.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'

        populateValidParams(params)
        def onixplUsageTermLicenseText = new OnixplUsageTermLicenseText(params)

        assert onixplUsageTermLicenseText.save() != null

        params.id = onixplUsageTermLicenseText.id

        def model = controller.show()

        assert model.onixplUsageTermLicenseTextInstance == onixplUsageTermLicenseText
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'

        populateValidParams(params)
        def onixplUsageTermLicenseText = new OnixplUsageTermLicenseText(params)

        assert onixplUsageTermLicenseText.save() != null

        params.id = onixplUsageTermLicenseText.id

        def model = controller.edit()

        assert model.onixplUsageTermLicenseTextInstance == onixplUsageTermLicenseText
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'

        response.reset()

        populateValidParams(params)
        def onixplUsageTermLicenseText = new OnixplUsageTermLicenseText(params)

        assert onixplUsageTermLicenseText.save() != null

        // test invalid parameters in update
        params.id = onixplUsageTermLicenseText.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/onixplUsageTermLicenseText/edit"
        assert model.onixplUsageTermLicenseTextInstance != null

        onixplUsageTermLicenseText.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/onixplUsageTermLicenseText/show/$onixplUsageTermLicenseText.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        onixplUsageTermLicenseText.clearErrors()

        populateValidParams(params)
        params.id = onixplUsageTermLicenseText.id
        params.version = -1
        controller.update()

        assert view == "/onixplUsageTermLicenseText/edit"
        assert model.onixplUsageTermLicenseTextInstance != null
        assert model.onixplUsageTermLicenseTextInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'

        response.reset()

        populateValidParams(params)
        def onixplUsageTermLicenseText = new OnixplUsageTermLicenseText(params)

        assert onixplUsageTermLicenseText.save() != null
        assert OnixplUsageTermLicenseText.count() == 1

        params.id = onixplUsageTermLicenseText.id

        controller.delete()

        assert OnixplUsageTermLicenseText.count() == 0
        assert OnixplUsageTermLicenseText.get(onixplUsageTermLicenseText.id) == null
        assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'
    }
}

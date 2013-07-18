package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(OnixplLicenseTextController)
@Mock(OnixplLicenseText)
class OnixplLicenseTextControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/onixplLicenseText/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.onixplLicenseTextInstanceList.size() == 0
        assert model.onixplLicenseTextInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.onixplLicenseTextInstance != null
    }

    void testSave() {
        controller.save()

        assert model.onixplLicenseTextInstance != null
        assert view == '/onixplLicenseText/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/onixplLicenseText/show/1'
        assert controller.flash.message != null
        assert OnixplLicenseText.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplLicenseText/list'

        populateValidParams(params)
        def onixplLicenseText = new OnixplLicenseText(params)

        assert onixplLicenseText.save() != null

        params.id = onixplLicenseText.id

        def model = controller.show()

        assert model.onixplLicenseTextInstance == onixplLicenseText
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplLicenseText/list'

        populateValidParams(params)
        def onixplLicenseText = new OnixplLicenseText(params)

        assert onixplLicenseText.save() != null

        params.id = onixplLicenseText.id

        def model = controller.edit()

        assert model.onixplLicenseTextInstance == onixplLicenseText
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplLicenseText/list'

        response.reset()

        populateValidParams(params)
        def onixplLicenseText = new OnixplLicenseText(params)

        assert onixplLicenseText.save() != null

        // test invalid parameters in update
        params.id = onixplLicenseText.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/onixplLicenseText/edit"
        assert model.onixplLicenseTextInstance != null

        onixplLicenseText.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/onixplLicenseText/show/$onixplLicenseText.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        onixplLicenseText.clearErrors()

        populateValidParams(params)
        params.id = onixplLicenseText.id
        params.version = -1
        controller.update()

        assert view == "/onixplLicenseText/edit"
        assert model.onixplLicenseTextInstance != null
        assert model.onixplLicenseTextInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/onixplLicenseText/list'

        response.reset()

        populateValidParams(params)
        def onixplLicenseText = new OnixplLicenseText(params)

        assert onixplLicenseText.save() != null
        assert OnixplLicenseText.count() == 1

        params.id = onixplLicenseText.id

        controller.delete()

        assert OnixplLicenseText.count() == 0
        assert OnixplLicenseText.get(onixplLicenseText.id) == null
        assert response.redirectedUrl == '/onixplLicenseText/list'
    }
}

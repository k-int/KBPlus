package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(OnixplLicenseController)
@Mock(OnixplLicense)
class OnixplLicenseControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/onixplLicense/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.onixplLicenseInstanceList.size() == 0
        assert model.onixplLicenseInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.onixplLicenseInstance != null
    }

    void testSave() {
        controller.save()

        assert model.onixplLicenseInstance != null
        assert view == '/onixplLicense/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/onixplLicense/show/1'
        assert controller.flash.message != null
        assert OnixplLicense.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplLicense/list'

        populateValidParams(params)
        def onixplLicense = new OnixplLicense(params)

        assert onixplLicense.save() != null

        params.id = onixplLicense.id

        def model = controller.show()

        assert model.onixplLicenseInstance == onixplLicense
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplLicense/list'

        populateValidParams(params)
        def onixplLicense = new OnixplLicense(params)

        assert onixplLicense.save() != null

        params.id = onixplLicense.id

        def model = controller.edit()

        assert model.onixplLicenseInstance == onixplLicense
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplLicense/list'

        response.reset()

        populateValidParams(params)
        def onixplLicense = new OnixplLicense(params)

        assert onixplLicense.save() != null

        // test invalid parameters in update
        params.id = onixplLicense.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/onixplLicense/edit"
        assert model.onixplLicenseInstance != null

        onixplLicense.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/onixplLicense/show/$onixplLicense.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        onixplLicense.clearErrors()

        populateValidParams(params)
        params.id = onixplLicense.id
        params.version = -1
        controller.update()

        assert view == "/onixplLicense/edit"
        assert model.onixplLicenseInstance != null
        assert model.onixplLicenseInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/onixplLicense/list'

        response.reset()

        populateValidParams(params)
        def onixplLicense = new OnixplLicense(params)

        assert onixplLicense.save() != null
        assert OnixplLicense.count() == 1

        params.id = onixplLicense.id

        controller.delete()

        assert OnixplLicense.count() == 0
        assert OnixplLicense.get(onixplLicense.id) == null
        assert response.redirectedUrl == '/onixplLicense/list'
    }
}

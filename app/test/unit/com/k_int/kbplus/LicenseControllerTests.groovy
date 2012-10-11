package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(LicenseController)
@Mock(License)
class LicenseControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/license/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.licenseInstanceList.size() == 0
        assert model.licenseInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.licenseInstance != null
    }

    void testSave() {
        controller.save()

        assert model.licenseInstance != null
        assert view == '/license/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/license/show/1'
        assert controller.flash.message != null
        assert License.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/license/list'


        populateValidParams(params)
        def license = new License(params)

        assert license.save() != null

        params.id = license.id

        def model = controller.show()

        assert model.licenseInstance == license
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/license/list'


        populateValidParams(params)
        def license = new License(params)

        assert license.save() != null

        params.id = license.id

        def model = controller.edit()

        assert model.licenseInstance == license
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/license/list'

        response.reset()


        populateValidParams(params)
        def license = new License(params)

        assert license.save() != null

        // test invalid parameters in update
        params.id = license.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/license/edit"
        assert model.licenseInstance != null

        license.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/license/show/$license.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        license.clearErrors()

        populateValidParams(params)
        params.id = license.id
        params.version = -1
        controller.update()

        assert view == "/license/edit"
        assert model.licenseInstance != null
        assert model.licenseInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/license/list'

        response.reset()

        populateValidParams(params)
        def license = new License(params)

        assert license.save() != null
        assert License.count() == 1

        params.id = license.id

        controller.delete()

        assert License.count() == 0
        assert License.get(license.id) == null
        assert response.redirectedUrl == '/license/list'
    }
}

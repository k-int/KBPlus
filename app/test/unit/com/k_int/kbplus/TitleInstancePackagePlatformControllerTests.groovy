package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(TitleInstancePackagePlatformController)
@Mock(TitleInstancePackagePlatform)
class TitleInstancePackagePlatformControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/titleInstancePackagePlatform/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.titleInstancePackagePlatformInstanceList.size() == 0
        assert model.titleInstancePackagePlatformInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.titleInstancePackagePlatformInstance != null
    }

    void testSave() {
        controller.save()

        assert model.titleInstancePackagePlatformInstance != null
        assert view == '/titleInstancePackagePlatform/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/titleInstancePackagePlatform/show/1'
        assert controller.flash.message != null
        assert TitleInstancePackagePlatform.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/titleInstancePackagePlatform/list'


        populateValidParams(params)
        def titleInstancePackagePlatform = new TitleInstancePackagePlatform(params)

        assert titleInstancePackagePlatform.save() != null

        params.id = titleInstancePackagePlatform.id

        def model = controller.show()

        assert model.titleInstancePackagePlatformInstance == titleInstancePackagePlatform
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/titleInstancePackagePlatform/list'


        populateValidParams(params)
        def titleInstancePackagePlatform = new TitleInstancePackagePlatform(params)

        assert titleInstancePackagePlatform.save() != null

        params.id = titleInstancePackagePlatform.id

        def model = controller.edit()

        assert model.titleInstancePackagePlatformInstance == titleInstancePackagePlatform
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/titleInstancePackagePlatform/list'

        response.reset()


        populateValidParams(params)
        def titleInstancePackagePlatform = new TitleInstancePackagePlatform(params)

        assert titleInstancePackagePlatform.save() != null

        // test invalid parameters in update
        params.id = titleInstancePackagePlatform.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/titleInstancePackagePlatform/edit"
        assert model.titleInstancePackagePlatformInstance != null

        titleInstancePackagePlatform.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/titleInstancePackagePlatform/show/$titleInstancePackagePlatform.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        titleInstancePackagePlatform.clearErrors()

        populateValidParams(params)
        params.id = titleInstancePackagePlatform.id
        params.version = -1
        controller.update()

        assert view == "/titleInstancePackagePlatform/edit"
        assert model.titleInstancePackagePlatformInstance != null
        assert model.titleInstancePackagePlatformInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/titleInstancePackagePlatform/list'

        response.reset()

        populateValidParams(params)
        def titleInstancePackagePlatform = new TitleInstancePackagePlatform(params)

        assert titleInstancePackagePlatform.save() != null
        assert TitleInstancePackagePlatform.count() == 1

        params.id = titleInstancePackagePlatform.id

        controller.delete()

        assert TitleInstancePackagePlatform.count() == 0
        assert TitleInstancePackagePlatform.get(titleInstancePackagePlatform.id) == null
        assert response.redirectedUrl == '/titleInstancePackagePlatform/list'
    }
}

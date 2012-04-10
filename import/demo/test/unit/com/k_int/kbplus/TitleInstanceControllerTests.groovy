package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(TitleInstanceController)
@Mock(TitleInstance)
class TitleInstanceControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/titleInstance/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.titleInstanceInstanceList.size() == 0
        assert model.titleInstanceInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.titleInstanceInstance != null
    }

    void testSave() {
        controller.save()

        assert model.titleInstanceInstance != null
        assert view == '/titleInstance/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/titleInstance/show/1'
        assert controller.flash.message != null
        assert TitleInstance.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/titleInstance/list'


        populateValidParams(params)
        def titleInstance = new TitleInstance(params)

        assert titleInstance.save() != null

        params.id = titleInstance.id

        def model = controller.show()

        assert model.titleInstanceInstance == titleInstance
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/titleInstance/list'


        populateValidParams(params)
        def titleInstance = new TitleInstance(params)

        assert titleInstance.save() != null

        params.id = titleInstance.id

        def model = controller.edit()

        assert model.titleInstanceInstance == titleInstance
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/titleInstance/list'

        response.reset()


        populateValidParams(params)
        def titleInstance = new TitleInstance(params)

        assert titleInstance.save() != null

        // test invalid parameters in update
        params.id = titleInstance.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/titleInstance/edit"
        assert model.titleInstanceInstance != null

        titleInstance.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/titleInstance/show/$titleInstance.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        titleInstance.clearErrors()

        populateValidParams(params)
        params.id = titleInstance.id
        params.version = -1
        controller.update()

        assert view == "/titleInstance/edit"
        assert model.titleInstanceInstance != null
        assert model.titleInstanceInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/titleInstance/list'

        response.reset()

        populateValidParams(params)
        def titleInstance = new TitleInstance(params)

        assert titleInstance.save() != null
        assert TitleInstance.count() == 1

        params.id = titleInstance.id

        controller.delete()

        assert TitleInstance.count() == 0
        assert TitleInstance.get(titleInstance.id) == null
        assert response.redirectedUrl == '/titleInstance/list'
    }
}

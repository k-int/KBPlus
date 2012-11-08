package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(IdentifierController)
@Mock(Identifier)
class IdentifierControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/identifier/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.identifierInstanceList.size() == 0
        assert model.identifierInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.identifierInstance != null
    }

    void testSave() {
        controller.save()

        assert model.identifierInstance != null
        assert view == '/identifier/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/identifier/show/1'
        assert controller.flash.message != null
        assert Identifier.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/identifier/list'


        populateValidParams(params)
        def identifier = new Identifier(params)

        assert identifier.save() != null

        params.id = identifier.id

        def model = controller.show()

        assert model.identifierInstance == identifier
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/identifier/list'


        populateValidParams(params)
        def identifier = new Identifier(params)

        assert identifier.save() != null

        params.id = identifier.id

        def model = controller.edit()

        assert model.identifierInstance == identifier
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/identifier/list'

        response.reset()


        populateValidParams(params)
        def identifier = new Identifier(params)

        assert identifier.save() != null

        // test invalid parameters in update
        params.id = identifier.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/identifier/edit"
        assert model.identifierInstance != null

        identifier.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/identifier/show/$identifier.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        identifier.clearErrors()

        populateValidParams(params)
        params.id = identifier.id
        params.version = -1
        controller.update()

        assert view == "/identifier/edit"
        assert model.identifierInstance != null
        assert model.identifierInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/identifier/list'

        response.reset()

        populateValidParams(params)
        def identifier = new Identifier(params)

        assert identifier.save() != null
        assert Identifier.count() == 1

        params.id = identifier.id

        controller.delete()

        assert Identifier.count() == 0
        assert Identifier.get(identifier.id) == null
        assert response.redirectedUrl == '/identifier/list'
    }
}

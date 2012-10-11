package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(IdentifierOccurrenceController)
@Mock(IdentifierOccurrence)
class IdentifierOccurrenceControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/identifierOccurrence/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.identifierOccurrenceInstanceList.size() == 0
        assert model.identifierOccurrenceInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.identifierOccurrenceInstance != null
    }

    void testSave() {
        controller.save()

        assert model.identifierOccurrenceInstance != null
        assert view == '/identifierOccurrence/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/identifierOccurrence/show/1'
        assert controller.flash.message != null
        assert IdentifierOccurrence.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/identifierOccurrence/list'


        populateValidParams(params)
        def identifierOccurrence = new IdentifierOccurrence(params)

        assert identifierOccurrence.save() != null

        params.id = identifierOccurrence.id

        def model = controller.show()

        assert model.identifierOccurrenceInstance == identifierOccurrence
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/identifierOccurrence/list'


        populateValidParams(params)
        def identifierOccurrence = new IdentifierOccurrence(params)

        assert identifierOccurrence.save() != null

        params.id = identifierOccurrence.id

        def model = controller.edit()

        assert model.identifierOccurrenceInstance == identifierOccurrence
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/identifierOccurrence/list'

        response.reset()


        populateValidParams(params)
        def identifierOccurrence = new IdentifierOccurrence(params)

        assert identifierOccurrence.save() != null

        // test invalid parameters in update
        params.id = identifierOccurrence.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/identifierOccurrence/edit"
        assert model.identifierOccurrenceInstance != null

        identifierOccurrence.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/identifierOccurrence/show/$identifierOccurrence.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        identifierOccurrence.clearErrors()

        populateValidParams(params)
        params.id = identifierOccurrence.id
        params.version = -1
        controller.update()

        assert view == "/identifierOccurrence/edit"
        assert model.identifierOccurrenceInstance != null
        assert model.identifierOccurrenceInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/identifierOccurrence/list'

        response.reset()

        populateValidParams(params)
        def identifierOccurrence = new IdentifierOccurrence(params)

        assert identifierOccurrence.save() != null
        assert IdentifierOccurrence.count() == 1

        params.id = identifierOccurrence.id

        controller.delete()

        assert IdentifierOccurrence.count() == 0
        assert IdentifierOccurrence.get(identifierOccurrence.id) == null
        assert response.redirectedUrl == '/identifierOccurrence/list'
    }
}

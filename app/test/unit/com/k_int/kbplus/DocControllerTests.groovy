package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(DocController)
@Mock(Doc)
class DocControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/doc/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.docInstanceList.size() == 0
        assert model.docInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.docInstance != null
    }

    void testSave() {
        controller.save()

        assert model.docInstance != null
        assert view == '/doc/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/doc/show/1'
        assert controller.flash.message != null
        assert Doc.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/doc/list'


        populateValidParams(params)
        def doc = new Doc(params)

        assert doc.save() != null

        params.id = doc.id

        def model = controller.show()

        assert model.docInstance == doc
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/doc/list'


        populateValidParams(params)
        def doc = new Doc(params)

        assert doc.save() != null

        params.id = doc.id

        def model = controller.edit()

        assert model.docInstance == doc
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/doc/list'

        response.reset()


        populateValidParams(params)
        def doc = new Doc(params)

        assert doc.save() != null

        // test invalid parameters in update
        params.id = doc.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/doc/edit"
        assert model.docInstance != null

        doc.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/doc/show/$doc.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        doc.clearErrors()

        populateValidParams(params)
        params.id = doc.id
        params.version = -1
        controller.update()

        assert view == "/doc/edit"
        assert model.docInstance != null
        assert model.docInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/doc/list'

        response.reset()

        populateValidParams(params)
        def doc = new Doc(params)

        assert doc.save() != null
        assert Doc.count() == 1

        params.id = doc.id

        controller.delete()

        assert Doc.count() == 0
        assert Doc.get(doc.id) == null
        assert response.redirectedUrl == '/doc/list'
    }
}

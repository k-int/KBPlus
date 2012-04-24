package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(OrgController)
@Mock(Org)
class OrgControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/org/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.orgInstanceList.size() == 0
        assert model.orgInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.orgInstance != null
    }

    void testSave() {
        controller.save()

        assert model.orgInstance != null
        assert view == '/org/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/org/show/1'
        assert controller.flash.message != null
        assert Org.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/org/list'


        populateValidParams(params)
        def org = new Org(params)

        assert org.save() != null

        params.id = org.id

        def model = controller.show()

        assert model.orgInstance == org
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/org/list'


        populateValidParams(params)
        def org = new Org(params)

        assert org.save() != null

        params.id = org.id

        def model = controller.edit()

        assert model.orgInstance == org
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/org/list'

        response.reset()


        populateValidParams(params)
        def org = new Org(params)

        assert org.save() != null

        // test invalid parameters in update
        params.id = org.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/org/edit"
        assert model.orgInstance != null

        org.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/org/show/$org.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        org.clearErrors()

        populateValidParams(params)
        params.id = org.id
        params.version = -1
        controller.update()

        assert view == "/org/edit"
        assert model.orgInstance != null
        assert model.orgInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/org/list'

        response.reset()

        populateValidParams(params)
        def org = new Org(params)

        assert org.save() != null
        assert Org.count() == 1

        params.id = org.id

        controller.delete()

        assert Org.count() == 0
        assert Org.get(org.id) == null
        assert response.redirectedUrl == '/org/list'
    }
}

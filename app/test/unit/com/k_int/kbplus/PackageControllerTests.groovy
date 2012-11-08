package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(PackageController)
@Mock(Package)
class PackageControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/package/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.packageInstanceList.size() == 0
        assert model.packageInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.packageInstance != null
    }

    void testSave() {
        controller.save()

        assert model.packageInstance != null
        assert view == '/package/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/package/show/1'
        assert controller.flash.message != null
        assert Package.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/package/list'


        populateValidParams(params)
        def package = new Package(params)

        assert package.save() != null

        params.id = package.id

        def model = controller.show()

        assert model.packageInstance == package
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/package/list'


        populateValidParams(params)
        def package = new Package(params)

        assert package.save() != null

        params.id = package.id

        def model = controller.edit()

        assert model.packageInstance == package
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/package/list'

        response.reset()


        populateValidParams(params)
        def package = new Package(params)

        assert package.save() != null

        // test invalid parameters in update
        params.id = package.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/package/edit"
        assert model.packageInstance != null

        package.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/package/show/$package.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        package.clearErrors()

        populateValidParams(params)
        params.id = package.id
        params.version = -1
        controller.update()

        assert view == "/package/edit"
        assert model.packageInstance != null
        assert model.packageInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/package/list'

        response.reset()

        populateValidParams(params)
        def package = new Package(params)

        assert package.save() != null
        assert Package.count() == 1

        params.id = package.id

        controller.delete()

        assert Package.count() == 0
        assert Package.get(package.id) == null
        assert response.redirectedUrl == '/package/list'
    }
}

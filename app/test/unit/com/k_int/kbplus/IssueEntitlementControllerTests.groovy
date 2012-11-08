package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(IssueEntitlementController)
@Mock(IssueEntitlement)
class IssueEntitlementControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/issueEntitlement/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.issueEntitlementInstanceList.size() == 0
        assert model.issueEntitlementInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.issueEntitlementInstance != null
    }

    void testSave() {
        controller.save()

        assert model.issueEntitlementInstance != null
        assert view == '/issueEntitlement/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/issueEntitlement/show/1'
        assert controller.flash.message != null
        assert IssueEntitlement.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/issueEntitlement/list'

        populateValidParams(params)
        def issueEntitlement = new IssueEntitlement(params)

        assert issueEntitlement.save() != null

        params.id = issueEntitlement.id

        def model = controller.show()

        assert model.issueEntitlementInstance == issueEntitlement
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/issueEntitlement/list'

        populateValidParams(params)
        def issueEntitlement = new IssueEntitlement(params)

        assert issueEntitlement.save() != null

        params.id = issueEntitlement.id

        def model = controller.edit()

        assert model.issueEntitlementInstance == issueEntitlement
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/issueEntitlement/list'

        response.reset()

        populateValidParams(params)
        def issueEntitlement = new IssueEntitlement(params)

        assert issueEntitlement.save() != null

        // test invalid parameters in update
        params.id = issueEntitlement.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/issueEntitlement/edit"
        assert model.issueEntitlementInstance != null

        issueEntitlement.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/issueEntitlement/show/$issueEntitlement.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        issueEntitlement.clearErrors()

        populateValidParams(params)
        params.id = issueEntitlement.id
        params.version = -1
        controller.update()

        assert view == "/issueEntitlement/edit"
        assert model.issueEntitlementInstance != null
        assert model.issueEntitlementInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/issueEntitlement/list'

        response.reset()

        populateValidParams(params)
        def issueEntitlement = new IssueEntitlement(params)

        assert issueEntitlement.save() != null
        assert IssueEntitlement.count() == 1

        params.id = issueEntitlement.id

        controller.delete()

        assert IssueEntitlement.count() == 0
        assert IssueEntitlement.get(issueEntitlement.id) == null
        assert response.redirectedUrl == '/issueEntitlement/list'
    }
}

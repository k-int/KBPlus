package com.k_int.kbplus

import org.junit.*
import grails.test.mixin.*

@TestFor(OnixplUsageTermController)
@Mock([OnixplUsageTerm])
class OnixplUsageTermControllerTests {

    def populateValidParams(params) {
      assert params != null
      params['oplLicense'] = new OnixplLicense()
      params['usageType'] = new RefdataValue()
      params['usageStatus'] = new RefdataValue()
    }

    void testIndex() {
        controller.index()
        assert "/onixplUsageTerm/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.onixplUsageTermInstanceList.size() == 0
        assert model.onixplUsageTermInstanceTotal == 0
    }

    void testCreate() {
        request.method = 'GET'
        def model = controller.create()

        assert model.onixplUsageTermInstance != null
    }


  void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTerm/list'

        populateValidParams(params)
        def onixplUsageTerm = new OnixplUsageTerm(params)

        assert onixplUsageTerm.save() != null

        params.id = onixplUsageTerm.id

        def model = controller.show()

        assert model.onixplUsageTermInstance == onixplUsageTerm
    }

    void testEdit() {
        request.method = 'GET'
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTerm/list'

        populateValidParams(params)
        def onixplUsageTerm = new OnixplUsageTerm(params)

        assert onixplUsageTerm.save() != null

        params.id = onixplUsageTerm.id

        def model = controller.edit()

        assert model.onixplUsageTermInstance == onixplUsageTerm
    }


  void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/onixplUsageTerm/list'

        response.reset()

        populateValidParams(params)
        def onixplUsageTerm = new OnixplUsageTerm(params)

        assert onixplUsageTerm.save() != null
        assert OnixplUsageTerm.count() == 1

        params.id = onixplUsageTerm.id

        controller.delete()

        assert OnixplUsageTerm.count() == 0
        assert OnixplUsageTerm.get(onixplUsageTerm.id) == null
        assert response.redirectedUrl == '/onixplUsageTerm/list'
    }
}

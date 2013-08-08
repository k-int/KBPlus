package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(OnixplUsageTermLicenseTextController)
@Mock(OnixplUsageTermLicenseText)
class OnixplUsageTermLicenseTextControllerTests {

  def populateValidParams(params) {
    assert params != null
    params['usageTerm'] = new OnixplUsageTerm()
    params['licenseText'] = new OnixplLicenseText()
  }

  void testIndex() {
    controller.index()
    assert "/onixplUsageTermLicenseText/list" == response.redirectedUrl
  }

  void testList() {

    def model = controller.list()

    assert model.onixplUsageTermLicenseTextInstanceList.size() == 0
    assert model.onixplUsageTermLicenseTextInstanceTotal == 0
  }

  void testCreate() {
    def model = controller.create()

    assert model.onixplUsageTermLicenseTextInstance != null
  }


  void testShow() {
    controller.show()

    assert flash.message != null
    assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'

    populateValidParams(params)
    def onixplUsageTermLicenseText = new OnixplUsageTermLicenseText(params)

    assert onixplUsageTermLicenseText.save() != null

    params.id = onixplUsageTermLicenseText.id

    def model = controller.show()

    assert model.onixplUsageTermLicenseTextInstance == onixplUsageTermLicenseText
  }

  void testEdit() {
    controller.edit()

    assert flash.message != null
    assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'

    populateValidParams(params)
    def onixplUsageTermLicenseText = new OnixplUsageTermLicenseText(params)

    assert onixplUsageTermLicenseText.save() != null

    params.id = onixplUsageTermLicenseText.id

    def model = controller.edit()

    assert model.onixplUsageTermLicenseTextInstance == onixplUsageTermLicenseText
  }

  void testDelete() {
    controller.delete()
    assert flash.message != null
    assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'

    response.reset()

    populateValidParams(params)
    def onixplUsageTermLicenseText = new OnixplUsageTermLicenseText(params)

    assert onixplUsageTermLicenseText.save() != null
    assert OnixplUsageTermLicenseText.count() == 1

    params.id = onixplUsageTermLicenseText.id

    controller.delete()

    assert OnixplUsageTermLicenseText.count() == 0
    assert OnixplUsageTermLicenseText.get(onixplUsageTermLicenseText.id) == null
    assert response.redirectedUrl == '/onixplUsageTermLicenseText/list'
  }

}

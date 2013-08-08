package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(OnixplLicenseTextController)
@Mock([OnixplLicenseText,OnixplLicense])
class OnixplLicenseTextControllerTests {

  def ELEMENT_ID = "Ex001", DISPLAY_NUM = "1.1", TEXT = "Some license text",
      OPL = new OnixplLicense()

  def populateValidParams(params) {
    assert params != null
    params['text'] = TEXT
    params['elementId'] = ELEMENT_ID
    params['displayNum'] = DISPLAY_NUM
    params['oplLicense'] = OPL
  }

  void testIndex() {
    controller.index()
    assert "/onixplLicenseText/list" == response.redirectedUrl
  }

  void testList() {

    def model = controller.list()

    assert model.onixplLicenseTextInstanceList.size() == 0
    assert model.onixplLicenseTextInstanceTotal == 0
  }

  void testCreate() {
    def model = controller.create()

    assert model.onixplLicenseTextInstance != null
  }


  void testShow() {
    controller.show()

    assert flash.message != null
    assert response.redirectedUrl == '/onixplLicenseText/list'

    populateValidParams(params)
    def onixplLicenseText = new OnixplLicenseText(params)

    assert onixplLicenseText.save() != null

    params.id = onixplLicenseText.id

    def model = controller.show()

    assert model.onixplLicenseTextInstance == onixplLicenseText
  }

  void testEdit() {
    controller.edit()

    assert flash.message != null
    assert response.redirectedUrl == '/onixplLicenseText/list'

    populateValidParams(params)
    def onixplLicenseText = new OnixplLicenseText(params)

    assert onixplLicenseText.save() != null

    params.id = onixplLicenseText.id

    def model = controller.edit()

    assert model.onixplLicenseTextInstance == onixplLicenseText
  }


  void testDelete() {
    controller.delete()
    assert flash.message != null
    assert response.redirectedUrl == '/onixplLicenseText/list'

    response.reset()

    populateValidParams(params)
    def onixplLicenseText = new OnixplLicenseText(params)

    assert onixplLicenseText.save() != null
    assert OnixplLicenseText.count() == 1

    params.id = onixplLicenseText.id

    controller.delete()

    assert OnixplLicenseText.count() == 0
    assert OnixplLicenseText.get(onixplLicenseText.id) == null
    assert response.redirectedUrl == '/onixplLicenseText/list'
  }
}

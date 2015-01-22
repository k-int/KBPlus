package com.k_int.kbplus



import org.junit.*
import grails.test.mixin.*

@TestFor(OnixplLicenseController)
@Mock([OnixplLicense,OnixplLicenseController,Doc])
class OnixplLicenseControllerTests {

  def populateValidParams(params) {
    assert params != null
    params['lastmod'] = new Date()
    params['doc'] = new Doc()
    params['title'] = "A Title"
  }

  void testIndex() {
    controller.index()
    assert "/onixplLicense/list" == response.redirectedUrl
  }

  void testList() {

    def model = controller.list()

    assert model.onixplLicenseInstanceList.size() == 0
    assert model.onixplLicenseInstanceTotal == 0
  }

  void testCreate() {
    populateValidParams(params)
    def model = controller.create()

    assert response.redirectedUrl == '/onixplLicense/show/1'
  }

  void testShow() {
    controller.show()

    assert flash.message != null
    assert response.redirectedUrl == '/onixplLicense/list'

    populateValidParams(params)
    def onixplLicense = new OnixplLicense(params)

    assert onixplLicense.save() != null

    params.id = onixplLicense.id

    def model = controller.show()

    assert model.onixplLicenseInstance == onixplLicense
  }

  void testEdit() {
    controller.edit()

    assert flash.message != null
    assert response.redirectedUrl == '/onixplLicense/list'
  }

  // void testEditSaved(){
  //   populateValidParams(params)
  //   def onixplLicense = new OnixplLicense(params)

  //   assert onixplLicense.save() != null
  //   params['id'] = onixplLicense.id

  //   def model = controller.edit()
    
  //   assert model.onixplLicenseInstance == onixplLicense
  // }

  void testDelete() {
    controller.delete()
    assert flash.message != null
    assert response.redirectedUrl == '/onixplLicense/list'

    response.reset()

    populateValidParams(params)
    def onixplLicense = new OnixplLicense(params)

    assert onixplLicense.save() != null
    assert OnixplLicense.count() == 1

    params.id = onixplLicense.id

    controller.delete()

    assert OnixplLicense.count() == 0
    assert OnixplLicense.get(onixplLicense.id) == null
    assert response.redirectedUrl == '/onixplLicense/list'
  }
}

package com.k_int.kbplus

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@Mock([OnixplLicense,OnixplLicenseText,Doc])
@TestFor(OnixplLicenseText)
class OnixplLicenseTextTests {

  def ELEMENT_ID = "Ex001", DISPLAY_NUM = "1.1", TEXT = "Some license text",
      OPL = new OnixplLicense(lastmod: new Date(), doc: new Doc(), title: "License title"),
      TERM = new OnixplUsageTerm()

  def oplt, incompleteOplt, blankDisplayNumOplt, blankTextOplt
  def oplts, validOplts

  void setUp() {
    // Create mocked OPL, invalid but works as a placeholder
    mockForConstraintsTests(OnixplLicense, [OPL])

    // Full license text
    oplt = new OnixplLicenseText(
        text: TEXT,
        elementId: ELEMENT_ID,
        displayNum: DISPLAY_NUM,
        oplLicense: OPL,
        term: TERM
    )
    incompleteOplt = new OnixplLicenseText()
    // Blank display num is ok
    blankDisplayNumOplt = new OnixplLicenseText(
        text: TEXT,
        elementId: ELEMENT_ID,
        displayNum: "",
        oplLicense: OPL,
        term: TERM
    )
    // Allowing blank values seems to be broken as any blank values are converted to null
    // Blank text is ok
//    blankTextOplt = new OnixplLicenseText(
//        text: "",
//        elementId: ELEMENT_ID,
//        displayNum: DISPLAY_NUM,
//        oplLicense: OPL
//    )

//    oplts = [oplt, incompleteOplt, blankDisplayNumOplt, blankTextOplt]
    oplts = [oplt, incompleteOplt, blankDisplayNumOplt]
    validOplts = [oplt, blankDisplayNumOplt]
    // Mock the OPLT instances with constraint methods
    mockForConstraintsTests(OnixplLicenseText, oplts)
    //mockDomain(OnixplLicenseText, oplts)
    oplts.each { it.save() }
  }

  void tearDown() {
    // Tear down logic here
  }

  void testDomain() {
    // Only 3 should be successfully saved
    assert validOplts.size() == OnixplLicenseText.all.size()
    assert 2 == OnixplLicenseText.findAllByText(TEXT).size()
    assert 2 == OnixplLicenseText.findAllByElementId(ELEMENT_ID).size()
    // Text can be blank but not null
//    assertEquals 1, OnixplLicenseText.findAllByText("").size()
    assert 0 == OnixplLicenseText.findAllByTextIsNull().size()
    // 2 displaynum, 1 blank displaynum
    assert 1 == OnixplLicenseText.findAllByDisplayNum(DISPLAY_NUM).size()
    // Blank display num apparently recorded as null when nullable
    assert 1 == OnixplLicenseText.findAllByDisplayNum(null).size()
    assert 0 == OnixplLicenseText.findAllByDisplayNum("").size()

    assert 0 == OnixplLicenseText.findAllByOplLicense(null).size()
    assert validOplts.size() == OnixplLicenseText.findAllByOplLicense(OPL).size()
  }



  void testConstraints() {
    // Empty license text does not validate because of no text, elementId or opl
    assertFalse incompleteOplt.validate()
    assert 4 == incompleteOplt.errors.errorCount

    assert null == incompleteOplt.text
    assert null == incompleteOplt.elementId

    assert "nullable" == incompleteOplt.errors["text"]
    assert "nullable" == incompleteOplt.errors["elementId"]

    validOplts.each {
      assertTrue it.validate()
      assertNotNull it.text
      assertNotNull it.elementId
      assertTrue it.elementId.size() <= 20
      assertTrue it.elementId.size() >= 0
      if (it.usageTermLicenseText) assertTrue it.usageTermLicenseText.size() >= 1
    }

  }


  void testMultiplicity() {
    OnixplLicenseText.all.each{
      if (it.usageTermLicenseText) assertTrue it.usageTermLicenseText.size() >= 1
    }
  }

  void testToString() {
    assertTrue OnixplLicenseText.all.size() > 0
    OnixplLicenseText.all.each{
      assertNotSame "", it.toString()
    }
  }

}

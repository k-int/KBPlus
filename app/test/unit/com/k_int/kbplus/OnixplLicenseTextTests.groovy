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
      OPL = new OnixplLicense()

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
        oplLicense: OPL
    )
    incompleteOplt = new OnixplLicenseText()
    // Blank display num is ok
    blankDisplayNumOplt = new OnixplLicenseText(
        text: TEXT,
        elementId: ELEMENT_ID,
        displayNum: "",
        oplLicense: OPL
    )
    // Blank text is ok
    blankTextOplt = new OnixplLicenseText(
        text: "",
        elementId: ELEMENT_ID,
        displayNum: DISPLAY_NUM,
        oplLicense: OPL
    )

    oplts = [oplt, incompleteOplt, blankDisplayNumOplt, blankTextOplt]
    validOplts = [oplt, blankDisplayNumOplt, blankTextOplt]
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
    assertEquals validOplts.size(), OnixplLicenseText.all.size()
    assertEquals 2, OnixplLicenseText.findAllByText(TEXT).size()
    assertEquals 3, OnixplLicenseText.findAllByElementId(ELEMENT_ID).size()
    // Text can be blank but not null
    assertEquals 1, OnixplLicenseText.findAllByText("").size()
    assertEquals 0, OnixplLicenseText.findAllByTextIsNull().size()
    // 2 displaynum, 1 blank displaynum
    assertEquals 2, OnixplLicenseText.findAllByDisplayNum(DISPLAY_NUM).size()
    // Blank display num apparently recorded as null when nullable
    assertEquals 1, OnixplLicenseText.findAllByDisplayNum(null).size()
    assertEquals 0, OnixplLicenseText.findAllByDisplayNum("").size()

    assertEquals 0, OnixplLicenseText.findAllByOplLicense(null).size()
    assertEquals validOplts.size(), OnixplLicenseText.findAllByOplLicense(OPL).size()
  }



  void testConstraints() {
    // Empty license text does not validate because of no text, elementId or opl
    assertFalse incompleteOplt.validate()
    assertEquals 3, incompleteOplt.errors.errorCount

    assertEquals null, incompleteOplt.text
    assertEquals null, incompleteOplt.elementId

    assertEquals "nullable", incompleteOplt.errors["text"]
    assertEquals "nullable", incompleteOplt.errors["elementId"]

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

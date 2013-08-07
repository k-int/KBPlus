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

  def ELEMENT_ID = "Ex001", DISPLAY_NUM = "1.1", TEXT = "Some license text"

  def oplt, incompleteOplt, blankDisplayNumOplt, blankTextOplt
  def oplts, validOplts

  void setUp() {
    oplt = new OnixplLicenseText(
        text: TEXT,
        elementId: ELEMENT_ID,
        displayNum: DISPLAY_NUM,
        oplLicense: OnixplLicense
    )
    incompleteOplt = new OnixplLicenseText()
    blankDisplayNumOplt = new OnixplLicenseText(
        text: TEXT,
        elementId: ELEMENT_ID,
        displayNum: "",
        oplLicense: OnixplLicense
    )
    blankTextOplt = new OnixplLicenseText(
        text: "",
        elementId: ELEMENT_ID,
        displayNum: DISPLAY_NUM,
        oplLicense: OnixplLicense
    )
    // Create the association object:
    /*def ass = new OnixplUsageTermLicenseText(
        usageTerm: term,
        licenseText: oplt
    )*/
    oplts = [oplt, incompleteOplt, blankDisplayNumOplt, blankTextOplt]
    validOplts = [oplt, blankDisplayNumOplt, blankTextOplt]
    // Mock the OPLT instances with constraint methods
    mockForConstraintsTests(OnixplLicenseText, oplts)
    oplts.each { it.save() }
  }

  void tearDown() {
    // Tear down logic here
  }

  void testDomain() {
    // Only 3 should be successfully saved
    assertEquals 3, OnixplLicenseText.all.size()
    assertEquals 2, OnixplLicenseText.findAllByText(TEXT).size()
    assertEquals 3, OnixplLicenseText.findAllByElementId(ELEMENT_ID).size()
    // Text can be blank but not null
    assertEquals 1, OnixplLicenseText.findAllByText("").size()
    assertEquals 0, OnixplLicenseText.findAllByText(null).size()
    // 2 displaynum, 1 blank displaynum
    assertEquals 2, OnixplLicenseText.findAllByDisplayNum(DISPLAY_NUM).size()
    // Blank display num apparently recorded as null when nullable
    assertEquals 1, OnixplLicenseText.findAllByDisplayNum(null).size()
    assertEquals 0, OnixplLicenseText.findAllByDisplayNum("").size()
  }



  void testConstraints() {
    // Empty license text does not validate because of no text, elementId or opl
    assertFalse incompleteOplt.validate()
    assertEquals 3, incompleteOplt.errors.errorCount

    assertEquals null, incompleteOplt.text
    assertEquals null, incompleteOplt.elementId

    assertEquals "nullable", incompleteOplt.errors["text"]
    assertEquals "nullable", incompleteOplt.errors["elementId"]

    // Blank display num license text is ok
    assertTrue blankDisplayNumOplt.validate()

    // Blank text license text is ok
    assertTrue blankTextOplt.validate()
    //assertEquals 1, blankTextOplt.errors.errorCount
    //assertEquals "blank", blankTextOplt.errors["text"]

    // Full license text should be ok
    assertTrue oplt.validate()
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

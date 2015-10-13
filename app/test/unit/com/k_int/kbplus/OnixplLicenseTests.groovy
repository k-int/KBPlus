package com.k_int.kbplus

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@Mock([Doc,OnixplLicense])
@TestFor(OnixplLicense)
class OnixplLicenseTests {

  def TITLE = "A license"
  def incompleteOpl, blankTitleOpl, nullLastModOpl, fullOpl
  def opls

  void setUp() {
    incompleteOpl = new OnixplLicense()
    def doc = new Doc();
    blankTitleOpl = new OnixplLicense(
        lastmod: null,
        doc:     doc,
        title:   ""
    )
    nullLastModOpl = new OnixplLicense(
        lastmod: null,
        doc:     doc,
        title:   TITLE
    )
    fullOpl = new OnixplLicense(
        lastmod: new Date(),
        doc:     doc,
        title:   TITLE
    )
    opls = [incompleteOpl, blankTitleOpl, nullLastModOpl, fullOpl]
    // Mock the OPL instances with constraint methods
    mockDomain(OnixplLicense, opls)
  }

  void tearDown() {
    // Tear down logic here
  }

  void testDomain() {
    // Only 2 should be successfully saved
    assertEquals 2, OnixplLicense.all.size()
    assertEquals 2, OnixplLicense.findAllByTitle(TITLE).size()
    assertEquals 0, OnixplLicense.findAllByTitle("").size()
    assertEquals 0, OnixplLicense.findAllByTitle(null).size()
  }


  void testConstraints() {
    // Empty license does not validate because of no title or doc
    assertFalse incompleteOpl.validate()
    assertEquals 2, incompleteOpl.errors.errorCount

    assertEquals null, incompleteOpl.title
    assertEquals null, incompleteOpl.doc

    // Blank title OPL does not validate
    assertFalse blankTitleOpl.validate()
    assertEquals 1, blankTitleOpl.errors.errorCount

    // OPL with null date is ok
    assertTrue nullLastModOpl.validate()
    assertTrue nullLastModOpl.doc.validate()

    // Full OPL should be ok
    assertTrue fullOpl.validate()
    assertTrue fullOpl.doc.validate()
  }

  void testMultiplicity() {
    OnixplLicense.all.each{
      if (it.licenses)    assertTrue it.licenses.size() >= 1
    }
  }

  void testToString() {
    assert OnixplLicense.all.size() > 0
    OnixplLicense.all.each{
      assert it.toString() != "";
    }
  }

}

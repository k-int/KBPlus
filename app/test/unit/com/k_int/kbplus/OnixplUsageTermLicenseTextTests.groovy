package com.k_int.kbplus



import grails.test.mixin.*
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(OnixplUsageTermLicenseText)
@TestMixin(GrailsUnitTestMixin)
@Mock([OnixplLicenseText,OnixplUsageTerm,OnixplUsageTermLicenseText])
class OnixplUsageTermLicenseTextTests {

  def OPUT = new OnixplUsageTerm(),
      OPLT = new OnixplLicenseText()

  def oputlt, nullOputlt
  def oputlts, validOputlts

  void setUp() {
    // Create mocked objects
    mockForConstraintsTests(OnixplUsageTerm, [OPUT])
    mockForConstraintsTests(OnixplLicenseText, [OPLT])

    oputlt = new OnixplUsageTermLicenseText(
        usageTerm:   OPUT,
        licenseText: OPLT
    )
    nullOputlt = new OnixplUsageTermLicenseText(
        usageTerm: null,
    )

    oputlts = [oputlt,nullOputlt]
    validOputlts = [oputlt]
    mockForConstraintsTests(OnixplUsageTermLicenseText, oputlts)
    oputlts.each { it.save() }
  }

  void tearDown() {
    // Tear down logic here
  }


  void testDomain() {
    // Only valid instances should be successfully saved
    assertEquals validOputlts.size(), OnixplUsageTermLicenseText.all.size()
    assertEquals 0, OnixplUsageTermLicenseText.findAllByUsageTermIsNull().size()
    assertEquals 0, OnixplUsageTermLicenseText.findAllByLicenseTextIsNull().size()
  }

  void testConstraints() {
    assertTrue oputlt.validate()

    // Won't validate if null or blank values
    assertFalse nullOputlt.validate()
    assertEquals 1, nullOputlt.errors.errorCount
    assertEquals "nullable", nullOputlt.errors["usageTerm"]

    assertEquals null, nullOputlt.usageTerm
    assertEquals null, nullOputlt.licenseText
  }

}

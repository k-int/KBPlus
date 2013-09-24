package com.k_int.kbplus

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@Mock([OnixplUsageTerm, OnixplLicense, OnixplUsageTermLicenseText, RefdataValue, Doc])
@TestFor(OnixplUsageTerm)
class OnixplUsageTermTests {

    def OPL = new OnixplLicense(),
        RDV = new RefdataValue(),
        OLT = new OnixplLicenseText(text: "Test text"),
        OLT2 = new OnixplLicenseText(text: "Test text2"),
        TLT = new OnixplUsageTermLicenseText(licenseText: OLT),
        TLT2 = new OnixplUsageTermLicenseText(licenseText: OLT2)

    def oput, oput2, incompleteOput, nullOput
    def oputs, validOputs

    void setUp() {
        // Create mocked objects
        mockForConstraintsTests(OnixplLicense, [OPL])
        mockForConstraintsTests(RefdataValue, [RDV])
        mockForConstraintsTests(OnixplUsageTermLicenseText, [TLT, TLT2])
        mockForConstraintsTests(OnixplLicenseText, [OLT, OLT2])

        incompleteOput = new OnixplUsageTerm()
        oput = new OnixplUsageTerm(
                oplLicense: OPL,
                usageType: RDV,
                usageStatus: RDV,
                usageTermLicenseText: [TLT],
                user: [RDV],
                usedResource: [RDV],
        )
        oput2 = new OnixplUsageTerm(
                oplLicense: OPL,
                usageType: RDV,
                usageStatus: RDV,
                usageTermLicenseText: [TLT2],
                user: [RDV],
                usedResource: [RDV],
        )
        nullOput = new OnixplUsageTerm(
                oplLicense: null,
                usageType: null,
                usageStatus: null
        )

        oputs = [oput, oput2, incompleteOput, nullOput]
        validOputs = [oput, oput2]
        // Mock the OPLT instances with constraint methods
        mockForConstraintsTests(OnixplUsageTerm, oputs)
        //mockDomain(OnixplUsageTerm, oputs)
        oputs.each { it.save() }
    }

    void tearDown() {
        // Tear down logic here
    }


    void testDomain() {
        // Only valid instances should be successfully saved
        assertEquals validOputs.size(), OnixplUsageTerm.all.size()
        assertEquals 2, OnixplUsageTerm.findAllByOplLicense(OPL).size()
        assertEquals 0, OnixplUsageTerm.findAllByOplLicenseIsNull().size()
        assertEquals 0, OnixplUsageTerm.findAllByUsageTypeIsNull().size()
        assertEquals 0, OnixplUsageTerm.findAllByUsageStatusIsNull().size()
        assertEquals 0, OnixplUsageTerm.findAllByUsageTermLicenseTextIsNull().size()
    }

    void testConstraints() {
        // Empty license text does not validate because of no text, elementId or opl
        [incompleteOput, nullOput].each { anOput ->
            assertFalse anOput.validate()
            assertEquals 2, anOput.errors.errorCount
            assertEquals null, anOput.oplLicense
            assertEquals null, anOput.usageType
            assertEquals null, anOput.usageStatus
            ["oplLicense", "usageType", "usageStatus"].each { prop ->
                assertEquals "nullable", anOput.errors[prop]
            }
        }

        validOputs.each {
            assertTrue it.validate()
            assertNotNull it.oplLicense
            assertNotNull it.usageType
            assertNotNull it.usageStatus
            if (it.usageTermLicenseText) assertTrue it.usageTermLicenseText.size() >= 1
        }

    }

    void testCompare() {
        assert oput.compare(oput);
    }

    void testCompareFalse() {
        println("Term1: " + oput);
        println("Term2: " + oput2);
        assert !oput.compare(oput2);
    }

}

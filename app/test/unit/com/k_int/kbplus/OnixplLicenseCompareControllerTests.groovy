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
class OnixplLicenseCompareControllerTests {

    void setUp() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void testCheckParam() {
        assert OnixplLicenseCompareController.checkParam("5", 10) == 5;
    }

    void testCheckParamText() {
        assert OnixplLicenseCompareController.checkParam("abcd", 10) == 10;
    }

    void testFilterLicenses() {
        OnixplUsageTerm ut1 = new OnixplUsageTerm(usageType: RefdataValue.findById(135), usageStatus: RefdataValue.findById(141));
        OnixplLicense opl1 = new OnixplLicense(title: "Test1", usageTerm: [ut1]);
        ArrayList<OnixplLicense> filteredList = OnixplLicenseCompareController.filterLicenses(buildList(opl1), opl1, "", "");
        assert filteredList.size() == 4;
    }

    void testFilterLicensesMatch() {
        OnixplUsageTerm ut1 = new OnixplUsageTerm(usageType: RefdataValue.findById(135), usageStatus: RefdataValue.findById(141));
        OnixplLicense opl1 = new OnixplLicense(title: "Test1", usageTerm: [ut1]);
        ArrayList<OnixplLicense> filteredList = OnixplLicenseCompareController.filterLicenses(buildList(opl1), opl1, "true", "");
        assert filteredList.size() == 4;
    }

    void testFilterLicensesNoMatch() {
        OnixplUsageTerm ut1 = new OnixplUsageTerm(usageType: RefdataValue.findById(135), usageStatus: RefdataValue.findById(141));
        OnixplLicense opl1 = new OnixplLicense(title: "Test1", usageTerm: [ut1]);
        ArrayList<OnixplLicense> filteredList = OnixplLicenseCompareController.filterLicenses(buildList(opl1), opl1, "false", "");
        assert filteredList.size() == 0;
    }

    private ArrayList<OnixplLicense> buildList(OnixplLicense opl) {
        OnixplUsageTerm ut1 = new OnixplUsageTerm(usageType: RefdataValue.get(135), usageStatus: RefdataValue.get(141));
        OnixplUsageTerm ut2 = new OnixplUsageTerm(usageType: RefdataValue.get(136), usageStatus: RefdataValue.get(141));
        mockForConstraintsTests(OnixplUsageTerm, [ut1, ut2]);
        OnixplLicense opl1 = new OnixplLicense(title: "Test1", usageTerm: [ut1]);
        OnixplLicense opl2 = new OnixplLicense(title: "Test2", usageTerm: [ut1]);
        OnixplLicense opl3 = new OnixplLicense(title: "Test3", usageTerm: [ut2]);
        OnixplLicense opl4 = new OnixplLicense(title: "Test1", usageTerm: [ut2]);
        mockForConstraintsTests(OnixplLicense, [opl1, opl2, opl3, opl4]);
        return new ArrayList<OnixplLicense>(3) {{
            add(opl);
            add(opl1);
            add(opl2);
            add(opl3);
            add(opl4);
        }};
    }
}

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
        OnixplUsageTerm ut1 = new OnixplUsageTerm(usageType: RefdataValue.findByValue("Include"), usageStatus: RefdataValue.findByValue("Permitted"));
        OnixplLicense opl1 = new OnixplLicense(title: "Test1", usageTerm: [ut1]);
        ArrayList<OnixplLicense> filteredList = OnixplLicenseCompareController.filterLicenses(buildList(opl1), opl1, "", "");
        assert filteredList.size() == 4;
    }

    void testFilterLicensesMatch() {
        RefdataValue type = new RefdataValue(value: "Include");
        RefdataValue status = new RefdataValue(value: "Permitted");
        OnixplUsageTerm ut1 = new OnixplUsageTerm(usageType: type, usageStatus: status);
        OnixplLicenseText lt = new OnixplLicenseText(text: "Test text");
        OnixplLicense opl1 = new OnixplLicense(title: "Test1", usageTerm: [ut1], licenseText: [lt]);
        ArrayList<OnixplLicense> filteredList = OnixplLicenseCompareController.filterLicenses(buildList(opl1), opl1, "true", "");
        assert filteredList.size() == 1;
    }

    void testFilterLicensesNoMatch() {
        RefdataValue type = new RefdataValue(value: "Include");
        RefdataValue status = new RefdataValue(value: "Permitted");
        OnixplUsageTerm ut1 = new OnixplUsageTerm(usageType: type, usageStatus: status);
        OnixplLicenseText lt = new OnixplLicenseText(text: "Test text");
        OnixplLicense opl1 = new OnixplLicense(title: "Test1", usageTerm: [ut1], licenseText: [lt]);
        ArrayList<OnixplLicense> filteredList = OnixplLicenseCompareController.filterLicenses(buildList(opl1), opl1, "false", "");
        assert filteredList.size() == 3;
    }

    private ArrayList<OnixplLicense> buildList(OnixplLicense opl) {
        RefdataValue type = new RefdataValue(value: "Include");
        RefdataValue type2 = new RefdataValue(value: "Access");
        RefdataValue status = new RefdataValue(value: "Permitted");
        OnixplUsageTerm ut1 = new OnixplUsageTerm(usageType: type, usageStatus: status);
        OnixplUsageTerm ut2 = new OnixplUsageTerm(usageType: type2, usageStatus: status);
        mockForConstraintsTests(OnixplUsageTerm, [ut1, ut2]);
        OnixplLicenseText lt1 = new OnixplLicenseText(text: "Test text");
        OnixplLicenseText lt2 = new OnixplLicenseText(text: "Test text2");
        mockForConstraintsTests(OnixplLicenseText, [lt1, lt2]);
        OnixplLicense opl1 = new OnixplLicense(title: "Test1", usageTerm: [ut1], licenseText: [lt1]);
        OnixplLicense opl2 = new OnixplLicense(title: "Test2", usageTerm: [ut1], licenseText: [lt2]);
        OnixplLicense opl3 = new OnixplLicense(title: "Test3", usageTerm: [ut2], licenseText: [lt1]);
        OnixplLicense opl4 = new OnixplLicense(title: "Test1", usageTerm: [ut2], licenseText: [lt2]);
        mockForConstraintsTests(OnixplLicense, [opl1, opl2, opl3, opl4]);
        return new ArrayList<OnixplLicense>(5) {{
            add(opl);
            add(opl1);
            add(opl2);
            add(opl3);
            add(opl4);
        }};
    }
}

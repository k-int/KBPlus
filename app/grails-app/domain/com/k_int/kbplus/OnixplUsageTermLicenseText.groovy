package com.k_int.kbplus

class OnixplUsageTermLicenseText {

    OnixplUsageTerm usageTerm
    OnixplLicenseText licenseText

    static mapping = {
        id column: 'opul_id'
        version column: 'opul_version'
        usageTerm column: 'opul_oput_fk'
        licenseText column: 'opul_oplt_fk'
    }

    static constraints = {
        usageTerm(nullable:false, blank: false)
        licenseText(nullable:false, blank: false)
    }
}

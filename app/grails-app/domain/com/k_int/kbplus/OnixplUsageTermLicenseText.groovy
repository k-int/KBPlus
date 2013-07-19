package com.k_int.kbplus

/**
 * An association class for the many-to-many relationship between OnixplUsageTerm
 * and OnixPlLicenseText.
 */
class OnixplUsageTermLicenseText {

  static hasOne= [
      usageTerm:OnixplUsageTerm,
      usageTermLicenseText:OnixplLicenseText
  ]

  static belongsTo = [
      usageTerm:OnixplUsageTerm
  ]

  static mapping = {
    //id column:        'opul_id'
    //version column:   'opul_version'
    usageTerm column:   'opul_oput_fk', index:'opul_entry_idx'
    usageTermLicenseText column: 'opul_oplt_fk', index:'opul_entry_idx'
  }

    static constraints = {
      usageTerm(nullable:false, blank: false)
      usageTermLicenseText(nullable:false, blank: false)
    }

}

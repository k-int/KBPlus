package com.k_int.kbplus

/**
 * An association class for the many-to-many relationship between OnixplUsageTerm
 * and OnixPlLicenseText.
 */
class OnixplUsageTermLicenseText {

  static hasOne= [
      usageTerm:OnixplUsageTerm,
      licenseText:OnixplLicenseText
  ]

  static belongsTo = [
      usageTerm:OnixplUsageTerm
  ]

  static mapping = {
    //id column:        'opul_id'
    //version column:   'opul_version'
    usageTerm column:   'opul_oput_fk', index:'opul_entry_idx'
    licenseText column: 'opul_oplt_fk', index:'opul_entry_idx'
  }

}

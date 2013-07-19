package com.k_int.kbplus

/**
 * An OnixplUsageTerm belongs to an OnixplLicense and can have many OnixplLicenseTexts.
 */
class OnixplUsageTerm {

  RefdataValue usageType
  RefdataValue usageStatus

  //static hasMany = [ licenseText:OnixplLicenseText ]
  static hasMany = [ licenseText:OnixplUsageTermLicenseText ]

  static belongsTo = [
      oplLicense:OnixplLicense
  ]
  //static hasOne = [onixplLicense:OnixplLicense]

  static mapping = {
    id column:          'oput_id'
    version column:     'oput_version'
    oplLicense column:  'oput_opl_fk', index:'oput_entry_idx'
    usageType column:   'oput_usage_type_rv_fk', index:'oput_entry_idx'
    usageStatus column: 'oput_usage_status_rv_fk', index:'oput_entry_idx'
  }

  /*static constraints = {
    licenseText unique: true
  }*/

}

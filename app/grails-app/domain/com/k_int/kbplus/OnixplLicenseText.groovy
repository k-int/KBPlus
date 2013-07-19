package com.k_int.kbplus

/**
 * An OnixplLicenseText belongs to a OnixPlLicense, and one or more
 * OnixplUsageTerms in a many-to-many relation via OnixplUsageTermLicenseText.
 */
class OnixplLicenseText {

  String elementId;
  String displayNum;
  String text;

  static belongsTo = OnixplUsageTerm;
  OnixplLicense oplLicense
  /*static belongsTo = [
      OnixplUsageTerm,
      oplLicense:OnixplLicense
  ]*/

  static hasMany = [ licenseText:OnixplUsageTermLicenseText ]

  static mappedBy = [ usageTerm: 'licenseText' ]

  static mapping = {
    id column:         'oplt_id'
    version column:    'oplt_version'
    oplLicense column: 'oplt_opl_fk'
    elementId column:  'oplt_el_id',      index:'oplt_el_id_idx', maxSize:20
    displayNum column: 'oplt_display_num'
    text column:       'oplt_text',       type:'text'
  }

  static constraints = {
    displayNum(nullable:true)
    text(nullable:false)
    elementId(nullable:false)
  }

}

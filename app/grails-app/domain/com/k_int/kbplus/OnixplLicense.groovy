package com.k_int.kbplus

/**
 * An OnixplLicense has many OnixplUsageTerms and OnixplLicenseTexts.
 * It can be associated with 0..1 license.
 * The OnixplLicenseTexts relation is redundant as UsageTerms refer to the
 * LicenseTexts, but is a convenient way to access the whole license text.
 */
class OnixplLicense {

  Date lastmod;

  // An ONIX-PL license relates to a KB+ license and a doc
  License license;
  Doc doc;

  // One to many
  static hasMany = [
      usageTerm:   OnixplUsageTerm,
      licenseText: OnixplLicenseText
  ]

  // Reference to license in the many
  static mappedBy = [
      usageTerm:   'oplLicense',
      licenseText: 'oplLicense'
  ]

  static mapping = {
    id column:      'opl_id'
    version column: 'opl_version'
    license column: 'opl_lic_fk'
    doc column:     'opl_doc_fk'
    lastmod column: 'opl_lastmod'
  }


  static constraints = {
    license(nullable:true)
  }


}

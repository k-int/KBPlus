package com.k_int.kbplus

/**
 * An OnixplLicense has many OnixplUsageTerms.
 */
class OnixplLicense {

  Date lastmod;

  // An ONIX-PL license relates to a KB+ license and a doc
  License license;
  Doc doc;

  /*static belongsTo = [
      license:License,
      doc:Doc
  ]*/

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

  }


}

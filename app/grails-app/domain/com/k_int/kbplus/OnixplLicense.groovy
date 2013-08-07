package com.k_int.kbplus

import com.k_int.kbplus.auth.Role

/**
 * An OnixplLicense has many OnixplUsageTerms and OnixplLicenseTexts.
 * It can be associated with many licenses.
 * The OnixplLicenseTexts relation is redundant as UsageTerms refer to the
 * LicenseTexts, but is a convenient way to access the whole license text.
 */
class OnixplLicense {

  Date lastmod;
  String title;

  // An ONIX-PL license relates to a a doc
  Doc doc;

  // One to many
  static hasMany = [
    usageTerm:   OnixplUsageTerm,
    licenseText: OnixplLicenseText,
    licenses:    License
  ]

  // Reference to license in the many
  static mappedBy = [
      usageTerm:   'oplLicense',
      licenseText: 'oplLicense',
      licenses:    'onixplLicense',
  ]

  static mapping = {
    id column:      'opl_id'
    version column: 'opl_version'
    doc column:     'opl_doc_fk'
    lastmod column: 'opl_lastmod'
    title column:   'opl_title'
    usageTerm cascade: 'all-delete-orphan'

  }

  static constraints = {
    doc(nullable: false, blank: false)
    lastmod(nullable: true, blank: true)
    title(nullable: false, blank: false)
  }

  // Only admin has permission to change ONIX-PL licenses;
  // anyone can view them.
  def hasPerm(perm, user) {
    if (perm == 'view') return true;
    // If user is a member of admin role, they can do anything.
    def admin_role = Role.findByAuthority('ROLE_ADMIN');
    if (admin_role) return user.getAuthorities().contains(admin_role);
    false;
  }

  @Override
  public String toString() {
    return "Id: " + id + " | Version: " + version + " | Title: " + title
    + " | Licenses: " + licenses.size() + " | Document: " + doc.toString();
  }

}

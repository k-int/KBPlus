package com.k_int.kbplus

import com.k_int.kbplus.auth.Role

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
      license(nullable: true, blank: false)
      doc(nullable: false, blank: false)
      lastmod(nullable: true, blank: true)
  }

    def hasPerm(perm, user) {
        def result = false

        if (perm == 'view' && license.isPublic?.value == 'Yes') {
            result = true;
        }

        if (!result) {
            // If user is a member of admin role, they can do anything.
            def admin_role = Role.findByAuthority('ROLE_ADMIN');
            if (admin_role) {
                if (user.getAuthorities().contains(admin_role)) {
                    result = true;
                }
            }
        }

        if (!result) {
            result = checkPermissions(perm, user);
        }

        result;
    }

    def checkPermissions(perm, user) {
        def result = false
        def principles = user.listPrincipalsGrantingPermission(perm);   // This will list all the orgs and people granted the given perm
        log.debug("The target list if principles : ${principles}");

        // Now we need to see if we can find a path from this object to any of those resources... Any of these orgs can edit

        // If this is a concrete license, the owner is the
        // If it's a template, the owner is the consortia that negotiated
        // def owning org list
        // We're looking for all org links that grant a role with the corresponding edit property.
        Set object_orgs = new HashSet();
        license.orgLinks.each { ol ->
            def perm_exists = false
            if (!ol.roleType)
                log.warn("Org link with no role type! Org Link ID is ${ol.id}");

            ol.roleType?.sharedPermissions.each { sp ->
                if (sp.perm.code == perm)
                    perm_exists = true;
            }
            if (perm_exists) {
                log.debug("Looks like org ${ol.org} has perm ${perm} shared with it.. so add to list")
                object_orgs.add("${ol.org.id}:${perm}")
            }
        }

        log.debug("After analysis, the following relevant org_permissions were located ${object_orgs}, user has the following orgs for that perm ${principles}")

        // Now find the intersection
        def intersection = principles.retainAll(object_orgs)

        log.debug("intersection is ${principles}")

        if (principles.size() > 0)
            result = true

        result
    }

    def getNote(domain) {
        def note = DocContext.findByLicenseAndDomain(license, domain);
        note
    }

    @Override
    public String toString() {
        return "Id: " + id + " | Version: " + version + " | License: " + license.toString() + " | Document: " + doc.toString();
    }
}

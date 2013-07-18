package com.k_int.kbplus

import com.k_int.kbplus.auth.Role

/**
 * An OnixplUsageTerm belongs to an OnixplLicense and can have many OnixplLicenseTexts.
 */
class OnixplUsageTerm {

    OnixplLicense oplLicense
  RefdataValue usageType
  RefdataValue usageStatus

    static hasMany = [
            usageTermLicenseText: OnixplUsageTermLicenseText
    ]

    static mappedBy = [
            usageTermLicenseText: 'usageTerm'
    ]
  static mapping = {
    id column:          'oput_id'
    version column:     'oput_version'
    oplLicense column:  'oput_opl_fk', index:'oput_entry_idx'
    usageType column:   'oput_usage_type_rv_fk', index:'oput_entry_idx'
    usageStatus column: 'oput_usage_status_rv_fk', index:'oput_entry_idx'
  }

    static constraints = {
        oplLicense(nullable:false, blank: false)
        usageType(nullable:false, blank: false)
        usageStatus(nullable:false, blank: false)
    }

  /*static constraints = {
    licenseText unique: true
  }*/

    def hasPerm(perm, user) {
        def result = false

        if (perm == 'view' && oplLicense.license.isPublic?.value == 'Yes') {
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
        oplLicense.license.orgLinks.each { ol ->
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

}

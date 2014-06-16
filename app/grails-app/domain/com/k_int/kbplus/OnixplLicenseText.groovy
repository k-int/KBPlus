package com.k_int.kbplus

/**
 * An OnixplLicenseText belongs to a OnixPlLicense, and one or more
 * OnixplUsageTerms in a many-to-many relation via OnixplUsageTermLicenseText.
 */
import com.k_int.kbplus.auth.Role

class OnixplLicenseText {

  String elementId;
  String displayNum;
  String text;

  static belongsTo = [ term:OnixplUsageTerm,
                       oplLicense:OnixplLicense ]

  /*static belongsTo = [
      OnixplUsageTerm,
      oplLicense:OnixplLicense
  ]*/

  static hasMany = [ usageTermLicenseText:OnixplUsageTermLicenseText ]

  static mappedBy = [ usageTerm: 'licenseText' ]

  static mapping = {
    id column:         'oplt_id'
    version column:    'oplt_version'
    oplLicense column: 'oplt_opl_fk'
    elementId column:  'oplt_el_id',      index:'oplt_el_id_idx'
    displayNum column: 'oplt_display_num'
    text column:       'oplt_text',       type:'text'
  }

  static constraints = {
    oplLicense(nullable:true,blank:true)
    displayNum(nullable:true,blank:true)
    text(nullable:false,blank:true)
    elementId(nullable:false,maxSize:50)
    oplLicense(nullable:false)
  }

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

  @Override
  public java.lang.String toString() {
    return "OnixplLicenseText{" +
        "id=" + id +
        ", elementId='" + elementId + '\'' +
        ", displayNum='" + displayNum + '\'' +
        ", text='" + text + '\'' +
        ", oplLicense=" + oplLicense +
        '}';
  }
}

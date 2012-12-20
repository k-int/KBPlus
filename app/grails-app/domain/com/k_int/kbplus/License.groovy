package com.k_int.kbplus

import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.k_int.kbplus.auth.Role

class License {

  static auditable = true

  RefdataValue status
  RefdataValue type

  String reference

  RefdataValue concurrentUsers
  RefdataValue remoteAccess
  RefdataValue walkinAccess
  RefdataValue multisiteAccess
  RefdataValue partnersAccess
  RefdataValue alumniAccess
  RefdataValue ill
  RefdataValue coursepack
  RefdataValue vle
  RefdataValue enterprise
  RefdataValue pca
  RefdataValue isPublic

  Long concurrentUserCount=0
  String noticePeriod
  String licenseUrl
  String licensorRef
  String licenseeRef
  String licenseType
  String licenseStatus
  long lastmod

  static hasMany = [
    subscriptions:Subscription, 
    documents:DocContext,
    orgLinks:OrgRole,
    outgoinglinks:Link,
    incomingLinks:Link,
    pendingChanges:PendingChange
  ]

  static mappedBy = [ subscriptions: 'owner',
                      documents: 'license',
                      orgLinks:'lic',
                      outgoinglinks:'fromLic',
                      incomingLinks:'toLic',
                      pendingChanges:'license']

  static mapping = {
                     id column:'lic_id'
                version column:'lic_version'
                 status column:'lic_status_rv_fk'
                   type column:'lic_type_rv_fk'
              reference column:'lic_ref'
        concurrentUsers column:'lic_concurrent_users_rdv_fk'
           remoteAccess column:'lic_remote_access_rdv_fk'
           walkinAccess column:'lic_walkin_access_rdv_fk'
        multisiteAccess column:'lic_multisite_access_rdv_fk'
         partnersAccess column:'lic_partners_access_rdv_fk'
           alumniAccess column:'lic_alumni_access_rdv_fk'
                    ill column:'lic_ill_rdv_fk'
             coursepack column:'lic_coursepack_rdv_fk'
                    vle column:'lic_vle_rdv_fk'
             enterprise column:'lic_enterprise_rdv_fk'
                    pca column:'lic_pca_rdv_fk'
               isPublic column:'lic_is_public_rdv_fk'
    concurrentUserCount column:'lic_concurrent_user_count'
           noticePeriod column:'lic_notice_period'
             licenseUrl column:'lic_license_url'
            licensorRef column:'lic_licensor_ref'
            licenseeRef column:'lic_licensee_ref'
            licenseType column:'lic_license_type_str'
          licenseStatus column:'lic_license_status_str'
                lastmod column:'lic_lastmod'
              documents sort:'id', order:'asc'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    reference(nullable:true, blank:true)
    concurrentUsers(nullable:true, blank:true)
    remoteAccess(nullable:true, blank:true)
    walkinAccess(nullable:true, blank:true)
    multisiteAccess(nullable:true, blank:true)
    partnersAccess(nullable:true, blank:true)
    alumniAccess(nullable:true, blank:true)
    ill(nullable:true, blank:true)
    coursepack(nullable:true, blank:true)
    vle(nullable:true, blank:true)
    enterprise(nullable:true, blank:true)
    pca(nullable:true, blank:true)
    isPublic(nullable:true, blank:true)
    concurrentUserCount(nullable:true)
    noticePeriod(nullable:true, blank:true)
    licenseUrl(nullable:true, blank:true)
    licensorRef(nullable:true, blank:true)
    licenseeRef(nullable:true, blank:true)
    licenseType(nullable:true, blank:true)
    licenseStatus(nullable:true, blank:true)
    lastmod(nullable:true, blank:true)
 }

  def getLicensor() {
    def result = null;
    orgLinks.each { or ->
      if ( or?.roleType?.value=='Licensor' )
        result = or.org;
    }
    result
  }

  def getLicensee() {
    def result = null;
    orgLinks.each { or ->
      if ( or?.roleType?.value=='Licensee' )
        result = or.org;
    }
    result
  }


  def getNote(domain) {
    def note = DocContext.findByLicenseAndDomain(this, domain)
    note
  }

  def setNote(domain, note_content) {
    def note = DocContext.findByLicenseAndDomain(this, domain)
    if ( note ) {
      log.debug("update existing note...");
      note.owner.content = note_content
      note.owner.save(flush:true);
    }
    else {
      log.debug("Create new note...");
      if ( ( note_content ) && ( note_content.trim().length() > 0 ) ) {
        def doc = new Doc(content:note_content, lastUpdated:new Date(), dateCreated: new Date())
        def newctx = new DocContext(license: this, owner: doc, domain:domain)
        doc.save();
        newctx.save(flush:true);
      }
    }
  }

  def getGenericLabel() {
    return reference
  }

  // determin if a user can edit this subscription
  def isEditableBy(user, request) {
    hasPerm("edit", user);
  }

  def hasPerm(perm, user) {
    def result = false

    if ( perm=='view' && this.isPublic?.value=='Yes' ) {
      result = true;
    }

    if (!result) {
      // If user is a member of admin role, they can do anything.
      def admin_role = Role.findByAuthority('ROLE_ADMIN');
      if ( admin_role ) {
        if ( user.getAuthorities().contains(admin_role) ) {
          result = true;
        }
      }
    }

    if ( !result ) {
      result = checkPermissions(perm,user);
    }

    result;
  }

  def checkPermissions(perm, user) {
    def result = false
    def principles = user.listPrincipalsGrantingPermission(perm);   // This will list all the orgs and people granted the given perm
    log.debug("The target list if principles : ${principles}");

    // Now we need to see if we can find a path from this object to any of those resources... Any of these orgs can edit
    
    // If this is a concrete license, the owner is the 
    // If it's a template, the owner is the consortia that negoited
    // def owning org list
    // We're looking for all org links that grant a role with the corresponding edit property.
    Set object_orgs = new HashSet();
    orgLinks.each { ol ->
      def perm_exists=false
      ol.roleType.sharedPermissions.each { sp ->
        if ( sp.perm.code==perm )
          perm_exists=true;
      }
      if ( perm_exists ) {
        log.debug("Looks like org ${ol.org} has perm ${perm} shared with it.. so add to list")
        object_orgs.add("${ol.org.id}:${perm}")
      }
    }
    
    log.debug("After analysis, the following relevant org_permissions were located ${object_orgs}, user has the following orgs for that perm ${principles}")

    // Now find the intersection
    def intersection = principles.retainAll(object_orgs)

    log.debug("intersection is ${principles}")

    if ( principles.size() > 0 )
      result = true

    result
  }

  def onChange = { oldMap,newMap ->
    log.debug("license onChange....");
    def changeNotificationService = ApplicationHolder.application.mainContext.getBean("changeNotificationService")
    def controlledProperties = ['licenseUrl','licenseeRef','licensorRef','noticePeriod','reference','concurrentUserCount']

    controlledProperties.each { cp ->
      if ( oldMap[cp] != newMap[cp] ) {
        changeNotificationService.notifyLicenseChange(this.id, cp, oldMap[cp], newMap[cp], '');
      }
    }

    log.debug("On change complete");
  }


}

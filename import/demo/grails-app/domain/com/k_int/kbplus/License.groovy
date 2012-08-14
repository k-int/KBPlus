package com.k_int.kbplus

class License {

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
    incomingLinks:Link
  ]

  static mappedBy = [ subscriptions: 'owner',
                      documents: 'license',
                      orgLinks:'lic',
                      outgoinglinks:'fromLic',
                      incomingLinks:'toLic']

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
  def isEditableBy(user) {
    def result = false
    // users are allowed to edit a subscription if they belong to an institution who has a role as subscriber
    def user_orgs = user.affiliations.collect { it.org }
    if ( user_orgs.contains( getLicensee() ) ) {
      result = true;
    }
    result
  }

}

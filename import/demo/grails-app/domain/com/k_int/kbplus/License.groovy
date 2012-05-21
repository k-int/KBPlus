package com.k_int.kbplus

class License {

  RefdataValue status
  RefdataValue type

  String reference
  String concurrentUsers
  String remoteAccess
  String walkinAccess
  String multisiteAccess
  String partnersAccess
  String alumniAccess
  String ill
  String coursepack
  String vle
  String enterprise
  String pca
  String noticePeriod
  String licenseUrl
  String licensorRef
  String licenseeRef
  String licenseType
  String licenseStatus
  long lastmod

  static hasMany = [
    subscriptions:Subscription
  ]

  static mappedBy = [ subscriptions: 'owner']

  static mapping = {
                id column:'lic_id'
           version column:'lic_version'
            status column:'lic_status_rv_fk'
              type column:'lic_type_rv_fk'
         reference column:'lic_ref'
   concurrentUsers column:'lic_concurrent_users'
      remoteAccess column:'lic_remote_access'
      walkinAccess column:'lic_walkin_access'
   multisiteAccess column:'lic_multisite_access'
    partnersAccess column:'lic_partners_access'
      alumniAccess column:'lic_alumni_access'
               ill column:'lic_ill'
        coursepack column:'lic_coursepack'
               vle column:'lic_vle'
        enterprise column:'lic_enterprise'
               pca column:'lic_pca'
      noticePeriod column:'lic_notice_period'
        licenseUrl column:'lic_license_url'
       licensorRef column:'lic_licensor_ref'
       licenseeRef column:'lic_licensee_ref'
       licenseType column:'lic_license_type_str'
     licenseStatus column:'lic_license_status_str'
           lastmod column:'lic_lastmod'
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
    noticePeriod(nullable:true, blank:true)
    licenseUrl(nullable:true, blank:true)
    licensorRef(nullable:true, blank:true)
    licenseeRef(nullable:true, blank:true)
    licenseType(nullable:true, blank:true)
    licenseStatus(nullable:true, blank:true)
    lastmod(nullable:true, blank:true)
 }
}

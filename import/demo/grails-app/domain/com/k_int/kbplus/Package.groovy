package com.k_int.kbplus

class Package {

  String identifier
  String name
  String impId
  RefdataValue packageType
  RefdataValue packageStatus
  Org contentProvider

  static hasMany = [tipps: TitleInstancePackagePlatform, 
                    orgs: OrgRole, 
                    subscriptions: SubscriptionPackage]

  static mappedBy = [tipps: 'pkg', 
                     orgs: 'pkg',
                     subscriptions: 'pkg']


  static mapping = {
                   id column:'pkg_id'
              version column:'pkg_version'
           identifier column:'pkg_identifier'
                 name column:'pkg_name'
                impId column:'pkg_imp_id', index:'pkg_imp_id_idx'
          packageType column:'pkg_type_rv_fk'
        packageStatus column:'pkg_status_rv_fk'
      contentProvider column:'pkg_content_provider_fk'
                tipps sort:'title.title', order: 'asc'
//                 orgs sort:'org.name', order: 'asc'
  }

  static constraints = {
        packageType(nullable:true, blank:false)
      packageStatus(nullable:true, blank:false)
  }
}

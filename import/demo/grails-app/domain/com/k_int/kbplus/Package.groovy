package com.k_int.kbplus

class Package {

  String identifier
  String name
  String impId
  OrgRole contentProvider
  ReferenceValue packageType
  ReferenceValue packageStatus

  static mappedBy = [tipps: 'pkg']

  static hasMany = [tipps: TitleInstancePackagePlatform]

  static mapping = {
                   id column:'pkg_id'
              version column:'pkg_version'
           identifier column:'pkg_identifier'
                 name column:'pkg_name'
                impId column:'pkg_imp_id'
      contentProvider column:'pkg_or_fk'
          packageType column:'pkg_type_rv_fk'
        packageStatus column:'pkg_status_rv_fk'
  }

  static constraints = {
    contentProvider(nullable:true, blank:false)
        packageType(nullable:true, blank:false)
      packageStatus(nullable:true, blank:false)
  }
}

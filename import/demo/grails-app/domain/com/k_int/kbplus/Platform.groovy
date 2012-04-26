package com.k_int.kbplus

class Platform {

  String impId
  String name
  RefdataValue type
  RefdataValue status


  static mappedBy = [tipps: 'platform']
  static hasMany = [tipps: TitleInstancePackagePlatform]

  static mapping = {
                id column:'plat_id'
           version column:'plat_version'
             impId column:'plat_imp_id'
              name column:'plat_name'
              type column:'plat_type_rv_fk'
            status column:'plat_status_rv_fk'
             tipps sort: 'title.title', order: 'asc'
  }

  static constraints = {
    type(nullable:true, blank:false)
    status(nullable:true, blank:false)
  }
}

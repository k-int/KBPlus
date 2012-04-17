package com.k_int.kbplus

class TitleInstance {

  String title
  String impId
  RefdataValue status
  RefdataValue type

  static mappedBy = [tipps: 'title', ids: 'ti']
  // static hasMany = [tipps: TitleInstancePackagePlatform, ids: TitleSID]
  static hasMany = [tipps: TitleInstancePackagePlatform, ids: IdentifierOccurrence]


  static mapping = {
         id column:'ti_id'
      title column:'ti_title'
    version column:'ti_version'
      impId column:'ti_imp_id'
     status column:'ti_status_rv_fk'
       type column:'ti_type_rv_fk'
  }

  static constraints = {
    status(nullable:true, blank:false);
    type(nullable:true, blank:false);
  }
}

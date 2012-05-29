package com.k_int.kbplus

class TitleInstance {

  String title
  String impId
  RefdataValue status
  RefdataValue type
  Long lastModified = System.currentTimeMillis();

  static mappedBy = [tipps: 'title', ids: 'ti']
  static hasMany = [tipps: TitleInstancePackagePlatform, ids: IdentifierOccurrence]


  static mapping = {
         id column:'ti_id'
      title column:'ti_title'
    version column:'ti_version'
      impId column:'ti_imp_id', index:'ti_imp_id_idx'
     status column:'ti_status_rv_fk'
       type column:'ti_type_rv_fk'
      tipps sort:'startDate', order: 'asc'
  }

  static constraints = {
    status(nullable:true, blank:false);
    type(nullable:true, blank:false);
  }
}

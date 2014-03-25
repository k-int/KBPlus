package com.k_int.kbplus

class Fact {

  Date factFrom
  Date factTo
  String factValue
  String factUid
  Long reportingYear
  Long reportingMonth
  RefdataValue factType

  TitleInstance relatedTitle
  Org supplier
  Org inst
  IdentifierOccurrence juspio

  static constraints = {
    factUid(nullable:true, blank:false,unique:true)
    relatedTitle(nullable:true, blank:false)
    supplier(nullable:true, blank:false)
    inst(nullable:true, blank:false)
    juspio(nullable:true, blank:false)
    reportingYear(nullable:true, blank:false)
    reportingMonth(nullable:true, blank:false)
  }

  static mapping = {
                id column:'fact_id'
           version column:'fact_version'
           factUid column:'fact_uid', index:'fact_uid_idx'
          factType column:'fact_type_rdv_fk'
      relatedTitle index:'fact_access_idx'
          supplier index:'fact_access_idx'
              inst index:'fact_access_idx'
  }

}



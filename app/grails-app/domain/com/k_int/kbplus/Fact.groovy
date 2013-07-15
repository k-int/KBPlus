package com.k_int.kbplus

class Fact {

  RefdataValue factType
  Date factFrom
  Date factTo
  String factValue
  String factUid
  Long reportingYear
  Long reportingMonth

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


}



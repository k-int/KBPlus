package com.k_int.kbplus

class Fact {

  RefdataValue factType
  Date factFrom
  Date factTo
  String factValue
  String factUid

  TitleInstance relatedTitle
  Org supplier
  Org inst

  static constraints = {
    factUid(nullable:true, blank:false)
    relatedTitle(nullable:true, blank:false)
    supplier(nullable:true, blank:false)
    inst(nullable:true, blank:false)
  }


}



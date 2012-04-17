package com.k_int.kbplus

class IdentifierOccurrence {

  TitleInstance ti
  TitleInstancePackagePlatform tipp

  static mapping = {
      id column:'io_id'
      ti column:'io_ti_fk'
    tipp column:'io_tipp_fk'
  }

  static constraints = {
      ti(nullable:true)
    tipp(nullable:true)
  }
}

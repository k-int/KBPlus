package com.k_int.kbplus

class IdentifierOccurrence {

  Identifier identifier

  static belongsTo = [
    ti:TitleInstance,
    tipp:TitleInstancePackagePlatform
  ]

  static mapping = {
            id column:'io_id'
    identifier column:'io_canonical_id'
            ti column:'io_ti_fk'
          tipp column:'io_tipp_fk'
  }

  static constraints = {
      ti(nullable:true)
    tipp(nullable:true)
  }
}

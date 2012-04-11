package com.k_int.kbplus

class TitleSID {

  String namespace
  String identifier

  static belongsTo = [ owner: TitleInstance ]

  static mapping = {
            id column:'tsi_id'
     namespace column:'tsi_namespace'
    identifier column:'tsi_identifier'
         owner column:'tsi_ti_fk'
  }

  static constraints = {
  }
}

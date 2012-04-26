package com.k_int.kbplus

class RefdataValue {

  String value

  static belongsTo = [
    owner:RefdataCategory
  ]

  static mapping = {
         id column:'rdv_id'
    version column:'rdv_version'
      owner column:'rdv_owner', index:'rdv_entry_idx'
      value column:'rdv_value', index:'rdv_entry_idx'
  }

  static constraints = {
  }
}

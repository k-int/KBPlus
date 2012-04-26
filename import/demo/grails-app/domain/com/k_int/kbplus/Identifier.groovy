package com.k_int.kbplus

class Identifier {

  IdentifierNamespace ns
  String value

  static constraints = {
  }

  static mapping = {
       id column:'id_id'
       ns column:'id_ns_fk', index:'id_value_idx'
    value column:'id_value', index:'id_value_idx'
  }


}

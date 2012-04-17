package com.k_int.kbplus

class Identifier {

  IdentifierNamespace ns
  String value

  static constraints = {
  }

  static mapping = {
       id column:'id_id'
    value column:'id_value'
  }


}

package com.k_int.kbplus

class IdentifierNamespace {

  String ns
  RefdataValue nstype
  Boolean hide

  static mapping = {
    id column:'idns_id'
    ns column:'idns_ns'
    nstype column:'idns_type_fl'
    hide column:'idns_hide'
  }

  static constraints = {
    nstype nullable:true, blank:false
    hide nullable:true, blank:false
  }
}

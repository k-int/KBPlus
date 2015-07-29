package com.k_int.kbplus

class IdentifierNamespace {

  String ns
  RefdataValue nstype
  Boolean hide
  String validationRegex
  String family

  static mapping = {
    id column:'idns_id'
    ns column:'idns_ns'
    nstype column:'idns_type_fl'
    hide column:'idns_hide'
    validationRegex column:'idns_val_regex'
    family column:'idns_family'
  }

  static constraints = {
    nstype nullable:true, blank:false
    hide nullable:true, blank:false
    validationRegex nullable:true, black:false
    family nullable:true, black:false
  }
}

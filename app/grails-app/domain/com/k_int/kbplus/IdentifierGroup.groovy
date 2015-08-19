package com.k_int.kbplus

class IdentifierGroup {

  static hasMany = [ identifiers:Identifier]
  static mappedBy = [ identifiers:'ig']

  static mapping = {
    id column:'ig_id'
  }

  static constraints = {
  }

}

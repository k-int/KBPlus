package com.k_int.custprops

class ObjectDefinition extends PropertyDefinition {

  static hasMany = [properties:PropertyDefinition]

  static constraints = {
  }

  static mapping = {
    properties joinTable: [name:'obj_prop_members', key:'obj_prop', column:'member_prop']
  }
}

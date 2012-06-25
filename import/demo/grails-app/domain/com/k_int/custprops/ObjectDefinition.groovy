package com.k_int.custprops

class ObjectDefinition extends TypeDefinition {

  static hasMany = [properties:ObjectProperty]

  static constraints = {
  }

  static def ensureType(string typeName) {
    ObjectDefinition.findByTypeName(typeName) ?: new ObjectDefinition(typeName:typeName).save(flush:true);
  }

  def ensureProperty(propName, propType) {
    // Make sure this type has a property of the identified type
  }
}

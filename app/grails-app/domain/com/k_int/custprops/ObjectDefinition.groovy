package com.k_int.custprops

class ObjectDefinition extends TypeDefinition {

  static hasMany = [properties:ObjectProperty]

  static constraints = {
  }

  static def ensureType(tn) {
    ObjectDefinition.findByTypeName(tn) ?: new ObjectDefinition(typeName:tn).save(flush:true);
  }

  def ensureProperty(Map params) {
    // Make sure this type has a property of the identified type
    log.debug("Ensure property ${params.propName}, ${params.propType}");
  }
}

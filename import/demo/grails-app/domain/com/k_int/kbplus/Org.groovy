package com.k_int.kbplus

class Org {

  String name
  String impId

  static mapping = {
         id column:'org_id'
    version column:'org_version'
      impId column:'org_imp_id'
       name column:'org_name'
  }

  static constraints = {
  }
}

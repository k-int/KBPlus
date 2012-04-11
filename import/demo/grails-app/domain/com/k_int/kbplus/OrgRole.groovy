package com.k_int.kbplus

class OrgRole {

  String roletype

  static belongsTo {
    org:Org
  }

  static constraints = {
  }
}

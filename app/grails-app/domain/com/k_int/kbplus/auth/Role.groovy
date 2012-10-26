package com.k_int.kbplus.auth

class Role {

  String authority
  String roleType

  static mapping = {
    cache true
  }

  static constraints = {
    authority blank: false, unique: true
    roleType blank: false, nullable:true
  }
}

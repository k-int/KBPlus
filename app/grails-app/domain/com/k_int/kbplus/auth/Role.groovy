package com.k_int.kbplus.auth

class Role {

  String authority
  String roleType
  Set grantedPermissions = []


  static mapping = {
    cache true
  }

  static hasMany = [
   grantedPermissions:PermGrant
  ]

  static mappedBy = [
    grantedPermissions:"role"
  ]

  static constraints = {
    authority blank: false, unique: true
    roleType blank: false, nullable:true
  }
}

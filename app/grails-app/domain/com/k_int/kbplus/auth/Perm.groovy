package com.k_int.kbplus.auth

class Perm {

  String code
  Set grantedTo = []

  static mapping = {
    cache true
  }

  static constraints = {
    code blank: false, unique: true, nullable:false
  }

  static hasMany = [
   grantedTo:PermGrant
  ]

  static mappedBy = [
    grantedTo:"perm"
  ]

}

package com.k_int.kbplus.auth

class Perm {

  String code

  static mapping = {
    cache true
  }

  static constraints = {
    code blank: false, unique: true, nullable:false
  }
}

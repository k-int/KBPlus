package com.k_int.kbplus

class Package {

  String identifier
  String name
  static hasMany = [tipps: TitleInstancePackagePlatform]

  static constraints = {
  }
}

package com.k_int.kbplus

class Package {

  String identifier
  String name
  String impId
  Org contentProvider

  static hasMany = [tipps: TitleInstancePackagePlatform]

  static constraints = {
    contentProvider(nullable:true, blank:false)
  }
}

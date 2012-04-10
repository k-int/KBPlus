package com.k_int.kbplus

class Platform {

  String impId
  String name

  static hasMany = [tipps: TitleInstancePackagePlatform]

  static constraints = {
  }
}

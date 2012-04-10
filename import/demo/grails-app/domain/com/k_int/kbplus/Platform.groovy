package com.k_int.kbplus

class Platform {

  String impId
  String name

  static mappedBy = [tipps: 'platform']
  static hasMany = [tipps: TitleInstancePackagePlatform]

  static constraints = {
  }
}

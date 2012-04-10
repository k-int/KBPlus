package com.k_int.kbplus

class TitleInstance {

  String title
  String impId
  static hasMany = [tipps: TitleInstancePackagePlatform, ids: TitleSID]

  static constraints = {
  }
}

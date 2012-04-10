package com.k_int.kbplus

class TitleInstance {

  String title
  static hasMany = [tipps: TitleInstancePackagePlatform, ids: TitleSID]

  static constraints = {
  }
}

package com.k_int.kbplus

class TitleInstancePackagePlatform {

  static belongsTo = [
    pkg:Package,
    platform:Platform,
    title:TitleInstance
  ]

  static constraints = {
  }
}

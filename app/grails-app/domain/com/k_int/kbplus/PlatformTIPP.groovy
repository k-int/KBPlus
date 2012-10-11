package com.k_int.kbplus

class PlatformTIPP {

  //TitleInstancePackagePlatform tipp
  //Platform platform
  String titleUrl
  String rel

  static belongsTo = [
    tipp:TitleInstancePackagePlatform,
    platform:Platform
  ]

  static constraints = {
    titleUrl(nullable:true, blank:true);
    rel(nullable:true, blank:true);
  }
}

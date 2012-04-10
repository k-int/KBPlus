package com.k_int.kbplus

class TitleInstancePackagePlatform {

  String startDate
  String startVolume
  String startIssue
  String endDate
  String endVolume
  String endIssue
  String embargo
  String coverageDepth
  String coverageNote

  static belongsTo = [
    pkg:Package,
    platform:Platform,
    title:TitleInstance
  ]

  static mapping = {
    coverageNote type: 'text'
  }

  static constraints = {
    startDate(nullable:true, blank:true);
    startVolume(nullable:true, blank:true);
    startIssue(nullable:true, blank:true);
    endDate(nullable:true, blank:true);
    endVolume(nullable:true, blank:true);
    endIssue(nullable:true, blank:true);
    embargo(nullable:true, blank:true);
    coverageDepth(nullable:true, blank:true);
    coverageNote(nullable:true, blank:true);
  }
}

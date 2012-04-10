package com.k_int.kbplus

class TitleInstance {

  String title
  String impId

  static mappedBy = [tipps: 'title', 
                     ids: 'owner']

  static hasMany = [tipps: TitleInstancePackagePlatform, 
                    ids: TitleSID]


  static constraints = {
  }
}

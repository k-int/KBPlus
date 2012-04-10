package com.k_int.kbplus

class TitleSID {


  String namespace
  String identifier

  static belongsTo = [ owner: TitleInstance ]
  static constraints = {
  }
}

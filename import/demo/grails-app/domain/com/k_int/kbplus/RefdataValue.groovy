package com.k_int.kbplus

class RefdataValue {

  String value

  static belongsTo = [
    owner:RefdataCategory
  ]

  static constraints = {
  }
}

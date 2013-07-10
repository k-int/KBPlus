package com.k_int.kbplus

class Fact {

  RefdataValue type
  Date factTimestamp
  String value
  static mappedBy = [classifiers: 'fact']
  static hasMany = [classifiers: FactClassifier]
}

package com.k_int.kbplus

class Fact {

  RefdataValue factType
  Date factFrom
  Date factTo
  String factValue
  String factUid
  static mappedBy = [classifiers: 'fact']
  static hasMany = [classifiers: FactClassifier]
}

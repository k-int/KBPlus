package com.k_int.kbplus

class DocContext {

  static belongsTo = [
    owner:Doc
  ]

  static mapping = {
          id column:'dc_id'
     version column:'dc_version'
       owner column:'dc_doc_fk'
  }

  static constraints = {
  }
}

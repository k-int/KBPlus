package com.k_int.kbplus

class DocContext {

  static belongsTo = [
    owner:Doc
  ]

  RefdataValue doctype
  License license

  static mapping = {
          id column:'dc_id'
     version column:'dc_version'
       owner column:'dc_doc_fk'
     doctype column:'dc_rv_doctype_fk'
     license column:'dc_lic_fk'
  }

  static constraints = {
    doctype(nullable:true, blank:false);
    license(nullable:true, blank:false);
  }
}

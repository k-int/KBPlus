package com.k_int.kbplus

class PendingChange {

  License license
  Doc doc
  String updateProperty
  String updateValue
  String updateReason

  static mapping = {
           license column:'pc_lic_fk'
               doc column:'pc_doc_fk'
    updateProperty column:'pc_update_prop'
       updateValue column:'pc_update_value'
      updateReason column:'pc_update_reason'

  }

  static constraints = {
    license(nullable:true, blank:false);
    doc(nullable:true, blank:false);
    updateProperty(nullable:false, blank:false);
    updateValue(nullable:false, blank:false);
    updateReason(nullable:true, blank:false);
  }
}
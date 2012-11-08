package com.k_int.kbplus

class DocContext {

  static belongsTo = [
    owner:Doc
  ]

  RefdataValue status
  RefdataValue doctype
  License license
  Subscription subscription
  Alert alert

  // We may attach a note to a particular colum, in which case, we set domain here as a discriminator
  String domain

  static mapping = {
               id column:'dc_id'
          version column:'dc_version'
            owner column:'dc_doc_fk'
          doctype column:'dc_rv_doctype_fk'
          license column:'dc_lic_fk'
     subscription column:'dc_sub_fk'
           status column:'dc_status_fk'
            alert column:'dc_alert_fk'
  }

  static constraints = {
    doctype(nullable:true, blank:false);
    license(nullable:true, blank:false);
    subscription(nullable:true, blank:false);
    domain(nullable:true, blank:false);
    status(nullable:true, blank:false);
    alert(nullable:true, blank:false);
  }
}

package com.k_int.kbplus

class Doc {

  RefdataValue status
  RefdataValue type
  Alert alert
  String content 

  static mapping = {
                id column:'doc_id'
           version column:'doc_version'
            status column:'doc_status_rv_fk'
              type column:'doc_type_rv_fk'
             alert column:'doc_alert_fk'
           content column:'doc_content', type:'text'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    alert(nullable:true, blank:false)
    content(nullable:true, blank:false)
  }
}

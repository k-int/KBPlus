package com.k_int.kbplus

class Doc {

  RefdataValue status
  RefdataValue type
  Alert alert
  String title
  Integer contentType=0 // 0=String, 1=docstore
  String content 
  String uuid 
  Date dateCreated
  Date lastUpdated


  static mapping = {
                id column:'doc_id'
           version column:'doc_version'
            status column:'doc_status_rv_fk'
              type column:'doc_type_rv_fk'
             alert column:'doc_alert_fk'
       contentType column:'doc_content_type'
              uuid column:'doc_docstore_uuid'
             title column:'doc_title'
           content column:'doc_content', type:'text'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    alert(nullable:true, blank:false)
    content(nullable:true, blank:false)
    uuid(nullable:true, blank:false)
    contentType(nullable:true, blank:false)
    title(nullable:true, blank:false)
  }
}

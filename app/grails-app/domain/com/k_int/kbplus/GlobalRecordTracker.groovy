package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder

class GlobalRecordTracker {

  GlobalRecordInfo owner
  String localOid
  String identifier
  String name

  static mapping = {
                   id column:'grt_id'
              version column:'grt_version'
               source column:'grt_owner_fk'
           identifier column:'grt_identifier'
             localOid column:'grt_local_oid'
                 name column:'grt_name'
  }

  static constraints = {
          name(nullable:true, blank:false)
      localOid(nullable:true, blank:false)
  }

}
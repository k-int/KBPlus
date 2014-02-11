package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder

class GlobalRecordInfo {

  GlobalRecordSource source
  String identifier
  String name
  Date ts

  static mapping = {
                   id column:'gri_id'
              version column:'gri_version'
               source column:'gri_source_fk'
            timestamp column:'gri_timestamp'
           identifier column:'gri_identifier'
                 name column:'gri_name'
  }

  static constraints = {
          name(nullable:true, blank:false)
  }

}

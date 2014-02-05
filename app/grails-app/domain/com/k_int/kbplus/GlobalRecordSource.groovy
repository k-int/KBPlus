package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder

class GlobalRecordSource {

  String identifier
  String name

  static mapping = {
                   id column:'grs_id'
              version column:'grs_version'
           identifier column:'grs_identifier'
                 name column:'grs_name'
  }

  static constraints = {
          name(nullable:true, blank:false)
  }

}

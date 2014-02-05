package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder

class GlobalRecordInfo {

  String identifier
  String name

  static mapping = {
                   id column:'gri_id'
              version column:'gri_version'
           identifier column:'gri_identifier'
                 name column:'gri_name'
  }

  static constraints = {
          name(nullable:true, blank:false)
  }

}

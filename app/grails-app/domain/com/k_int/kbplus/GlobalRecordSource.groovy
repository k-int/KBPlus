package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder

class GlobalRecordSource {

  String identifier
  String name
  String type
  Date haveUpTo
  String uri
  String listPrefix
  String fullPrefix
  String principal
  String credentials

  static mapping = {
                   id column:'grs_id'
              version column:'grs_version'
           identifier column:'grs_identifier'
                 name column:'grs_name'
             haveUpTo column:'grs_have_up_to'
                  uri column:'grs_uri'
           fullPrefix column:'grs_list_prefix'
           listPrefix column:'grs_full_prefix'
                 type column:'grs_type'
            principal column:'grs_principal'
          credentials column:'grs_creds'
  }

  static constraints = {
     identifier(nullable:true, blank:false)
           name(nullable:true, blank:false)
       haveUpTo(nullable:true, blank:false)
            uri(nullable:true, blank:false)
           type(nullable:true, blank:false)
     fullPrefix(nullable:true, blank:false)
     listPrefix(nullable:true, blank:false)
      principal(nullable:true, blank:false)
    credentials(nullable:true, blank:false)
  }

}

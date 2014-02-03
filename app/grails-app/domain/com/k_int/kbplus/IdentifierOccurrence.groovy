package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder


class IdentifierOccurrence {

  static auditable = true

  Identifier identifier

  static belongsTo = [
    ti:TitleInstance,
    org:Org,
    tipp:TitleInstancePackagePlatform
  ]

  static mapping = {
            id column:'io_id'
    identifier column:'io_canonical_id'
            ti column:'io_ti_fk'
          tipp column:'io_tipp_fk'
           org column:'io_org_fk'
  }

  static constraints = {
     org(nullable:true)
      ti(nullable:true)
    tipp(nullable:true)
  }
  
  String toString() {
    "IdentifierOccurrence(${id} - ti:${ti}, org:${org}, tipp:${tipp}";
  }

  @Transient
  def onSave = {
  }

  @Transient
  def onDelete = {
  }


}

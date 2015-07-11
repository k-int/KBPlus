package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder


class IdentifierOccurrence {

  static auditable = true

  Identifier identifier

  static belongsTo = [
    ti:TitleInstance,
    org:Org,
    tipp:TitleInstancePackagePlatform,
    pkg:Package,
    sub:Subscription
  ]

  static mapping = {
            id column:'io_id'
    identifier column:'io_canonical_id'
            ti column:'io_ti_fk'
          tipp column:'io_tipp_fk'
           org column:'io_org_fk'
           pkg column:'io_pkg_fk'
           sub column:'io_sub_fk'
  }

  static constraints = {
     org(nullable:true)
      ti(nullable:true)
    tipp(nullable:true)
     pkg(nullable:true)
     sub(nullable:true)
  }
  
  String toString() {
    "IdentifierOccurrence(${id} - ti:${ti}, org:${org}, tipp:${tipp}, pkg:${pkg}, sub:${sub}";
  }

  @Transient
  def onSave = {
  }

  @Transient
  def onDelete = {
  }


}

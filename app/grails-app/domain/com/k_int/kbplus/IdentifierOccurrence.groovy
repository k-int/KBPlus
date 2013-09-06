package com.k_int.kbplus

import javax.persistence.Transient

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

    log.debug("onSave")
    if ( ti != null ) {
      def changeNotificationService = ApplicationHolder.application.mainContext.getBean("changeNotificationService")

      changeNotificationService.notifyChangeEvent([
                                                   OID:"${ti.class.name}:${ti.id}",
                                                   event:'TitleInstance.identifierAdded'
                                                  ])
    }
  }

  @Transient
  def onDelete = {

    log.debug("onDelete")
    if ( ti != null ) {
      def changeNotificationService = ApplicationHolder.application.mainContext.getBean("changeNotificationService")

      changeNotificationService.notifyChangeEvent([
                                                   OID:"${ti.class.name}:${ti.id}",
                                                   event:'TitleInstance.identifierRemoved'
                                                  ])
    }
  }


}

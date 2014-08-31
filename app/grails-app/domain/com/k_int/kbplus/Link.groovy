package com.k_int.kbplus

class Link {

  RefdataValue status
  RefdataValue type

  // Participant 1 - One of these
  License fromLic

  // Participant 2 - One of these
  License toLic

  // If slaved then any changes from fromLic should automaticallly be propagated to toLic
  Boolean slaved

  static mapping = {
                id column:'link_id'
           version column:'link_version'
            status column:'link_status_rv_fk'
              type column:'link_type_rv_fk'
           fromLic column:'link_from_lic_fk'
             toLic column:'link_to_lic_fk'
            slaved column:'link_is_slaved'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    fromLic(nullable:true, blank:false)
    toLic(nullable:true, blank:false)
    slaved(nullable:true, blank:false)
  }


  def getLinkSource() {
    def result = null;
    if ( fromLic )
      result = fromLic
    result
  }

  def getLinkTarget() {
    def result = null;
    if ( toLic )
      result = toLic
    result
  }

}

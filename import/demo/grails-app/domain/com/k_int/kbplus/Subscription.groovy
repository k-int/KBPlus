package com.k_int.kbplus

class Subscription {

  ReferenceValue status
  ReferenceValue type

  License owner

  static mapping = {
                id column:'sub_id'
           version column:'sub_version'
            status column:'sub_status_rv_fk'
              type column:'sub_type_rv_fk'
             owner column:'sub_owner_license_fk'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    owner(nullable:true, blank:false)
  }
}

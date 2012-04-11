package com.k_int.kbplus

class LicenseEntitlement {

  ReferenceValue status

  Subscription owner

  static mapping = {
                id column:'le_id'
           version column:'le_version'
            status column:'le_status_rv_fk'
             owner column:'le_owner_subscription_fk'
  }

  static constraints = {
    status(nullable:true, blank:false)
    owner(nullable:true, blank:false)
  }
}

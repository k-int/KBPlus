package com.k_int.kbplus

class IssueEntitlement {

  RefdataValue status

  Subscription owner

  static mapping = {
                id column:'ie_id'
           version column:'ie_version'
            status column:'ie_status_rv_fk'
             owner column:'ie_owner_subscription_fk'
  }

  static constraints = {
    status(nullable:true, blank:false)
    owner(nullable:true, blank:false)
  }
}

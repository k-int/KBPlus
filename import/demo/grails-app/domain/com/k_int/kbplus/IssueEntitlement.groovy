package com.k_int.kbplus

class IssueEntitlement {

  RefdataValue status

  static belongsTo = [subscription: Subscription,tipp: TitleInstancePackagePlatform]

  static mapping = {
                id column:'ie_id'
           version column:'ie_version'
            status column:'ie_status_rv_fk'
      subscription column:'ie_subscription_fk'
              tipp column:'ie_tipp_fk'
  }

  static constraints = {
    status(nullable:true, blank:false)
    subscription(nullable:true, blank:false)
    tipp(nullable:true, blank:false)
  }
}

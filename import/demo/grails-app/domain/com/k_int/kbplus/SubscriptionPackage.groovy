package com.k_int.kbplus

class SubscriptionPackage {

  Subscription subscription
  Package pkg

  static mapping = {
                id column:'sp_id'
           version column:'sp_version'
      subscription column:'sp_sub_fk'
               pkg column:'sp_pkg_fk'
  }

  static constraints = {
    subscription(nullable:true, blank:false)
    pkg(nullable:true, blank:false)
  }
}

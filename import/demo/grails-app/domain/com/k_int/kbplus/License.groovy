package com.k_int.kbplus

class License {

  RefdataValue status
  RefdataValue type

  static hasMany = [
    subscriptions:Subscription
  ]

  static mappedBy = [ subscriptions: 'owner']

  static mapping = {
                id column:'lic_id'
           version column:'lic_version'
            status column:'lic_status_rv_fk'
              type column:'lic_type_rv_fk'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
  }
}

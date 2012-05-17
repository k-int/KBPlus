package com.k_int.kbplus

class Subscription {

  RefdataValue status
  RefdataValue type

  String name
  String identifier
  String impId
  Date startDate
  Date endDate
  Subscription instanceOf

  License owner

  static hasMany = [ packages : SubscriptionPackage ]
  static mappedBy = [ packages : 'subscription' ]

  static mapping = {
                id column:'sub_id'
           version column:'sub_version'
            status column:'sub_status_rv_fk'
              type column:'sub_type_rv_fk'
             owner column:'sub_owner_license_fk'
              name column:'sub_name'
        identifier column:'sub_identifier'
             impId column:'sub_imp_id'
         startDate column:'sub_start_date'
           endDate column:'sub_end_date'
        instanceOf column:'sub_parent_sub_fk'
          
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    owner(nullable:true, blank:false)
    impId(nullable:true, blank:false)
    startDate(nullable:true, blank:false)
    endDate(nullable:true, blank:false)
    instanceOf(nullable:true, blank:false)
  }
}

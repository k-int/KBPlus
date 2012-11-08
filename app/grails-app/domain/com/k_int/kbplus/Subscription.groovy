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
  String noticePeriod
  Date dateCreated
  Date lastUpdated
  // Org vendor

  License owner
  SortedSet issueEntitlements

  static transients = [ 'subscriber', 'provider', 'consortia' ]

  static hasMany = [ packages : SubscriptionPackage, 
                     issueEntitlements: IssueEntitlement,
                     documents:DocContext,
                     orgRelations: OrgRole ]

  static mappedBy = [ packages : 'subscription', 
                      issueEntitlements: 'subscription',
                      documents: 'subscription',
                      orgRelations: 'sub' ]

  static mapping = {
                  id column:'sub_id'
             version column:'sub_version'
              status column:'sub_status_rv_fk'
                type column:'sub_type_rv_fk'
               owner column:'sub_owner_license_fk'
                name column:'sub_name'
          identifier column:'sub_identifier'
               impId column:'sub_imp_id', index:'sub_imp_id_idx'
           startDate column:'sub_start_date'
             endDate column:'sub_end_date'
          instanceOf column:'sub_parent_sub_fk'
        noticePeriod column:'sub_notice_period'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    owner(nullable:true, blank:false)
    impId(nullable:true, blank:false)
    startDate(nullable:true, blank:false)
    endDate(nullable:true, blank:false)
    instanceOf(nullable:true, blank:false)
    noticePeriod(nullable:true, blank:true)
    // vendor(nullable:true, blank:false)
  }

  def getSubscriber() {
    def result = null;
    orgRelations.each { or ->
      if ( or?.roleType?.value=='Subscriber' )
        result = or.org;
    }
    result
  }

  def getProvider() {
    def result = null;
    orgRelations.each { or ->
      if ( or?.roleType?.value=='Content Provider' )
        result = or.org;
    }
    result
  }

  def getConsortia() {
    def result = null;
    orgRelations.each { or ->
      if ( or?.roleType?.value=='Subscription Consortia' )
        result = or.org;
    }
    result
  }

  // determin if a user can edit this subscription
  def isEditableBy(user) {
    def result = false
    // users are allowed to edit a subscription if they belong to an institution who has a role as subscriber
    def user_orgs = user.affiliations.collect { it.org }
    if ( user_orgs.contains( getSubscriber() ) ) {
      result = true;
    }
    result
  }
}

package com.k_int.kbplus

import javax.persistence.Transient

class Package {

  String identifier
  String name
  String impId
  RefdataValue packageType
  RefdataValue packageStatus
  RefdataValue packageListStatus
  Org contentProvider
  Platform nominalPlatform
  Date dateCreated
  Date lastUpdated


  static hasMany = [tipps: TitleInstancePackagePlatform, 
                    orgs: OrgRole, 
                    subscriptions: SubscriptionPackage]

  static mappedBy = [tipps: 'pkg', 
                     orgs: 'pkg',
                     subscriptions: 'pkg']


  static mapping = {
                   id column:'pkg_id'
              version column:'pkg_version'
           identifier column:'pkg_identifier'
                 name column:'pkg_name'
                impId column:'pkg_imp_id', index:'pkg_imp_id_idx'
          packageType column:'pkg_type_rv_fk'
        packageStatus column:'pkg_status_rv_fk'
    packageListStatus column:'pkg_list_status_rv_fk'
      nominalPlatform column:'pkg_nominal_platform_fk'
                tipps sort:'title.title', order: 'asc'
//                 orgs sort:'org.name', order: 'asc'
  }

  static constraints = {
          packageType(nullable:true, blank:false)
        packageStatus(nullable:true, blank:false)
      contentProvider(nullable:true, blank:false)
      nominalPlatform(nullable:true, blank:false)
    packageListStatus(nullable:true, blank:false)
  }

  /**
   * Materialise this package into a subscription of the given type (taken or offered)
   * @param subtype One of 'Subscription Offered' or 'Subscription Taken'
   */
  @Transient
  def createSubscription(subtype, subname, subidentifier, startdate, enddate, consortium_org) {

    // Create the header

    def result = new Subscription( name:subname,
                                   status:RefdataCategory.lookupOrCreate('Subscription Status','Current'),
                                   identifier:subidentifier,
                                   impId:null,
                                   startDate:startdate,
                                   endDate:enddate,
                                   type: RefdataValue.findByValue(subtype))
    if ( result.save(flush:true) ) {
    }

    if ( consortium_org ) {
      def sc_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Subscription Consortia');
      def or = new OrgRole(org: consortium_org, sub:result, roleType:sc_role).save();
    }

    def new_package_link = new SubscriptionPackage(subscription:result, pkg:this).save();


    def live_issue_entitlement = RefdataCategory.lookupOrCreate('Entitlement Issue Status', 'Live');

    // Copy the tipps into the IEs
    log.debug("Copy tipp entries into new subscription");

    tipps.each { tipp ->
      log.debug("adding ${tipp}");

      def new_ie = new IssueEntitlement(status: live_issue_entitlement,
                                        subscription: result,
                                        tipp: tipp,
                                        startDate:tipp.startDate,
                                        startVolume:tipp.startVolume,
                                        startIssue:tipp.startIssue,
                                        endDate:tipp.endDate,
                                        endVolume:tipp.endVolume,
                                        endIssue:tipp.endIssue,
                                        embargo:tipp.embargo,
                                        coverageDepth:tipp.coverageDepth,
                                        coverageNote:tipp.coverageNote).save()

    }

    log.debug("Completed...");

    result
  }
}

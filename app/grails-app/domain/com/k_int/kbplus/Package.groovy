package com.k_int.kbplus

import javax.persistence.Transient

class Package {

  static auditable = true

  @Transient
  def grailsApplication

  String identifier
  String name
  String impId
  RefdataValue packageType
  RefdataValue packageStatus
  RefdataValue packageListStatus
  RefdataValue breakable
  RefdataValue consistent
  RefdataValue fixed
  RefdataValue isPublic
  RefdataValue packageScope
  Platform nominalPlatform
  Date startDate
  Date endDate
  Date dateCreated
  Date lastUpdated
  License license
  String forumId
  Set pendingChanges
  Boolean autoAccept = false

  static hasMany = [tipps: TitleInstancePackagePlatform, 
                    orgs: OrgRole, 
                    documents:DocContext,
                    subscriptions: SubscriptionPackage,
                    pendingChanges:PendingChange ]

  static mappedBy = [tipps: 'pkg', 
                     orgs: 'pkg',
                     documents:'pkg',
                     subscriptions: 'pkg',
                     pendingChanges: 'pkg']


  static mapping = {
                   id column:'pkg_id'
              version column:'pkg_version'
           identifier column:'pkg_identifier'
                 name column:'pkg_name'
                impId column:'pkg_imp_id', index:'pkg_imp_id_idx'
          packageType column:'pkg_type_rv_fk'
        packageStatus column:'pkg_status_rv_fk'
    packageListStatus column:'pkg_list_status_rv_fk'
            breakable column:'pkg_breakable_rv_fk'
           consistent column:'pkg_consistent_rv_fk'
                fixed column:'pkg_fixed_rv_fk'
      nominalPlatform column:'pkg_nominal_platform_fk'
            startDate column:'pkg_start_date'
              endDate column:'pkg_end_date'
              license column:'pkg_license_fk'
             isPublic column:'pkg_is_public'
         packageScope column:'pkg_scope_rv_fk'
              forumId column:'pkg_forum_id'
                tipps sort:'title.title', order: 'asc'

//                 orgs sort:'org.name', order: 'asc'
  }

  static constraints = {
          packageType(nullable:true, blank:false)
        packageStatus(nullable:true, blank:false)
      nominalPlatform(nullable:true, blank:false)
    packageListStatus(nullable:true, blank:false)
            breakable(nullable:true, blank:false)
           consistent(nullable:true, blank:false)
                fixed(nullable:true, blank:false)
            startDate(nullable:true, blank:false)
              endDate(nullable:true, blank:false)
              license(nullable:true, blank:false)
             isPublic(nullable:true, blank:false)
         packageScope(nullable:true, blank:false)
              forumId(nullable:true, blank:false)
                impId(nullable:true, blank:false)
  }

  def getConsortia() {
    def result = null;
    orgs.each { or ->
      if ( ( or?.roleType?.value=='Subscription Consortia' ) || ( or?.roleType?.value=='Package Consortia' ) )
        result = or.org;
    }
    result
  }
  
  /**
   * Materialise this package into a subscription of the given type (taken or offered)
   * @param subtype One of 'Subscription Offered' or 'Subscription Taken'
   */
  @Transient
  def createSubscription(subtype, 
                         subname, 
                         subidentifier, 
                         startdate, 
                         enddate, 
                         consortium_org) {
    createSubscription(subtype,subname,subidentifier,startdate,enddate,consortium_org,true)
  }

  @Transient
  def createSubscription(subtype,
                         subname,
                         subidentifier,
                         startdate,
                         enddate,
                         consortium_org,
                         add_entitlements) {
    // Create the header

    def result = new Subscription( name:subname,
                                   status:RefdataCategory.lookupOrCreate('Subscription Status','Current'),
                                   identifier:subidentifier,
                                   impId:java.util.UUID.randomUUID().toString(),
                                   startDate:startdate,
                                   endDate:enddate,
                                   isPublic: RefdataCategory.lookupOrCreate('YN','No'),
                                   type: RefdataValue.findByValue(subtype))

    if ( result.save(flush:true) ) {
      if ( consortium_org ) {
        def sc_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Package Consortia');
        def or = new OrgRole(org: consortium_org, sub:result, roleType:sc_role).save();
      }

      def new_package_link = new SubscriptionPackage(subscription:result, pkg:this).save();
      def live_issue_entitlement = RefdataCategory.lookupOrCreate('Entitlement Issue Status', 'Live');

      // Copy the tipps into the IEs
      log.debug("Copy tipp entries into new subscription");

      if ( add_entitlements ) {
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
      }
    }
    else {
      result.errors.each { err ->
        log.error("Problem creating new sub: ${err}");
      }
    }

    result
  }

  @Transient
  def getContentProvider() {
    def result = null;
    orgs.each { or ->
      if ( or?.roleType?.value=='Content Provider' )
        result = or.org;
    }
    result
  }

  @Transient
  def updateNominalPlatform() {
    def platforms = [:]
    tipps.each{ tipp ->
      if ( !platforms.keySet().contains(tipp.platform.id) ) {
        platforms[tipp.platform.id] = [count:1, platform:tipp.platform]
      }
      else {
        platforms[tipp.platform.id].count++
      }
    }

    def selected_platform = null;
    def largest = 0;
    platforms.values().each { pl ->
      log.debug("Processing ${pl}");
      if ( pl['count'] > largest ) {
        selected_platform = pl['platform']
      }
    }

    nominalPlatform = selected_platform
  }

  @Transient
  def addToSubscription(subscription, createEntitlements) {
    // Add this package to the specified subscription
    // Step 1 - Make sure this package is not already attached to the sub
    // Step 2 - Connect
    def new_pkg_sub = new SubscriptionPackage(subscription:subscription, pkg:this).save();
    // Step 3 - If createEntitlements ...

    if ( createEntitlements ) {
       def live_issue_entitlement = RefdataCategory.lookupOrCreate('Entitlement Issue Status', 'Live');
      tipps.each { tipp ->
        def new_ie = new IssueEntitlement(status: live_issue_entitlement,
                                          subscription: subscription,
                                          tipp: tipp,
                                          accessStart:tipp.accessStart,
                                          accessEnd:tipp.accessEnd,
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
    }

  }
 

  /**
   *  Tell the event notification service how this object is known to any registered notification
   *  systems.
   */
  @Transient
  def getNotificationEndpoints() {
    [
      [ service:'zendesk.forum', remoteid:this.forumId ],
      [ service:'announcements' ]
    ]
  }

  public String toString() {
    "Package ${name}";
  }

  @Transient
  public String getURL() {
    "${grailsApplication.config.SystemBaseURL}/packageDetails/show/${id}".toString();
  }

  @Transient
  def onChange = { oldMap,newMap ->

    log.debug("onChange")

    def changeNotificationService = grailsApplication.mainContext.getBean("changeNotificationService")

    //controlledProperties.each { cp ->
    //  if ( oldMap[cp] != newMap[cp] ) {
    //    changeNotificationService.notifyChangeEvent([
    //                                                 OID:"${this.class.name}:${this.id}",
    //                                                 event:'TitleInstance.propertyChange',
    //                                                 prop:cp, old:oldMap[cp], new:newMap[cp]
    //                                                ])
    //  }
    //}
  }

 @Transient
  def onSave = {

    log.debug("onSave")
    def changeNotificationService = grailsApplication.mainContext.getBean("changeNotificationService")

    changeNotificationService.notifyChangeEvent([
                                                 OID:"com.k_int.kbplus.Package:${id}",
                                                 event:'Package.created'
                                                ])

  }

  @Transient
  def notifyDependencies(changeDocument) {
    def changeNotificationService = grailsApplication.mainContext.getBean("changeNotificationService")
    if ( changeDocument.event=='Package.created' ) {
      changeNotificationService.broadcastEvent("com.k_int.kbplus.SystemObject:1", changeDocument);
    }
  }

  @Transient
  static def refdataFind(params) {
    def result = [];
    def ql = null;
    ql = Package.findAllByNameIlike("${params.q}%",params)

    if ( ql ) {
      ql.each { t ->
        result.add([id:"${t.class.name}:${t.id}",text:"${t.name} (${t.identifier})"])
      }
    }

    result
  }


  @Transient
  def toComparablePackage() {
    def result = [:]

    def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    result.packageName = this.name
    result.packageId = this.identifier

    result.tipps = []
    this.tipps.each { tip ->

      // Title.ID needs to be the global identifier, so we need to pull out the global id for each title
      // and use that.
      def title_id = tip.title.getIdentifierValue('uri')?:"uri://KBPlus/localhost/title/${tip.title.id}";

      def newtip = [
                     title: [
                       name:tip.title.title,
                       identifiers:[]
                     ],
                     titleId:title_id,
                     platform:tip.platform.name,
                     platformId:tip.platform.id,
                     coverage:[],
                     url:tip.hostPlatformURL,
                     identifiers:[]
                   ];

      // Need to format these dates using correct mask
      newtip.coverage.add([
                        startDate:tip.startDate ? sdf.format(tip.startDate) : '',
                        endDate:tip.endDate ? sdf.format(tip.endDate) : '',
                        startVolume:tip.startVolume,
                        endVolume:tip.endVolume,
                        startIssue:tip.startIssue,
                        endIssue:tip.endIssue,
                        coverageDepth:tip.coverageDepth,
                        coverageNote:tip.coverageNote
                      ]);

      tip.title.ids.each { id ->
        newtip.title.identifiers.add([namespace:id.identifier.ns.ns, value:id.identifier.value]);
      }

      result.tipps.add(newtip)
    }

    result.tipps.sort{it.titleId}
    println("Rec conversion for package returns object with title ${result.title} and ${result.tipps?.size()} tipps");

    result
  }

}

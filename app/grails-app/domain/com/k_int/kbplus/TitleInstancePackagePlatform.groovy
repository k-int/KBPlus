package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.hibernate.proxy.HibernateProxy


class TitleInstancePackagePlatform {

  // @Transient
  // def grailsApplication

  static auditable = true
  static def controlledProperties = ['status',
                                     'startDate',
                                     'startVolume',
                                     'startIssue',
                                     'endDate',
                                     'endVolume',
                                     'endIssue',
                                     'embargo',
                                     'coverageDepth',
                                     'coverageNote',
                                     'hostPlatformURL' ]


  Date accessStartDate
  Date accessEndDate
  Date startDate
  String rectype="so"
  String startVolume
  String startIssue
  Date endDate
  String endVolume
  String endIssue
  String embargo
  String coverageDepth
  String coverageNote
  String impId
  RefdataValue status
  RefdataValue option
  RefdataValue delayedOA
  RefdataValue hybridOA
  RefdataValue statusReason
  RefdataValue payment
  String hostPlatformURL
  Date coreStatusStart
  Date coreStatusEnd

  TitleInstancePackagePlatform derivedFrom

  static mappedBy = [ids: 'tipp', additionalPlatforms: 'tipp']
  static hasMany = [ids: IdentifierOccurrence, 
                    additionalPlatforms: PlatformTIPP]


  static belongsTo = [
    pkg:Package,
    platform:Platform,
    title:TitleInstance,
    sub:Subscription
  ]

  static mapping = {
                id column:'tipp_id'
           rectype column:'tipp_rectype'
           version column:'tipp_version'
               pkg column:'tipp_pkg_fk', index: 'tipp_idx'
          platform column:'tipp_plat_fk', index: 'tipp_idx'
             title column:'tipp_ti_fk', index: 'tipp_idx'
         startDate column:'tipp_start_date'
       startVolume column:'tipp_start_volume'
        startIssue column:'tipp_start_issue'
           endDate column:'tipp_end_date'
         endVolume column:'tipp_end_volume'
          endIssue column:'tipp_end_issue'
           embargo column:'tipp_embargo'
     coverageDepth column:'tipp_coverage_depth'
      coverageNote column:'tipp_coverage_note',type: 'text'
             impId column:'tipp_imp_id', index: 'tipp_imp_id_idx'
            status column:'tipp_status_rv_fk'
         delayedOA column:'tipp_delayedoa_rv_fk'
          hybridOA column:'tipp_hybridoa_rv_fk'
      statusReason column:'tipp_status_reason_rv_fk'
           payment column:'tipp_payment_rv_fk'
            option column:'tipp_option_rv_fk'
   hostPlatformURL column:'tipp_host_platform_url'
               sub column:'tipp_sub_fk'
       derivedFrom column:'tipp_derived_from'
   coreStatusStart column:'tipp_core_status_start_date'
     coreStatusEnd column:'tipp_core_status_end_date'
   accessStartDate column:'tipp_access_start_date'
     accessEndDate column:'tipp_access_end_date'
  }

  static constraints = {
    startDate(nullable:true, blank:true);
    startVolume(nullable:true, blank:true);
    startIssue(nullable:true, blank:true);
    endDate(nullable:true, blank:true);
    endVolume(nullable:true, blank:true);
    endIssue(nullable:true, blank:true);
    embargo(nullable:true, blank:true);
    coverageDepth(nullable:true, blank:true);
    coverageNote(nullable:true, blank:true);
    impId(nullable:true, blank:true);
    status(nullable:true, blank:false);
    delayedOA(nullable:true, blank:false);
    hybridOA(nullable:true, blank:false);
    statusReason(nullable:true, blank:false);
    payment(nullable:true, blank:false);
    option(nullable:true, blank:false);
    sub(nullable:true, blank:false);
    hostPlatformURL(nullable:true, blank:true);
    derivedFrom(nullable:true, blank:true);
    coreStatusStart(nullable:true, blank:true);
    coreStatusEnd(nullable:true, blank:true);
    accessStartDate(nullable:true, blank:true);
    accessEndDate(nullable:true, blank:true);
  }

  
  def getHostPlatform() {
    def result = null;
    additionalPlatforms.each { p ->
      if ( p.rel == 'host' ) {
        result = p.titleUrl
      }
    }
    result
  }

  @Transient
  def onChange = { oldMap,newMap ->

    log.debug("onChange")

    def changeNotificationService = ApplicationHolder.application.mainContext.getBean("changeNotificationService")

    def domain_class = ApplicationHolder.application.getArtefact('Domain','com.k_int.kbplus.TitleInstancePackagePlatform');

    controlledProperties.each { cp ->
      log.debug("checking ${cp}")
      if ( oldMap[cp] != newMap[cp] ) {
        def prop_info = domain_class.getPersistentProperty(cp)

        def oldLabel = stringify(oldMap[cp])
        def newLabel = stringify(newMap[cp])

        if ( prop_info.isAssociation() ) {
          log.debug("Convert object reference into OID");
          oldMap[cp]= oldMap[cp] != null ? "${deproxy(oldMap[cp]).class.name}:${oldMap[cp].id}" : null;
          newMap[cp]= newMap[cp] != null ? "${deproxy(newMap[cp]).class.name}:${newMap[cp].id}" : null;
        }

        log.debug("notify change event")
        changeNotificationService.notifyChangeEvent([
                                                     OID:"${this.class.name}:${this.id}",
                                                     event:'TitleInstancePackagePlatform.updated',
                                                     prop:cp, 
                                                     old:oldMap[cp], 
                                                     oldLabel:oldLabel,
                                                     new:newMap[cp],
                                                     newLabel:newLabel
                                                    ])
      }
    }
    log.debug("onChange completed")
  }

  private def stringify(obj) {
    def result = null
    if ( obj != null ) {
      if ( obj instanceof Date ) {
        def df = new java.text.SimpleDateFormat('yyyy-MM-dd');
        result = df.format(obj);
      }
      else {
        result = obj.toString()
      }
    }
    result
  }

  @Transient
  def onSave = {

    log.debug("onSave")
    def changeNotificationService = ApplicationHolder.application.mainContext.getBean("changeNotificationService")

    changeNotificationService.notifyChangeEvent([
                                                 OID:"${this.class.name}:${this.id}",
                                                 event:'TitleInstancePackagePlatform.added',
                                                 linkedTitle:title.title,
                                                 linkedTitleId:title.id,
                                                 linkedPackage:pkg.name,
                                                 linkedPlatform:platform.name
                                                ])
  }

  @Transient
  def onDelete = {

    log.debug("onDelete")
    def changeNotificationService = ApplicationHolder.application.mainContext.getBean("changeNotificationService")

    changeNotificationService.notifyChangeEvent([
                                                 OID:"${this.class.name}:${this.id}",
                                                 event:'TitleInstancePackagePlatform.deleted',
                                                 linkedTitle:title.title,
                                                 linkedTitleId:title.id,
                                                 linkedPackage:pkg.name,
                                                 linkedPlatform:platform.name
                                                ])
  }

  @Transient
  def notifyDependencies(changeDocument) {
    log.debug("notifyDependencies(${changeDocument})");

    def changeNotificationService = ApplicationHolder.application.mainContext.getBean("changeNotificationService")
    changeNotificationService.broadcastEvent("com.k_int.kbplus.Package:${pkg.id}", changeDocument);
    changeNotificationService.broadcastEvent("${this.class.name}:${this.id}", changeDocument);

    def deleted_tipp_status = RefdataCategory.lookupOrCreate('TIPP Status','Deleted');
    def deleted_tipp_status_oid = "com.k_int.kbplus.RefdataValue:${deleted_tipp_status.id}".toString()

    if ( ( changeDocument.event=='TitleInstancePackagePlatform.updated' ) && 
         ( changeDocument.prop == 'status' ) && 
         ( changeDocument.new == deleted_tipp_status_oid ) ) {

      log.debug("TIPP STATUS CHANGE:: Broadcast pending change to IEs based on this tipp new status: ${changeDocument.new}");

      def dep_ies = IssueEntitlement.findAllByTipp(this)
      dep_ies.each { dep_ie ->
        def sub = deproxy(dep_ie.subscription)
        log.debug("Notify dependent ie ${dep_ie.id} whos sub is ${sub.id} and subscriber is ${sub.getSubscriber()}");
        if ( sub.getSubscriber() == null ) {
          // SO - Ignore!
        }
        else {
          changeNotificationService.registerPendingChange('subscription',
                                                          dep_ie.subscription,
                                                          "The package entry for title \"${this.title.title}\" was deleted. Apply this change to remove the corresponding Issue Entitlement from this Subscription",
                                                          sub.getSubscriber(),
                                                          [
                                                            changeType:'TIPPDeleted',
                                                            tippId:"${this.class.name}:${this.id}",
                                                            subId:"${sub.id}"
                                                          ])
        }
      }
    }
    else if ( (changeDocument.event=='TitleInstancePackagePlatform.updated') && ( changeDocument.new != changeDocument.old ) ) {

      // Tipp Property Change Event.. notify any dependent IEs
      def dep_ies = IssueEntitlement.findAllByTipp(this)
      dep_ies.each { dep_ie ->
        def sub = deproxy(dep_ie.subscription)
        changeNotificationService.registerPendingChange('subscription',
                                                        dep_ie.subscription,
                                                        "Information about title <a href=\"${ApplicationHolder.application.config.SystemBaseURL}/titleDetails/show/${this.title.id}\">\"${this.title.title}\"</a> changed in package <a href=\"${ApplicationHolder.application.config.SystemBaseURL}/packageDetails/show/${id}\">${this.pkg.name}</a>. <b>${changeDocument.prop}</b> was updated from <b>\"${changeDocument.oldLabel}\"</b>(${changeDocument.old}) to <b>\"${changeDocument.newLabel}\"</b>(${changeDocument.new}). Accept this change to make the same update to your issue entitlement",
                                                        sub.getSubscriber(),
                                                        [
                                                          changeTarget:"com.k_int.kbplus.IssueEntitlement:${dep_ie.id}",
                                                          changeType:'PropertyChange',
                                                          changeDoc:changeDocument
                                                        ])

      }
    }

    //If the change is in a controller property, store it up and note it against subs
  }

  public static <T> T deproxy(def element) {
    if (element instanceof HibernateProxy) {
      return (T) ((HibernateProxy) element).getHibernateLazyInitializer().getImplementation();
    }
    return (T) element;
  }

  public Date getDerivedAccessStartDate() {
    accessStartDate ? accessStartDate : pkg.startDate
  }

  public Date getDerivedAccessEndDate() {
    accessStartDate ? accessStartDate : pkg.endDate
  }

  public RefdataValue getDerivedStatus() {
    return getDerivedStatus(new Date());
  }
  

  public RefdataValue getDerivedStatus(Date as_at) {
    def result = null
    // If StartDate <= as_at <= EndDate - Current
    // if Date < StartDate - Expected
    // if Date > EndDate - Expired
    def tipp_access_start_date = getDerivedAccessStartDate()
    def tipp_access_end_date = getDerivedAccessEndDate()
    if ( as_at < tipp_access_start_date ) {
      // expected
      result = RefdataCategory.lookupOrCreate('TIPP Status','Expected');
    }
    else if ( as_at > tipp_access_end_date ) {
      // expired
      result = RefdataCategory.lookupOrCreate('TIPP Status','Expired');
    }
    else {
      result = RefdataCategory.lookupOrCreate('TIPP Status','Current');
    }
    result
  }

  
}

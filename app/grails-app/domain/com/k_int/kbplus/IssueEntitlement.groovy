package com.k_int.kbplus
import javax.persistence.Transient

class IssueEntitlement implements Comparable {

  Date accessStartDate
  Date accessEndDate

  RefdataValue status
  Date startDate
  String startVolume
  String startIssue
  Date endDate
  String endVolume
  String endIssue
  String embargo
  String coverageDepth
  String coverageNote
  // boolean coreTitle = false
  String ieReason
  Date coreStatusStart
  Date coreStatusEnd
  RefdataValue coreStatus // core Status is really core Medium.. dont ask.
  RefdataValue medium

  static belongsTo = [subscription: Subscription, tipp: TitleInstancePackagePlatform]

  @Transient
  def comparisonProps = ['derivedAccessStartDate', 'derivedAccessEndDate',
'coverageNote','coverageDepth','embargo','startVolume','startIssue','startDate','endDate','endIssue','endVolume']

  int compareTo(obj) {
    tipp?.title?.title.compareTo(obj.tipp?.title?.title)
  }

  static mapping = {
                id column:'ie_id'
           version column:'ie_version'
            status column:'ie_status_rv_fk'
      subscription column:'ie_subscription_fk'
              tipp column:'ie_tipp_fk'
         startDate column:'ie_start_date'
       startVolume column:'ie_start_volume'
        startIssue column:'ie_start_issue'
           endDate column:'ie_end_date'
         endVolume column:'ie_end_volume'
          endIssue column:'ie_end_issue'
           embargo column:'ie_embargo'
     coverageDepth column:'ie_coverage_depth'
      coverageNote column:'ie_coverage_note',type: 'text'
         // coreTitle column:'ie_core_title'
          ieReason column:'ie_reason'
   // coreStatusStart comumn:'ie_core_status_start'
   //   coreStatusEnd comumn:'ie_core_status_end'
   //      coreStatus comumn:'ie_core_status_rv_fk'
   accessStartDate column:'ie_access_start_date'
     accessEndDate column:'ie_access_end_date'
            medium column:'ie_medium_rv_fk'
  }

  static constraints = {
    status(nullable:true, blank:false)
    subscription(nullable:true, blank:false)
    tipp(nullable:true, blank:false)
    startDate(nullable:true, blank:true);
    startVolume(nullable:true, blank:true);
    startIssue(nullable:true, blank:true);
    endDate(nullable:true, blank:true);
    endVolume(nullable:true, blank:true);
    endIssue(nullable:true, blank:true);
    embargo(nullable:true, blank:true);
    coverageDepth(nullable:true, blank:true);
    coverageNote(nullable:true, blank:true);
    // coreTitle(nullable:true, blank:true);
    ieReason(nullable:true, blank:true);
    coreStatusStart(nullable:true, blank:true);
    coreStatusEnd(nullable:true, blank:true);
    coreStatus(nullable:true, blank:true);
    accessStartDate(nullable:true, blank:true);
    accessEndDate(nullable:true, blank:true);
    medium(nullable:true, blank:true);
  }

  public Date getDerivedAccessStartDate() {
    accessStartDate ? accessStartDate : subscription.derivedAccessStartDate
  }

  public Date getDerivedAccessEndDate() {
    accessEndDate ? accessEndDate : subscription.derivedAccessEndDate
  }

  public RefdataValue getAvailabilityStatus() {
    return getAvailabilityStatus(new Date());
  }

  @Transient
  public int compare(IssueEntitlement ieB){
    if(ieB == null) return -1;

    def noChange =true 
    comparisonProps.each{ noChange &= this."${it}" == ieB."${it}" }

    if(noChange) return 0;
    return 1;
  }

  public RefdataValue getAvailabilityStatus(Date as_at) {
    def result = null
    // If StartDate <= as_at <= EndDate - Current
    // if Date < StartDate - Expected
    // if Date > EndDate - Expired
    def ie_access_start_date = getDerivedAccessStartDate()
    def ie_access_end_date = getDerivedAccessEndDate()

    if ( ( ie_access_start_date == null ) || ( ie_access_end_date == null ) ) {
      result = RefdataCategory.lookupOrCreate('IE Access Status','ERROR - No Subscription Start and/or End Date');
    }
    else if ( ( accessEndDate == null ) && ( as_at > ie_access_end_date ) ) {
      result = RefdataCategory.lookupOrCreate('IE Access Status','Current(*)');
    }
    else if ( as_at < ie_access_start_date ) {
      // expected
      result = RefdataCategory.lookupOrCreate('IE Access Status','Expected');
    }
    else if ( as_at > ie_access_end_date ) {
      // expired
      result = RefdataCategory.lookupOrCreate('IE Access Status','Expired');
    }
    else {
      result = RefdataCategory.lookupOrCreate('IE Access Status','Current');
    }
    result
  }

  @Transient
  def getTIP(){
    def inst = subscription?.getSubscriber()
    def title = tipp?.title
    def provider = tipp?.pkg?.getContentProvider()
    if ( inst && title && provider ) {
      def tip = TitleInstitutionProvider.findByTitleAndInstitutionAndprovider(title, inst, provider)
      if(!tip){
        tip = new TitleInstitutionProvider(title:title,institution:inst,provider:provider)
        tip.save(flush:true)
      }
      return tip
    }
    return null
  }
  
  @Transient
  def coreStatusOn(as_at) {
    // Use the new core system to determine if this title really is core
    def result = false
    def tip = getTIP()
    if ( tip ) {
      if ( ( tip.startDate < as_at ) && ( ( tip.endDate == null) || ( tip.endDate > as_at ) ) ) {
        result = true
      }
    }
    result
  }
  
  @Transient
  def extendCoreDates(startDate, endDate){
    def tip = getTIP()
      tip?.extendCoreExtent(startDate,endDate)
  }

  @Transient
  static def refdataFind(params) {

    def result = [];
    def hqlParams = []
    def hqlString = "select ie from IssueEntitlement as ie"

    if ( params.subFilter ) {
      hqlString += ' where ie.subscription.id = ?'
      hqlParams.add(params.long('subFilter'))
    }

    def results = IssueEntitlement.executeQuery(hqlString,hqlParams)

    results?.each { t ->
      def resultText = t.tipp.title.title
      result.add([id:"${t.class.name}:${t.id}",text:resultText])
    }

    result

  }

}

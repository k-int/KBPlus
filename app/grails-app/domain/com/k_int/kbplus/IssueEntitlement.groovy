package com.k_int.kbplus

class IssueEntitlement implements Comparable {

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
  boolean coreTitle = false
  String ieReason

  static belongsTo = [subscription: Subscription,
                      tipp: TitleInstancePackagePlatform]


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
         coreTitle column:'ie_core_title'
          ieReason column:'ie_reason'
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
    coreTitle(nullable:true, blank:true);
    ieReason(nullable:true, blank:true);
  }

}

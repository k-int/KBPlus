package com.k_int.kbplus

class TitleInstancePackagePlatform {

  String startDate
  String startVolume
  String startIssue
  String endDate
  String endVolume
  String endIssue
  String embargo
  String coverageDepth
  String coverageNote
  String impId
  ReferenceValue status
  ReferenceValue option


  static belongsTo = [
    pkg:Package,
    platform:Platform,
    title:TitleInstance
  ]

  static mapping = {
               id column:'tipp_id'
          version column:'tipp_version'
              pkg column:'tipp_pkg_fk',index:'tipp_pkg_fk_idx'
         platform column:'tipp_plat_fk',index:'tipp_plat_fk_idx'
            title column:'tipp_ti_fk',index:'tipp_ti_fk_idx'
        startDate column:'tipp_start_date'
      startVolume column:'tipp_start_volume'
       startIssue column:'tipp_start_issue'
          endDate column:'tipp_end_date'
        endVolume column:'tipp_end_volume'
         endIssue column:'tipp_end_issue'
          embargo column:'tipp_embargo'
    coverageDepth column:'tipp_coverage_depth'
     coverageNote column:'tipp_coverage_note',type: 'text'
            impId column:'tipp_imp_id'
           status column:'tipp_status_rv_fk'
           option column:'tipp_option_rv_fk'
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
    option(nullable:true, blank:false);
  }
}

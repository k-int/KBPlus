package com.k_int.kbplus

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder


class TitleInstancePackagePlatform {

  static auditable = true

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
            option column:'tipp_option_rv_fk'
   hostPlatformURL column:'tipp_host_platform_url'
               sub column:'tipp_sub_fk'
       derivedFrom column:'tipp_derived_from'
   coreStatusStart column:'tipp_core_status_start_date'
     coreStatusEnd column:'tipp_core_status_end_date'
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
    sub(nullable:true, blank:false);
    hostPlatformURL(nullable:true, blank:true);
    derivedFrom(nullable:true, blank:true);
    coreStatusStart(nullable:true, blank:true);
    coreStatusEnd(nullable:true, blank:true);
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

    def controlledProperties = ['startDate', 
                                'startVolume', 
                                'startIssue', 
                                'endDate', 
                                'endVolume', 
                                'endIssue', 
                                'embargo', 
                                'coverageDepth', 
                                'coverageNote',
                                'status' ]

    controlledProperties.each { cp ->
      if ( oldMap[cp] != newMap[cp] ) {
        changeNotificationService.notifyChangeEvent([
                                                     OID:"${this.class.name}:${this.id}",
                                                     event:'TitleInstancePackagePlatform.updated',
                                                     prop:cp, old:oldMap[cp], new:newMap[cp]
                                                    ])
      }
    }
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
  }

}

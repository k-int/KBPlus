package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import grails.plugins.springsecurity.Secured
import grails.converters.*
import au.com.bytecode.opencsv.CSVReader
import com.k_int.custprops.PropertyDefinition

class AdminController {

  def springSecurityService
  def dataloadService
  def zenDeskSyncService
  def juspSyncService
  def globalSourceSyncService
  def messageService
  def changeNotificationService
  def enrichmentService

  def docstoreService


  static boolean ftupdate_running = false

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def index() { }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def manageAffiliationRequests() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)

    // List all pending requests...
    result.pendingRequests = UserOrg.findAllByStatus(0, [sort:'dateRequested'])
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def managePropertyDefinitions() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)

    result.definitions = PropertyDefinition.findAll()
    result.definitions.sort{it.name}
    result.error = flash.error
    result.newProp = flash.newProp
    log.debug("ERROR : ${result.error} newProp:${result.newProp}")
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def actionAffiliationRequest() {
    log.debug("actionMembershipRequest");
    def req = UserOrg.get(params.req);
    def user = User.get(springSecurityService.principal.id)
    if ( req != null ) {
      switch(params.act) {
        case 'approve':
          req.status = 1;
          break;
        case 'deny':
          req.status = 2;
          break;
        default:
          log.error("FLASH UNKNOWN CODE");
          break;
      }
      // req.actionedBy = user
      req.dateActioned = System.currentTimeMillis();
      req.save(flush:true);
    }
    else {
      log.error("FLASH");
    }
    redirect(action: "manageAffiliationRequests")
  }


  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def showAffiliations() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.users = User.list()

    withFormat {
      html {
        render(view:'showAffiliations',model:result)
      }
      json {
        def r2 = []
        result.users.each { u ->
          def row = [:]
          row.username = u.username
          row.display = u.display
          row.instname = u.instname
          row.instcode = u.instcode
          row.email = u.email
          row.shibbScope = u.shibbScope
          row.enabled = u.enabled
          row.accountExpired = u.accountExpired
          row.accountLocked = u.accountLocked
          row.passwordExpired = u.passwordExpired
          row.affiliations = []
          u.affiliations.each { ua ->
            row.affiliations.add( [org: ua.org.shortcode, status: ua.status, formalRole:formalRole?.authority] )
          }
          r2.add(row)
        }
        render r2 as JSON
      }
    }
  }
  
  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def allNotes() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    def sc = DocContext.createCriteria()
    result.alerts = sc.list {
      alert {
        gt('sharingLevel', -1)
      }
    }

    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def dataCleanse() {
    // Sets nominal platform
    dataloadService.dataCleanse()
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def titleAugment() {
    // Sets nominal platform
    dataloadService.titleAugment()
  }


  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def licenseLink() {
    if ( ( params.sub_identifier ) && ( params.lic_reference.length() > 0 ) ) {
    }
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def settings() {
    def result = [:]
    result.settings = Setting.list();
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def toggleBoolSetting() {
    def result = [:]
    def s = Setting.findByName(params.setting);
    if ( s ) {
      if ( s.tp == 1 ) {
        if ( s.value == 'true' )
          s.value = 'false'
        else
          s.value = 'true'
      }
      s.save(flush:true)
    }
    redirect controller: 'admin', action:'settings'
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def fullReset() {

    if ( ftupdate_running == false ) {
      try {
        ftupdate_running = true
        new EventLog(event:'kbplus.fullReset',message:'Full Reset',tstp:new Date(System.currentTimeMillis())).save(flush:true)
        log.debug("Delete all existing FT Control entries");
        FTControl.withTransaction {
          FTControl.executeUpdate("delete FTControl c");
        }

        log.debug("Clear ES");
        dataloadService.clearDownAndInitES();
  
        log.debug("manual start full text index");
        dataloadService.updateFTIndexes();
      }
      finally {
        ftupdate_running = false
        log.debug("fullReset complete..");
      }
    }
    else {
      log.debug("FT update already running");
    }

    log.debug("redirecting to home...");
    redirect(controller:'home')

  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def forumSync() {
    redirect(controller:'home')
    zenDeskSyncService.doSync()
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def juspSync() {
    log.debug("juspSync()");
    juspSyncService.doSync()
    redirect(controller:'home')
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def globalSync() {
    log.debug("start global sync...");
    globalSourceSyncService.runAllActiveSyncTasks()
    log.debug("done global sync...");
    redirect(controller:'home')
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def manageGlobalSources() {
    def result=[:]
    log.debug("manageGlobalSources...");
    result.sources = GlobalRecordSource.list()
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def newGlobalSource() {
    def result=[:]
    log.debug("manageGlobalSources...");
    result.newSource = GlobalRecordSource.findByIdentifier(params.identifier) ?: new GlobalRecordSource(
                                                                                         identifier:params.identifier,
                                                                                         name:params.name,
                                                                                         type:params.type,
                                                                                         haveUpTo:null,
                                                                                         uri:params.uri,
                                                                                         listPrefix:params.listPrefix,
                                                                                         fullPrefix:params.fullPrefix,
                                                                                         principal:params.principal,
                                                                                         credentials:params.credentials,
                                                                                         rectype:params.int('rectype'));
    result.newSource.save();

    redirect action:'manageGlobalSources'
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def manageContentItems() {
    def result=[:]

    result.items = ContentItem.list()

    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def newContentItem() {
    def result=[:]
    if ( ( params.key != null ) && ( params.content != null ) && ( params.key.length() > 0 ) && ( params.content.length() > 0 ) ) {

      def locale = ( ( params.locale != null ) && ( params.locale.length() > 0 ) ) ? params.locale : ''

      if ( ContentItem.findByKeyAndLocale(params.key,locale) != null ) {
        flash.message = 'Content item already exists'
      }
      else {
        ContentItem.lookupOrCreate(params.key, locale, params.content)
      }
    }

    redirect(action:'manageContentItems')

    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def editContentItem() {
    def result=[:]
    def idparts = params.id?.split(':')
    if ( idparts.length > 0 ) {
      def key = idparts[0]
      def locale = idparts.length > 1 ? idparts[1] : ''

      def contentItem = ContentItem.findByKeyAndLocale(key,locale)
      if ( contentItem != null ) {
        result.contentItem = contentItem
      }
      else {
        flash.message="Unable to locate content item for key ${idparts}"
        redirect(action:'manageContentItems');
      }
      if ( request.method.equalsIgnoreCase("post")) {
        contentItem.content = params.content
        contentItem.save(flush:true)
        messageService.update(key,locale)
        redirect(action:'manageContentItems');
      }
    }
    else {
      flash.message="Unable to parse content item id ${params.id} - ${idparts}"
      redirect(action:'manageContentItems');
    }

    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def forceSendNotifications() {
    changeNotificationService.aggregateAndNotifyChanges()
    redirect(controller:'home')
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def titleMerge() {

    log.debug(params)

    def result=[:]

    if ( ( params.titleIdToDeprecate != null ) &&
         ( params.titleIdToDeprecate.length() > 0 ) &&
         ( params.correctTitleId != null ) &&
         ( params.correctTitleId.length() > 0 ) ) {
      result.title_to_deprecate = TitleInstance.get(params.titleIdToDeprecate)
      result.correct_title = TitleInstance.get(params.correctTitleId)

      if ( params.MergeButton=='Go' ) {
        log.debug("Execute title merge....");
        result.title_to_deprecate.tipps.each { tipp ->
          log.debug("Update tipp... ${tipp.id}");
          tipp.title = result.correct_title
          tipp.save()
        }
        redirect(action:'titleMerge',params:[titleIdToDeprecate:params.titleIdToDeprecate, correctTitleId:params.correctTitleId])
      }

      result.title_to_deprecate.status = RefdataCategory.lookupOrCreate("TitleInstanceStatus", "Deleted")
      result.title_to_deprecate.save(flush:true);
    }
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def orgsExport() {
    response.setHeader("Content-disposition", "attachment; filename=orgsExport.csv")
    response.contentType = "text/csv"
    def out = response.outputStream
    out << "org.name,sector,consortia,id.jusplogin,id.JC,id.Ringold,id.UKAMF,iprange\n"
    Org.list().each { org ->
      def consortium = org.outgoingCombos.find{it.type.value=='Consortium'}.collect{it.toOrg.name}.join(':')

      out << "\"${org.name}\",\"${org.sector?:''}\",\"${consortium}\",\"${org.getIdentifierByType('jusplogin')?.value?:''}\",\"${org.getIdentifierByType('JC')?.value?:''}\",\"${org.getIdentifierByType('Ringold')?.value?:''}\",\"${org.getIdentifierByType('UKAMF')?.value?:''}\",\"${org.ipRange?:''}\"\n"
    }
    out.close()
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def orgsImport() {

    if ( request.method=="POST" ) {
      def upload_mime_type = request.getFile("orgs_file")?.contentType
      def upload_filename = request.getFile("orgs_file")?.getOriginalFilename()
      def input_stream = request.getFile("orgs_file")?.inputStream

      CSVReader r = new CSVReader( new InputStreamReader(input_stream, java.nio.charset.Charset.forName('UTF-8') ) )
      String[] nl;
      def first = true
      while ((nl = r.readNext()) != null) {
        if ( first ) {
          first = false; // Skip header
        }
        else {
          def candidate_identifiers = [
            'jusplogin':nl[3],
            'JC':nl[4],
            'Ringold':nl[5],
            'UKAMF':nl[6],
          ]
          log.debug("Load ${nl[0]}, ${nl[1]}, ${nl[2]} ${candidate_identifiers} ${nl[7]}");
          Org.lookupOrCreate(nl[0],
                             nl[1],
                             nl[2],
                             candidate_identifiers,
                             nl[7])
        }
      }
    }
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def docstoreMigrate() {
    docstoreService.migrateToDb()
    redirect(controller:'home')
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def triggerHousekeeping() {
    log.debug("trigggerHousekeeping()");
    enrichmentService.initiateHousekeeping()
    redirect(controller:'home')
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def deleteGlobalSource() {
    GlobalRecordSource.removeSource(params.long('id'));
    redirect(action:'manageGlobalSources')
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def initiateCoreMigration() {
    log.debug("initiateCoreMigration...");
    enrichmentService.initiateCoreMigration()
    redirect(controller:'home')
  }

 
}

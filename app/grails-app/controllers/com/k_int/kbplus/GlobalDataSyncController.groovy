package com.k_int.kbplus


import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder



class GlobalDataSyncController {

  def springSecurityService
  def globalSourceSyncService
  def genericOIDService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def result = [:]

    result.user = request.user
    result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;

    def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    def base_qry = " from GlobalRecordInfo as r where lower(r.name) like ? and r.source.rectype = 0 "

    def qry_params = []
    if ( params.q?.length() > 0 ) {
      qry_params.add("%${params.q.trim().toLowerCase()}%");
    }
    else {
      qry_params.add("%");
    }

    result.globalItemTotal = Subscription.executeQuery("select count(r) "+base_qry, qry_params )[0]
    result.items = Subscription.executeQuery("select r ${base_qry}", qry_params, [max:result.max, offset:result.offset]);
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def newCleanTracker() { 
    log.debug("params:"+params)
    def result = [:]
    result.item = GlobalRecordInfo.get(params.id)

    log.debug("Calling diff....");
    result.impact = globalSourceSyncService.diff(null, result.item)

    result.type='new'
    render view:'reviewTracker', model:result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def selectLocalPackage() { 
    log.debug("params:"+params)
    def result = [:]
    result.item = GlobalRecordInfo.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def cancelTracking() {
    log.debug("cancelTracking: " + params)
    GlobalRecordTracker.get(params.trackerId).delete()

    redirect(action:'index', params:[q:params.itemName])
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def buildMergeTracker() { 
    log.debug("params:"+params)
    def result = [:]
    result.type='existing'
    result.item = GlobalRecordInfo.get(params.id)
    result.localPkgOID = params.localPkg
    result.localPkg = genericOIDService.resolveOID(params.localPkg)

    log.debug("Calling diff....");
    result.impact = globalSourceSyncService.diff(result.localPkg, result.item)

    render view:'reviewTracker', model:result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def createTracker() {
    log.debug("params:"+params)
    def result = [:]

    result.item = GlobalRecordInfo.get(params.id)
    def new_tracker_id = java.util.UUID.randomUUID().toString()

    if ( params.synctype != null ) {
      // new tracker and redirect back to list page

      switch ( params.synctype ) {
        case 'new':
          log.debug("merge remote package with new local package...");
          def grt = new GlobalRecordTracker(
                                            owner:result.item, 
                                            identifier:new_tracker_id, 
                                            name:params.newPackageName,  
                                            autoAcceptTippAddition:params.autoAcceptTippUpdate=='on'?true:false,
                                            autoAcceptTippDelete:params.autoAcceptTippDelete=='on'?true:false,
                                            autoAcceptTippUpdate:params.autoAcceptTippAddition=='on'?true:false,
                                            autoAcceptPackageUpdate:params.autoAcceptPackageChange=='on'?true:false)
          if ( grt.save() ) {
            globalSourceSyncService.initialiseTracker(grt);
          }
          else {
            log.error(grt.errors)
          }
          redirect(action:'index',params:[q:result.item.name])
          break;
        case 'existing':
          log.debug("merge remote package with existing local package...");
          def grt = new GlobalRecordTracker( 
                                            owner:result.item, 
                                            identifier:new_tracker_id, 
                                            name:result.item.name,
                                            localOid:params.localPkg,
                                            autoAcceptTippAddition:params.autoAcceptTippUpdate=='on'?true:false,
                                            autoAcceptTippDelete:params.autoAcceptTippDelete=='on'?true:false,
                                            autoAcceptTippUpdate:params.autoAcceptTippAddition=='on'?true:false,
                                            autoAcceptPackageUpdate:params.autoAcceptPackageChange=='on'?true:false)
          if ( grt.save() ) {
            globalSourceSyncService.initialiseTracker(grt, params.localPkg);
          }
          else {
            log.error(grt.errors)
          }
          redirect(action:'index',params:[q:result.item.name])
          break;
        default:
          log.error("Unhandled package tracking type ${params.synctype}");
          break;
      }
    }

    result
  }

}

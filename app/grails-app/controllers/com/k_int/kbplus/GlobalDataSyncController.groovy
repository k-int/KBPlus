package com.k_int.kbplus


import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder



class GlobalDataSyncController {

  def springSecurityService
  def globalSourceSyncService


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def result = [:]

    result.user = request.user
    result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;

    def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    def base_qry = " from GlobalRecordInfo as r where lower(r.name) like ? ) "

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
  def newTracker() { 
    log.debug("params:"+params)
    def result = [:]

    result.item = GlobalRecordInfo.get(params.id)

    if ( ( params.trackerName != null ) && ( params.trackerId != null ) ) {
      // new tracker and redirect back to list page

      // Check that the new tracker ID will be valid

      def valid = true
      if ( valid ) {
        log.debug("redirecting...");
        def grt = new GlobalRecordTracker(owner:result.item, identifier:params.trackerId, name:params.trackerName)
        if ( grt.save() ) {
          globalSourceSyncService.initialiseTracker(grt);
        }
        else {
          log.error(grt.errors)
        }
        redirect(action:'index',params:[q:result.item.name])
      }
      else {
      }
    }

    result
  }
}

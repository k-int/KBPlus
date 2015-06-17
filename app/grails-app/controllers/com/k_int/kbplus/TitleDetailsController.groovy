package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.apache.log4j.*
import java.text.SimpleDateFormat
import com.k_int.kbplus.*;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class TitleDetailsController {

  def springSecurityService
  def ESSearchService


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def findTitleMatches() { 
    // find all titles by n_title proposedTitle
    def result=[:]
    if ( params.proposedTitle ) {
      // def proposed_title_key = com.k_int.kbplus.TitleInstance.generateKeyTitle(params.proposedTitle)
      // result.titleMatches=com.k_int.kbplus.TitleInstance.findAllByKeyTitle(proposed_title_key)
      def normalised_title = com.k_int.kbplus.TitleInstance.generateNormTitle(params.proposedTitle)
      result.titleMatches=com.k_int.kbplus.TitleInstance.findAllByNormTitleLike("${normalised_title}%")
    }
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def createTitle() {
    log.debug("Create new title for ${params.title}");
    def new_title = new TitleInstance(title:params.title, impId:java.util.UUID.randomUUID().toString())
    
    if ( new_title.save(flush:true) ) {
      log.debug("New title id is ${new_title.id}");
      redirect ( action:'edit', id:new_title.id);
    }
    else {
      log.error("Problem creating title: ${new_title.errors}");
      flash.message = "Problem creating title: ${new_title.errors}"
      redirect ( action:'findTitleMatches' )
    }
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def edit() {
    def result = [:]

    if ( SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN') )
      result.editable=true
    else
      result.editable=false

    result.ti = TitleInstance.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def show() {
    def result = [:]

    if ( SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN') )
      result.editable=true
    else
      result.editable=false

    result.ti = TitleInstance.get(params.id)

    result.titleHistory = TitleHistoryEvent.executeQuery("select distinct thep.event from TitleHistoryEventParticipant as thep where thep.participant = ?",[result.ti]);

    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def batchUpdate() {
    log.debug(params);
    def formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")
    def user = User.get(springSecurityService.principal.id)

      params.each { p ->
      if ( p.key.startsWith('_bulkflag.')&& (p.value=='on'))  {
        def tipp_id_to_edit = p.key.substring(10);
        def tipp_to_bulk_edit = TitleInstancePackagePlatform.get(tipp_id_to_edit)
        boolean changed = false

        if ( tipp_to_bulk_edit != null ) {
            def bulk_fields = [
                    [ formProp:'start_date', domainClassProp:'startDate', type:'date'],
                    [ formProp:'start_volume', domainClassProp:'startVolume'],
                    [ formProp:'start_issue', domainClassProp:'startIssue'],
                    [ formProp:'end_date', domainClassProp:'endDate', type:'date'],
                    [ formProp:'end_volume', domainClassProp:'endVolume'],
                    [ formProp:'end_issue', domainClassProp:'endIssue'],
                    [ formProp:'coverage_depth', domainClassProp:'coverageDepth'],
                    [ formProp:'coverage_note', domainClassProp:'coverageNote'],
                    [ formProp:'hostPlatformURL', domainClassProp:'hostPlatformURL']
            ]

            bulk_fields.each { bulk_field_defn ->
                if ( params["clear_${bulk_field_defn.formProp}"] == 'on' ) {
                    log.debug("Request to clear field ${bulk_field_defn.formProp}");
                    tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = null
                    changed = true
                }
                else {
                    def proposed_value = params['bulk_'+bulk_field_defn.formProp]
                    if ( ( proposed_value != null ) && ( proposed_value.length() > 0 ) ) {
                        log.debug("Set field ${bulk_field_defn.formProp} to ${proposed_value}");
                        if ( bulk_field_defn.type == 'date' ) {
                            tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = formatter.parse(proposed_value)
                        }
                        else {
                            tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = proposed_value
                        }
                        changed = true
                    }
                }
            }
          if (changed)
             tipp_to_bulk_edit.save();
        }
      }
    }

    redirect(controller:'titleDetails', action:'show', id:params.id);
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {

    log.debug("titleSearch : ${params}");

    def result=[:]

    if (springSecurityService.isLoggedIn()) {
      params.rectype = "Title" // Tells ESSearchService what to look for
      result.user = springSecurityService.getCurrentUser()
      params.max = result.user.defaultPageSize


      if(params.search.equals("yes")){
        //when searching make sure results start from first page
        params.offset = 0
        params.search = ""
      }
      params.sort = "title"
      if(params.q == "") params.remove('q');
      result =  ESSearchService.search(params)   
    }

    if ( SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN') )
      result.editable=true;
    else
      result.editable=false;


    log.debug(result);

    result  
   
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def history() {
    def result = [:]
    def exporting = params.format == 'csv' ? true : false

    if ( exporting ) {
      result.max = 9999999
      params.max = 9999999
      result.offset = 0
    }
    else {
      def user = User.get(springSecurityService.principal.id)
      result.max = params.max ? Integer.parseInt(params.max) : user.defaultPageSize
      params.max = result.max
      result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
    }

    result.titleInstance = TitleInstance.get(params.id)
    def base_query = 'from org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent as e where ( e.className = :instCls and e.persistedObjectId = :instId )'

    def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]

    def query_params = [ instCls:'com.k_int.kbplus.TitleInstance', instId:params.id]

    log.debug("base_query: ${base_query}, params:${query_params}, limits:${limits}");

    result.historyLines = org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent.executeQuery('select e '+base_query+' order by e.lastUpdated desc', query_params, limits);
    result.num_hl = org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent.executeQuery('select count(e) '+base_query, query_params)[0];
    result.formattedHistoryLines = []


    result.historyLines.each { hl ->

        def line_to_add = [:]
        def linetype = null

        switch(hl.className) {
          case 'com.k_int.kbplus.TitleInstance':
            def instance_obj = TitleInstance.get(hl.persistedObjectId);
            line_to_add = [ link: createLink(controller:'titleInstance', action: 'show', id:hl.persistedObjectId),
                            name: instance_obj.title,
                            lastUpdated: hl.lastUpdated,
                            propertyName: hl.propertyName,
                            actor: User.findByUsername(hl.actor),
                            oldValue: hl.oldValue,
                            newValue: hl.newValue
                          ]
            linetype = 'TitleInstance'
            break;
        }
        switch ( hl.eventName ) {
          case 'INSERT':
            line_to_add.eventName= "New ${linetype}"
            break;
          case 'UPDATE':
            line_to_add.eventName= "Updated ${linetype}"
            break;
          case 'DELETE':
            line_to_add.eventName= "Deleted ${linetype}"
            break;
          default:
            line_to_add.eventName= "Unknown ${linetype}"
            break;
        }
        result.formattedHistoryLines.add(line_to_add);
    }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def availability() {
    def result = [:]
    result.ti = TitleInstance.get(params.id)
    result.availability = IssueEntitlement.executeQuery("select ie from IssueEntitlement as ie where ie.tipp.title = ?",[result.ti]);

    result
  }

  @Secured(['ROLE_ADMIN', 'KBPLUS_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def dmIndex() {

    log.debug("dmIndex ${params}");

    def user = User.get(springSecurityService.principal.id)

    def result = [:]
    def qry_params = []
    def base_qry = "from TitleInstance as t"

    result.max = params.max ? Integer.parseInt(params.max) : user.defaultPageSize
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    if ( params.status ) {
      base_qry += ' where t.status.value = ?'
      qry_params.add(params.status);
    }

    log.debug("DM Title Query: ${base_qry}, ${params}");
    result.totalHits = com.k_int.kbplus.TitleInstance.executeQuery("select count(t) "+base_qry, qry_params, [max:result.max, offset:result.offset])[0];
    result.hits = com.k_int.kbplus.TitleInstance.executeQuery("select t "+base_qry, qry_params, [max:result.max, offset:result.offset]);
    result
  }

}

package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class SubscriptionDetailsController {

  def springSecurityService
  def docstoreService
  def ESWrapperService
  def gazetteerService
  def alertsService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    log.debug("subscriptionDetails id:${params.id} format=${response.format}");
    def result = [:]

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : ( response.format == "csv" ? 10000 : 10 );
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    log.debug("max = ${result.max}");
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)

    if ( ! result.subscriptionInstance.hasPerm("view",result.user) ) {
      render status: 401
      return
    }

    // result.institution = Org.findByShortcode(params.shortcode)
    result.institution = result.subscriptionInstance.subscriber
    if ( result.institution ) {
      result.subscriber_shortcode = result.institution.shortcode
      result.institutional_usage_identifier = result.institution.getIdentifierByType('JUSP');
    }

    if ( result.subscriptionInstance.isEditableBy(result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    def base_qry = null;

    def qry_params = [result.subscriptionInstance]

    if ( params.filter ) {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? and ( ie.status.value != 'Deleted' ) and ( ( lower(ie.tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = ie.tipp.title.id and io.identifier.value like ? ) ) )"
      qry_params.add("%${params.filter.trim().toLowerCase()}%")
      qry_params.add("%${params.filter}%")
    }
    else {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? and ( ie.status.value != 'Deleted' ) "
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += "order by ie.${params.sort} ${params.order} "
    }
    result.num_sub_rows = IssueEntitlement.executeQuery("select count(ie) "+base_qry, qry_params )[0]

    result.entitlements = IssueEntitlement.executeQuery("select ie "+base_qry, qry_params, [max:result.max, offset:result.offset]);

    def formatter = new java.text.SimpleDateFormat("yyyy/MM/dd")
    log.debug("subscriptionInstance returning... ${result.num_sub_rows} rows ");
    withFormat {
      html result
      csv {
         def jc_id = result.subscriptionInstance.getSubscriber()?.getIdentifierByType('JC')?.value

         response.setHeader("Content-disposition", "attachment; filename=${result.subscriptionInstance.identifier}.csv")
         response.contentType = "text/csv"
         def out = response.outputStream
         out.withWriter { writer ->
           def tsdate = result.subscriptionInstance.startDate ? formatter.format(result.subscriptionInstance.startDate) : ''
           def tedate = result.subscriptionInstance.endDate ? formatter.format(result.subscriptionInstance.endDate) : ''
           if ( ( params.omitHeader == null ) || ( params.omitHeader != 'Y' ) ) {
             writer.write("FileType,SpecVersion,JC_ID,TermStartDate,TermEndDate,SubURI,SystemIdentifier\n")
             writer.write("${result.subscriptionInstance.type.value},\"2.0\",${jc_id?:''},${tsdate},${tedate},\"uri://kbplus/sub/${result.subscriptionInstance.identifier}\",${result.subscriptionInstance.impId}\n")
           }

           // Output the body text
           // writer.write("publication_title,print_identifier,online_identifier,date_first_issue_subscribed,num_first_vol_subscribed,num_first_issue_subscribed,date_last_issue_subscribed,num_last_vol_subscribed,num_last_issue_subscribed,embargo_info,title_url,first_author,title_id,coverage_note,coverage_depth,publisher_name\n");
           writer.write("publication_title,print_identifier,online_identifier,date_first_issue_online,num_first_vol_online,num_first_issue_online,date_last_issue_online,num_last_vol_online,num_last_issue_online,title_url,first_author,title_id,embargo_info,coverage_depth,coverage_notes,publisher_name\n");

           result.entitlements.each { e ->

             def start_date = e.startDate ? formatter.format(e.startDate) : '';
             def end_date = e.endDate ? formatter.format(e.endDate) : '';
             def title_doi = (e.tipp?.title?.getIdentifierValue('DOI'))?:''
             def publisher = e.tipp?.title?.publisher

             writer.write("\"${e.tipp.title.title}\",\"${e.tipp?.title?.getIdentifierValue('ISSN')?:''}\",\"${e.tipp?.title?.getIdentifierValue('eISSN')?:''}\",${start_date},${e.startVolume?:''},${e.startIssue?:''},${end_date},${e.endVolume?:''},${e.endIssue?:''},\"${e.tipp?.hostPlatformURL?:''}\",,\"${title_doi}\",\"${e.embargo?:''}\",\"${e.tipp?.coverageDepth?:''}\",\"${e.tipp?.coverageNote?:''}\",\"${publisher?.name?:''}\"\n");
           }
           writer.flush()
           writer.close()
         }
         out.close()
      }
      json {
         def jc_id = result.subscriptionInstance.getSubscriber()?.getIdentifierByType('JC')?.value
         def response = [:]
         response.header = [:]
         response.entitlements = []

         response.header.type = result.subscriptionInstance.type.value
         response.header.version = "2.0"
         response.header.jcid = jc_id
         response.header.url = "uri://kbplus/sub/${result.subscriptionInstance.identifier}"

         result.entitlements.each { e ->
           def start_date = e.startDate ? formatter.format(e.startDate) : '';
           def end_date = e.endDate ? formatter.format(e.endDate) : '';
           def title_doi = (e.tipp?.title?.getIdentifierValue('DOI'))?:''
           def publisher = e.tipp?.title?.publisher

           def entitlement = [:]
           entitlement.title=e.tipp.title.title
           entitlement.issn=e.tipp?.title?.getIdentifierValue('ISSN')
           entitlement.eissn=e.tipp?.title?.getIdentifierValue('eISSN')
           entitlement.startDate=start_date;
           entitlement.endDate=end_date;
           entitlement.startVolume=e.startVolume?:''
           entitlement.endVolume=e.endVolume?:''
           entitlement.startIssue=e.startIssue?:''
           entitlement.endIssue=e.endIssue?:''
           entitlement.embargo=e.embargo?:''
           entitlement.titleUrl=e.tipp.hostPlatformURL?:''
           entitlement.doi=title_doi
           entitlement.coverageDepth = e.tipp.coverageDepth
           entitlement.coverageNote = e.tipp.coverageNote
           entitlement.publisher = publisher.name
           response.entitlements.add(entitlement);
         }
         render response as JSON
      }
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def subscriptionBatchUpdate() {
    def subscriptionInstance = Subscription.get(params.id)
    def formatter = new java.text.SimpleDateFormat("MM/dd/yyyy")
    def user = User.get(springSecurityService.principal.id)

    if ( ! subscriptionInstance.hasPerm("edit",user) ) {
      render status: 401
      return
    }

    log.debug("subscriptionBatchUpdate ${params}");

    params.each { p ->
      if (p.key.startsWith('_bulkflag.') ) {
        def ie_to_edit = p.key.substring(10);

        def ie = IssueEntitlement.get(ie_to_edit)

        if ( params.bulkOperation == "edit" ) {

          if ( params.bulk_start_date && ( params.bulk_start_date.trim().length() > 0 ) ) {
            ie.startDate = formatter.parse(params.bulk_start_date)
          }

          if ( params.bulk_end_date && ( params.bulk_end_date.trim().length() > 0 ) ) {
            ie.endDate = formatter.parse(params.bulk_end_date)
          }

          if ( params.bulk_embargo && ( params.bulk_embargo.trim().length() > 0 ) ) {
            ie.embargo = params.bulk_embargo
          }

          if ( params.bulk_core && (params.bulk_core.trim().length() > 0 ) ) {
            ie.coreTitle = params.bulk_core
          }
  
          if ( params.bulk_coverage && (params.bulk_coverage.trim().length() > 0 ) ) {
            ie.coverageDepth = params.bulk_coverage
          }

          if ( ie.save(flush:true) ) {
          }
          else {
            log.error("Problem saving ${ie.errors}")
          }
        }
        else if ( params.bulkOperation == "remove" ) {
          log.debug("Updating ie ${ie.id} status to deleted");
          def deleted_ie = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Deleted');
          ie.status = deleted_ie;
          if ( ie.save(flush:true) ) {
          }
          else {
            log.error("Problem saving ${ie.errors}")
          }
        }
      }
    }
 
    redirect action: 'index', params:[id:subscriptionInstance?.id], id:subscriptionInstance.id
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def addEntitlements() {
    log.debug("addEntitlements....");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result.institution = result.subscriptionInstance.subscriber

    if ( ! result.subscriptionInstance.hasPerm("edit",result.user) ) {
      render status: 401
      return
    }

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : 10;
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    if ( result.subscriptionInstance.isEditableBy(result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }


    log.debug("filter: \"${params.filter}\"");

    if ( result.subscriptionInstance?.instanceOf ) {
      // We need all issue entitlements from the parent subscription where no row exists in the current subscription for that item.
      def basequery = null;
      def qry_params = [result.subscriptionInstance.instanceOf, result.subscriptionInstance]

      if ( params.filter ) {
        log.debug("Filtering....");
        basequery = " from IssueEntitlement as ie where ie.subscription = ? and ie.status.value != 'Deleted' and ( not exists ( select ie2 from IssueEntitlement ie2 where ie2.subscription = ? and ie2.tipp = ie.tipp and ie2.status.value != 'Deleted' ) ) and ( ( lower(ie.tipp.title.title) like ? ) or ( exists ( select io from IdentifierOccurrence io where io.ti.id = ie.tipp.title.id and io.identifier.value like ? ) ) )"
        qry_params.add("%${params.filter.trim().toLowerCase()}%")
        qry_params.add("%${params.filter}%")
      }
      else {
        basequery = "from IssueEntitlement ie where ie.subscription = ? and not exists ( select ie2 from IssueEntitlement ie2 where ie2.subscription = ? and ie2.tipp = ie.tipp  and ie2.status.value != 'Deleted' )"
      }

      if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
        basequery += " order by ie.${params.sort} ${params.order} "
      }
      else {
        basequery += " order by ie.tipp.title.title asc "
      }



      result.num_sub_rows = IssueEntitlement.executeQuery("select count(ie) "+basequery, qry_params )[0]
      result.available_issues = IssueEntitlement.executeQuery("select ie ${basequery}", qry_params, [max:result.max, offset:result.offset]);
    }
    else {
      result.num_sub_rows = 0;
      result.available_issues = []
    }
    
    result
  }
  

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def processAddEntitlements() {
    log.debug("addEntitlements....");
    def result = [:]

    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.siid)
    result.institution = result.subscriptionInstance?.subscriber

    if ( ! result.subscriptionInstance.hasPerm("edit",result.user) ) {
      render status: 401
      return
    }

    if ( result.subscriptionInstance ) {
      params.each { p ->
        if (p.key.startsWith('_bulkflag.') ) {
          def ie_to_edit = p.key.substring(10);
          def ie = IssueEntitlement.get(ie_to_edit)

          if ( ie == null ) {
            log.error("Unable to locate entitlement ${ie_to_edit}");
            flash.error("Unable to locate entitlement ${ie_to_edit}");
          }
          else {
            def new_ie = new IssueEntitlement(status: ie.status,
                                              subscription: result.subscriptionInstance,
                                              tipp: ie.tipp,
                                              startDate:ie.tipp.startDate,
                                              startVolume:ie.tipp.startVolume,
                                              startIssue:ie.tipp.startIssue,
                                              endDate:ie.tipp.endDate,
                                              endVolume:ie.tipp.endVolume,
                                              endIssue:ie.tipp.endIssue,
                                              embargo:ie.tipp.embargo,
                                              coverageDepth:ie.tipp.coverageDepth,
                                              coverageNote:ie.tipp.coverageNote,
                                              ieReason:'Manually Added by User')
            if ( new_ie.save(flush:true) ) {
              log.debug("Added IE ${ie_to_edit} to sub ${params.siid}");
            }
            else {
              new_ie.errors.each { e ->
                log.error(e);
              }
              flash.error = new_ie.errors
            }
          }
        }
      }
    }
    else {
      log.error("Unable to locate subscription instance");
    }

    redirect action: 'index', id:result.subscriptionInstance?.id
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def removeEntitlement() {
    log.debug("removeEntitlement....");
    def ie = IssueEntitlement.get(params.ieid)
    def deleted_ie = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Deleted');
    ie.status = deleted_ie;
    
    redirect action: 'index', id:params.sub
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def notes() {

    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result.institution = result.subscriptionInstance.subscriber

    if ( result.institution ) {
      result.subscriber_shortcode = result.institution.shortcode
    }

    if ( ! result.subscriptionInstance.hasPerm("view",result.user) ) {
      render status: 401
      return
    }

    if ( result.subscriptionInstance.isEditableBy(result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def documents() {

    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result.institution = result.subscriptionInstance.subscriber

    if ( ! result.subscriptionInstance.hasPerm("view",result.user) ) {
      render status: 401
      return
    }

    if ( result.institution ) {
      result.subscriber_shortcode = result.institution.shortcode
    }

    if ( result.subscriptionInstance.isEditableBy(result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def deleteDocuments() {
    def ctxlist = []

    log.debug("deleteDocuments ${params}");

    params.each { p ->
      if (p.key.startsWith('_deleteflag.') ) {
        def docctx_to_delete = p.key.substring(12);
        log.debug("Looking up docctx ${docctx_to_delete} for delete");
        def docctx = DocContext.get(docctx_to_delete)
        docctx.status = RefdataCategory.lookupOrCreate('Document Context Status','Deleted');
      }
    }

    redirect controller: 'subscriptionDetails', action:'documents', id:params.subId
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def additionalInfo() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result.institution = result.subscriptionInstance.subscriber

    // if ( ! result.subscriptionInstance.hasPerm("view",result.user) ) {
    //   render status: 401
    //   return
    // }

    result
  }

}


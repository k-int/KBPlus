package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import com.k_int.kbplus.auth.*;
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent


//For Transform
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

@Mixin(com.k_int.kbplus.mixins.PendingChangeMixin)
class SubscriptionDetailsController {

  def springSecurityService
  def ESWrapperService
  def gazetteerService
  def alertsService
  def genericOIDService
  def transformerService
  def exportService
  def grailsApplication
  
  def renewals_reversemap = ['subject':'subject', 'provider':'provid', 'pkgname':'tokname' ]

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
  def verystarttime = exportService.printStart("SubscriptionDetails")
  
    log.debug("subscriptionDetails id:${params.id} format=${response.format}");
    def result = [:]

    result.user = User.get(springSecurityService.principal.id)
    result.transforms = grailsApplication.config.subscriptionTransforms

    result.max = params.max ? Integer.parseInt(params.max) : ( (response.format && response.format != "html" && response.format != "all" ) ? 10000 : result.user.defaultPageSize );
    result.offset = (params.offset && response.format && response.format != "html") ? Integer.parseInt(params.offset) : 0;

    log.debug("max = ${result.max}");
    result.subscriptionInstance = Subscription.get(params.id)

    def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")
    result.pendingChanges = PendingChange.executeQuery("select pc from PendingChange as pc where subscription=? and ( pc.status is null or pc.status = ? ) order by ts desc", [result.subscriptionInstance, pending_change_pending_status]);

    // If transformer check user has access to it
    if(params.transforms && !transformerService.hasTransformId(result.user, params.transforms)) {
      flash.error = "It looks like you are trying to use an unvalid transformer or one you don't have access to!"
      params.remove("transforms")
      params.remove("format")
      redirect action:'currentTitles', params:params
    }
  
    if ( ! result.subscriptionInstance.hasPerm("view",result.user) ) {
      log.debug("Result of hasPerm is false");
      response.sendError(401);
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
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? "
      if ( params.mode != 'advanced' ) {
        // If we are not in advanced mode, hide IEs that are not current, otherwise filter
        base_qry += "and ( ? >= coalesce(ie.accessStartDate,subscription.startDate) ) and ( ( ? <= coalesce(ie.accessEndDate,subscription.endDate) ) OR ( ie.accessEndDate is null ) )  "
      qry_params.add(new Date());
      qry_params.add(new Date());
      }
      base_qry += "and ( ( lower(ie.tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = ie.tipp.title.id and io.identifier.value like ? ) ) ) "
      qry_params.add("%${params.filter.trim().toLowerCase()}%")
      qry_params.add("%${params.filter}%")
    }
    else {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? "
      if ( params.mode != 'advanced' ) {
        // If we are not in advanced mode, hide IEs that are not current, otherwise filter

        base_qry += "and ( ? >= coalesce(ie.accessStartDate,subscription.startDate) ) and ( ( ? <= coalesce(ie.accessEndDate,subscription.endDate) ) OR ( ie.accessEndDate is null ) ) "
        qry_params.add(new Date());
        qry_params.add(new Date());
      }
    }

    if ( params.pkgfilter && ( params.pkgfilter != '' ) ) {
      base_qry += " and ie.tipp.pkg.id = ? "
      qry_params.add(Long.parseLong(params.pkgfilter));
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += "order by lower(ie.${params.sort}) ${params.order} "
    }
    else {
      base_qry += "order by lower(ie.tipp.title.title) asc"
    }

    result.num_sub_rows = IssueEntitlement.executeQuery("select count(ie) "+base_qry, qry_params )[0]

    result.entitlements = IssueEntitlement.executeQuery("select ie "+base_qry, qry_params, [max:result.max, offset:result.offset]);

    exportService.printDuration(verystarttime, "Querying")
  
    log.debug("subscriptionInstance returning... ${result.num_sub_rows} rows ");
    def filename = "subscriptionDetails_${result.subscriptionInstance.identifier}"

    withFormat {
      html result
      csv {
         response.setHeader("Content-disposition", "attachment; filename=${result.subscriptionInstance.identifier}.csv")
         response.contentType = "text/csv"
         def out = response.outputStream
         def header = ( params.omitHeader == null ) || ( params.omitHeader != 'Y' )
         exportService.StreamOutSubsCSV(out, result.subscriptionInstance, result.entitlements, header)
         out.close()
         exportService.printDuration(verystarttime, "Overall Time")
      }
      json {
          def starttime = exportService.printStart("Building Map")
          def map = exportService.getSubscriptionMap(result.subscriptionInstance, result.entitlements)
          exportService.printDuration(starttime, "Building Map")
      
          starttime = exportService.printStart("Create JSON")
          def json = map as JSON
          exportService.printDuration(starttime, "Create JSON")
      
          if(params.transforms){
            transformerService.triggerTransform(result.user, filename, params.transforms, json, response)
          }else{
            response.setHeader("Content-disposition", "attachment; filename=\"${filename}.json\"")
            response.contentType = "application/json"
            render json
          }
          exportService.printDuration(verystarttime, "Overall Time")
      }
      xml {
          def starttime = exportService.printStart("Building XML Doc")
          def doc = exportService.buildDocXML("Subscriptions")
          exportService.addSubIntoXML(doc, doc.getDocumentElement(), result.subscriptionInstance, result.entitlements)
          exportService.printDuration(starttime, "Building XML Doc")
      
          if( ( params.transformId ) && ( result.transforms[params.transformId] != null ) ) {
            String xml = exportService.streamOutXML(doc, new StringWriter()).getWriter().toString();
            transformerService.triggerTransform(result.user, filename, result.transforms[params.transformId], xml, response)
          }else{
            response.setHeader("Content-disposition", "attachment; filename=\"${filename}.xml\"")
            response.contentType = "text/xml"
            starttime = exportService.printStart("Sending XML")
            exportService.streamOutXML(doc, response.outputStream)
            exportService.printDuration(starttime, "Sending XML")
          }
          exportService.printDuration(verystarttime, "Overall Time")
      }
    }
  }
  
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def subscriptionBatchUpdate() {
    def subscriptionInstance = Subscription.get(params.id)
    // def formatter = new java.text.SimpleDateFormat("MM/dd/yyyy")
    def formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")
    def user = User.get(springSecurityService.principal.id)

    if ( ! subscriptionInstance.hasPerm("edit",user) ) {
      response.sendError(401);
      return
    }

    log.debug("subscriptionBatchUpdate ${params}");

    params.each { p ->
      if ( p.key.startsWith('_bulkflag.') && (p.value=='on'))  {
        def ie_to_edit = p.key.substring(10);

        def ie = IssueEntitlement.get(ie_to_edit)

        if ( params.bulkOperation == "edit" ) {

          if ( params.bulk_start_date && ( params.bulk_start_date.trim().length() > 0 ) ) {
            ie.startDate = formatter.parse(params.bulk_start_date)
          }

          if ( params.bulk_end_date && ( params.bulk_end_date.trim().length() > 0 ) ) {
            ie.endDate = formatter.parse(params.bulk_end_date)
          }

          if ( params.bulk_core_start && ( params.bulk_core_start.trim().length() > 0 ) ) {
            ie.coreStatusStart = formatter.parse(params.bulk_core_start)
          }

          if ( params.bulk_core_end && ( params.bulk_core_end.trim().length() > 0 ) ) {
            ie.coreStatusEnd = formatter.parse(params.bulk_core_end)
          }

          if ( params.bulk_embargo && ( params.bulk_embargo.trim().length() > 0 ) ) {
            ie.embargo = params.bulk_embargo
          }

          if ( params.bulk_core.trim().length() > 0 ) {
            def selected_refdata = genericOIDService.resolveOID(params.bulk_core.trim())
            log.debug("Selected core status is ${selected_refdata}");
            ie.coreStatus = selected_refdata
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
      response.sendError(401);
      return
    }

    result.max = params.max ? Integer.parseInt(params.max) : request.user.defaultPageSize;
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    if ( result.subscriptionInstance.isEditableBy(result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    def tipp_deleted = RefdataCategory.lookupOrCreate('TIPP Status','Deleted');
    def ie_deleted = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Deleted');

    log.debug("filter: \"${params.filter}\"");

    if ( result.subscriptionInstance ) {
      // We need all issue entitlements from the parent subscription where no row exists in the current subscription for that item.
      def basequery = null;
      def qry_params = [result.subscriptionInstance, tipp_deleted, result.subscriptionInstance, ie_deleted]

      if ( params.filter ) {
        log.debug("Filtering....");
        basequery = "from TitleInstancePackagePlatform tipp where tipp.pkg in ( select pkg from SubscriptionPackage sp where sp.subscription = ? ) and tipp.status != ? and ( not exists ( select ie from IssueEntitlement ie where ie.subscription = ? and ie.tipp.id = tipp.id and ie.status != ? ) ) and ( ( lower(tipp.title.title) like ? ) OR ( exists ( select io from IdentifierOccurrence io where io.ti.id = tipp.title.id and io.identifier.value like ? ) ) ) "
        qry_params.add("%${params.filter.trim().toLowerCase()}%")
        qry_params.add("%${params.filter}%")
      }
      else {
        basequery = "from TitleInstancePackagePlatform tipp where tipp.pkg in ( select pkg from SubscriptionPackage sp where sp.subscription = ? ) and tipp.status != ? and ( not exists ( select ie from IssueEntitlement ie where ie.subscription = ? and ie.tipp.id = tipp.id and ie.status != ? ) )"
      }

      if ( params.endsAfter && params.endsAfter.length() > 0 ) {
        def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
        def d = sdf.parse(params.endsAfter)
        basequery += " and tipp.endDate >= ?"
        qry_params.add(d)
      }

      if ( params.startsBefore && params.startsBefore.length() > 0 ) {
        def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
        def d = sdf.parse(params.startsBefore)
        basequery += " and tipp.startDate <= ?"
        qry_params.add(d)
      }


      if ( params.pkgfilter && ( params.pkgfilter != '' ) ) {
        basequery += " and tipp.pkg.id = ? "
        qry_params.add(Long.parseLong(params.pkgfilter));
      }


      if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
        basequery += " order by tipp.${params.sort} ${params.order} "
      }
      else {
        basequery += " order by tipp.title.title asc "
      }

      log.debug("Query ${basequery} ${qry_params}");
      
      result.num_tipp_rows = IssueEntitlement.executeQuery("select count(tipp) "+basequery, qry_params )[0]
      result.tipps = IssueEntitlement.executeQuery("select tipp ${basequery}", qry_params, [max:result.max, offset:result.offset]);
    }
    else {
      result.num_sub_rows = 0;
      result.tipps = []
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
      response.sendError(401);
      return
    }

    if ( result.subscriptionInstance ) {
      params.each { p ->
        if (p.key.startsWith('_bulkflag.') ) {
          def tipp_id = p.key.substring(10);
          // def ie = IssueEntitlement.get(ie_to_edit)
          def tipp = TitleInstancePackagePlatform.get(tipp_id)

          if ( tipp == null ) {
            log.error("Unable to tipp ${tipp_id}");
            flash.error("Unable to tipp ${tipp_id}");
          }
          else {
            def ie_current = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Current');

            def new_ie = new IssueEntitlement(status: ie_current,
                                              subscription: result.subscriptionInstance,
                                              tipp: tipp,
                                              startDate:tipp.startDate,
                                              startVolume:tipp.startVolume,
                                              startIssue:tipp.startIssue,
                                              endDate:tipp.endDate,
                                              endVolume:tipp.endVolume,
                                              endIssue:tipp.endIssue,
                                              embargo:tipp.embargo,
                                              coverageDepth:tipp.coverageDepth,
                                              coverageNote:tipp.coverageNote,
                                              ieReason:'Manually Added by User')
            if ( new_ie.save(flush:true) ) {
              log.debug("Added tipp ${tipp_id} to sub ${params.siid}");
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
      response.sendError(401);
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
      response.sendError(401);
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
  def renewals() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result.institution = result.subscriptionInstance.subscriber
    if ( ! result.subscriptionInstance.hasPerm("view",result.user) ) {
      response.sendError(401);
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

    if ( result.subscriptionInstance.isEditableBy(result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }


    // if ( ! result.subscriptionInstance.hasPerm("view",result.user) ) {
    //   render status: 401
    //   return
    // }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def launchRenewalsProcess() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result.institution = result.subscriptionInstance.subscriber

    def shopping_basket = UserFolder.findByUserAndShortcode(result.user,'SOBasket') ?: new UserFolder(user:result.user, shortcode:'SOBasket').save(flush:true);

    log.debug("Clear basket....");
    shopping_basket.items?.clear();
    shopping_basket.save(flush:true)

    def oid = "com.k_int.kbplus.Subscription:${params.id}"
    shopping_basket.addIfNotPresent(oid)
  
    redirect controller:'myInstitutions',action:'renewalsSearch',params:[shortcode:result.subscriptionInstance.subscriber.shortcode]
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def acceptChange() {
    processAcceptChange(params, Subscription.get(params.id), genericOIDService)
    redirect controller: 'subscriptionDetails', action:'index',id:params.id
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def rejectChange() {
    processRejectChange(params, Subscription.get(params.id))
    redirect controller: 'subscriptionDetails', action:'index',id:params.id
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def possibleLicensesForSubscription() {
    def result = []

    def subscription = genericOIDService.resolveOID(params.oid)
    def subscriber = subscription.getSubscriber();

    result.add([value:'', text:'None']);

    if ( subscriber ) {

      def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
      def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');

      def qry_params = [subscriber, licensee_role]
  
      def qry = "select l from License as l where exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted' order by l.reference"

      def license_list = License.executeQuery(qry, qry_params);
      license_list.each { l ->
        result.add([value:"${l.class.name}:${l.id}",text:l.reference ?: "No reference - license ${l.id}"]);
      }
    }
    render result as JSON
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def linkPackage() {
    log.debug("Link package, params: ${params}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result.institution = result.subscriptionInstance.subscriber

    if ( ! result.subscriptionInstance.hasPerm("edit",result.user) ) {
      response.sendError(401);
      return
    }

    
    if ( result.subscriptionInstance.isEditableBy(result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    if ( result.institution ) {
      result.subscriber_shortcode = result.institution.shortcode
      result.institutional_usage_identifier = result.institution.getIdentifierByType('JUSP');
    }

    
    StringWriter sw = new StringWriter()
    def fq = null;
    boolean has_filter = false
  
    params.each { p ->
      if ( p.key.startsWith('fct:') && p.value.equals("on") ) {
        log.debug("start year ${p.key} : -${p.value}-");

        if ( !has_filter )
          has_filter = true
        else
          sw.append(" AND ")

        String[] filter_components = p.key.split(':');
            switch ( filter_components[1] ) {
              case 'consortiaName':
                sw.append('consortiaName')
                break;
              case 'startYear':
                sw.append('startYear')
                break;
              case 'cpname':
                sw.append('cpname')
                break;
            }
            if ( filter_components[2].indexOf(' ') > 0 ) {
              sw.append(":\"");
              sw.append(filter_components[2])
              sw.append("\"");
            }
            else {
              sw.append(":");
              sw.append(filter_components[2])
            }
      }
    }

    if ( has_filter ) {
      fq = sw.toString();
      log.debug("Filter Query: ${fq}");
    }

    try {

      // Get hold of some services we might use ;)
      org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
      org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
      
          params.max = Math.min(params.max ? params.int('max') : result.user.defaultPageSize, 100)
          params.offset = params.offset ? params.int('offset') : 0

          //def params_set=params.entrySet()

          def query_str = buildRenewalsQuery(params)
          if ( fq ) 
            query_str = query_str + " AND ( " + fq + " ) "
          
          log.debug("query: ${query_str}");
          result.es_query = query_str;

          def search = esclient.search{
            indices "kbplus"
            source {
              from = params.offset
              size = params.max
              sort = [
                'sortname' : [ 'order' : 'asc' ]
              ]
              query {
                query_string (query: query_str)
              }
              facets {
                consortiaName {
                  terms {
                    field = 'consortiaName'
                    size = 25
                  }
                }
                cpname {
                  terms {
                    field = 'cpname'
                    size = 25
                  }
                }
                startYear {
                  terms {
                    field = 'startYear'
                    size = 25
                  }
                }
              }
            }

          }

      if ( search?.response ) {
        result.hits = search.response.hits
        result.resultsTotal = search.response.hits.totalHits

        // We pre-process the facet response to work around some translation issues in ES
        if ( search.response.facets != null ) {
          result.facets = [:]
          search.response.facets.facets.each { facet ->
            def facet_values = []
            facet.value.entries.each { fe ->
              facet_values.add([term: fe.term,display:fe.term,count:"${fe.count}"])
            }
            result.facets[facet.key] = facet_values
          }
        }
      }
          
      if ( params.addType && ( params.addType != '' ) ) {
        def pkg_to_link = Package.get(params.addId)
        log.debug("Add package ${params.addType} to subscription ${params}");
        if ( params.addType == 'With' ) {
          pkg_to_link.addToSubscription(result.subscriptionInstance, true)
          redirect action:'index', id:params.id
        }
        else if ( params.addType == 'Without' ) {
          pkg_to_link.addToSubscription(result.subscriptionInstance, false)
          redirect action:'addEntitlements', id:params.id
        }
      }

    }
    finally {
      try {
      }
      catch ( Exception e ) {
        log.error("problem",e);
      }
    }
    
    result
  }

  def buildRenewalsQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    // sw.write("subtype:'Subscription Offered'")
    sw.write("rectype:'Package'")

    renewals_reversemap.each { mapping ->

      // log.debug("testing ${mapping.key}");

      if ( params[mapping.key] != null ) {
        if ( params[mapping.key].class == java.util.ArrayList) {
          params[mapping.key].each { p ->
                sw.write(" AND ")
                sw.write(mapping.value)
                sw.write(":")
                sw.write("\"${p}\"")
          }
        }
        else {
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *
          if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
            sw.write(" AND ")
            sw.write(mapping.value)
            sw.write(":")
            sw.write("\"${params[mapping.key]}\"")
          }
        }
      }
    }


    def result = sw.toString();
    result;
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def history() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscription = Subscription.get(params.id)

    if ( ! result.subscription.hasPerm("view",result.user) ) {
      response.sendError(401);
      return
    }

    if ( result.subscription.hasPerm("edit",result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    result.max = params.max ?: result.user.defaultPageSize;
    result.offset = params.offset ?: 0;

    def qry_params = [result.subscription.class.name, "${result.subscription.id}"]
    result.historyLines = AuditLogEvent.executeQuery("select e from AuditLogEvent as e where className=? and persistedObjectId=? order by id desc", qry_params, [max:result.max, offset:result.offset]);
    result.historyLinesTotal = AuditLogEvent.executeQuery("select count(e.id) from AuditLogEvent as e where className=? and persistedObjectId=?",qry_params)[0];
    result.todoHistoryLines = PendingChange.executeQuery("select pc from PendingChange as pc where subscription=? order by ts desc", result.subscription);

    result
  }

}


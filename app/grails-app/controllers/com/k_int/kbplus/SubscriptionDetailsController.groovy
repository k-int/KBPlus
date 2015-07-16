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
  def pendingChangeService
  def institutionsService
  def ESSearchService
  def executorWrapperService
  def renewals_reversemap = ['subject':'subject', 'provider':'provid', 'pkgname':'tokname' ]


  private static String INVOICES_FOR_SUB_HQL =
     'select co.invoice, sum(co.costInLocalCurrency), sum(co.costInBillingCurrency) from CostItem as co where co.sub = :sub group by co.invoice order by min(co.invoice.startDate) desc';

  private static String USAGE_FOR_SUB_IN_PERIOD =
    'select f.reportingYear, f.reportingMonth+1, sum(factValue) '+
    'from Fact as f '+
    'where f.factFrom >= :start and f.factTo <= :end and f.factType.value=:jr1a and exists '+
    '( select ie.tipp.title from IssueEntitlement as ie where ie.subscription = :sub and ie.tipp.title = f.relatedTitle)'+
    'group by f.reportingYear, f.reportingMonth order by f.reportingYear desc, f.reportingMonth desc';

  private static String TOTAL_USAGE_FOR_SUB_IN_PERIOD =
    'select sum(factValue) '+
    'from Fact as f '+
    'where f.factFrom >= :start and f.factTo <= :end and f.factType.value=:jr1a and exists '+
    '( select ie.tipp.title from IssueEntitlement as ie where ie.subscription = :sub and ie.tipp.title = f.relatedTitle)';



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
    def pendingChanges = PendingChange.executeQuery("select pc.id from PendingChange as pc where subscription=? and ( pc.status is null or pc.status = ? ) order by ts desc", [result.subscriptionInstance, pending_change_pending_status ]);

    if(result.subscriptionInstance?.isSlaved?.value == "Yes" && pendingChanges){
      log.debug("Slaved subscription, auto-accept pending changes")
      def changesDesc = []
      pendingChanges.each{change ->
        if(!pendingChangeService.performAccept(change,request)){
          log.debug("Auto-accepting pending change has failed.")
        }else{
          changesDesc.add(PendingChange.get(change).desc)
        }
      }
      flash.message = changesDesc
    }else{
      result.pendingChanges = pendingChanges.collect{PendingChange.get(it)}
    }



    // If transformer check user has access to it
    if(params.transforms && !transformerService.hasTransformId(result.user, params.transforms)) {
      flash.error = "It looks like you are trying to use an unvalid transformer or one you don't have access to!"
      params.remove("transforms")
      params.remove("format")
      redirect action:'currentTitles', params:params
    }

    if (! result.subscriptionInstance?.hasPerm("view",result.user) ) {
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

    if (params.mode == "advanced"){
      params.asAt = null
    }

    def base_qry = null;

    def deleted_ie = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Deleted');
    def qry_params = [result.subscriptionInstance]

    def date_filter
    if(params.asAt && params.asAt.length() > 0) {
      def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
      date_filter = sdf.parse(params.asAt)
      result.as_at_date = date_filter;
      result.editable = false;
    }else{
      date_filter = new Date()
      result.as_at_date = date_filter;
    }
    // We dont want this filter to reach SQL query as it will break it.
    def core_status_filter = params.sort == 'core_status'
    if(core_status_filter) params.remove('sort');

    if ( params.filter ) {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? "
      if ( params.mode != 'advanced' ) {
        // If we are not in advanced mode, hide IEs that are not current, otherwise filter
        // base_qry += "and ie.status <> ? and ( ? >= coalesce(ie.accessStartDate,subscription.startDate) ) and ( ( ? <= coalesce(ie.accessEndDate,subscription.endDate) ) OR ( ie.accessEndDate is null ) )  "
        // qry_params.add(deleted_ie);
        base_qry += "and ( ? >= coalesce(ie.accessStartDate,subscription.startDate) ) and ( ( ? <= coalesce(ie.accessEndDate,subscription.endDate) ) OR ( ie.accessEndDate is null ) )  "
        qry_params.add(date_filter);
        qry_params.add(date_filter);
      }
      base_qry += "and ( ( lower(ie.tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = ie.tipp.title.id and io.identifier.value like ? ) ) ) "
      qry_params.add("%${params.filter.trim().toLowerCase()}%")
      qry_params.add("%${params.filter}%")
    }
    else {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? "
      if ( params.mode != 'advanced' ) {
        // If we are not in advanced mode, hide IEs that are not current, otherwise filter

        base_qry += " and ( ? >= coalesce(ie.accessStartDate,subscription.startDate) ) and ( ( ? <= coalesce(ie.accessEndDate,subscription.endDate) ) OR ( ie.accessEndDate is null ) ) "
        qry_params.add(date_filter);
        qry_params.add(date_filter);
      }
    }

    base_qry += " and ie.status <> ? "
    qry_params.add(deleted_ie);

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

    if(params.format == 'html' || params.format == null){
      result.entitlements = IssueEntitlement.executeQuery("select ie "+base_qry, qry_params, [max:result.max, offset:result.offset]);
    }else{
      result.entitlements = IssueEntitlement.executeQuery("select ie "+base_qry, qry_params);
    }

    // Now we add back the sort so that the sortable column will recognize asc/desc
    // Ignore the sorting if we are doing an export
    if(core_status_filter){
      params.put('sort','core_status');
      if(params.format == 'html' || params.format == null)  sortOnCoreStatus(result,params);
    }

    exportService.printDuration(verystarttime, "Querying")

    log.debug("subscriptionInstance returning... ${result.num_sub_rows} rows ");
    def filename = "subscriptionDetails_${result.subscriptionInstance.identifier}"


    if(executorWrapperService.hasRunningProcess(result.subscriptionInstance)){
      result.processingpc = true
    }
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
  def unlinkPackage(){
    log.debug("unlinkPackage :: ${params}")
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscription = Subscription.get(params.subscription.toLong())
    result.package = Package.get(params.package.toLong())
    def query = "from IssueEntitlement ie, Package pkg where ie.subscription =:sub and pkg.id =:pkg_id and ie.tipp in ( select tipp from TitleInstancePackagePlatform tipp where tipp.pkg.id = :pkg_id ) "
    def queryParams = [sub:result.subscription,pkg_id:result.package.id]

    if (result.subscription.isEditableBy(result.user) ) {
      result.editable = true
      if(params.confirmed){
      //delete matches

        removePackagePendingChanges(result.package.id,result.subscription.id,params.confirmed)
        def deleteIdList = IssueEntitlement.executeQuery("select ie.id ${query}",queryParams)
        IssueEntitlement.executeUpdate("delete from IssueEntitlement ie where ie.id in (:delList)",[delList:deleteIdList])
        SubscriptionPackage.executeUpdate("delete from SubscriptionPackage sp where sp.pkg=? and sp.subscription=? ",[result.package,result.subscription])
      }else{
        def numOfPCs = removePackagePendingChanges(result.package.id,result.subscription.id,params.confirmed)

        def numOfIEs = IssueEntitlement.executeQuery("select count(ie) ${query}",queryParams)[0]
        def conflict_item_pkg = [name:"Linked Package",details:[['link':createLink(controller:'packageDetails', action: 'show', id:result.package.id), 'text': result.package.name]],action:[actionRequired:false,text:'Link will be removed']]
        def conflicts_list = [conflict_item_pkg]

        if(numOfIEs > 0){
          def conflict_item_ie = [name:"Package IEs",details:[['text': 'Number of IEs: '+numOfIEs]],action:[actionRequired:false,text:'IEs will be deleted']]
          conflicts_list += conflict_item_ie
        }
        if(numOfPCs > 0){
          def conflict_item_pc = [name:"Pending Changes",details:[['text': 'Number of PendingChanges: '+ numOfPCs]],action:[actionRequired:false,text:'Pending Changes will be deleted']]
          conflicts_list += conflict_item_pc
        }

        return render(template: "unlinkPackageModal",model:[pkg:result.package,subscription:result.subscription,conflicts_list:conflicts_list])
      }
    }else{
      result.editable = false
    }


    redirect action: 'index', id:params.subscription

  }
  def removePackagePendingChanges(pkg_id,sub_id,confirmed){

    def tipp_class = TitleInstancePackagePlatform.class.getName()
    def tipp_id_query = "from TitleInstancePackagePlatform tipp where tipp.pkg.id = ?"
    def change_doc_query = "from PendingChange pc where pc.subscription.id = ? "
    def tipp_ids = TitleInstancePackagePlatform.executeQuery("select tipp.id ${tipp_id_query}",[pkg_id])
    def pendingChanges = PendingChange.executeQuery("select pc.id, pc.changeDoc ${change_doc_query}",[sub_id])

    def pc_to_delete = []
    pendingChanges.each{pc->
      def parsed_change_info = JSON.parse(pc[1])
      def (oid_class,ident) = parsed_change_info.changeDoc.OID.split(":")
      if(oid_class == tipp_class && tipp_ids.contains(ident.toLong()) ){
        pc_to_delete += pc[0]
      }
    }
    if(confirmed && pc_to_delete){
      log.debug("Deleting Pending Changes: ${pc_to_delete}")
      def del_pc_query = "delete from PendingChange where id in (:del_list) "
      PendingChange.executeUpdate(del_pc_query,[del_list:pc_to_delete])
    }else{
      return pc_to_delete.size()
    }
  }

  def sortOnCoreStatus(result,params){
    result.entitlements.sort{it.getTIP()?.coreStatus(null)}
    if(params.order == 'desc') result.entitlements.reverse(true);
    result.entitlements = result.entitlements.subList(result.offset, (result.offset+result.max).intValue() )
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def compare(){
    def result = [:]
    result.unionList = []

    result.user = User.get(springSecurityService.principal.id)
    result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0

    if(params.subA?.length() > 0 && params.subB?.length() > 0 ){
      log.debug("Subscriptions submitted for comparison ${params.subA} and ${params.subB}.")
      log.debug("Dates submited are ${params.dateA} and ${params.dateB}")

      result.subInsts = []
      result.subDates = []

      def listA
      def listB
      try{
      listA = createCompareList(params.subA ,params.dateA, params, result)
      listB = createCompareList(params.subB, params.dateB, params, result)
        if(!params.countA){
          def countQuery = "select count(elements(sub.issueEntitlements)) from Subscription sub where sub.id = ?"
          params.countA = Subscription.executeQuery(countQuery, [result.subInsts.get(0).id])
          params.countB = Subscription.executeQuery(countQuery, [result.subInsts.get(1).id])
        }
      }catch(IllegalArgumentException e){
        flash.error = e.getMessage()
        return
      }

      result.listACount = listA.size()
      result.listBCount = listB.size()

      def mapA = listA.collectEntries { [it.tipp.title.title, it] }
      def mapB = listB.collectEntries { [it.tipp.title.title, it] }

      //FIXME: It should be possible to optimize the following lines
      def unionList = mapA.keySet().plus(mapB.keySet()).toList()
      unionList = unionList.unique()
      result.unionListSize = unionList.size()
      unionList.sort()

      def filterRules = [params.insrt?true:false, params.dlt?true:false, params.updt?true:false, params.nochng?true:false ]
      withFormat{
        html{
          def toIndex = result.offset+result.max < unionList.size()? result.offset+result.max: unionList.size()
          result.comparisonMap =
              institutionsService.generateComparisonMap(unionList,mapA,mapB,result.offset, toIndex.intValue(),filterRules)
          log.debug("Comparison Map"+result.comparisonMap)
          result
        }
        csv {
          try{
          log.debug("Create CSV Response")
          def comparisonMap =
          institutionsService.generateComparisonMap(unionList, mapA, mapB, 0, unionList.size(),filterRules)
          def dateFormatter = new java.text.SimpleDateFormat('yyyy-MM-dd')

           response.setHeader("Content-disposition", "attachment; filename=subscriptionComparison.csv")
           response.contentType = "text/csv"
           def out = response.outputStream
           out.withWriter { writer ->
            writer.write("${result.subInsts[0].name} on ${params.dateA}, ${result.subInsts[1].name} on ${params.dateB}\n")
            writer.write('IE Title, pISSN, eISSN, Start Date A, Start Date B, Start Volume A, Start Volume B, Start Issue A, Start Issue B, End Date A, End Date B, End Volume A, End Volume B, End Issue A, End Issue B, Coverage Note A, Coverage Note B, ColorCode\n');
            log.debug("UnionList size is ${unionList.size}")
            comparisonMap.each{ title, values ->
              def ieA = values[0]
              def ieB = values[1]
              def colorCode = values[2]
              def pissn = ieA ? ieA.tipp.title.getIdentifierValue('issn') : ieB.tipp.title.getIdentifierValue('issn');
              def eissn = ieA ? ieA.tipp.title.getIdentifierValue('eISSN') : ieB.tipp.title.getIdentifierValue('eISSN')

              writer.write("\"${title}\",\"${pissn?:''}\",\"${eissn?:''}\",\"${formatDateOrNull(dateFormatter,ieA?.startDate)}\",\"${formatDateOrNull(dateFormatter,ieB?.startDate)}\",\"${ieA?.startVolume?:''}\",\"${ieB?.startVolume?:''}\",\"${ieA?.startIssue?:''}\",\"${ieB?.startIssue?:''}\",\"${formatDateOrNull(dateFormatter,ieA?.endDate)}\",\"${formatDateOrNull(dateFormatter,ieB?.endDate)}\",\"${ieA?.endVolume?:''}\",\"${ieB?.endVolume?:''}\",\"${ieA?.endIssue?:''}\",\"${ieB?.endIssue?:''}\",\"${ieA?.coverageNote?:''}\",\"${ieB?.coverageNote?:''}\",\"${colorCode}\"\n")
            }
            writer.write("END");
            writer.flush();
            writer.close();
           }
           out.close()

          }catch(Exception e){
            log.error("An Exception was thrown here",e)
          }
        }
      }
    }else{
      def currentDate = new java.text.SimpleDateFormat('yyyy-MM-dd').format(new Date())
      params.dateA = currentDate
      params.dateB = currentDate
      params.insrt = "Y"
      params.dlt = "Y"
      params.updt = "Y"
      if(params.shortcode){
        result.institutionName = Org.findByShortcode(params.shortcode).name
        log.debug("FIND ORG NAME ${result.institutionName}")
      }
      flash.message = "Please select two subscriptions for comparison"
    }
    result
  }
  def formatDateOrNull(formatter, date) {
      def result;
      if(date){
        result = formatter.format(date)
      }else{
        result = ''
      }
      return result
    }
  def createCompareList(sub,dateStr,params, result){
   def returnVals = [:]
   def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd')
   def date = dateStr?sdf.parse(dateStr):new Date()
   def subId = sub.substring( sub.indexOf(":")+1)

   def subInst = Subscription.get(subId)
   if(subInst.startDate > date || subInst.endDate < date){
      def errorMsg = "${subInst.name} start date is: ${sdf.format(subInst.startDate)} and end date is: ${sdf.format(subInst.endDate)}. You have selected to compare it on date ${sdf.format(date)}."
          throw new IllegalArgumentException(errorMsg)
   }

   result.subInsts.add(subInst)

   result.subDates.add(sdf.format(date))

   def queryParams = [subInst]
   def query = generateIEQuery(params,queryParams, true, date)

   def list = IssueEntitlement.executeQuery("select ie "+query,  queryParams);
   list

  }

  def generateIEQuery(params, qry_params, showDeletedTipps, asAt) {

    def base_qry = "from IssueEntitlement as ie where ie.subscription = ? and ie.tipp.title.status.value != 'Deleted' "

    if ( showDeletedTipps == false ) {
         base_qry += "and ie.tipp.status.value != 'Deleted' "
    }

    if ( params.filter ) {
      base_qry += " and ( ( lower(ie.tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = ie.tipp.title.id and io.identifier.value like ? ) ) )"
      qry_params.add("%${params.filter.trim().toLowerCase()}%")
      qry_params.add("%${params.filter}%")
    }

    if ( params.startsBefore && params.startsBefore.length() > 0 ) {
        def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
        def d = sdf.parse(params.startsBefore)
        base_qry += " and ie.startDate <= ?"
        qry_params.add(d)
    }

    if ( asAt != null ) {
        base_qry += " and ( ( ? >= coalesce(ie.tipp.accessStartDate, ie.startDate) ) and ( ( ? <= ie.tipp.accessEndDate ) or ( ie.tipp.accessEndDate is null ) ) ) "
        qry_params.add(asAt);
        qry_params.add(asAt);
    }

    return base_qry
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

          if ( params.bulk_coreStatus.trim().length() > 0 ) {
            def selected_refdata = genericOIDService.resolveOID(params.bulk_coreStatus.trim())
            log.debug("Selected core status is ${selected_refdata}");
            ie.coreStatus = selected_refdata
          }

          if ( params.bulk_medium.trim().length() > 0 ) {
            def selected_refdata = genericOIDService.resolveOID(params.bulk_medium.trim())
            log.debug("Selected medium is ${selected_refdata}");
            ie.medium = selected_refdata
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

    redirect action: 'index', params:[id:subscriptionInstance?.id,sort:params.sort,order:params.order,offset:params.offset,max:params.max]
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
    def previous() {
       previousAndExpected(params,'previous');
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def expected() {
        previousAndExpected(params,'expected');
    }

    def previousAndExpected (params, screen){
        log.debug("previousAndExpected ${params}");
        def result = [:]

        result.user = User.get(springSecurityService.principal.id)
        def subscriptionInstance = Subscription.get(params.id)
        if (!subscriptionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Subscription'), params.id])
            redirect action: 'list'
            return
        }
        result.subscriptionInstance = subscriptionInstance

        result.max = params.max ? Integer.parseInt(params.max) : request.user.defaultPageSize
        params.max = result.max
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

        def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]

        def qry_params = [subscriptionInstance]
        def date_filter =  new Date();

        def base_qry = "from IssueEntitlement as ie where ie.subscription = ? "
        base_qry += "and ie.status.value != 'Deleted' "
        if ( date_filter != null ) {
            if(screen.equals('previous')) {
                base_qry += " and ( ie.accessEndDate <= ? ) "
            }else{
                base_qry += " and (ie.accessStartDate > ? )"
            }
            qry_params.add(date_filter);
        }

        log.debug("Base qry: ${base_qry}, params: ${qry_params}, result:${result}");
        result.titlesList = IssueEntitlement.executeQuery("select ie "+base_qry, qry_params, limits);
        result.num_ie_rows = IssueEntitlement.executeQuery("select count(ie) "+base_qry, qry_params )[0]

        result.lastie = result.offset + result.max > result.num_ie_rows ? result.num_ie_rows : result.offset + result.max;

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
                                              accessStartDate: tipp.accessStartDate,
                                              accessEndDate: tipp.accessEndDate,
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

    redirect controller: 'subscriptionDetails', action:params.redirectAction, id:params.instanceId
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
    log.debug("Going for ES")
    params.rectype = "Package"
    result.putAll(ESSearchService.search(params))

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
  def edit_history() {
    log.debug("subscriptionDetails::edit_history ${params}");
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

    result
  }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def todo_history() {
    log.debug("subscriptionDetails::todo_history ${params}");
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

    result.todoHistoryLines = PendingChange.executeQuery("select pc from PendingChange as pc where subscription=? order by ts desc", [result.subscription], [max:result.max, offset:result.offset]);

    result.todoHistoryLinesTotal = PendingChange.executeQuery("select count(pc) from PendingChange as pc where subscription=?", result.subscription)[0];

    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def costPerUse() {
    def result = [:]

    result.user = User.get(springSecurityService.principal.id)
    result.subscription = Subscription.get(params.id)

    // result.institution = Org.findByShortcode(params.shortcode)
    result.institution = result.subscription.subscriber
    if ( result.institution ) {
      result.subscriber_shortcode = result.institution.shortcode
      result.institutional_usage_identifier = result.institution.getIdentifierByType('JUSP');
    }

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

    // Get a unique list of invoices
    // select inv, sum(cost) from costItem as ci where ci.sub = x
    log.debug("Get all invoices for sub ${result.subscription}");
    result.costItems = []
    CostItem.executeQuery(INVOICES_FOR_SUB_HQL,[sub:result.subscription]).each {
      def cost_row = [invoice:it[0],total:it[2]]

      cost_row.total_cost_for_sub = it[2];
      def usage_str = Fact.executeQuery(TOTAL_USAGE_FOR_SUB_IN_PERIOD,[start:it[0].startDate, end:it[0].endDate, sub:result.subscription, jr1a:'JUSP:JR1' ])[0]

      if ( usage_str && usage_str.trim().length() > 0 ) {
        cost_row.total_usage_for_sub = Double.parseDouble(usage_str);
        if ( cost_row.total_usage_for_sub > 0 ) {
          cost_row.overall_cost_per_use = cost_row.total_cost_for_sub / cost_row.total_usage_for_sub;
        }
        else {
          cost_row.overall_cost_per_use = 0;
        }
      }
      else {
        cost_row.total_usage_for_sub = Double.parseDouble('0');
        cost_row.overall_cost_per_use = cost_row.total_usage_for_sub
      }


      // Work out what cost items appear under this subscription in the period given
      cost_row.usage = Fact.executeQuery(USAGE_FOR_SUB_IN_PERIOD,[start:it[0].startDate, end:it[0].endDate, sub:result.subscription, jr1a:'JUSP:JR1' ])

      result.costItems.add(cost_row);

    }


    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def details() {
    def result = [:]

    result.user = User.get(springSecurityService.principal.id)
    result.subscription = Subscription.get(params.id)
    result.subscriptionInstance = result.subscription


    // result.institution = Org.findByShortcode(params.shortcode)
    result.institution = result.subscription.subscriber
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


    result
  }
}

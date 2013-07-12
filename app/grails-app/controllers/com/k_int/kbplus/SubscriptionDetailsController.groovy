package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

@Mixin(com.k_int.kbplus.mixins.PendingChangeMixin)
class SubscriptionDetailsController {

  def springSecurityService
  def docstoreService
  def ESWrapperService
  def gazetteerService
  def alertsService
  def genericOIDService
  
  def renewals_reversemap = ['subject':'subject', 'provider':'provid', 'pkgname':'tokname' ]

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    log.debug("subscriptionDetails id:${params.id} format=${response.format}");
    def result = [:]

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : ( (response.format && response.format != "html") ? 10000 : 10 );
    result.offset = (params.offset && response.format && response.format != "html") ? Integer.parseInt(params.offset) : 0;

    log.debug("max = ${result.max}");
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)

    if ( ! result.subscriptionInstance.hasPerm("view",result.user) ) {
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
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? and ( ie.status.value != 'Deleted' ) and ( ( lower(ie.tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = ie.tipp.title.id and io.identifier.value like ? ) ) ) "
      qry_params.add("%${params.filter.trim().toLowerCase()}%")
      qry_params.add("%${params.filter}%")
    }
    else {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? and ( ie.status.value != 'Deleted' ) "
    }

    if ( params.pkgfilter && ( params.pkgfilter != '' ) ) {
      base_qry += " and ie.tipp.pkg.id = ? "
      qry_params.add(Long.parseLong(params.pkgfilter));
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += "order by ie.${params.sort} ${params.order} "
    }
    else {
      base_qry += "order by ie.tipp.title.title asc"
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
		  def response = [:]
		  def subscriptions = []
		  
		  def sub = result.subscriptionInstance
		  def subscription = [:]
		  subscription."SubscriptionID" = sub.id
		  subscription."SubscriptionName" = sub.name
		  subscription."SubTermStartDate" = sub.startDate?formatter.format(sub.startDate):''
		  subscription."SubTermEndDate" = sub.endDate?formatter.format(sub.endDate):''
		  
		  subscription."RelatedOrgs" = []
		  
		  sub.orgRelations.each { or ->
			  def org = [:]
			  org."OrgID" = or.org.id
			  org."OrgName" = or.org.name
			  org."OrgRole" = or.roleType.value
			  
			  def ids = [:]
			  or.org.ids.each(){ id ->
				  def value = id.identifier.value
				  def ns = id.identifier.ns.ns
				  if(ids.containsKey(ns)){
					  def current = ids[ns]
					  def newval = []
					  newval << current
					  newval << value
					  ids[ns] = newval
				  } else {
					  ids[ns]=value
				  }
			  }
			  org."OrgIDs" = ids
				  
			  subscription."RelatedOrgs" << org
		  }
		  
		  subscription."Licences" = []
		  def licence = [:]
		  
		  if(sub.owner){
			  def owner = sub.owner
			  
			  licence."LicenceReference" = owner.reference
			  licence."NoticePeriod" = owner.noticePeriod
			  licence."LicenceURL" = owner.licenseUrl
			  licence."LicensorRef" = owner.licensorRef
			  licence."LicenseeRef" = owner.licenseeRef
				  
			  licence."RelatedOrgs" = []
			  sub.owner?.orgLinks.each { or ->
				  def org = [:]
				  org."OrgID" = or.org.id
				  org."OrgName" = or.org.name
				  org."OrgRole" = or.roleType.value
				  
				  def ids = [:]
				  or.org.ids.each(){ id ->
					  def value = id.identifier.value
					  def ns = id.identifier.ns.ns
					  if(ids.containsKey(ns)){
						  def current = ids[ns]
						  def newval = []
						  newval << current
						  newval << value
						  ids[ns] = newval
					  } else {
						  ids[ns]=value
					  }
				  }
				  org."OrgIDs" = ids
					  
				  licence."RelatedOrgs" << org
			  }
			  
			  
			  
			  def prop = licence."LicenceProperties" = [:]
			  def ca = prop."ConcurrentAccess" = [:]
			  ca."Status" = owner.concurrentUsers?.value
			  ca."UserCount" = owner.concurrentUserCount
			  ca."Notes" = owner.getNote("concurrentUsers")?.owner?.content?:""
			  def ra = prop."RemoteAccess" = [:]
			  ra."Status" = owner.remoteAccess?.value
			  ra."Notes" = owner.getNote("remoteAccess")?.owner?.content?:""
			  def wa = prop."WalkingAccess" = [:]
			  wa."Status" = owner.walkinAccess?.value
			  wa."Notes" = owner.getNote("remoteAccess")?.owner?.content?:""
			  def ma = prop."MultisiteAccess" = [:]
			  ma."Status" = owner.multisiteAccess?.value
			  ma."Notes" = owner.getNote("multisiteAccess")?.owner?.content?:""
			  def pa = prop."PartnersAccess" = [:]
			  pa."Status" = owner.partnersAccess?.value
			  pa."Notes" = owner.getNote("partnersAccess")?.owner?.content?:""
			  def aa = prop."AlumniAccess" = [:]
			  aa."Status" = owner.alumniAccess?.value
			  aa."Notes" = owner.getNote("alumniAccess")?.owner?.content?:""
			  def ill = prop."InterLibraryLoans" = [:]
			  ill."Status" = owner.ill?.value
			  ill."Notes" = owner.getNote("ill")?.owner?.content?:""
			  def cp = prop."IncludeinCoursepacks" = [:]
			  cp."Status" = owner.coursepack?.value
			  cp."Notes" = owner.getNote("coursepack")?.owner?.content?:""
			  def vle = prop."IncludeinVLE" = [:]
			  vle."Status" = owner.vle?.value
			  vle."Notes" = owner.getNote("vle")?.owner?.content?:""
			  def ea = prop."EntrepriseAccess" = [:]
			  ea."Status" = owner.enterprise?.value
			  ea."Notes" = owner.getNote("enterprise")?.owner?.content?:""
			  def pca = prop."PostCancellationAccessEntitlement" = [:]
			  pca."Status" = owner.pca?.value
			  pca."Notes" = owner.getNote("pca")?.owner?.content?:""
		  }
		  
		  // Should only be one, we have an array to keep teh same format has licenses json
		  subscription."Licences" << licence
						  
		  subscription."TitleList" = []
		  result.entitlements.each { entitlement ->
			  def ti = entitlement.tipp.title
			  
			  def title = [:]
			  title."Title" = ti.title
			  
			  def ids = [:]
			  ti.ids.each(){ id ->
				  def value = id.identifier.value
				  def ns = id.identifier.ns.ns
				  if(ids.containsKey(ns)){
					  def current = ids[ns]
					  def newval = []
					  newval << current
					  newval << value
					  ids[ns] = newval
				  } else {
					  ids[ns]=value
				  }
			  }
			  title."TitleIDs" = ids
			  
			  // Should only be one, we have an array to keep teh same format has titles json
			  title."CoverageStatements" = []
			  
			  def ie = [:]
			  ie."CoverageStatementType" = "Issue Entitlement"
			  ie."SubscriptionID" = sub.id
			  ie."SubscriptionName" = sub.name
			  ie."StartDate" = entitlement.startDate?formatter.format(entitlement.startDate):''
			  ie."StartVolume" = entitlement.startVolume?:''
			  ie."StartIssue" = entitlement.startIssue?:''
			  ie."EndDate" = entitlement.endDate?formatter.format(entitlement.endDate):''
			  ie."EndVolume" = entitlement.endVolume?:''
			  ie."EndIssue" = entitlement.endIssue?:''
			  ie."Embargo" = entitlement.embargo?:''
			  ie."Coverage" = entitlement.coverageDepth?:''
			  ie."CoverageNote" = entitlement.coverageNote?:''
			  ie."HostPlatformName" = entitlement.tipp?.platform?.name?:''
			  ie."HostPlatformURL" = entitlement.tipp?.hostPlatformURL?:''
			  ie."AdditionalPlatforms" = []
			  entitlement.tipp?.additionalPlatforms.each(){ ap ->
				  def platform = [:]
				  platform.PlatformName = ap.platform?.name?:''
				  platform.PlatformRole = ap.rel?:''
				  platform.PlatformURL = ap.platform?.primaryUrl?:''
				  ie."AdditionalPlatforms" << platform
			  }
			  ie."CoreStatus" = entitlement.coreStatus?.value?:''
			  ie."CoreStart" = entitlement.coreStatusStart?formatter.format(entitlement.coreStatusStart):''
			  ie."CoreEnd" = entitlement.coreStatusEnd?formatter.format(entitlement.coreStatusEnd):''
			  ie."PackageID" = entitlement.tipp?.pkg?.id?:''
			  ie."PackageName" = entitlement.tipp?.pkg?.name?:''
				  
			  title."CoverageStatements".add(ie)
			  
			  subscription."TitleList" << title
		  }
			  
		  subscriptions.add(subscription)
		  
		  response."Subscriptions" = subscriptions
		  
		  render response as JSON
		  
	  }
	  xml {
		  def writer = new StringWriter()
		  def xmlBuilder = new MarkupBuilder(writer)
		  xmlBuilder.getMkp().xmlDeclaration(version:'1.0', encoding: 'UTF-8')
		  
		  def sub = result.subscriptionInstance
		  
		  xmlBuilder.Subscriptions() {
			  Subscription(){
				  SubscriptionID(sub.id)
				  SubscriptionName(sub.name)
				  SubTermStartDate(sub.startDate?formatter.format(sub.startDate):'')
				  SubTermEndDate(sub.endDate?formatter.format(sub.endDate):'')
				  
				  sub.orgRelations.each { or ->
					  RelatedOrg(id: or.org.id){
						  OrgName(or.org.name)
						  OrgRole(or.roleType.value)
						  
						  OrgIDs(){
							  or.org.ids.each(){ id ->
								  def value = id.identifier.value
								  def ns = id.identifier.ns.ns
								  ID(namespace: ns, value)
							  }
						  }
						  
						  
					  }
				  }
				  
				  def owner = sub.owner
				  Licence(){
					  if(owner){
						  LicenceReference(owner.reference)
						  NoticePeriod(owner.noticePeriod)
						  LicenceURL(owner.licenseUrl)
						  LicensorRef(owner.licensorRef)
						  LicenseeRef(owner.licenseeRef)
						  
						  sub.owner?.orgLinks.each { or ->
							  RelatedOrg(id: or.org.id){
								  OrgName(or.org.name)
								  OrgRole(or.roleType.value)
								  
								  OrgIDs(){
									  or.org.ids.each(){ id ->
										  def value = id.identifier.value
										  def ns = id.identifier.ns.ns
										  ID(namespace: ns, value)
									  }
								  }
							  }
						  }
						  
						  LicenceProperties(){
							  ConcurrentAccess(){
								  Status(owner.concurrentUsers?.value)
								  UserCount(owner.concurrentUserCount)
								  Notes(owner.getNote("concurrentUsers")?.owner?.content?:"")
							  }
							  RemoteAccess(){
								  Status(owner.remoteAccess?.value)
								  Notes(owner.getNote("remoteAccess")?.owner?.content?:"")
							  }
							  WalkingAccess(){
								  Status(owner.walkinAccess?.value)
								  Notes(owner.getNote("walkinAccess")?.owner?.content?:"")
							  }
							  MultisiteAccess(){
								  Status(owner.multisiteAccess?.value)
								  Notes(owner.getNote("multisiteAccess")?.owner?.content?:"")
							  }
							  PartnersAccess(){
								  Status(owner.partnersAccess?.value)
								  Notes(owner.getNote("partnersAccess")?.owner?.content?:"")
							  }
							  AlumniAccess(){
								  Status(owner.alumniAccess?.value)
								  Notes(owner.getNote("alumniAccess")?.owner?.content?:"")
							  }
							  InterLibraryLoans(){
								  Status(owner.ill?.value)
								  Notes(owner.getNote("ill")?.owner?.content?:"")
							  }
							  IncludeinCoursepacks(){
								  Status(owner.coursepack?.value)
								  Notes(owner.getNote("coursepack")?.owner?.content?:"")
							  }
							  IncludeinVLE(){
								  Status(owner.vle?.value)
								  Notes(owner.getNote("vle")?.owner?.content?:"")
							  }
							  EntrepriseAccess(){
								  Status(owner.enterprise?.value)
								  Notes(owner.getNote("enterprise")?.owner?.content?:"")
							  }
							  PostCancellationAccessEntitlement(){
								  Status(owner.pca?.value)
								  Notes(owner.getNote("pca")?.owner?.content?:"")
							  }
						  }
					  }
				  }//End Licence
				  
				  Title{
					  result.entitlements.each { entitlement ->
						  def ti = entitlement.tipp.title
						  
						  TitleListEntry(){
							  Title(ti.title)
							  
							  TitleIDs(){
								  ti.ids.each(){ id ->
									  def value = id.identifier.value
									  def ns = id.identifier.ns.ns
									  ID(namespace: ns, value)
								  }
							  }
							  
							  CoverageStatement(type: 'Issue Entitlement'){
								  SubscriptionID(sub.id)
								  SubscriptionName(sub.name)
								  StartDate(entitlement.startDate?formatter.format(entitlement.startDate):'')
								  StartVolume(entitlement.startVolume?:'')
								  StartIssue(entitlement.startIssue?:'')
								  EndDate(entitlement.endDate?formatter.format(entitlement.endDate):'')
								  EndVolume(entitlement.endVolume?:'')
								  EndIssue(entitlement.endIssue?:'')
								  Embargo(entitlement.embargo?:'')
								  Coverage(entitlement.coverageDepth?:'')
								  CoverageNote(entitlement.coverageNote?:'')
								  HostPlatformName(entitlement.tipp?.platform?.name?:'')
								  HostPlatformURL(entitlement.tipp?.hostPlatformURL?:'')
								  
								  Patform(){
									  entitlement.tipp?.additionalPlatforms.each(){ ap ->
										  PlatformName(ap.platform?.name?:'')
										  PlatformRole(ap.rel?:'')
										  PlatformURL(ap.platform?.primaryUrl?:'')
									  }
								  }
								  
								  CoreStatus(entitlement.coreStatus?.value?:'')
								  CoreStart(entitlement.coreStatusStart?formatter.format(entitlement.coreStatusStart):'')
								  CoreEnd(entitlement.coreStatusEnd?formatter.format(entitlement.coreStatusEnd):'')
								  PackageID(entitlement.tipp?.pkg?.id?:'')
								  PackageName(entitlement.tipp?.pkg?.name?:'')
							  }
						  }
					  }
				  }
			  }
		  }
		  		  
		  render writer.toString()
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

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : 10;
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
        // basequery = " from IssueEntitlement as ie where ie.subscription = ? and ie.status.value != 'Deleted' and ( not exists ( select ie2 from IssueEntitlement ie2 where ie2.subscription = ? and ie2.tipp = ie.tipp and ie2.status.value != 'Deleted' ) ) and ( ( lower(ie.tipp.title.title) like ? ) or ( exists ( select io from IdentifierOccurrence io where io.ti.id = ie.tipp.title.id and io.identifier.value like ? ) ) )"
        // basequery = "from TitleInstancePackagePlatform tipp where tipp.pkg in ( select pkg from SubscriptionPackage sp where sp.subscription = ? ) and tipp.status.value != 'Deleted' and ( not exists ( select ie from IssueEntitlement ie where ie.subscription = ? and ie.tipp = tipp and ie.status.value != 'Deleted' ) )"
        basequery = "from TitleInstancePackagePlatform tipp where tipp.pkg in ( select pkg from SubscriptionPackage sp where sp.subscription = ? ) and tipp.status != ? and ( not exists ( select ie from IssueEntitlement ie where ie.subscription = ? and ie.tipp.id = tipp.id and ie.status != ? ) ) and ( ( lower(tipp.title.title) like ? ) OR ( exists ( select io from IdentifierOccurrence io where io.ti.id = tipp.title.id and io.identifier.value like ? ) ) ) "
        // select ie.tipp from IssueEntitlement where ie.subscription = ? and ie.tipp = tipp
        qry_params.add("%${params.filter.trim().toLowerCase()}%")
        qry_params.add("%${params.filter}%")
      }
      else {
        // basequery = "from IssueEntitlement ie where ie.subscription = ? and not exists ( select ie2 from IssueEntitlement ie2 where ie2.subscription = ? and ie2.tipp = ie.tipp  and ie2.status.value != 'Deleted' )"
        // basequery = "from TitleInstancePackagePlatform tipp where tipp.pkg in ( select pkg from SubscriptionPackage sp where sp.subscription = ? )"
        // basequery = "from TitleInstancePackagePlatform tipp where tipp.pkg in ( select pkg from SubscriptionPackage sp where sp.subscription = ? ) and tipp.status.value != 'Deleted' and ( not exists ( select ie.tipp from IssueEntitlement ie where ie.subscription = ? and ie.tipp = tipp and ie.status.value != 'Deleted' ) )"
        basequery = "from TitleInstancePackagePlatform tipp where tipp.pkg in ( select pkg from SubscriptionPackage sp where sp.subscription = ? ) and tipp.status != ? and ( not exists ( select ie from IssueEntitlement ie where ie.subscription = ? and ie.tipp.id = tipp.id and ie.status != ? ) )"
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
    if ( subscriber ) {

      def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
      def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');

      def qry_params = [subscriber, licensee_role]
  
      def qry = "select l from License as l where exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted'"

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
      
          params.max = Math.min(params.max ? params.int('max') : 10, 100)
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
          redirect action:'addEntitlements', id:params.id
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
}


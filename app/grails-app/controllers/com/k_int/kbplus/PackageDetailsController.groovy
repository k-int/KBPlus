package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder


class PackageDetailsController {

  def ESWrapperService
  def springSecurityService

  def pkg_qry_reversemap = ['subject':'subject', 'provider':'provid', 'pkgname':'tokname' ]



    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      params.max = Math.min(params.max ? params.int('max') : 10, 100)

      result.editable = true

      def paginate_after = params.paginate_after ?: 19;
      result.max = params.max 
      result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

      def deleted_package_status =  RefdataCategory.lookupOrCreate( 'Package Status', 'Deleted' );
      def qry_params = [deleted_package_status]

      def base_qry = " from Package as p where ( (p.packageStatus is null ) OR ( p.packageStatus = ? ) ) "

      if ( params.q?.length() > 0 ) {
        base_qry += " and ( ( lower(p.name) like ? ) or ( lower(p.identifier) like ? ) )"
        qry_params.add("%${params.q.trim().toLowerCase()}%");
        qry_params.add("%${params.q.trim().toLowerCase()}%");
      }

      // if ( date_restriction ) {
      //   base_qry += " and s.startDate <= ? and s.endDate >= ? "
      //   qry_params.add(date_restriction)
      //   qry_params.add(date_restriction)
      // }

      // if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      //   base_qry += " order by ${params.sort} ${params.order}"
      // }
      // else {
      //   base_qry += " order by s.name asc"
      // }

      result.packageInstanceTotal = Subscription.executeQuery("select count(p) "+base_qry, qry_params )[0]
      result.packageInstanceList = Subscription.executeQuery("select p ${base_qry}", qry_params, [max:result.max, offset:result.offset]);

      result
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def create() {
      def user = User.get(springSecurityService.principal.id)

      switch (request.method) {
        case 'GET':
          [packageInstance: new Package(params), user:user]
          break
        case 'POST':
          def providerName = params.contentProviderName
          def packageName = params.packageName
          def identifier = params.identifier

          def contentProvider = Org.findByName(providerName);
          def existing_pkg = Package.findByIdentifier(identifier);

          if ( contentProvider && existing_pkg==null ) {
            log.debug("Create new package, content provider = ${contentProvider}, identifier is ${identifier}");
            Package new_pkg = new Package(identifier:identifier, 
                                          contentProvider:contentProvider,
                                          name:packageName,
                                          impId:java.util.UUID.randomUUID().toString());
            if ( new_pkg.save(flush:true) ) {
              redirect action: 'edit', id:new_pkg.id
            }
            else {
              new_pkg.errors.each { e ->
                log.error("Problem: ${e}");
              }
              render view: 'create', model: [packageInstance: new_pkg, user:user]
            }
          }
          else {
            render view: 'create', model: [packageInstance: packageInstance, user:user]
            return
          }

          // flash.message = message(code: 'default.created.message', args: [message(code: 'package.label', default: 'Package'), packageInstance.id])
          // redirect action: 'show', id: packageInstance.id
          break
      }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
      def result = [:]
      
      if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') )
        result.editable=true
      else
        result.editable=false

      result.user = User.get(springSecurityService.principal.id)
      def packageInstance = Package.get(params.id)
      if (!packageInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
        redirect action: 'list'
        return
      }

      result.pkg_link_str="${ApplicationHolder.application.config.SystemBaseURL}/packageDetails/show/${params.id}"

      if ( packageInstance.forumId != null ) {
        result.forum_url = "${ApplicationHolder.application.config.ZenDeskBaseURL}/forums/${packageInstance.forumId}"
      }

      result.subscriptionList=[]
      // We need to cycle through all the users institutions, and their respective subscripions, and add to this list
      // and subscription that does not already link this package
      result.user?.getAuthorizedAffiliations().each { ua ->
        if ( ua.formalRole.authority == 'INST_ADM' ) {
          def qry_params = [ua.org, packageInstance]
          def q = "select s from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) AND ( not exists ( select sp from s.packages as sp where sp.pkg = ? ) )"
          Subscription.executeQuery(q, qry_params).each { s ->
            if ( ! result.subscriptionList.contains(s) ) {
              // Need to make sure that this package is not already linked to this subscription
              result.subscriptionList.add([org:ua.org,sub:s])
            }
          }
        }
      }
	  
      result.max = params.max ? Integer.parseInt(params.max) : 25
      params.max = result.max
      def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
      result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
	  
	  def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]
	  
      def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.pkg = ? "
      def qry_params = [packageInstance]

      if ( params.filter ) {
        base_qry += " and ( ( lower(tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = tipp.title.id and io.identifier.value like ? ) ) )"
        qry_params.add("%${params.filter.trim().toLowerCase()}%")
        qry_params.add("%${params.filter}%")
      }

      if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
        base_qry += " order by ${params.sort} ${params.order}"
      }
      else {
        base_qry += " order by tipp.title.title asc"
      }

      log.debug("Base qry: ${base_qry}, params: ${qry_params}, result:${result}");
      result.titlesList = TitleInstancePackagePlatform.executeQuery("select tipp "+base_qry, qry_params, limits);
      result.num_tipp_rows = TitleInstancePackagePlatform.executeQuery("select count(tipp) "+base_qry, qry_params )[0]

      result.lasttipp = result.offset + result.max > result.num_tipp_rows ? result.num_tipp_rows : result.offset + result.max;


      result.packageInstance = packageInstance
	  
	  withFormat {
		  html result
		  json {
			  def formatter = new java.text.SimpleDateFormat("yyyy/MM/dd")
			  
			  def response = [:]
			  def packages = []
			  
			  def pi = packageInstance
			  
			  def pck = [:]
			  
			  pck."PackageID" = pi.id
			  pck."PackageName" = pi.name
			  pck."PackageTermStartDate" = pi.startDate
			  pck."PackageTermEndDate" = pi.endDate
			  
			  pck."RelatedOrgs" = []
			  pi.orgs.each { or ->
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
					  
				  pck."RelatedOrgs" << org
			  }
			  
			  pck."Licences" = []
			  def lic = [:]
			  if(pi.license){
				  def licence = pi.license
				  lic."LicenceReference" = licence.reference
				  lic."NoticePeriod" = licence.noticePeriod
				  lic."LicenceURL" = licence.licenseUrl
				  lic."LicensorRef" = licence.licensorRef
				  lic."LicenseeRef" = licence.licenseeRef
					  
				  lic."RelatedOrgs" = []
				  licence.orgLinks.each { or ->
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
					  
					  lic."RelatedOrgs" << org
				  }
				  
				  def prop = lic."LicenceProperties" = [:]
				  def ca = prop."ConcurrentAccess" = [:]
				  ca."Status" = licence.concurrentUsers?.value
				  ca."UserCount" = licence.concurrentUserCount
				  ca."Notes" = licence.getNote("concurrentUsers")?.owner?.content?:""
				  def ra = prop."RemoteAccess" = [:]
				  ra."Status" = licence.remoteAccess?.value
				  ra."Notes" = licence.getNote("remoteAccess")?.owner?.content?:""
				  def wa = prop."WalkingAccess" = [:]
				  wa."Status" = licence.walkinAccess?.value
				  wa."Notes" = licence.getNote("remoteAccess")?.owner?.content?:""
				  def ma = prop."MultisiteAccess" = [:]
				  ma."Status" = licence.multisiteAccess?.value
				  ma."Notes" = licence.getNote("multisiteAccess")?.owner?.content?:""
				  def pa = prop."PartnersAccess" = [:]
				  pa."Status" = licence.partnersAccess?.value
				  pa."Notes" = licence.getNote("partnersAccess")?.owner?.content?:""
				  def aa = prop."AlumniAccess" = [:]
				  aa."Status" = licence.alumniAccess?.value
				  aa."Notes" = licence.getNote("alumniAccess")?.owner?.content?:""
				  def ill = prop."InterLibraryLoans" = [:]
				  ill."Status" = licence.ill?.value
				  ill."Notes" = licence.getNote("ill")?.owner?.content?:""
				  def cp = prop."IncludeinCoursepacks" = [:]
				  cp."Status" = licence.coursepack?.value
				  cp."Notes" = licence.getNote("coursepack")?.owner?.content?:""
				  def vle = prop."IncludeinVLE" = [:]
				  vle."Status" = licence.vle?.value
				  vle."Notes" = licence.getNote("vle")?.owner?.content?:""
				  def ea = prop."EntrepriseAccess" = [:]
				  ea."Status" = licence.enterprise?.value
				  ea."Notes" = licence.getNote("enterprise")?.owner?.content?:""
				  def pca = prop."PostCancellationAccessEntitlement" = [:]
				  pca."Status" = licence.pca?.value
				  pca."Notes" = licence.getNote("pca")?.owner?.content?:""
			  }
			  // Should only be one, we have an array to keep teh same format has licenses json
			  pck."Licences" << lic
			  
			  pck."TitleList" = []
			  
			  result.titlesList.each { tipp ->
				  def ti = tipp.title
				  
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
				  ie."CoverageStatementType" = "TIPP"
				  ie."StartDate" = tipp.startDate?formatter.format(tipp.startDate):''
				  ie."StartVolume" = tipp.startVolume?:''
				  ie."StartIssue" = tipp.startIssue?:''
				  ie."EndDate" = tipp.endDate?formatter.format(tipp.endDate):''
				  ie."EndVolume" = tipp.endVolume?:''
				  ie."EndIssue" = tipp.endIssue?:''
				  ie."Embargo" = tipp.embargo?:''
				  ie."Coverage" = tipp.coverageDepth?:''
				  ie."CoverageNote" = tipp.coverageNote?:''
				  ie."HostPlatformName" = tipp?.platform?.name?:''
				  ie."HostPlatformURL" = tipp?.hostPlatformURL?:''
				  ie."AdditionalPlatforms" = []
				  tipp.additionalPlatforms?.each(){ ap ->
					  def platform = [:]
					  platform.PlatformName = ap.platform?.name?:''
					  platform.PlatformRole = ap.rel?:''
					  platform.PlatformURL = ap.platform?.primaryUrl?:''
					  ie."AdditionalPlatforms" << platform
				  }
				  ie."CoreStatus" = tipp.status?.value?:''
				  ie."CoreStart" = tipp.coreStatusStart?formatter.format(tipp.coreStatusStart):''
				  ie."CoreEnd" = tipp.coreStatusEnd?formatter.format(tipp.coreStatusEnd):''
				  ie."PackageID" = tipp?.pkg?.id?:''
				  ie."PackageName" = tipp?.pkg?.name?:''
					  
				  title."CoverageStatements".add(ie)
				  
				  pck."TitleList" << title
			  }
			  
			  packages << pck
			  response."Packages" = packages
			  
			  render response as JSON
		  }
		  xml {
			  def formatter = new java.text.SimpleDateFormat("yyyy/MM/dd")
			  
			  def writer = new StringWriter()
			  def xmlBuilder = new MarkupBuilder(writer)
			  xmlBuilder.getMkp().xmlDeclaration(version:'1.0', encoding: 'UTF-8')
			  
			  def pi = packageInstance
			  
			  xmlBuilder.Packages() {
				  Package(){
					  PackageID(pi.id)
					  PackageName(pi.name)
					  PackageTermStartDate(pi.startDate)
					  PackageTermEndDate(pi.endDate)
					  
					  pi.orgs.each { or ->
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
							  
					  Licence(){
						  if(pi.license){
							  def licence = pi.license
							  
							  LicenceReference(licence.reference)
							  NoticePeriod(licence.noticePeriod)
							  LicenceURL(licence.licenseUrl)
							  LicensorRef(licence.licensorRef)
							  LicenseeRef(licence.licenseeRef)
							  
							  licence.orgLinks.each { or ->
								  RelatdOrg(id: or.org.id){
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
									  Status(licence.concurrentUsers?.value)
									  UserCount(licence.concurrentUserCount)
									  Notes(licence.getNote("concurrentUsers")?.owner?.content?:"")
								  }
								  RemoteAccess(){
									  Status(licence.remoteAccess?.value)
									  Notes(licence.getNote("remoteAccess")?.owner?.content?:"")
								  }
								  WalkingAccess(){
									  Status(licence.walkinAccess?.value)
									  Notes(licence.getNote("walkinAccess")?.owner?.content?:"")
								  }
								  MultisiteAccess(){
									  Status(licence.multisiteAccess?.value)
									  Notes(licence.getNote("multisiteAccess")?.owner?.content?:"")
								  }
								  PartnersAccess(){
									  Status(licence.partnersAccess?.value)
									  Notes(licence.getNote("partnersAccess")?.owner?.content?:"")
								  }
								  AlumniAccess(){
									  Status(licence.alumniAccess?.value)
									  Notes(licence.getNote("alumniAccess")?.owner?.content?:"")
								  }
								  InterLibraryLoans(){
									  Status(licence.ill?.value)
									  Notes(licence.getNote("ill")?.owner?.content?:"")
								  }
								  IncludeinCoursepacks(){
									  Status(licence.coursepack?.value)
									  Notes(licence.getNote("coursepack")?.owner?.content?:"")
								  }
								  IncludeinVLE(){
									  Status(licence.vle?.value)
									  Notes(licence.getNote("vle")?.owner?.content?:"")
								  }
								  EntrepriseAccess(){
									  Status(licence.enterprise?.value)
									  Notes(licence.getNote("enterprise")?.owner?.content?:"")
								  }
								  PostCancellationAccessEntitlement(){
									  Status(licence.pca?.value)
									  Notes(licence.getNote("pca")?.owner?.content?:"")
								  }
							  }
						  }
					  }
							  
					  TitleList(){
						  result.titlesList.each { tipp ->
							  def ti = tipp.title
							  TitleListEntry(){
								  Title(ti.title)
								  
								  TitleIDs(){
									  ti.ids.each(){ id ->
										  def value = id.identifier.value
										  def ns = id.identifier.ns.ns
										  ID(namespace: ns, value)
									  }
								  }
								  
								  CoverageStatement(type: 'TIPP'){
										StartDate(tipp.startDate?formatter.format(tipp.startDate):'')
										StartVolume(tipp.startVolume?:'')
										StartIssue(tipp.startIssue?:'')
										EndDate(tipp.endDate?formatter.format(tipp.endDate):'')
										EndVolume(tipp.endVolume?:'')
										EndIssue(tipp.endIssue?:'')
										Embargo(tipp.embargo?:'')
										Coverage(tipp.coverageDepth?:'')
										CoverageNote(tipp.coverageNote?:'')
										HostPlatformName(tipp.platform?.name?:'')
										HostPlatformURL(tipp.hostPlatformURL?:'')
							
										tipp.additionalPlatforms.each(){ ap ->
											Platform(){
												PlatformName(ap.platform?.name?:'')
												PlatformRole(ap.rel?:'')
												PlatformURL(ap.platform?.primaryUrl?:'')
											}
										}
										
										CoreStatus(tipp.status?.value?:'')
										CoreStart(tipp.coreStatusStart?formatter.format(tipp.coreStatusStart):'')
										CoreEnd(tipp.coreStatusEnd?formatter.format(tipp.coreStatusEnd):'')
										PackageID(tipp.pkg?.id?:'')
										PackageName(tipp.pkg?.name?:'')
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

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def uploadTitles() {
    def pkg = Package.get(params.id)
    def upload_mime_type = request.getFile("titleFile")?.contentType
    log.debug("Uploaded content type: ${upload_mime_type}");
    def input_stream = request.getFile("titleFile")?.inputStream

    if ( upload_mime_type=='application/vnd.ms-excel' ) {
      attemptXLSLoad(pkg,input_stream);
    }
    else {
      attemptCSVLoad(pkg,input_stream);
    }

    redirect action:'show', id:params.id
  }

  def attemptXLSLoad(pkg,stream) {
    log.debug("attemptXLSLoad");
    HSSFWorkbook wb = new HSSFWorkbook(stream);
    HSSFSheet hssfSheet = wb.getSheetAt(0);

    attemptv1XLSLoad(pkg,hssfSheet);
  }

  def attemptCSVLoad(pkg,stream) {
    log.debug("attemptCSVLoad");
    attemptv1CSVLoad(pkg,stream);
  }

  def attemptv1XLSLoad(pkg,hssfSheet) {

    log.debug("attemptv1XLSLoad");
    def extracted = [:]
    extracted.rows = []

    int row_counter = 0;
    Iterator rowIterator = hssfSheet.rowIterator();
    while (rowIterator.hasNext()) {
      HSSFRow hssfRow = (HSSFRow) rowIterator.next();
      switch(row_counter++){
        case 0:
          break;
        case 1:
          break;
        case 2:
          // Record header row
          log.debug("Header");
          hssfRow.cellIterator().each { c ->
            log.debug("Col: ${c.toString()}");
          }
          break;
        default:
          // A real data row
          def row_info = [
            issn:hssfRow.getCell(0)?.toString(),
            eissn:hssfRow.getCell(1)?.toString(),
            date_first_issue_online:hssfRow.getCell(2)?.toString(),
            num_first_volume_online:hssfRow.getCell(3)?.toString(),
            num_first_issue_online:hssfRow.getCell(4)?.toString(),
            date_last_issue_online:hssfRow.getCell(5)?.toString(),
            date_first_volume_online:hssfRow.getCell(6)?.toString(),
            date_first_issue_online:hssfRow.getCell(7)?.toString(),
            embargo:hssfRow.getCell(8)?.toString(),
            coverageDepth:hssfRow.getCell(9)?.toString(),
            coverageNote:hssfRow.getCell(10)?.toString(),
            platformUrl:hssfRow.getCell(11)?.toString()
          ]

          extracted.rows.add(row_info);
          log.debug("datarow: ${row_info}");
          break;
      }
    }
    
    processExractedData(pkg,extracted);
  }

  def attemptv1CSVLoad(pkg,stream) {
    log.debug("attemptv1CSVLoad");
    def extracted = [:]
    processExractedData(pkg,extracted);
  }

  def processExractedData(pkg, extracted_data) {
    log.debug("processExractedData...");
    List old_title_list = [ [title: [id:667]], [title:[id:553]], [title:[id:19]] ]
    List new_title_list = [ [title: [id:19]], [title:[id:554]], [title:[id:667]] ]

    reconcile(old_title_list, new_title_list);
  }

  def reconcile(old_title_list, new_title_list) {
    def title_list_comparator = new com.k_int.kbplus.utils.TitleComparator()
    Collections.sort(old_title_list, title_list_comparator)
    Collections.sort(new_title_list, title_list_comparator)

    Iterator i1 = old_title_list.iterator()
    Iterator i2 = new_title_list.iterator()

    def current_old_title = i1.hasNext() ? i1.next() : null;
    def current_new_title = i2.hasNext() ? i2.next() : null;
    
    while ( current_old_title || current_new_title ) {
      if ( current_old_title == null ) {
        // We have exhausted all old titles. Everything in the new title list must be newly added
        log.debug("Title added: ${current_new_title.title.id}");
        current_new_title = i2.hasNext() ? i2.next() : null;
      }
      else if ( current_new_title == null ) {
        // We have exhausted new old titles. Everything remaining in the old titles list must have been removed
        log.debug("Title removed: ${current_old_title.title.id}");
        current_old_title = i1.hasNext() ? i1.next() : null;
      }
      else {
        // Work out whats changed
        if ( current_old_title.title.id == current_new_title.title.id ) {
          // This title appears in both old and new lists, it may be an updated
          log.debug("title ${current_old_title.title.id} appears in both lists - possible update / unchanged");
          current_old_title = i1.hasNext() ? i1.next() : null;
          current_new_title = i2.hasNext() ? i2.next() : null;
        }
        else {
          if ( current_old_title.title.id > current_new_title.title.id ) {
            // The current old title id is greater than the current new title. This means that a new title must
            // have been introduced into the new list with a lower title id than the one on the current list.
            // hence, current_new_title.title.id is a new record. Consume it and move forwards.
            log.debug("Title added: ${current_new_title.title.id}");
            current_new_title = i2.hasNext() ? i2.next() : null;
          }
          else {
            // The current old title is less than the current new title. This indicates that the current_old_title
            // must have been removed in the new list. Process it as a removal and continue.
            log.debug("Title removed: ${current_old_title.title.id}");
            current_old_title = i1.hasNext() ? i1.next() : null;
          }
        }
      }
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {

    log.debug("packaheSearch : ${params}");
    log.debug("Start year filters: ${params.startYear}");

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

    // Be mindful that the behavior of this controller is strongly influenced by the schema setup in ES.
    // Specifically, see KBPlus/import/processing/processing/dbreset.sh for the mappings that control field type and analysers
    // Internal testing with http://localhost:9200/kbplus/_search?q=subtype:'Subscription%20Offered'
    def result=[:]

    // Get hold of some services we might use ;)
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
    result.user = springSecurityService.getCurrentUser()

    if (springSecurityService.isLoggedIn()) {

      try {

          params.max = Math.min(params.max ? params.int('max') : 10, 100)
          params.offset = params.offset ? params.int('offset') : 0

          //def params_set=params.entrySet()

          def query_str = buildPackageQuery(params)
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
                    size = 100
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
      }
      finally {
        try {
        }
        catch ( Exception e ) {
          log.error("problem",e);
        }
      }

    }  // If logged in

    result
  }

  def buildPackageQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    // sw.write("subtype:'Subscription Offered'")
    sw.write("rectype:'Package'")

    pkg_qry_reversemap.each { mapping ->

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
  def addToSub() {
    def pkg = Package.get(params.id)
    def sub = Subscription.get(params.subid)

    def add_entitlements = ( params.addEntitlements == 'true' ? true : false )
    pkg.addToSubscription(sub,add_entitlements)

    redirect(action:'show', id:params.id);
  }
}

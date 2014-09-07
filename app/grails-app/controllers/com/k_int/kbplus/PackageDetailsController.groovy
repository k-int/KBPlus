package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import java.text.SimpleDateFormat

class PackageDetailsController {

  def ESWrapperService
  def springSecurityService
  def transformerService
  def genericOIDService

  def pkg_qry_reversemap = ['subject':'subject', 
                            'provider':'provid', 
                            'startYear':'startYear', 
                            'endYear':'endYear', 
                            'endYear':'endYear', 
                            'pkgname':'tokname' ]

    def exportService

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)

      result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;

      result.editable = true

      def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
      result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

      def deleted_package_status =  RefdataCategory.lookupOrCreate( 'Package Status', 'Deleted' );
      def qry_params = [deleted_package_status]

      def base_qry = " from Package as p where ( (p.packageStatus is null ) OR ( p.packageStatus = ? ) ) "

      if ( params.q?.length() > 0 ) {
        base_qry += " and ( ( lower(p.name) like ? ) or ( lower(p.identifier) like ? ) )"
        qry_params.add("%${params.q.trim().toLowerCase()}%");
        qry_params.add("%${params.q.trim().toLowerCase()}%");
      }

      if ( params.startDate?.length() > 0 ) {
        base_qry += " and ( p.lastUpdated > ? )"
        qry_params.add(params.date('startDate','yyyy-MM-dd'));
      }

      if ( params.endDate?.length() > 0 ) {
        base_qry += " and ( p.lastUpdated < ? )"
        qry_params.add(params.date('endDate','yyyy-MM-dd'));
      }

      if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
        base_qry += " order by lower(p.${params.sort}) ${params.order}"
      }
      else {
        base_qry += " order by lower(p.name) asc"
      }


      log.debug(base_qry)
      result.packageInstanceTotal = Subscription.executeQuery("select count(p) "+base_qry, qry_params )[0]


      withFormat {
        html {
          result.packageInstanceList = Subscription.executeQuery("select p ${base_qry}", qry_params, [max:result.max, offset:result.offset]);
          result
        }
        csv {
           response.setHeader("Content-disposition", "attachment; filename=packages.csv")
           response.contentType = "text/csv"
           def packages = Subscription.executeQuery("select p ${base_qry}", qry_params) 
           def out = response.outputStream
	   log.debug('colheads');
           out.withWriter { writer ->
             writer.write('Package Name, Creation Date, Last Modified, Identifier\n');
             packages.each { 
               log.debug(it);
               writer.write("${it.name},${it.dateCreated},${it.lastUpdated},${it.identifier}\n")
             }
             writer.write("END");
             writer.flush();
             writer.close();
           }
           out.close()
        }
      }

    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def consortia(){

      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      result.packageInstance = Package.get(params.id)
      result.editable=isEditable()
      result.id = params.id
      def packageInstance = result.packageInstance
      def consortia = packageInstance.getConsortia()

      def type = RefdataCategory.lookupOrCreate('Organisational Role', 'Package Consortia')
      def consortiaInstitutions = Combo.findAllByToOrgAndType(consortia,type).collect{it.fromOrg}

      def consortiaInstsWithStatus = [:]
      def hql = "SELECT role.org FROM OrgRole as role WHERE role.org = ? AND role.roleType.value = 'Subscriber'  AND ( EXISTS ( select sp from role.sub.packages as sp where sp.pkg = ? ) )"
      consortiaInstitutions.each{org ->
        def queryParams = [org,packageInstance]
        def hasPackage = OrgRole.executeQuery(hql,  queryParams)
        if(hasPackage){
          consortiaInstsWithStatus.put(org,RefdataCategory.lookupOrCreate("YNO","Yes"))
        }else{
          consortiaInstsWithStatus.put(org,RefdataCategory.lookupOrCreate("YNO","No"))
        }
      }
      result.consortia = consortia
      result.consortiaInstsWithStatus = consortiaInstsWithStatus

      // log.debug("institutions with status are ${consortiaInstsWithStatus}")
      
      
      result
    }
    def generateSlaveSubscriptions(){
      params.each { p ->
        if(p.key.startsWith("_create.")){
         def orgID = p.key.substring(8)
         def orgaisation = Org.get(orgID)
         log.debug("Create slave subscription for ${orgaisation.name}")
         if(orgaisation)
          createNewSubscription(orgaisation,params.id);

        }
      }
      redirect controller:'packageDetails', action:'consortia', params: [id:params.id]
    }

    def createNewSubscription(org,packageId){
      //Initialize default subscription values
      def cal = new java.util.GregorianCalendar()
      def sdf = new SimpleDateFormat('yyyy-MM-dd')

      cal.setTimeInMillis(System.currentTimeMillis())
      cal.set(Calendar.MONTH, Calendar.JANUARY)
      cal.set(Calendar.DAY_OF_MONTH, 1)
      def defaultStartYear = (cal.getTime())
      cal.set(Calendar.MONTH, Calendar.DECEMBER)
      cal.set(Calendar.DAY_OF_MONTH, 31)
      def defaultEndYear = (cal.getTime())
      def defaultSubIdentifier = java.util.UUID.randomUUID().toString()

      // initialize the subscription
      //  def new_sub = new Subscription(type: RefdataValue.findByValue("Subscription Taken"),
      //               status: RefdataCategory.lookupOrCreate('Subscription Status', 'Current'),
      //               name: "Generated slave sub",
      //               startDate: defaultStartYear,
      //               endDate: defaultEndYear,
      //               identifier: defaultSubIdentifier,
      //               isPublic: RefdataCategory.lookupOrCreate('YN', 'No'),
      //               slaved: true,
      //               impId: defaultSubIdentifier)
      // if (new_sub.save(failOnError: true)) 
      // {
      //   log.debug("New subscription saved ${new_sub}")
      //   def new_sub_link = new OrgRole(org: org,
      //           sub: new_sub,
      //           roleType: RefdataCategory.lookupOrCreate('Organisational Role', 'Subscriber')).save(failOnError: true);
      // }
      //Link to package 

      def pkg_to_link = Package.get(packageId)
      pkg_to_link.createSubscription("Subscription Taken", "Generated slave sub", defaultSubIdentifier,
        defaultStartYear,defaultEndYear,org, "Subscriber", true, true)

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
    def compare(){
        def result = [:]
        result.unionList=[]
      
        result.user = User.get(springSecurityService.principal.id)
        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

        if (params.pkgA?.length() >0 && params.pkgB?.length() >0 ){

          result.pkgA = params.pkgA
          result.pkgB = params.pkgB
          result.dateA = params.dateA
          result.dateB = params.dateB

          result.pkgInsts = []
          result.pkgDates = []

          def listA = createCompareList(params.pkgA, params.dateA, params, result)
          def listB = createCompareList(params.pkgB, params.dateB, params, result)

          //FIXME: It should be possible to optimize the following lines
          def unionList = listA.collect{it.title.title}.plus(listB.collect {it.title.title})
          unionList = unionList.unique()
          result.unionListSize = unionList.size()
          unionList.sort()
          
          log.debug("List sizes are ${listA.size()} and ${listB.size()} and the union is ${unionList.size()}")

          def toIndex = result.offset+result.max < unionList.size()? result.offset+result.max: unionList.size()
          unionList = unionList.subList(result.offset, toIndex.intValue())
          result.listA = listA
          result.listB = listB
          result.unionList = unionList

        }else{
          def currentDate = new java.text.SimpleDateFormat('yyyy-MM-dd').format(new Date())
          result.dateA = currentDate
          result.dateB = currentDate
          flash.message = "Please select two packages for comparison"
        }
      
        result
    }
    def createCompareList(pkg,dateStr,params, result){
       def returnVals = [:]
       def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd')
       def date = dateStr?sdf.parse(dateStr):new Date()
       def packageId = pkg.substring( pkg.indexOf(":")+1)
        
       def packageInstance = Package.get(packageId)

       result.pkgInsts.add(packageInstance)

       result.pkgDates.add(sdf.format(date))

       def queryParams = [packageInstance]         

       def query = generateBasePackageQuery(params,queryParams, true, date)
       def list = TitleInstancePackagePlatform.executeQuery("select tipp "+query,  queryParams);
   
       return list
    }
    
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
      def verystarttime = exportService.printStart("SubscriptionDetails")
    
      def result = [:]
      boolean showDeletedTipps=false

      result.transforms = grailsApplication.config.packageTransforms
      
      if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') ) {
        result.editable=true
        showDeletedTipps=true
      }
      else {
        result.editable=false
      }

      result.user = User.get(springSecurityService.principal.id)
      def packageInstance = Package.get(params.id)
      if (!packageInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
        redirect action: 'list'
        return
      }

      def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")

      result.pendingChanges = PendingChange.executeQuery("select pc from PendingChange as pc where pc.pkg=? and ( pc.status is null or pc.status = ? ) order by ts desc", [packageInstance, pending_change_pending_status]);

      log.debug("Package has ${result.pendingChanges?.size()} pending changes");

      result.pkg_link_str="${grailsApplication.config.SystemBaseURL}/packageDetails/show/${params.id}"

      if ( packageInstance.forumId != null ) {
        result.forum_url = "${grailsApplication.config.ZenDeskBaseURL}/forums/${packageInstance.forumId}"
      }

      result.subscriptionList=[]
      // We need to cycle through all the users institutions, and their respective subscripions, and add to this list
      // and subscription that does not already link this package
      result.user?.getAuthorizedAffiliations().each { ua ->
        if ( ua.formalRole.authority == 'INST_ADM' ) {
          def qry_params = [ua.org, packageInstance, new Date()]
          def q = "select s from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) AND ( not exists ( select sp from s.packages as sp where sp.pkg = ? ) ) AND s.endDate >= ?"
          Subscription.executeQuery(q, qry_params).each { s ->
            if ( ! result.subscriptionList.contains(s) ) {
              // Need to make sure that this package is not already linked to this subscription
              result.subscriptionList.add([org:ua.org,sub:s])
            }
          }
        }
      }
    
      result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
      params.max = result.max
      def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
      result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
    
      def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]
    
      // def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.pkg = ? "
      def qry_params = [packageInstance]

      def date_filter
      if(params.mode == 'advanced'){
         date_filter = null
         params.asAt = null
      }else if(params.asAt && params.asAt.length() > 0 ) {
         def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
         date_filter = sdf.parse(params.asAt)    
         result.editable= false
      }else{
         date_filter = new Date()
      }

      def base_qry = generateBasePackageQuery(params, qry_params, showDeletedTipps, date_filter);

      // log.debug("Base qry: ${base_qry}, params: ${qry_params}, result:${result}");
      result.titlesList = TitleInstancePackagePlatform.executeQuery("select tipp "+base_qry, qry_params, limits);
      result.num_tipp_rows = TitleInstancePackagePlatform.executeQuery("select count(tipp) "+base_qry, qry_params )[0]

      result.lasttipp = result.offset + result.max > result.num_tipp_rows ? result.num_tipp_rows : result.offset + result.max;


      result.packageInstance = packageInstance
    
    def filename = "packageDetails_${result.packageInstance.name}"
    withFormat {
      html result
      json {
        def map = exportService.getPackageMap(packageInstance, result.titlesList)
        
        def json = map as JSON
        
          response.setHeader("Content-disposition", "attachment; filename=\"${filename}.json\"")
          response.contentType = "application/json"
          render json
      }
      xml {
        def starttime = exportService.printStart("Building XML Doc")
        def doc = exportService.buildDocXML("Packages")
        exportService.addPackageIntoXML(doc, doc.getDocumentElement(), packageInstance, result.titlesList)
        exportService.printDuration(starttime, "Building XML Doc")
        
        if( ( params.transformId ) && ( result.transforms[params.transformId] != null ) ) {
          String xml = exportService.streamOutXML(doc, new StringWriter()).getWriter().toString();
          transformerService.triggerTransform(result.user, filename, result.transforms[params.transformId], xml, response)
        }else{ // send the XML to the user
          response.setHeader("Content-disposition", "attachment; filename=\"${filename}.xml\"")
          response.contentType = "text/xml"
          exportService.streamOutXML(doc, response.outputStream)
        }

      }
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def current() {
    log.debug("current ${params}");
    def result = [:]
    boolean showDeletedTipps=false
    result.user = User.get(springSecurityService.principal.id)
    result.editable=isEditable()

    def packageInstance = Package.get(params.id)
    if (!packageInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
      redirect action: 'list'
      return
    }
    result.packageInstance = packageInstance

    result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize
    params.max = result.max
    def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]

    // def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.pkg = ? "
    def qry_params = [packageInstance]
    def date_filter =  params.mode == 'advanced' ? null : new Date();

    def base_qry = generateBasePackageQuery(params, qry_params, showDeletedTipps, date_filter);

    log.debug("Base qry: ${base_qry}, params: ${qry_params}, result:${result}");
    result.titlesList = TitleInstancePackagePlatform.executeQuery("select tipp "+base_qry, qry_params, limits);
    result.num_tipp_rows = TitleInstancePackagePlatform.executeQuery("select count(tipp) "+base_qry, qry_params )[0]

    result.lasttipp = result.offset + result.max > result.num_tipp_rows ? result.num_tipp_rows : result.offset + result.max;


    result
  }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def deleteDocuments() {
        def ctxlist = []

        log.debug("deleteDocuments ${params}");

        params.each { p ->
            if (p.key.startsWith('_deleteflag"@.') ) {
                def docctx_to_delete = p.key.substring(12);
                log.debug("Looking up docctx ${docctx_to_delete} for delete");
                def docctx = DocContext.get(docctx_to_delete)
                docctx.status = RefdataCategory.lookupOrCreate('Document Context Status','Deleted');
            }
        }

        redirect controller: 'packageDetails', action:params.redirectAction, id:params.instanceId
    }



  @Secured(['ROLE_USER','IS_AUTHENTICATED_FULLY'])
  def documents() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      result.packageInstance = Package.get(params.id)
      result.editable=isEditable()

      result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def expected() {
    log.debug("expected ${params}");
    def result = [:]
    boolean showDeletedTipps=false
    result.user = User.get(springSecurityService.principal.id)
    result.editable=isEditable()
    def packageInstance = Package.get(params.id)
    if (!packageInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
      redirect action: 'list'
      return
    }
    result.packageInstance = packageInstance

    result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize
    params.max = result.max
    def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]

    // def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.pkg = ? "
    def qry_params = [packageInstance]
    def date_filter =  params.mode == 'advanced' ? null : new Date();

    def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.pkg = ? "
    base_qry += "and tipp.status.value != 'Deleted' "
    if ( date_filter != null ) {
      base_qry += " and ( coalesce(tipp.accessStartDate, tipp.pkg.startDate) >= ? ) "
      qry_params.add(date_filter);
    }



    log.debug("Base qry: ${base_qry}, params: ${qry_params}, result:${result}");
    result.titlesList = TitleInstancePackagePlatform.executeQuery("select tipp "+base_qry, qry_params, limits);
    result.num_tipp_rows = TitleInstancePackagePlatform.executeQuery("select count(tipp) "+base_qry, qry_params )[0]

    result.lasttipp = result.offset + result.max > result.num_tipp_rows ? result.num_tipp_rows : result.offset + result.max;

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def previous() {
    log.debug("previous ${params}");
    def result = [:]
    boolean showDeletedTipps=false
    result.user = User.get(springSecurityService.principal.id)
    result.editable=isEditable()

    def packageInstance = Package.get(params.id)
    if (!packageInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
      redirect action: 'list'
      return
    }
    result.packageInstance = packageInstance

    result.max = params.max ? Integer.parseInt(params.max) : 25
    params.max = result.max
    def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]

    // def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.pkg = ? "
    def qry_params = [packageInstance]
    def date_filter =  params.mode == 'advanced' ? null : new Date();

    def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.pkg = ? "
    base_qry += "and tipp.status.value != 'Deleted' "
    if ( date_filter != null ) {
      base_qry += " and ( tipp.accessEndDate <= ? ) "
      qry_params.add(date_filter);
    }

    log.debug("Base qry: ${base_qry}, params: ${qry_params}, result:${result}");
    result.titlesList = TitleInstancePackagePlatform.executeQuery("select tipp "+base_qry, qry_params, limits);
    result.num_tipp_rows = TitleInstancePackagePlatform.executeQuery("select count(tipp) "+base_qry, qry_params )[0]

    result.lasttipp = result.offset + result.max > result.num_tipp_rows ? result.num_tipp_rows : result.offset + result.max;

    result
  }


  def generateBasePackageQuery(params, qry_params, showDeletedTipps, asAt) {

    def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.pkg = ? "

     if ( showDeletedTipps != true ) {
         base_qry += "and tipp.status.value != 'Deleted' "
     }

    if ( asAt != null ) {
      base_qry += " and ( ( ? >= coalesce(tipp.accessStartDate, tipp.pkg.startDate) ) and ( ( ? <= tipp.accessEndDate ) or ( tipp.accessEndDate is null ) ) ) "
      qry_params.add(asAt);
      qry_params.add(asAt);
    }

    if ( params.filter ) {
      base_qry += " and ( ( lower(tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = tipp.title.id and io.identifier.value like ? ) ) )"
      qry_params.add("%${params.filter.trim().toLowerCase()}%")
      qry_params.add("%${params.filter}%")
    }

    if ( params.coverageNoteFilter ) {
      base_qry += "and lower(tipp.coverageNote) like ?"
      qry_params.add("%${params.coverageNoteFilter?.toLowerCase()}%")
    }

    if ( params.endsAfter && params.endsAfter.length() > 0 ) {
      def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
      def d = sdf.parse(params.endsAfter)
      base_qry += " and tipp.endDate >= ?"
      qry_params.add(d)
    }

    if ( params.startsBefore && params.startsBefore.length() > 0 ) {
      def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
      def d = sdf.parse(params.startsBefore)
      base_qry += " and tipp.startDate <= ?"
      qry_params.add(d)
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += " order by lower(${params.sort}) ${params.order}"
    }
    else {
      base_qry += " order by lower(tipp.title.title) asc"
    }

    return base_qry
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
          case 'endYear':
                sw.append('endYear')
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

          params.max = Math.min(params.max ? params.int('max') : result.user.defaultPageSize, 100)
          params.offset = params.offset ? params.int('offset') : 0

          if(params.search.equals("yes")){
              //when searching make sure results start from first page
            params.offset = 0
            params.search = ""
          }
          //def params_set=params.entrySet()

          def query_str = buildPackageQuery(params)
          if ( fq ) 
            query_str = query_str + " AND ( " + fq + " ) "
          
          log.debug("query: ${query_str} sort: ${params.sorting}");
          result.es_query = query_str;

          // if params.sorting==lastmod

         def search = esclient.search{
            indices grailsApplication.config.aggr.es.index ?: "kbplus"
            source {
              from = params.offset
              size = params.max
              sort = [
                ("${params.sorting?:'sortname'}".toString()) : [ 'order' : (params.order?:'desc') ]
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
                endYear {
                  terms {
                    field = 'endYear'
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

  def isEditable(){
      if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') ) {
          return true
      }
      else {
          return false
      }
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
                sw.write("(${p})")
          }
        }
        else {
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *
          if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
            sw.write(" AND ")
            sw.write(mapping.value)
            sw.write(":")
            sw.write("(${params[mapping.key]})")
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


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def notes() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.packageInstance = Package.get(params.id)
    result.editable=isEditable()
    result
  }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def packageBatchUpdate() {

    def packageInstance = Package.get(params.id)
    boolean showDeletedTipps=false

    if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') ) {
      showDeletedTipps=true
    }

    log.debug("packageBatchUpdate ${params}");

    def formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")

    def bulk_fields = [
      [ formProp:'start_date', domainClassProp:'startDate', type:'date'],
      [ formProp:'start_volume', domainClassProp:'startVolume'],
      [ formProp:'start_issue', domainClassProp:'startIssue'],
      [ formProp:'end_date', domainClassProp:'endDate', type:'date'],
      [ formProp:'end_volume', domainClassProp:'endVolume'],
      [ formProp:'end_issue', domainClassProp:'endIssue'],
      [ formProp:'coverage_depth', domainClassProp:'coverageDepth'],
      [ formProp:'coverage_note', domainClassProp:'coverageNote'],
      [ formProp:'embargo', domainClassProp:'embargo'],
      [ formProp:'delayedOA', domainClassProp:'delayedOA', type:'ref'],
      [ formProp:'hybridOA', domainClassProp:'hybridOA', type:'ref'],
      [ formProp:'payment', domainClassProp:'payment', type:'ref'],
      [ formProp:'hostPlatformURL', domainClassProp:'hostPlatformURL'],
    ]

    
    if ( params.BatchSelectedBtn=='on' ) {
      log.debug("Apply batch changes - selected")
      params.filter=null //remove filters
      params.coverageNoteFilter=null
      params.startsBefore=null
      params.endsAfter=null
      params.each { p ->
        if (p.key.startsWith('_bulkflag.') && ( p.value == 'on' ) ) {
          def tipp_id_to_edit = p.key.substring(10);
          log.debug("row selected for bulk edit: ${tipp_id_to_edit}");
          def tipp_to_bulk_edit = TitleInstancePackagePlatform.get(tipp_id_to_edit);
          boolean changed = false
  
          if ( params.bulkOperation=='edit') {
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
                          else if ( bulk_field_defn.type == 'ref' ) {
                            tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = genericOIDService.resolveOID(proposed_value)
                          }
                          else {
                              tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = proposed_value
                          }
                          changed = true
                      }
              }
            }
            if ( changed )
              tipp_to_bulk_edit.save();
          }
          else {
            log.debug("Bulk removal ${tipp_to_bulk_edit.id}");
            tipp_to_bulk_edit.status = RefdataCategory.lookupOrCreate( 'TIPP Status', 'Deleted' );
            tipp_to_bulk_edit.save();
          }
        }
      }
    }
    else if ( params.BatchAllBtn=='on' ) {
      log.debug("Batch process all filtered by: "+params.filter);
      def qry_params = [packageInstance]
      def base_qry = generateBasePackageQuery(params, qry_params, showDeletedTipps, new Date())
      def tipplist = TitleInstancePackagePlatform.executeQuery("select tipp "+base_qry, qry_params)
      tipplist.each {  tipp_to_bulk_edit ->
        boolean changed=false
        log.debug("update tipp ${tipp_to_bulk_edit.id}");
        if ( params.bulkOperation=='edit') {
          bulk_fields.each { bulk_field_defn ->
            if ( params["clear_${bulk_field_defn.formProp}"] == 'on' ) {
              log.debug("Request to clear field ${bulk_field_defn.formProp}");
              tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = null
              changed = true
            }
            else {
              def proposed_value = params['bulk_'+bulk_field_defn.formProp]
              if ( ( proposed_value != null ) && ( proposed_value.length() > 0 ) ) {
                log.debug("Set field ${bulk_field_defn.formProp} to proposed_value");
                if ( bulk_field_defn.type == 'date' ) {
                  tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = formatter.parse(proposed_value)
                }
                else if ( bulk_field_defn.type == 'ref' ) {
                  tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = genericOIDService.resolveOID(proposed_value)
                }
                else {
                  tipp_to_bulk_edit[bulk_field_defn.domainClassProp] = proposed_value
                }
                changed = true
              }
            }
          }
          if ( changed )
            tipp_to_bulk_edit.save();
        }
      }
    }

    redirect(action:'show', params:[id:params.id,sort:params.sort,order:params.order,max:params.max,offset:params.offset]);
  }
}

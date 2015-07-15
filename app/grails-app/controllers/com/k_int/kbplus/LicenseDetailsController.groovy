package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.StreamingMarkupBuilder
import com.k_int.kbplus.auth.*;
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent
import org.springframework.security.access.annotation.Secured

@Mixin(com.k_int.kbplus.mixins.PendingChangeMixin)
class LicenseDetailsController {

  def springSecurityService
  def docstoreService
  def ESWrapperService
  def gazetteerService
  def alertsService
  def genericOIDService
  def transformerService
  def exportService
  def institutionsService
  def pendingChangeService
  def executorWrapperService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    log.debug("licenseDetails: ${params}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    // result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)
    result.transforms = grailsApplication.config.licenceTransforms

    if ( ! result?.license?.hasPerm("view",result.user) ) {
      log.debug("return 401....");
      flash.error = "You do not have permission to view ${result.license.reference}. Please request access to ${result.license?.licensee?.name?:'licence institution'} on the profile page";
      response.sendError(401);
      return
    }

    if ( result.license.hasPerm("edit",result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }
  
    def license_reference_str = result.license.reference?:'NO_LIC_REF_FOR_ID_'+params.id

    def filename = "licenceDetails_${license_reference_str.replace(" ", "_")}"
    result.onixplLicense = result.license.onixplLicense;

    def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")
    def pendingChanges = PendingChange.executeQuery("select pc.id from PendingChange as pc where license=? and ( pc.status is null or pc.status = ? ) order by pc.ts desc", [result.license, pending_change_pending_status]);

      //Filter any deleted subscriptions out of displayed links
      Iterator<Subscription> it = result.license.subscriptions.iterator()
      while(it.hasNext()){
          def sub = it.next();
          if(sub.status == RefdataCategory.lookupOrCreate('Subscription Status','Deleted')){
              it.remove();
          }
      }

    log.debug("pc result is ${result.pendingChanges}");
    if(result.license.incomingLinks.find{it?.isSlaved?.value == "Yes"} && pendingChanges){
      log.debug("Slaved lincence, auto-accept pending changes")
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
    if(executorWrapperService.hasRunningProcess(result.license)){
      result.processingpc = true
    }
    result.availableSubs = getAvailableSubscriptions(result.license,result.user)

    withFormat {
      html result
      json {
        def map = exportService.addLicensesToMap([:], [result.license])
        
        def json = map as JSON
        response.setHeader("Content-disposition", "attachment; filename=\"${filename}.json\"")
        response.contentType = "application/json"
        render json.toString()
      }
      xml {
        def doc = exportService.buildDocXML("Licences")
        if(params.format_content=="subpkg"){
            exportService.addLicenceSubPkgXML(doc, doc.getDocumentElement(),[result.license])
        }else if(params.format_content=="subie"){
            exportService.addLicenceSubPkgTitleXML(doc, doc.getDocumentElement(),[result.license])
        }else if(!params.format_content){
          exportService.addLicencesIntoXML(doc, doc.getDocumentElement(), [result.license])
        }
        if ((params.transformId) && (result.transforms[params.transformId] != null)) {
            String xml = exportService.streamOutXML(doc, new StringWriter()).getWriter().toString();
            transformerService.triggerTransform(result.user, filename, result.transforms[params.transformId], xml, response)
        }else{
            response.setHeader("Content-disposition", "attachment; filename=\"${filename}.xml\"")
            response.contentType = "text/xml"
            exportService.streamOutXML(doc, response.outputStream)
        }
        
      }
      csv {
        response.setHeader("Content-disposition", "attachment; filename=\"${filename}.csv\"")
        response.contentType = "text/csv"
        def out = response.outputStream
        exportService.StreamOutLicenceCSV(out,null,[result.license])
        out.close()
      }
    }
  }
  def getAvailableSubscriptions(licence,user){
    def licenceInstitutions = licence?.orgLinks?.findAll{ orgRole ->
      orgRole.roleType.value == "Licensee"
    }?.collect{  it.org?.hasUserWithRole(user,'INST_ADM')?it.org:null  }

    def subscriptions = null
    if(licenceInstitutions){
      def sdf = new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)
      def date_restriction =  new Date(System.currentTimeMillis())

      def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org in (:orgs) ) ) ) AND ( s.status.value != 'Deleted' ) AND (s.owner = null) "
      def qry_params = [orgs:licenceInstitutions]
      base_qry += " and s.startDate <= (:start) and s.endDate >= (:start) "
      qry_params.putAll([start:date_restriction])
      subscriptions = Subscription.executeQuery("select s ${base_qry}", qry_params)
    }
    return subscriptions
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def linkToSubscription(){
    log.debug("linkToSubscription :: ${params}")
    if(params.subscription && params.licence){
      def sub = Subscription.get(params.subscription)
      def owner = License.get(params.licence)
      owner.addToSubscriptions(sub)
      owner.save(flush:true)
    }
    redirect controller:'licenseDetails', action:'index', params: [id:params.licence]

  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def consortia() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.licence = License.get(params.id)
   
    def hasAccess
    def isAdmin
    if (result.user.getAuthorities().contains(Role.findByAuthority('ROLE_ADMIN'))) {
        isAdmin = true;
    }else{
       hasAccess = result.licence.orgLinks.find{it.roleType.value == 'Licensing Consortium' &&
      it.org.hasUserWithRole(result.user,'INST_ADM') }
    }
    if( !isAdmin && (result.licence.licenseType != "Template" || hasAccess == null)) {
      flash.error = message(code:'licence.consortia.access.error')
      response.sendError(401) 
      return
    }
    if ( result.licence.hasPerm("edit",result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    log.debug("${result.licence}")
    def consortia = result.licence?.orgLinks?.find{
      it.roleType.value == 'Licensing Consortium'}?.org

    if(consortia){
      result.consortia = consortia
      result.consortiaInstsWithStatus = []
    def type = RefdataCategory.lookupOrCreate('Combo Type', 'Consortium')
    def institutions_in_consortia_hql = "select c.fromOrg from Combo as c where c.type = ? and c.toOrg = ? order by c.fromOrg.name"
    def consortiaInstitutions = Combo.executeQuery(institutions_in_consortia_hql, [type, consortia])

     result.consortiaInstsWithStatus = [ : ]
     def findOrgLicences = "SELECT lic from License AS lic WHERE exists ( SELECT link from lic.orgLinks AS link WHERE link.org = ? and link.roleType.value = 'Licensee') AND exists ( SELECT incLink from lic.incomingLinks AS incLink WHERE incLink.fromLic = ? ) AND lic.status.value != 'Deleted'"
     consortiaInstitutions.each{ 
        def queryParams = [ it, result.licence]
        def hasLicence = License.executeQuery(findOrgLicences, queryParams)
        if (hasLicence){
          result.consortiaInstsWithStatus.put(it, RefdataCategory.lookupOrCreate("YNO","Yes") )    
        }else{
          result.consortiaInstsWithStatus.put(it, RefdataCategory.lookupOrCreate("YNO","No") )    
        }
      }
    }else{
      flash.error=message(code:'licence.consortia.noneset')
    }

    result
  }
  
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def generateSlaveLicences(){
    def slaved = RefdataCategory.lookupOrCreate('YN','Yes')
    params.each { p ->
        if(p.key.startsWith("_create.")){
         def orgID = p.key.substring(8)
         def orgaisation = Org.get(orgID)
          def attrMap = [shortcode:orgaisation.shortcode,baselicense:params.baselicense,lic_name:params.lic_name,isSlaved:slaved]
          log.debug("Create slave licence for ${orgaisation.name}")
          attrMap.copyStartEnd = true
          institutionsService.copyLicence(attrMap);          
        }
    }
    redirect controller:'licenseDetails', action:'consortia', params: [id:params.baselicense]
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def links() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    // result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)

    if ( result.license.hasPerm("edit",result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }


    if ( ! result.license.hasPerm("view",result.user) ) {
      response.sendError(401);
      return
    }

    result
  }
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def edit_history() {
    log.debug("licenseDetails::edit_history : ${params}");

    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.license = License.get(params.id)

    if ( ! result.license.hasPerm("view",result.user) ) {
      response.sendError(401);
      return
    }

    if ( result.license.hasPerm("edit",result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
    result.offset = params.offset ?: 0;


    def qry_params = [licClass:result.license.class.name, prop:LicenseCustomProperty.class.name,owner:result.license, licId:"${result.license.id}"]

    result.historyLines = AuditLogEvent.executeQuery("select e from AuditLogEvent as e where (( className=:licClass and persistedObjectId=:licId ) or (className = :prop and persistedObjectId in (select lp.id from LicenseCustomProperty as lp where lp.owner=:owner))) order by e.dateCreated desc", qry_params, [max:result.max, offset:result.offset]);
    
    def propertyNameHql = "select pd.name from LicenseCustomProperty as licP, PropertyDefinition as pd where licP.id= ? and licP.type = pd"
    
    result.historyLines?.each{
      if(it.className == qry_params.prop ){
        def propertyName = LicenseCustomProperty.executeQuery(propertyNameHql,[it.persistedObjectId.toLong()])[0]
        it.propertyName = propertyName
      }
    }

    result.historyLinesTotal = AuditLogEvent.executeQuery("select count(e.id) from AuditLogEvent as e where ( (className=:licClass and persistedObjectId=:licId) or (className = :prop and persistedObjectId in (select lp.id from LicenseCustomProperty as lp where lp.owner=:owner))) ",qry_params)[0];

    result

  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def todo_history() {
    log.debug("licenseDetails::todo_history : ${params}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.license = License.get(params.id)

    if ( ! result.license.hasPerm("view",result.user) ) {
      response.sendError(401);
      return
    }

    if ( result.license.hasPerm("edit",result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }
    result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
    result.offset = params.offset ?: 0;

    result.todoHistoryLines = PendingChange.executeQuery("select pc from PendingChange as pc where pc.license=? order by pc.ts desc", [result.license],[max:result.max,offset:result.offset]);

    result.todoHistoryLinesTotal = PendingChange.executeQuery("select count(pc) from PendingChange as pc where pc.license=? order by pc.ts desc", [result.license])[0];
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def notes() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    // result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)

    if ( ! result.license.hasPerm("view",result.user) ) {
      response.sendError(401);
      return
    }

    if ( result.license.hasPerm("edit",result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def documents() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.license = License.get(params.id)

    if ( ! result.license.hasPerm("view",result.user) ) {
      response.sendError(401);
      return
    }

    if ( result.license.hasPerm("edit",result.user) ) {
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

    def user = User.get(springSecurityService.principal.id)
    def l = License.get(params.instanceId);

    if ( ! l.hasPerm("edit",user) ) {
      response.sendError(401);
      return
    }

    params.each { p ->
      if (p.key.startsWith('_deleteflag.') ) {
        def docctx_to_delete = p.key.substring(12);
        log.debug("Looking up docctx ${docctx_to_delete} for delete");
        def docctx = DocContext.get(docctx_to_delete)
        docctx.status = RefdataCategory.lookupOrCreate('Document Context Status','Deleted');
        docctx.save(flush:true);
      }
    }

    redirect controller: 'licenseDetails', action:params.redirectAction, params:[shortcode:params.shortcode], id:params.instanceId, fragment:'docstab'
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def acceptChange() {
    processAcceptChange(params, License.get(params.id), genericOIDService)
    redirect controller: 'licenseDetails', action:'index',id:params.id
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def rejectChange() {
    processRejectChange(params, License.get(params.id))
    redirect controller: 'licenseDetails', action:'index',id:params.id
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def additionalInfo() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.license = License.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def create() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def processNewTemplateLicense() {
    if ( params.reference && ( ! params.reference.trim().equals('') ) ) {

      def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');
      def license_status_current = RefdataCategory.lookupOrCreate('License Status','Current');
      
      def new_template_license = new License(reference:params.reference,
                                             type:template_license_type,
                                             status:license_status_current).save(flush:true);
      redirect(action:'index', id:new_template_license.id);
    }
    else {
      redirect(action:'create');
    }
  }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def unlinkLicense() {
        log.debug("unlinkLicense :: ${params}")
        License license = License.get(params.license_id);
        OnixplLicense opl = OnixplLicense.get(params.opl_id);
        if(! (opl && license)){
          log.error("Something has gone mysteriously wrong. Could not get Licence or OnixLicence. params:${params} license:${license} onix: ${opl}")
          flash.message = "An error occurred when unlinking the ONIX-PL license";
          redirect(action: 'index', id: license.id);
        }

        String oplTitle = opl?.title;
        DocContext dc = DocContext.findByOwner(opl.doc);
        Doc doc = opl.doc;
        license.removeFromDocuments(dc);
        opl.removeFromLicenses(license);
        // If there are no more links to this ONIX-PL License then delete the license and
        // associated data
        if (opl.licenses.isEmpty()) {
            opl.usageTerm.each{
              it.usageTermLicenseText.each{
                it.delete()
              }
            }
            opl.delete();
            dc.delete();
            doc.delete();
        }
        if (license.hasErrors()) {
            license.errors.each {
                log.error("License error: " + it);
            }
            flash.message = "An error occurred when unlinking the ONIX-PL license '${oplTitle}'";
        } else {
            flash.message = "The ONIX-PL license '${oplTitle}' was unlinked successfully";
        }
        redirect(action: 'index', id: license.id);
    }
}

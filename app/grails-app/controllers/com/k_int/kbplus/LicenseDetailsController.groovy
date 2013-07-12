package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent

@Mixin(com.k_int.kbplus.mixins.PendingChangeMixin)
class LicenseDetailsController {

  def springSecurityService
  def docstoreService
  def ESWrapperService
  def gazetteerService
  def alertsService
  def genericOIDService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    // result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)

    if ( ! result.license.hasPerm("view",result.user) ) {
      log.debug("return 401....");
      response.sendError(401);
      return
    }

    if ( result.license.hasPerm("edit",result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    withFormat {
		  html result
		  json {
			  def formatter = new java.text.SimpleDateFormat("yyyy/MM/dd")
			  
			  def response = [:]
			  def licences = []
			  
			  def lic = [:]
			  def licence = result.license
			  
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
			  wa."Notes" = licence.getNote("walkinAccess")?.owner?.content?:""
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
			  
			  licences << lic
			  response."Licences" = licences
			  
			  render response as JSON
		  }
		  xml {
			  def formatter = new java.text.SimpleDateFormat("yyyy/MM/dd")
			  
			  def writer = new StringWriter()
			  def xmlBuilder = new MarkupBuilder(writer)
			  xmlBuilder.getMkp().xmlDeclaration(version:'1.0', encoding: 'UTF-8')
			  
			  def licence = result.license
			  
			  xmlBuilder.Licences() {
				  Licence(){
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
			  
			  render writer.toString()
		  }
    }
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
  def history() {
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

    result.max = params.max ?: 20;
    result.offset = params.offset ?: 0;

    def qry_params = [result.license.class.name, "${result.license.id}"]
    result.historyLines = AuditLogEvent.executeQuery("select e from AuditLogEvent as e where className=? and persistedObjectId=? order by id desc", qry_params, [max:result.max, offset:result.offset]);
    result.historyLinesTotal = AuditLogEvent.executeQuery("select count(e.id) from AuditLogEvent as e where className=? and persistedObjectId=?",qry_params)[0];

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
  def uploadDocument() {
    log.debug("upload document....");

    def user = User.get(springSecurityService.principal.id)

    def l = License.get(params.licid);

    if ( ! l.hasPerm("edit",result.user) ) {
      response.sendError(401);
      return
    }

    def input_stream = request.getFile("upload_file")?.inputStream
    def original_filename = request.getFile("upload_file")?.originalFilename

    log.debug("uploadDocument ${params} upload file = ${original_filename}");

    if ( l && input_stream ) {
      def docstore_uuid = docstoreService.uploadStream(input_stream, original_filename, params.upload_title)
      log.debug("Docstore uuid is ${docstore_uuid}");

      if ( docstore_uuid ) {
        log.debug("Docstore uuid present (${docstore_uuid}) Saving info");
        def doc_content = new Doc(contentType:1,
                                  uuid: docstore_uuid,
                                  filename: original_filename,
                                  mimeType: request.getFile("upload_file")?.contentType,
                                  title: params.upload_title,
                                  type:RefdataCategory.lookupOrCreate('Document Type',params.doctype)).save()

        def doc_context = new DocContext(license:l,
                                         owner:doc_content,
                                         user: user,
                                         doctype:RefdataCategory.lookupOrCreate('Document Type',params.doctype)).save(flush:true);
      }
    }

    log.debug("Redirecting...");
    redirect controller: 'licenseDetails', action:'index', id:params.licid, fragment:params.fragment
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def deleteDocuments() {
    def ctxlist = []

    log.debug("deleteDocuments ${params}");

    def user = User.get(springSecurityService.principal.id)
    def l = License.get(params.licid);

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

    redirect controller: 'licenseDetails', action:'index', params:[shortcode:params.shortcode], id:params.licid, fragment:'docstab'
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
}

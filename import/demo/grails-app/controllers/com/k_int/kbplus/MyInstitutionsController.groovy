package com.k_int.kbplus

import com.k_int.kbplus.auth.*
import grails.plugins.springsecurity.Secured

class MyInstitutionsController {

  def springSecurityService
  def docstoreService


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    // Work out what orgs this user has admin level access to
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)

    def adminRole = Role.findByAuthority('ROLE_ADMIN')

    
    if ( result.user.authorities.contains(adminRole) ) {
      log.debug("User is in admin role");
      result.orgs = Org.findAllBySector("Higher Education");
    }
    else {
      result.orgs = Org.findAllBySector("Higher Education");
    }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def manageAffiliations() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def licenses() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
    def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');
    def model_licenses = License.findAllByType(template_license_type);

    // We want to find all org role objects for this instutution where role type is licensee
    result.licenses = []
    result.licenses.addAll(model_licenses)

    // Find all licenses for this institution...
    result.licenses.addAll(OrgRole.findAllByOrgAndRoleType(result.institution, licensee_role).collect { it.lic } )

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def subscriptions() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def processJoinRequest() {
    log.debug("processJoinRequest org with id ${params.org}");
    def user = User.get(springSecurityService.principal.id)
    def org = com.k_int.kbplus.Org.get(params.org)
    if ( ( org != null ) && ( params.role != null ) ) {
      def p = new UserOrg(dateRequested:System.currentTimeMillis(), 
                          status:0, 
                          org:org, 
                          user:user, 
                          role:params.role)
      p.save(flush:true)
    }
    redirect(action: "manageAffiliations")
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def newLicense() {
    def user = User.get(springSecurityService.principal.id)
    def org = Org.findByShortcode(params.shortcode)
    
    switch (request.method) {
      case 'GET':
        [licenseInstance: new License(params)]
        break
      case 'POST':
        def baseLicense = params.baselicense ? License.get(params.baselicense) : null;
        def license_type = RefdataCategory.lookupOrCreate('License Type','Actual')
        def license_status = RefdataCategory.lookupOrCreate('License Status','Current')
        def licenseInstance = new License(reference:params.new_license_ref_name, 
                                          type:license_type,
                                          concurrentUsers:baseLicense?.concurrentUsers,
                                          remoteAccess:baseLicense?.remoteAccess,
                                          walkinAccess:baseLicense?.walkinAccess,
                                          multisiteAccess:baseLicense?.multisiteAccess,
                                          partnersAccess:baseLicense?.partnersAccess,
                                          alumniAccess:baseLicense?.alumniAccess,
                                          ill:baseLicense?.ill,
                                          coursepack:baseLicense?.coursepack,
                                          vle:baseLicense?.vle,
                                          enterprise:baseLicense?.enterprise,
                                          pca:baseLicense?.pca,
                                          noticePeriod:baseLicense?.noticePeriod,
                                          licenseUrl:baseLicense?.licenseUrl)

        // the url will set the shortcode of the organisation that this license should be linked with.
        if (!licenseInstance.save(flush: true)) {
          log.error("Problem saving license ${licenseInstance.errors}");
          render view: 'editLicense', model: [licenseInstance: licenseInstance]
          return
        }
        else {
          log.debug("Save ok");
          def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee')
          log.debug("adding org link to new license");
          org.links.add(new OrgRole(lic:licenseInstance, org:org, roleType:licensee_role));
          if ( baseLicense?.licensor ) {
            def licensor_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensor')
            org.links.add(new OrgRole(lic:licenseInstance, org:baseLicense.licensor, roleType:licensor_role));
          }

          if ( org.save(flush:true) ) {
          }
          else {
            log.error("Problem saving org links to license ${org.errors}");
          }
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'license.label', default: 'License'), licenseInstance.id])
        redirect action: 'licenseDetails', params:params, id:licenseInstance.id
        //redirect action: 'show', id: licenseInstance.id
        break
    }

  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def licenseDetails() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)
    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def uploadDocument() {

    def input_stream = request.getFile("upload_file")?.inputStream
    def original_filename = request.getFile("upload_file")?.originalFilename
    def l = License.get(params.licid);

    log.debug("uploadDocument ${params} upload file = ${original_filename}");

    if ( l && input_stream ) {
      def docstore_uuid = docstoreService.uploadStream(input_stream, original_filename, params.upload_title)
      log.debug("Docstore uuid is ${docstore_uuid}");

      if ( docstore_uuid ) {
        log.debug("Docstore uuid present (${docstore_uuid}) Saving info");
        def doc_content = new Doc(contentType:1,
                                  uuid: docstore_uuid,
                                  filename: original_filename,
                                  title: params.upload_title,
                                  type:RefdataCategory.lookupOrCreate('Document Type','License')).save()
  
        def doc_context = new DocContext(license:l,
                                         owner:doc_content,
                                         doctype:RefdataCategory.lookupOrCreate('Document Type','License')).save(flush:true);
      }
    }

    log.debug("Redirecting...");
    redirect action: 'licenseDetails', params:[shortcode:params.shortcode], id:params.licid, fragment:'docstab'
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def deleteDocuments() {
    def doclist = []
    def ctxlist = []
    
    log.debug("deleteDocuments ${params}");

    params.each { p ->
      if (p.key.startsWith('_deleteflag.') ) {
        def docctx_to_delete = p.key.substring(12);
        log.debug("Looking up docctx ${docctx_to_delete}");
        def docctx = DocContext.get(docctx_to_delete)
        log.debug("Got docctx ${docctx.id} ${docctx.owner.uuid} ${docctx.owner.title}");
        doclist.add(docctx?.owner?.uuid);

        // Find all doc ctx entries that point to this document
        DocContext.findByOwner(docctx.owner).each { ctx ->
          ctx.delete(flush:true);
        }

        docctx.owner.delete(flush:true);
      }
    }

    docstoreService.deleteDocs(doclist);

   

    log.debug("done");

    redirect action: 'licenseDetails', params:[shortcode:params.shortcode], id:params.licid, fragment:'docstab'
  }
}

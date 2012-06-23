package com.k_int.kbplus

import com.k_int.kbplus.auth.*
import grails.plugins.springsecurity.Secured

class MyInstitutionsController {

  def springSecurityService


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
    // We want to find all org role objects for this instutution where role type is licensee
    result.licenses = OrgRole.findAllByOrgAndRoleType(result.institution, licensee_role)

    // Find all licenses for this institution...
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
    switch (request.method) {
      case 'GET':
        [licenseInstance: new License(params)]
        break
      case 'POST':
        def licenseInstance = new License(reference:params.new_license_ref_name)
        // the url will set the shortcode of the organisation that this license should be linked with.
        if (!licenseInstance.save(flush: true)) {
          render view: 'editLicense', model: [licenseInstance: licenseInstance]
          return
        }

        //flash.message = message(code: 'default.created.message', args: [message(code: 'license.label', default: 'License'), licenseInstance.id])
        //redirect action: 'show', id: licenseInstance.id
        break
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def editLicense() {
  }

}

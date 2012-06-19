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
  def manageLicenses() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
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

}

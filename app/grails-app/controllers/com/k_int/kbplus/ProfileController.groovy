package com.k_int.kbplus


import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class ProfileController {

  def springSecurityService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def processJoinRequest() {
    log.debug("processJoinRequest org with id ${params.org} role ${params.formalRole}");
    def user = User.get(springSecurityService.principal.id)
    def org = com.k_int.kbplus.Org.get(params.org)
    def formal_role = com.k_int.kbplus.auth.Role.get(params.formalRole)


    if ( ( org != null ) && ( formal_role != null ) ) {
      def existingRel = UserOrg.find( { org==org && user==user && formalRole==formal_role } )
      if ( existingRel ) {
        log.debug("existing rel");
        flash.error="You already have a relation with the requested organisation."
      }
      else {
        log.debug("Create new user_org entry....");
        def p = new UserOrg(dateRequested:System.currentTimeMillis(),
                            status:0,
                            org:org,
                            user:user,
                            formalRole:formal_role)
        p.save(flush:true)
      }
    }
    else {
      log.error("Unable to locate org or role");
    }

    redirect(action: "index")
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def updateProfile() {
    def user = User.get(springSecurityService.principal.id)
    user.display = params.userDispName
    user.email = params.email
    user.save();

    flash.message="Profile Updated"

    redirect(action: "index")
  }
}

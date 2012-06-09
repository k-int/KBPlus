package com.k_int.kbplus

import javax.servlet.http.HttpServletResponse


import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.authentication.AbstractProcessingFilter;
import org.springframework.security.web.savedrequest.*;


class ProcessLoginController {

  def grailsApplication
  def ediAuthTokenMap

  def index() { 

    log.debug("ProcessLoginController::index() - session = ${request.session.id}");

    def ctx = grailsApplication.mainContext

    // Check that request comes from 127.0.0.1


    log.debug("remote institution appears to be : ${params.ea_edinaOrgId}");
    log.debug("remote user appears to be : ${params.ea_edinaUserId}");

    def response_str = 'NO EA_EXTRA FOUND';

    if ( params.ea_extra ) {
      def map = params.ea_extra.split('&').inject([:]) { map, kv -> def (key, value) = 
        kv.split('=').toList(); map[key] = value != null ? URLDecoder.decode(value) : null; map 
      }

      log.debug("Auth inst = ${map.authInstitutionName}");
      log.debug("UserId = ${map.eduPersonTargetedID}");
      log.debug("email = ${map.mail}");
      log.debug("Inst Addr = ${map.authInstitutionAddress}");

      def user = com.k_int.kbplus.auth.User.findByUsername('map.eduPersonTargetedId')
      if ( !user ) {
        user = new com.k_int.kbplus.auth.User(username:map.eduPersonTargetedID,
                                              password:'**',
                                              enabled:true,
                                              accountExpired:false,
                                              accountLocked:false, 
                                              passwordExpired:false,
                                              instname:map.authInstitutionName,
                                              shibbScope:map.shibbScope,
                                              email:map.mail).save(flush:true);
        def userRole = com.k_int.kbplus.auth.Role.findByAuthority('ROLE_USER')
        def new_role_allocation = new com.k_int.kbplus.auth.UserRole(user:user,role:userRole);
        new_role_allocation.save(flush:true);
      }
    
      // securityContext.authentication = new PreAuthenticatedAuthenticationToken(map.eduPersonTargetedID, map, roles)
      // securityContext.authentication.setDetails(user)
      // log.debug("Auth set, isAuthenticated = ${securityContext.authentication.isAuthenticated()}, name=${securityContext.authentication.getName()}");
      // log.debug("ea_context=${map.ea_context}");

      def tok = java.util.UUID.randomUUID().toString()
      ediAuthTokenMap[tok] = map.eduPersonTargetedID

      log.debug("Setting entry in ediAuthTokenMap to ${tok} = ${map.eduPersonTargetedID}");
      log.debug(ediAuthTokenMap)

      if ( ( params.ea_context ) && ( params.ea_context.trim().length() > 0 ) ) {
        response_str="${params.ea_context}?ediauthToken=${tok}"
      }
      else {
        response_str="http://knowplusdev.edina.ac.uk:8080/kbplus/?ediauthToken=${tok}"
      }
    }

    log.debug("Rendering processLoginController response, URL will be ${response_str}");

    // redirect(controller:'home');
    render "${response_str}"
  }
}

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

  def index() { 
    log.debug("ProcessLoginController::index()");
    // Check that request comes from 127.0.0.1

    log.debug("remote institution appears to be : ${params.ea_edinaOrgId}");
    log.debug("remote user appears to be : ${params.ea_edinaUserId}");
    log.debug("ea_extra is : ${params.ea_extra}");

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
      }
    
      def roles = [] // List of org.springframework.security.core.GrantedAuthority
      def securityContext = SCH.context
      // def principal = <whatever you use as principal>
      // def credentials = <...>
      log.debug("Setting pre authentication context...");
      securityContext.authentication = new PreAuthenticatedAuthenticationToken(user, map, roles)
      log.debug("Auth set, isAuthenticated = ${securityContext.authentication.isAuthenticated()}, name=${securityContext.authentication.getName()}");

      SavedRequest savedRequest = new HttpSessionRequestCache().getRequest((javax.servlet.http.HttpServletRequest)request, (javax.servlet.http.HttpServletResponse)response);
      log.debug("Saved request is ${savedRequest}");

      if ( ( map.ea_context ) && ( map.ea_context.trim().length() > 0 ) ) {
        response_str=map.ea_context
      }
      else {
        response_str='http://knowplusdev.edina.ac.uk:8080/kbplus/'
      }
    }

    log.debug("Rendering processLoginController response, URL will be ${response_str}");

    // redirect(controller:'home');
    render response_str
  }
}

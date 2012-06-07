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


class ProcessLoginController {

  def index() { 
    log.debug("ProcessLoginController::index()");
    // Check that request comes from 127.0.0.1

    log.debug("remote institution appears to be : ${params.ea_edinaOrgId}");
    log.debug("remote user appears to be : ${params.ea_edinaUserId}");
    log.debug("ea_extra is : ${params.ea_extra}");

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
                                            email:map.mail).save(flush:true);
    }
    
    def securityContext = SCH.context
    // def principal = <whatever you use as principal>
    // def credentials = <...>
    securityContext.authentication = new PreAuthenticatedAuthenticationToken(user, map)

    // redirect(controller:'home');
    render 'http://knowplusdev.edina.ac.uk:8080/kbplus/'
  }
}

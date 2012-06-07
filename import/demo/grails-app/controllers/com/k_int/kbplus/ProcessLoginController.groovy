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

    def securityContext = SCH.context
    // def principal = <whatever you use as principal>
    // def credentials = <...>
    securityContext.authentication = new PreAuthenticatedAuthenticationToken(principal, credentials)

    redirect(controller:'home');
  }
}

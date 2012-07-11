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

      def user = com.k_int.kbplus.auth.User.findByUsername(map.eduPersonTargetedID)
      if ( !user ) {
        log.debug("Creating user");
        user = new com.k_int.kbplus.auth.User(username:map.eduPersonTargetedID,
                                              password:'**',
                                              enabled:true,
                                              accountExpired:false,
                                              accountLocked:false, 
                                              passwordExpired:false,
                                              instname:map.authInstitutionName,
                                              shibbScope:map.shibbScope,
                                              email:map.mail)

        if ( user.save(flush:true) ) {
          log.debug("Created user, allocating user role");
          def userRole = com.k_int.kbplus.auth.Role.findByAuthority('ROLE_USER')

          if ( userRole ) {
            log.debug("looked up user role: ${userRole}");
            def new_role_allocation = new com.k_int.kbplus.auth.UserRole(user:user,role:userRole);
  
            if ( new_role_allocation.save(flush:true) ) {
              log.debug("New role created...");
            }
            else {
              new_role_allocation.errors.each { e ->
                log.error(e);
              }
            }
          }
          else {
            log.error("Unable to look up ROLE_USER");
          }
  
          log.debug("Granting user ROLE_EDITOR");
          new com.k_int.kbplus.auth.UserRole(user:user,role:com.k_int.kbplus.auth.Role.findByAuthority('ROLE_EDITOR')).save(flush:true)

          // See if we can find the org this user is attached to
          createUserOrgLink(user, map.authInstitutionName, map.shibbScope);
  
          log.debug("Done creating user");
        }
      }
      else {
        log.error("Problem creating user......");
        user.errors.each { err ->
          log.error(err);
        }
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
        response_str="http://knowplus.edina.ac.uk/kbplus/?ediauthToken=${tok}"
      }
    }

    log.debug("Rendering processLoginController response, URL will be ${response_str}");

    // redirect(controller:'home');
    render "${response_str}"
  }

  def createUserOrgLink(user, authInstitutionName, shibbScope) {
    if ( ( authInstitutionName ) && ( authInstitutionName.length() > 0 ) ) {
      def candidate = authInstitutionName.trim().replaceAll(" ","_")
      def org = com.k_int.kbplus.Org.findByScope(shibbScope)

      // If we didn't match by scope, try matching by normalised name
      if ( ! org )
        org = com.k_int.kbplus.Org.findByShortcode(candidate)

      if ( org ) {
        def user_org_link = new com.k_int.kbplus.auth.UserOrg(user:user, 
                                                              org:org, 
                                                              role:'Staff', 
                                                              status:3, 
                                                              dateRequested:System.currentTimeMillis(), 
                                                              dateActioned:System.currentTimeMillis())
        if ( !user_org_link.save(flush:true) ) {
          log.error("Problem saving user org link");
          user_org_link.errors.each { e ->
            log.error(e);
          }
        }
        else {
          log.debug("Linked user with org ${org.id} based on name ${authInstitutionName}");
        }
      }
    }
  }

}

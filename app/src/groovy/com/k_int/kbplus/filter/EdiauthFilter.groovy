package com.k_int.kbplus.filter

import com.k_int.kbplus.auth.*
import com.k_int.kbplus.*

public class EdiauthFilter extends org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter {

  private java.util.HashMap map = null;
  def grailsApplication

  def setEdiAuthTokenMap(java.util.HashMap map) {
    this.map = map;
  }

  def getPreAuthenticatedPrincipal(javax.servlet.http.HttpServletRequest request) {

    log.debug("EdiauthFilter::getPreAuthenticatedPrincipal ${request} - config = ${grailsApplication?.config?.kbplus?.authmethod}");

    def result

    if ( grailsApplication?.config?.kbplus?.authmethod=='shib' ) {
      if ( request.getRemoteUser() != null ) {
        log.debug("In shibboleth authentication mode. If we're here - the user is pre-authenticated. Extract username and make sure there is a user record");
        // User ID should be in request.getAttribute('persistent-id');
        log.debug("Remote User(fn):: ${request.getRemoteUser()}");
        log.debug("Remote User:: ${request.getAttribute('REMOTE_USER')}");
        log.debug("Persistent Id:: ${request.getAttribute('persistent-id')}");

        def tst_attrs = [ 'persistent-id',
                      'eppn',
                      'mail',
                      'givenname',
                      'affiliation',
                      'uid',
                      'Shib-Session-Index',
                      'Shib-Session-ID',
                      'Shib-AuthnContext-Class',
                      'Shib-Application-ID',
                      'unscoped-affiliation',
                      'primary-affiliation',
                      'entitlement',
                      'targeted-id',
                      'primary-orgunit-dn',
                      'orgunit-dn',
                      'org-dn',
                      'cn',
                      'employeeNumber',
                      'displayName',
                      'description'
                    ]
        // tst_attrs.each { it ->
        //   log.debug("tst:: ${it} : ${request.getAttribute(it)}");
        // }
  

        User.withTransaction { status ->
          log.debug("Lookup  user...${request.getRemoteUser()}");
          def existing_user = User.findByUsername(request.getRemoteUser())
          if ( existing_user ) {
            log.debug("User found, all is well ${existing_user}");
          }
          else {
            log.debug("Create new user...${request.getRemoteUser()}");
            existing_user = new User(
                                     username:request.getRemoteUser(),
                                     password:'**',
                                     enabled:true,
                                     accountExpired:false,
                                     accountLocked:false,
                                     passwordExpired:false,
                                     instname:null,
                                     shibbScope:null,
                                     email:request.getAttribute('email'))

            if ( existing_user.save(flush:true) ) {
              log.debug("Created user, allocating user role");
              def userRole = com.k_int.kbplus.auth.Role.findByAuthority('ROLE_USER')
  
              if ( userRole ) {
                log.debug("looked up user role: ${userRole}");
                def new_role_allocation = new com.k_int.kbplus.auth.UserRole(user:existing_user,role:userRole);
  
                if ( new_role_allocation.save(flush:true) ) {
                  log.debug("New role created...");
                }
                else {
                    new_role_allocation.errors.each { e ->
                    log.error(e);
                  }
                }

                // Shib property: affiliation : staff@shu.ac.uk;member@shu.ac.uk
                def shib_affiliations = request.getAttribute('affiliation')
                if ( shib_affiliations ) {
                  def parsed_affiliations = shib_affiliations.split(';');
                  parsed_affiliations.each { pa ->
                    def pa_parts = pa.split('@');
                    if ( ( pa_parts != null ) && ( pa_parts.length == 2 ) ) {
                      def org = Org.findByScope(pa_parts[1]);
                      if ( org ) {
                        if ( pa_parts[0]?.toLowerCase() == 'staff' ) {
                          def editorRole = Role.findByAuthority('INST_USER') ?: new Role(authority: 'INST_USER', roleType:'global').save(failOnError: true)
                          def uo = new UserOrg(status:3,
                                               org:org,
                                               user:existing_user,
                                               formalRole:editorRole,
                                               dateRequested:System.currentTimeMillis(),
                                               dateActioned:System.currentTimeMillis()).save(flush:true)
                        }
                        def new_role = Role.findByAuthority(pa_parts[0]) ?: new Role(authority: pa_parts[0], roleType:'global').save(failOnError: true)
                        def uo2 = new UserOrg(status:3,
                                              org:org,
                                              user:existing_user,
                                              formalRole:new_role,
                                              dateRequested:System.currentTimeMillis(),
                                              dateActioned:System.currentTimeMillis()).save(flush:true)
                      }
                    }
                  }
                }
  
              }
              else {
                log.error("Unable to look up ROLE_USER");
              }
            }
            else {
              log.error("Unable to save new user...");
            }
          }
        }

        log.debug("At end, remote user is ${request.getRemoteUser()}");

        result = request.getRemoteUser()
      }
    }
    // else {
    //   def ediauthToken = request.getParameter("ediauthToken")

      // System.out.println("in getPreAuthenticatedPrincipal");

    //   if ( ( ediauthToken ) && ( map[ediauthToken] != null ) ) {
    //     // System.out.println("Located ediauth token : ${ediauthToken}, ${map[ediauthToken]}");
    //     result = map.remove(ediauthToken)
    //   }
    //   log.debug("Returning ${result}");
    //   result
    // }
    log.debug("Exiting");
    result
  }

  def getPreAuthenticatedCredentials(javax.servlet.http.HttpServletRequest request) {
    log.debug("EdiauthFilter::getPreAuthenticatedCredentials()");
    return "";
  }
}

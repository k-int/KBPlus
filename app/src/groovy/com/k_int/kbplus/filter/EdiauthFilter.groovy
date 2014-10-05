package com.k_int.kbplus.filter

import com.k_int.kbplus.auth.*

public class EdiauthFilter extends org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter {

  private java.util.HashMap map = null;
  def grailsApplication

  def setEdiAuthTokenMap(java.util.HashMap map) {
    this.map = map;
  }

  def getPreAuthenticatedPrincipal(javax.servlet.http.HttpServletRequest request) {

    // log.debug("EdiauthFilter::getPreAuthenticatedPrincipal ${request}");

    def result

    if ( grailsApplication?.config?.kbplus?.authmethod=='shib' ) {
      if ( request.getRemoteUser() != null ) {
        // log.debug("In shibboleth authentication mode. If we're here - the user is pre-authenticated. Extract username and make sure there is a user record");
        // User ID should be in request.getAttribute('persistent-id');
        // log.debug("Remote User(fn):: ${request.getRemoteUser()}");
        // log.debug("Remote User:: ${request.getAttribute('REMOTE_USER')}");
        // log.debug("Persistent Id:: ${request.getAttribute('persistent-id')}");

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
        tst_attrs.each { it ->
          log.debug("tst:: ${it} : ${request.getAttribute(it)}");
        }
  

        User.withTransaction { status ->
          def existing_user = User.findByUsername(request.getRemoteUser())
          if ( existing_user ) {
            // log.debug("User found, all is well");
          }
          else {
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
              }
              else {
                log.error("Unable to look up ROLE_USER");
              }
            }
          }
        }

        result = request.getRemoteUser()
      }
    }
    else {
      def ediauthToken = request.getParameter("ediauthToken")

      // System.out.println("in getPreAuthenticatedPrincipal");

      if ( ( ediauthToken ) && ( map[ediauthToken] != null ) ) {
        // System.out.println("Located ediauth token : ${ediauthToken}, ${map[ediauthToken]}");
        result = map.remove(ediauthToken)
      }
      // log.debug("Returning ${result}");
      result
    }
  }

  def getPreAuthenticatedCredentials(javax.servlet.http.HttpServletRequest request) {
    return "";
  }
}

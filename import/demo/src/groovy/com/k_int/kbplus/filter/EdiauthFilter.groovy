package com.k_int.kbplus.filter


public class EdiauthFilter extends org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter {

  def grailsApplication

  def getPreAuthenticatedPrincipal(javax.servlet.http.HttpServletRequest request) {
    def result
    if ( request.getParameter("ediauthToken") ) {
      def ctx = grailsApplication.mainContext
      if ( ctx.ediAuthTokenMap.ediauthToken ) {
        result = ctx.ediAuthTokenMap.remove(ediauthToken)
      }
    }
    result
  }

  def getPreAuthenticatedCredentials(javax.servlet.http.HttpServletRequest request) {
    return null;
  }
}

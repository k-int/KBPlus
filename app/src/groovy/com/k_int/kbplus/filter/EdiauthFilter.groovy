package com.k_int.kbplus.filter


public class EdiauthFilter extends org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter {

  private java.util.HashMap map = null;

  def setEdiAuthTokenMap(java.util.HashMap map) {
    this.map = map;
  }

  def getPreAuthenticatedPrincipal(javax.servlet.http.HttpServletRequest request) {
    def result
    def ediauthToken = request.getParameter("ediauthToken")

    // System.out.println("in getPreAuthenticatedPrincipal");

    if ( ( ediauthToken ) && ( map[ediauthToken] != null ) ) {
      // System.out.println("Located ediauth token : ${ediauthToken}, ${map[ediauthToken]}");
      result = map.remove(ediauthToken)
    }
    // log.debug("Returning ${result}");
    result
  }

  def getPreAuthenticatedCredentials(javax.servlet.http.HttpServletRequest request) {
    return "";
  }
}

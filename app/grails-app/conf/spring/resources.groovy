// Place your Spring DSL code here
beans = {

  userDetailsService(org.codehaus.groovy.grails.plugins.springsecurity.GormUserDetailsService) {
    grailsApplication = ref('grailsApplication')
  }
  
  userDetailsByNameServiceWrapper(org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper) {
    userDetailsService = ref('userDetailsService')
  }

  preAuthenticatedAuthenticationProvider(org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider) {
    preAuthenticatedUserDetailsService = ref('userDetailsByNameServiceWrapper')
  }

  securityContextPersistenceFilter(org.springframework.security.web.context.SecurityContextPersistenceFilter){
  }

  ediAuthTokenMap(java.util.HashMap) {
  }

  ediauthFilter(com.k_int.kbplus.filter.EdiauthFilter){
    grailsApplication = ref('grailsApplication')
    authenticationManager = ref('authenticationManager')
    ediAuthTokenMap = ref('ediAuthTokenMap')
  }

  apiauthFilter(com.k_int.kbplus.filter.ApiauthFilter){
    authenticationManager = ref("authenticationManager")
    rememberMeServices = ref("rememberMeServices")
    springSecurityService = ref("springSecurityService")
  }

  // preAuthFilter(org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter) {
  //   principalRequestHeader = 'remoteUser'
  //   authenticationManager = ref('authenticationManager')
  // }
}

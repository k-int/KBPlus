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

  // preAuthFilter(org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter) {
  //   principalRequestHeader = 'remoteUser'
  //   authenticationManager = ref('authenticationManager')
  // }
}

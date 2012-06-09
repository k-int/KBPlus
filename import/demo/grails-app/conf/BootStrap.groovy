import com.k_int.kbplus.*

import com.k_int.kbplus.auth.*
import org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class BootStrap {

  def ESWrapperService 
  def grailsApplication


  def init = { servletContext ->
    def so_filetype = DataloadFileType.findByName('Subscription Offered File') ?: new DataloadFileType(name:'Subscription Offered File');
    def plat_filetype = DataloadFileType.findByName('Platforms File') ?: new DataloadFileType(name:'Platforms File');

    if ( grailsApplication.config.localauth ) {
      log.debug("localauth is set.. ensure user accounts present");
      def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
      def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)

      log.debug("Create admin user...");
      def adminUser = User.findByUsername('admin')
      if ( ! adminUser ) {
        def newpass = java.util.UUID.randomUUID().toString()
        log.error("No admin user found, create with temporary password ${newpass}")
        adminUser = new User(
                        username: 'admin',
                        password: 'admin',
                        email: 'admin@localhost',
                        enabled: true).save(failOnError: true)
      }

      if (!adminUser.authorities.contains(adminRole)) {
        UserRole.create adminUser, adminRole
      }

      if (!adminUser.authorities.contains(userRole)) {
        UserRole.create adminUser, userRole
      }


    }


     // SpringSecurityUtils.clientRegisterFilter( 'oracleSSOFilter', SecurityFilterPosition.PRE_AUTH_FILTER.order)
     // SpringSecurityUtils.clientRegisterFilter('securityContextPersistenceFilter', SecurityFilterPosition.PRE_AUTH_FILTER) 
     SpringSecurityUtils.clientRegisterFilter('ediauthFilter', SecurityFilterPosition.PRE_AUTH_FILTER) 
  }

  def destroy = {
  }
}

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


    // Global System Roles
    def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER', roleType:'global').save(failOnError: true)
    def editorRole = Role.findByAuthority('ROLE_EDITOR') ?: new Role(authority: 'ROLE_EDITOR', roleType:'global').save(failOnError: true)
    def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN', roleType:'global').save(failOnError: true)

    // Institutional Roles
    def institutionalAdmin = Role.findByAuthority('INST_ADM') ?: new Role(authority: 'INST_ADM', roleType:'user').save(failOnError: true)
    def institutionalUser = Role.findByAuthority('INST_USER') ?: new Role(authority: 'INST_USER', roleType:'user').save(failOnError: true)

    // Permissions
    def edit_permission = Perm.findByCode('edit') ?: new Perm(code:'edit').save(failOnError: true)
    def view_permission = Perm.findByCode('view') ?: new Perm(code:'view').save(failOnError: true)

    if ( grailsApplication.config.localauth ) {
      log.debug("localauth is set.. ensure user accounts present");

      log.debug("Create admin user...");
      def adminUser = User.findByUsername('admin')
      if ( ! adminUser ) {
        def newpass = java.util.UUID.randomUUID().toString()
        log.error("No admin user found, create with temporary password ${newpass}")
        adminUser = new User(
                        username: 'admin',
                        password: 'admin',
                        display: 'Admin',
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

    // Register extension types
    def la = com.k_int.custprops.ObjectDefinition.ensureType('LicenseAttributes');
    la.ensureProperty(propName:'Concurrent Users', propType:0);
    la.ensureProperty(propName:'Remote Access', propType:0);
    la.ensureProperty(propName:'Walk In Access', propType:0);
    la.ensureProperty(propName:'Multisite Access', propType:0);
    la.ensureProperty(propName:'Partners Access', propType:0);
    la.ensureProperty(propName:'Alumni Access', propType:0);
    la.ensureProperty(propName:'ILL', propType:0);
    la.ensureProperty(propName:'Coursepack', propType:0);
    la.ensureProperty(propName:'VLE', propType:0);
    la.ensureProperty(propName:'Enterprise', propType:0);
    la.ensureProperty(propName:'PCA', propType:0);

    def auto_approve_memberships = Setting.findByName('AutoApproveMemberships') ?: new Setting(name:'AutoApproveMemberships', tp:1, defvalue:'true', value:'true').save();

    // SpringSecurityUtils.clientRegisterFilter( 'oracleSSOFilter', SecurityFilterPosition.PRE_AUTH_FILTER.order)
    // SpringSecurityUtils.clientRegisterFilter('securityContextPersistenceFilter', SecurityFilterPosition.PRE_AUTH_FILTER) 
    SpringSecurityUtils.clientRegisterFilter('ediauthFilter', SecurityFilterPosition.PRE_AUTH_FILTER) 

    def uo_with_null_role = UserOrg.findAllByFormalRoleIsNull()
    if ( uo_with_null_role.size() > 0 ) {
      log.warn("There are user org rows with no role set. Please update the table to add role FKs");
    }
  }

  def destroy = {
  }
}

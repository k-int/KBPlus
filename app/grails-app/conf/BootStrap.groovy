import com.k_int.kbplus.*

import com.k_int.kbplus.auth.*
import org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class BootStrap {

  def ESWrapperService 
  def grailsApplication


  def init = { servletContext ->

    def evt_startup = new EventLog(event:'kbplus.startup',message:'Normal startup',tstp:new Date(System.currentTimeMillis())).save(flush:true)

    def so_filetype = DataloadFileType.findByName('Subscription Offered File') ?: new DataloadFileType(name:'Subscription Offered File');
    def plat_filetype = DataloadFileType.findByName('Platforms File') ?: new DataloadFileType(name:'Platforms File');

    // Permissions
    def edit_permission = Perm.findByCode('edit') ?: new Perm(code:'edit').save(failOnError: true)
    def view_permission = Perm.findByCode('view') ?: new Perm(code:'view').save(failOnError: true)

    // Global System Roles
    def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER', roleType:'global').save(failOnError: true)
    def editorRole = Role.findByAuthority('ROLE_EDITOR') ?: new Role(authority: 'ROLE_EDITOR', roleType:'global').save(failOnError: true)
    def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN', roleType:'global').save(failOnError: true)
    def kbplus_editor = Role.findByAuthority('KBPLUS_EDITOR') ?: new Role(authority: 'KBPLUS_EDITOR', roleType:'global').save(failOnError: true)
    def apiRole = Role.findByAuthority('ROLE_API') ?: new Role(authority: 'ROLE_API', roleType:'global').save(failOnError: true)

    // Institutional Roles
    def institutionalAdmin = Role.findByAuthority('INST_ADM')
    if ( !institutionalAdmin ) {
      institutionalAdmin = new Role(authority: 'INST_ADM', roleType:'user').save(failOnError: true)
    }
    ensurePermGrant(institutionalAdmin,edit_permission);
    ensurePermGrant(institutionalAdmin,view_permission);

    def institutionalUser = Role.findByAuthority('INST_USER') 
    if ( !institutionalUser ) {
      institutionalUser = new Role(authority: 'INST_USER', roleType:'user').save(failOnError: true)
    }
    ensurePermGrant(institutionalUser,view_permission);

    if ( grailsApplication.config.localauth ) {
      log.debug("localauth is set.. ensure user accounts present (From local config file) ${grailsApplication.config.sysusers}");

      grailsApplication.config.sysusers.each { su ->
        log.debug("test ${su.name} ${su.pass} ${su.display} ${su.roles}");
        def user = User.findByUsername(su.name)
        if ( user ) {
          if ( user.password != su.pass ) {
            log.debug("Hard change of user password from config ${user.password} -> ${su.pass}");
            user.password = su.pass;
            user.save(failOnError: true)
          }
          else {
            log.debug("${su.name} present and correct");
          }
        }
        else {
          log.debug("Create user...");
          user = new User(
                        username: su.name,
                        password: su.pass,
                        display: su.display,
                        email: su.email,
                        enabled: true).save(failOnError: true)
        }

        log.debug("Add roles for ${su.name}");
        su.roles.each { r ->
          def role = Role.findByAuthority(r)
          if ( ! ( user.authorities.contains(role) ) ) {
            log.debug("  -> adding role ${role}");
            UserRole.create user, role
          }
          else {
            log.debug("  -> ${role} already present");
          }
        }
      }
    }

    def auto_approve_memberships = Setting.findByName('AutoApproveMemberships') ?: new Setting(name:'AutoApproveMemberships', tp:1, defvalue:'true', value:'true').save();

    // SpringSecurityUtils.clientRegisterFilter( 'oracleSSOFilter', SecurityFilterPosition.PRE_AUTH_FILTER.order)
    // SpringSecurityUtils.clientRegisterFilter('securityContextPersistenceFilter', SecurityFilterPosition.PRE_AUTH_FILTER) 
    SpringSecurityUtils.clientRegisterFilter('ediauthFilter', SecurityFilterPosition.PRE_AUTH_FILTER) 

    def uo_with_null_role = UserOrg.findAllByFormalRoleIsNull()
    if ( uo_with_null_role.size() > 0 ) {
      log.warn("There are user org rows with no role set. Please update the table to add role FKs");
    }


    setupRefdata();
  }

  def destroy = {
    def evt_startup = new EventLog(event:'kbplus.shutdown',message:'Normal shutdown',tstp:new Date(System.currentTimeMillis())).save(flush:true)
  }

  def ensurePermGrant(role,perm) {
    log.debug("ensurePermGrant");
    def existingPermGrant = PermGrant.findByRoleAndPerm(role,perm)
    if ( !existingPermGrant ) {
      log.debug("Create new perm grant for ${role}, ${perm}");
      def new_grant = new PermGrant(role:role, perm:perm).save();
    }
    else {
      log.debug("grant already exists ${role}, ${perm}");
    }
  }

  // Setup extra refdata
  def setupRefdata = { 
    // New Organisational Role
    def sc_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Package Consortia');

    // -------------------------------------------------------------------
    // ONIX-PL Additions
    // -------------------------------------------------------------------
    // New document type
    RefdataCategory.lookupOrCreate('Document Type','ONIX-PL License')
    // Controlled values from the <UsageType> element.
    /*
    log.debug("Adding ONIX-PL UsageTypes to RefdataCategory");
    // Disabled until we know whether we need to know all possible terms in advance
    RefdataCategory.lookupOrCreate('UsageType', 'Access')
    RefdataCategory.lookupOrCreate('UsageType', 'AccessByRobot')
    RefdataCategory.lookupOrCreate('UsageType', 'AccessByStreaming')
    RefdataCategory.lookupOrCreate('UsageType', 'Copy')
    RefdataCategory.lookupOrCreate('UsageType', 'Deposit')
    RefdataCategory.lookupOrCreate('UsageType', 'DepositInPerpetuity')
    RefdataCategory.lookupOrCreate('UsageType', 'Include')
    RefdataCategory.lookupOrCreate('UsageType', 'MakeAvailable')
    RefdataCategory.lookupOrCreate('UsageType', 'MakeAvailableByStreaming')
    RefdataCategory.lookupOrCreate('UsageType', 'MakeDerivedWork')
    RefdataCategory.lookupOrCreate('UsageType', 'MakeDigitalCopy')
    RefdataCategory.lookupOrCreate('UsageType', 'MakeTemporaryDigitalCopy')
    RefdataCategory.lookupOrCreate('UsageType', 'Modify')
    RefdataCategory.lookupOrCreate('UsageType', 'Perform')
    RefdataCategory.lookupOrCreate('UsageType', 'Photocopy')
    RefdataCategory.lookupOrCreate('UsageType', 'PrintCopy')
    RefdataCategory.lookupOrCreate('UsageType', 'ProvideIntegratedAccess')
    RefdataCategory.lookupOrCreate('UsageType', 'ProvideIntegratedIndex')
    RefdataCategory.lookupOrCreate('UsageType', 'Reformat')
    RefdataCategory.lookupOrCreate('UsageType', 'RemoveObscureOrModify')
    RefdataCategory.lookupOrCreate('UsageType', 'Sell')
    RefdataCategory.lookupOrCreate('UsageType', 'SupplyCopy')
    RefdataCategory.lookupOrCreate('UsageType', 'SystematicallyCopy')
    RefdataCategory.lookupOrCreate('UsageType', 'Use')
    RefdataCategory.lookupOrCreate('UsageType', 'UseForDataMining')
    */
    // Controlled values from the <UsageStatus> element. All are prefixed with "onixPL:" in the document
    log.debug("Adding ONIX-PL UsageStatuses to RefdataCategory");
    RefdataCategory.lookupOrCreate('UsageStatus', 'greenTick',      'UseForDataMining')
    RefdataCategory.lookupOrCreate('UsageStatus', 'greenTick',      'InterpretedAsPermitted')
    RefdataCategory.lookupOrCreate('UsageStatus', 'redCross',       'InterpretedAsProhibited')
    RefdataCategory.lookupOrCreate('UsageStatus', 'greenTick',      'Permitted')
    RefdataCategory.lookupOrCreate('UsageStatus', 'redCross',       'Prohibited')
    RefdataCategory.lookupOrCreate('UsageStatus', 'purpleQuestion', 'SilentUninterpreted')
    RefdataCategory.lookupOrCreate('UsageStatus', 'purpleQuestion', 'NotApplicable')
  }

}

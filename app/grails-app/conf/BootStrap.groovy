import com.k_int.kbplus.*

import com.k_int.kbplus.auth.*
import org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class BootStrap {

  def ESWrapperService 
  def grailsApplication
  // def docstoreService

  def init = { servletContext ->

      log.error("Sys id: ${grailsApplication.config.kbplusSystemId}")
  
    if ( grailsApplication.config.kbplusSystemId != null ) {
      def system_object = SystemObject.findBySysId(grailsApplication.config.kbplusSystemId) ?: new SystemObject(sysId:grailsApplication.config.kbplusSystemId).save(flush:true);
    }

    def evt_startup = new EventLog(event:'kbplus.startup',message:'Normal startup',tstp:new Date(System.currentTimeMillis())).save(flush:true)

    def so_filetype = DataloadFileType.findByName('Subscription Offered File') ?: new DataloadFileType(name:'Subscription Offered File');
    def plat_filetype = DataloadFileType.findByName('Platforms File') ?: new DataloadFileType(name:'Platforms File');

    // Permissions
    def edit_permission = Perm.findByCode('edit') ?: new Perm(code:'edit').save(failOnError: true)
    def view_permission = Perm.findByCode('view') ?: new Perm(code:'view').save(failOnError: true)

    def ref_yes = RefdataCategory.lookupOrCreate("YN","Yes")
    def ref_no = RefdataCategory.lookupOrCreate("YN","No")

    def or_licensee_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee');
    def or_subscriber_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Subscriber');
    def or_sc_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Subscription Consortia');
    def cons_combo = RefdataCategory.lookupOrCreate('Combo Type', 'Consortium');

    OrgPermShare.assertPermShare(view_permission, or_licensee_role);
    OrgPermShare.assertPermShare(edit_permission, or_licensee_role);
    OrgPermShare.assertPermShare(view_permission, or_subscriber_role);
    OrgPermShare.assertPermShare(edit_permission, or_subscriber_role);
    OrgPermShare.assertPermShare(view_permission, or_sc_role);
    OrgPermShare.assertPermShare(edit_permission, or_sc_role);
    OrgPermShare.assertPermShare(view_permission, cons_combo);


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

    // Allows values to be added to the vocabulary control list by passing an array with RefdataCategory as the key
    // and a list of values to be added to the RefdataValue table.
    grailsApplication.config.refdatavalues.each { rdc, rdvList ->
        rdvList.each { rdv ->
            RefdataCategory.lookupOrCreate(rdc, rdv);
        }
    }

  // Transforms types and formats Refdata 
  // !!! HAS TO BE BEFORE the script adding the Transformers as it is used by those tables !!!
  def json_format = RefdataCategory.lookupOrCreate('Transform Format', 'json');
  def xml_format = RefdataCategory.lookupOrCreate('Transform Format', 'xml');
  def url_format = RefdataCategory.lookupOrCreate('Transform Format', 'url');
  def subscription_type = RefdataCategory.lookupOrCreate('Transform Type', 'subscription');
  def licence_type = RefdataCategory.lookupOrCreate('Transform Type', 'licence');
  def title_type = RefdataCategory.lookupOrCreate('Transform Type', 'title');
  def package_type = RefdataCategory.lookupOrCreate('Transform Type', 'package');
  
  // Add Transformers and Transforms define in the demo-config.groovy
  grailsApplication.config.systransforms.each { tr ->
    def transformName = tr.transforms_name //"${tr.name}-${tr.format}-${tr.type}"
    
    def transforms = Transforms.findByName("${transformName}")
    def transformer = Transformer.findByName("${tr.transformer_name}")
    if ( transformer ) {
      if ( transformer.url != tr.url ) {
        log.debug("Change transformer [${tr.transformer_name}] url to ${tr.url}");
        transformer.url = tr.url;
        transformer.save(failOnError: true, flush: true)
      }
      else {
        log.debug("${tr.transformer_name} present and correct");
      }
    } else {
      log.debug("Create transformer ${tr.transformer_name}...");
      transformer = new Transformer(
            name: tr.transformer_name,
            url: tr.url).save(failOnError: true, flush: true)
    }
    
    log.debug("Create transform ${transformName}...");
    def types = RefdataValue.findAllByOwner(RefdataCategory.findByDesc('Transform Type'))
    def formats = RefdataValue.findAllByOwner(RefdataCategory.findByDesc('Transform Format'))
    
    if ( transforms ) {
      
      if( tr.type ){
        // split values
        def type_list = tr.type.split(",")
        type_list.each { new_type ->
          if( !transforms.accepts_types.any { f -> f.value == new_type } ){
            log.debug("Add transformer [${transformName}] type: ${new_type}");
            def type = types.find{ t -> t.value == new_type }
            transforms.addToAccepts_types(type)
          }
        }
      }
      if ( transforms.accepts_format.value != tr.format ) {
        log.debug("Change transformer [${transformName}] format to ${tr.format}");
        def format = formats.findAll{ t -> t.value == tr.format }
        transforms.accepts_format = format[0]
      }
      if ( transforms.return_mime != tr.return_mime ) {
        log.debug("Change transformer [${transformName}] return format to ${tr.'mime'}");
        transforms.return_mime = tr.return_mime;
      }
      if ( transforms.return_file_extention != tr.return_file_extension ) {
        log.debug("Change transformer [${transformName}] return format to ${tr.'return'}");
        transforms.return_file_extention = tr.return_file_extension;
      }
      if ( transforms.path_to_stylesheet != tr.path_to_stylesheet ) {
        log.debug("Change transformer [${transformName}] return format to ${tr.'path'}");
        transforms.path_to_stylesheet = tr.path_to_stylesheet;
      }
      transforms.save(failOnError: true, flush: true)
    }
    else {
      def format = formats.findAll{ t -> t.value == tr.format }
      
      assert format.size()==1
      
      transforms = new Transforms(
        name: transformName,
        accepts_format: format[0],
        return_mime: tr.return_mime,
        return_file_extention: tr.return_file_extension,
        path_to_stylesheet: tr.path_to_stylesheet,
        transformer: transformer).save(failOnError: true, flush: true)
        
      def type_list = tr.type.split(",")
      type_list.each { new_type ->
        def type = types.find{ t -> t.value == new_type }
        transforms.addToAccepts_types(type)
      }
    }
  }
  
  
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
    SpringSecurityUtils.clientRegisterFilter('apiauthFilter', SecurityFilterPosition.SECURITY_CONTEXT_FILTER.order + 10)

    def uo_with_null_role = UserOrg.findAllByFormalRoleIsNull()
    if ( uo_with_null_role.size() > 0 ) {
      log.warn("There are user org rows with no role set. Please update the table to add role FKs");
    }

    setupRefdata();

    // if ( grailsApplication.config.doDocstoreMigration == true ) {
    //   docstoreService.migrateToDb();
    // }
  }

  def destroy = {
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
    def or_licensee_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee');

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

    RefdataCategory.lookupOrCreate('UsageStatus', 'greenTick',      'UseForDataMining')
    RefdataCategory.lookupOrCreate('UsageStatus', 'greenTick',      'InterpretedAsPermitted')
    RefdataCategory.lookupOrCreate('UsageStatus', 'redCross',       'InterpretedAsProhibited')
    RefdataCategory.lookupOrCreate('UsageStatus', 'greenTick',      'Permitted')
    RefdataCategory.lookupOrCreate('UsageStatus', 'redCross',       'Prohibited')
    RefdataCategory.lookupOrCreate('UsageStatus', 'purpleQuestion', 'SilentUninterpreted')
    RefdataCategory.lookupOrCreate('UsageStatus', 'purpleQuestion', 'NotApplicable')

    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.DelayedOA", "No").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.DelayedOA", "Unknown").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.DelayedOA", "Yes").save()

    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.HybridOA", "No").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.HybridOA", "Unknown").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.HybridOA", "Yes").save()

    RefdataCategory.lookupOrCreate("Tipp.StatusReason", "Transfer Out").save()
    RefdataCategory.lookupOrCreate("Tipp.StatusReason", "Transfer In").save()

    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.PaymentType", "Complimentary").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.PaymentType", "Limited Promotion").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.PaymentType", "Paid").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.PaymentType", "OA").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.PaymentType", "Opt Out Promotion").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.PaymentType", "Uncharged").save()
    RefdataCategory.lookupOrCreate("TitleInstancePackagePlatform.PaymentType", "Unknown").save()

    RefdataCategory.lookupOrCreate("Package.ListStatus", "Checked").save()
    RefdataCategory.lookupOrCreate("Package.ListStatus", "In Progress").save()
    RefdataCategory.lookupOrCreate("Package.Breakable", "No").save()
    RefdataCategory.lookupOrCreate("Package.Breakable", "Yes").save()
    RefdataCategory.lookupOrCreate("Package.Breakable", "Unknown").save()
    RefdataCategory.lookupOrCreate("Package.Consistent", "No").save()
    RefdataCategory.lookupOrCreate("Package.Consistent", "Yes").save()
    RefdataCategory.lookupOrCreate("Package.Consistent", "Unknown").save()
    RefdataCategory.lookupOrCreate("Package.Fixed", "No").save()
    RefdataCategory.lookupOrCreate("Package.Fixed", "Yes").save()
    RefdataCategory.lookupOrCreate("Package.Fixed", "Unknown").save()
    RefdataCategory.lookupOrCreate("Package.Scope", "Aggregator").save()
    RefdataCategory.lookupOrCreate("Package.Scope", "Front File").save()
    RefdataCategory.lookupOrCreate("Package.Scope", "Back File").save()
    RefdataCategory.lookupOrCreate("Package.Scope", "Master File").save()
    RefdataCategory.lookupOrCreate("Package.Scope", "Scope Undefined").save()

    RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending").save()
    RefdataCategory.lookupOrCreate("PendingChangeStatus", "Accepted").save()
    RefdataCategory.lookupOrCreate("PendingChangeStatus", "Rejected").save()

    RefdataCategory.lookupOrCreate("LicenseCategory", "Content").save()
    RefdataCategory.lookupOrCreate("LicenseCategory", "Software").save()
    RefdataCategory.lookupOrCreate("LicenseCategory", "Other").save()

    RefdataCategory.lookupOrCreate("TitleInstanceStatus", "Current").save()
    RefdataCategory.lookupOrCreate("TitleInstanceStatus", "Deleted").save()

    RefdataCategory.lookupOrCreate("License Status", "In Progress").save()

    log.debug("validate content items...");
    // The default template for a property change on a title
    ContentItem.lookupOrCreate('ChangeNotification.TitleInstance.propertyChange','','''
Title change - The <strong>${evt.prop}</strong> field was changed from  "<strong>${evt.oldLabel?:evt.old}</strong>" to "<strong>${evt.newLabel?:evt.new}</strong>".
''');

    ContentItem.lookupOrCreate('ChangeNotification.TitleInstance.identifierAdded','','''
An identifier was added to title ${OID?.title}.
''');

    ContentItem.lookupOrCreate('ChangeNotification.TitleInstance.identifierRemoved','','''
An identifier was removed from title ${OID?.title}.
''');

    ContentItem.lookupOrCreate('ChangeNotification.TitleInstancePackagePlatform.updated','','''
TIPP change for title ${OID?.title?.title} - The <strong>${evt.prop}</strong> field was changed from  "<strong>${evt.oldLabel?:evt.old}</strong>" to "<strong>${evt.newLabel?:evt.new}</strong>".
''');

    ContentItem.lookupOrCreate('ChangeNotification.TitleInstancePackagePlatform.added','','''
TIPP Added for title ${OID?.title?.title} ${evt.linkedTitle} on platform ${evt.linkedPlatform} .
''');

    ContentItem.lookupOrCreate('ChangeNotification.TitleInstancePackagePlatform.deleted','','''
TIPP Deleted for title ${OID?.title?.title} ${evt.linkedTitle} on platform ${evt.linkedPlatform} .
''');

    ContentItem.lookupOrCreate('ChangeNotification.Package.created','','''
New package added with id ${OID.id} - "${OID.name}".
''');

    ContentItem.lookupOrCreate('kbplus.noHostPlatformURL','','''
No Host Platform URL Content
''');


   def gokb_record_source = GlobalRecordSource.findByIdentifier('gokbPackages') ?: new GlobalRecordSource(
                                                                                         identifier:'gokbPackages',
                                                                                         name:'GOKB',
                                                                                         type:'OAI',
                                                                                         haveUpTo:null,
                                                                                         uri:'https://gokb.k-int.com/gokb/oai/packages',
                                                                                         listPrefix:'oai_dc',
                                                                                         fullPrefix:'gokb',
                                                                                         principal:null,
                                                                                         credentials:null,
                                                                                         rectype:0).save(flush:true)

  }

}

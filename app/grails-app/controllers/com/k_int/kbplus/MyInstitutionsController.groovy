package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;  
import org.apache.poi.hslf.model.*;
import java.text.SimpleDateFormat



class MyInstitutionsController {

  def springSecurityService
  def docstoreService
  def ESWrapperService
  def gazetteerService
  def alertsService
  def genericOIDService

  // Map the parameter names we use in the webapp with the ES fields
  def renewals_reversemap = ['subject':'subject', 'provider':'provid', 'pkgname':'name' ]
  def reversemap = ['subject':'subject', 'provider':'provid', 'studyMode':'presentations.studyMode','qualification':'qual.type','level':'qual.level' ]

  def possible_date_formats = [
    new SimpleDateFormat('yyyy/MM/dd'),
    new SimpleDateFormat('dd/MM/yyyy'),
    new SimpleDateFormat('dd/MM/yy'),
    new SimpleDateFormat('yyyy/MM'),
    new SimpleDateFormat('yyyy')
  ];


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    // Work out what orgs this user has admin level access to
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.userAlerts = alertsService.getAllVisibleAlerts(result.user);
    result.staticAlerts = alertsService.getStaticAlerts(request);

    
    log.debug("result.userAlerts: ${result.userAlerts}");
    log.debug("result.userAlerts.size(): ${result.userAlerts.size()}");
    log.debug("result.userAlerts.class.name: ${result.userAlerts.class.name}");
    // def adminRole = Role.findByAuthority('ROLE_ADMIN')
    // if ( result.user.authorities.contains(adminRole) ) {
    //   log.debug("User is in admin role");
    //   result.orgs = Org.findAllBySector("Higher Education");
    // }
    // else {
    //   result.orgs = Org.findAllBySector("Higher Education");
    // }

    if ( ( result.user.affiliations == null ) || ( result.user.affiliations.size() == 0 ) ) {
      redirect controller:'profile', action: 'index'
    }
    else {
    }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def dashboard() {
    // Work out what orgs this user has admin level access to
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.userAlerts = alertsService.getAllVisibleAlerts(result.user);
    result.staticAlerts = alertsService.getStaticAlerts(request);

    log.debug("result.userAlerts: ${result.userAlerts}");
    log.debug("result.userAlerts.size(): ${result.userAlerts.size()}");
    log.debug("result.userAlerts.class.name: ${result.userAlerts.class.name}");
    // def adminRole = Role.findByAuthority('ROLE_ADMIN')
    // if ( result.user.authorities.contains(adminRole) ) {
    //   log.debug("User is in admin role");
    //   result.orgs = Org.findAllBySector("Higher Education");
    // }
    // else {
    //   result.orgs = Org.findAllBySector("Higher Education");
    // }

    if ( ( result.user.affiliations == null ) || ( result.user.affiliations.size() == 0 ) ) {
      redirect controller:'profile', action: 'index'
    }
    else {
    }

    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def actionLicenses() {
    if ( params['copy-licence'] ) {
      newLicense(params)
    }
    else if ( params[ 'delete-licence' ] ) {
      deleteLicense(params)
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def currentLicenses() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    if ( !checkUserIsMember(result.user, result.institution) ) {
      flash.error="You do not have permission to view ${result.institution.name}. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${result.institution.name}. Please request access on the profile page");
      return;
    }

    if ( checkUserHasRole(result.user, result.institution, 'INST_ADM') ) {
      result.is_admin = true
    }
    else {
      result.is_admin=false;
    }


    def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
    def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');

    // def qry = "select l from License as l left outer join l.orgLinks ol where ( ( l.type = ? ) OR ( ol.org = ? and ol.roleType = ? ) ) AND l.status.value != 'Deleted'"
    // def qry = "select l from License as l left outer join l.orgLinks ol where ( ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted'"
    def qry = "select l from License as l where exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted'"

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      qry += " order by l.${params.sort} ${params.order}"
    }
    else {
      qry += " order by l.reference asc"
    }

    // result.licenses = License.executeQuery(qry, [template_license_type, result.institution, licensee_role] )
    result.licenses = License.executeQuery(qry, [result.institution, licensee_role] )

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def addLicense() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    // if ( !checkUserHasRole(result.user, result.institution, 'INST_ADM') ) {
    if ( !checkUserIsMember(result.user,result.institution) ) {
      flash.error="You do not have permission to view ${result.institution.name}. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to add licences to ${result.institution.name}");
      return;
    }

    if ( checkUserHasRole(result.user, result.institution, 'INST_ADM') ) {
      result.is_admin = true
    }
    else {
      result.is_admin=false;
    }

    def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
    def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');
    def public_flag = RefdataCategory.lookupOrCreate('YN','Yes');

    // def qry = "select l from License as l left outer join l.orgLinks ol where ( ( l.type = ? ) OR ( ol.org = ? and ol.roleType = ? ) ) AND l.status.value != 'Deleted'"
    // def qry = "select l from License as l left outer join l.orgLinks ol where l.type = ? AND l.status.value != 'Deleted'"
    def qry = "select l from License as l where ( ( l.type = ? ) OR ( exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) ) OR ( l.isPublic=? ) ) AND l.status.value != 'Deleted'"

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      qry += " order by l.${params.sort} ${params.order}"
    }
    else {
      qry += " order by reference asc"
    }

    result.licenses = License.executeQuery(qry, [template_license_type, result.institution, licensee_role, public_flag] )
    // result.licenses = License.executeQuery(qry, [template_license_type])

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def currentSubscriptions() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    
    def date_restriction = null;
    def sdf = new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)
    
    if ( params.validOn == null ) {
      result.validOn = sdf.format(new Date(System.currentTimeMillis()))
      date_restriction = sdf.parse(result.validOn)
    }
    else if ( params.validOn == '' ) {     
      result.validOn = sdf.format(new Date(System.currentTimeMillis()))
    }
    else {
      result.validOn=params.validOn
      date_restriction = sdf.parse(params.validOn)
    }

    if ( !checkUserIsMember(result.user, result.institution) ) {
      flash.error="You do not have permission to access ${result.institution.name} pages. Please request access on the profile page";
      response.sendError(401)
      return;
    }

    if ( checkUserHasRole(result.user, result.institution, 'INST_ADM') ) {
      result.is_admin = true
    }
    else {
      result.is_admin=false;
    }

    def public_flag = RefdataCategory.lookupOrCreate('YN','Yes');

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : 10;
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;


    // def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) OR ( s.isPublic=? ) ) AND ( s.status.value != 'Deleted' ) "
    def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
    // def qry_params = [result.institution, public_flag]
    def qry_params = [result.institution]

    if ( params.q?.length() > 0 ) {
      base_qry += " and ( lower(s.name) like ? or exists ( select sp from SubscriptionPackage as sp where sp.subscription = s and ( lower(sp.pkg.name) like ? ) ) ) "
      qry_params.add("%${params.q.trim().toLowerCase()}%");
      qry_params.add("%${params.q.trim().toLowerCase()}%");
    }
    
    if ( date_restriction ) {
      base_qry += " and s.startDate <= ? and s.endDate >= ? "
      qry_params.add(date_restriction)
      qry_params.add(date_restriction)
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += " order by ${params.sort} ${params.order}"
    }
    else {
      base_qry += " order by s.name asc"
    }

    result.num_sub_rows = Subscription.executeQuery("select count(s) "+base_qry, qry_params )[0]
    result.subscriptions = Subscription.executeQuery("select s ${base_qry}", qry_params, [max:result.max, offset:result.offset]);

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def addSubscription() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    def date_restriction = null;
    def sdf = new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)
    
    if ( params.validOn == null ) {
      result.validOn = sdf.format(new Date(System.currentTimeMillis()))
      date_restriction = sdf.parse(result.validOn)
    }
    else if ( params.validOn == '' ) {     
      result.validOn = sdf.format(new Date(System.currentTimeMillis()))
    }
    else {
      result.validOn=params.validOn
      date_restriction = sdf.parse(params.validOn)
    }
    
    // if ( !checkUserHasRole(result.user, result.institution, 'INST_ADM') ) {
    if ( !checkUserIsMember(result.user,result.institution) ) {
      flash.error="You do not have admin permissions to access ${result.institution.name} pages. Please request access on the profile page";
      response.sendError(401)
      result.is_admin=false;
      // render(status: '401', text:"You do not have permission to add subscriptions to ${result.institution.name}. Please request editor access on the profile page");
      return;
    }

    if ( checkUserHasRole(result.user, result.institution, 'INST_ADM') ) {
      result.is_admin = true
    }
    else {
      result.is_admin=false;
    }

    def public_flag = RefdataCategory.lookupOrCreate('YN','Yes');

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : 10;
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;


    // def base_qry = " from Subscription as s where s.type.value = 'Subscription Offered' and s.isPublic=?"
    def base_qry = " from Subscription as s where s.type.value = 'Subscription Offered'"
    // def qry_params = [public_flag]
    def qry_params = []

    if ( params.q?.length() > 0 ) {
      base_qry += " and ( lower(s.name) like ? or exists ( select sp from SubscriptionPackage as sp where sp.subscription = s and ( lower(sp.pkg.name) like ? ) ) ) "
      qry_params.add("%${params.q.trim().toLowerCase()}%");
      qry_params.add("%${params.q.trim().toLowerCase()}%");
    }

    if ( date_restriction ) {
      base_qry += " and s.startDate <= ? and s.endDate >= ? "
      qry_params.add(date_restriction)
      qry_params.add(date_restriction)
    }

    // Only list subscriptions where the user has view perms against the org
    // base_qry += "and ( ( exists select or from OrgRole where or.org =? and or.user = ? and or.perms.contains'view' ) "

    // Or the user is a member of an org who has a consortial membership that has view perms
    // base_qry += " or ( 2==2 ) )"

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += " order by ${params.sort} ${params.order}"
    }
    else {
      base_qry += " order by s.name asc"
    }

    result.num_sub_rows = Subscription.executeQuery("select count(s) "+base_qry, qry_params )[0]
    result.subscriptions = Subscription.executeQuery("select s ${base_qry}", qry_params, [max:result.max, offset:result.offset]);

    result
  }

  def buildQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    if ( params?.q?.length() > 0 )
      sw.write(params.q)
    else
      sw.write("*:*")
      
    reversemap.each { mapping ->

      // log.debug("testing ${mapping.key}");

      if ( params[mapping.key] != null ) {
        if ( params[mapping.key].class == java.util.ArrayList) {
          params[mapping.key].each { p ->  
                sw.write(" AND ")
                sw.write(mapping.value)
                sw.write(":")
                sw.write("\"${p}\"")
          }
        }
        else {
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *
          if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
            sw.write(" AND ")
            sw.write(mapping.value)
            sw.write(":")
            sw.write("\"${params[mapping.key]}\"")
          }
        }
      }
    }

    sw.write(" AND type:\"Subscription Offered\"");
    def result = sw.toString();
    result;
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def cleanLicense() {
    def user = User.get(springSecurityService.principal.id)
    def org = Org.findByShortcode(params.shortcode)

    if ( !checkUserHasRole(user, org, 'INST_ADM') ) {
      flash.error="You do not have edit permission to access ${org.name} pages. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${org.name}. Please request access on the profile page");
      return;
    }

    def license_type = RefdataCategory.lookupOrCreate('License Type','Actual')
    def license_status = RefdataCategory.lookupOrCreate('License Status','Current')
    def licenseInstance = new License( type:license_type, status:license_status )
    if (!licenseInstance.save(flush: true)) {
    }
    else {
      log.debug("Save ok");
      def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee')
      log.debug("adding org link to new license");
      org.links.add(new OrgRole(lic:licenseInstance, org:org, roleType:licensee_role));
      if ( org.save(flush:true) ) {
      }
      else {
        log.error("Problem saving org links to license ${org.errors}");
      }
    }
    flash.message = message(code: 'license.created.message', args: [message(code: 'license.label', default: 'License'), licenseInstance.id])
    redirect controller:'licenseDetails', action: 'index', params:params, id:licenseInstance.id
  }

  def newLicense(params) {
    def user = User.get(springSecurityService.principal.id)
    def org = Org.findByShortcode(params.shortcode)
    
    if ( !checkUserHasRole(user, org, 'INST_ADM') ) {
      flash.error="You do not have permission to access ${org.name} pages. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${org.name}. Please request access on the profile page");
      return;
    }

    switch (request.method) {
      case 'GET':
        [licenseInstance: new License(params)]
        break
      case 'POST':
        def baseLicense = params.baselicense ? License.get(params.baselicense) : null;
        
        if ( ! baseLicense?.hasPerm("view",user) ) {
          log.debug("return 401....");
          flash.error="You do not have permission to view the selected license. Please request access on the profile page";
          response.sendError(401)
          // flash.message = message(code:'noperm',default:'You do not have edit permission for the selected license.')
          // redirect(url: request.getHeader('referer'))
          return
        }

        def license_type = RefdataCategory.lookupOrCreate('License Type','Actual')
        def license_status = RefdataCategory.lookupOrCreate('License Status','Current')
        def licenseInstance = new License(reference:"Copy of ${baseLicense?.reference}",
                                          status:license_status,
                                          type:license_type,
                                          concurrentUsers:baseLicense?.concurrentUsers,
                                          remoteAccess:baseLicense?.remoteAccess,
                                          walkinAccess:baseLicense?.walkinAccess,
                                          multisiteAccess:baseLicense?.multisiteAccess,
                                          partnersAccess:baseLicense?.partnersAccess,
                                          alumniAccess:baseLicense?.alumniAccess,
                                          ill:baseLicense?.ill,
                                          coursepack:baseLicense?.coursepack,
                                          vle:baseLicense?.vle,
                                          enterprise:baseLicense?.enterprise,
                                          pca:baseLicense?.pca,
                                          noticePeriod:baseLicense?.noticePeriod,
                                          licenseUrl:baseLicense?.licenseUrl)

        // the url will set the shortcode of the organisation that this license should be linked with.
        if (!licenseInstance.save(flush: true)) {
          log.error("Problem saving license ${licenseInstance.errors}");
          render view: 'editLicense', model: [licenseInstance: licenseInstance]
          return
        }
        else {
          log.debug("Save ok");
          def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee')
          log.debug("adding org link to new license");
          org.links.add(new OrgRole(lic:licenseInstance, org:org, roleType:licensee_role));
          if ( baseLicense?.licensor ) {
            def licensor_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensor')
            org.links.add(new OrgRole(lic:licenseInstance, org:baseLicense.licensor, roleType:licensor_role));
          }

          if ( org.save(flush:true) ) {
          }
          else {
            log.error("Problem saving org links to license ${org.errors}");
          }

          // Clone documents
          baseLicense?.documents?.each { dctx ->
            DocContext ndc = new DocContext(owner:dctx.owner,
                                            license: licenseInstance,
                                            domain: dctx.domain,
                                            status: dctx.status,
                                            doctype: dctx.doctype).save()
          }

          // Finally, create a link
          def new_link = new Link(fromLic:baseLicense, toLic:licenseInstance).save()
        }

        if ( baseLicense ) 
          flash.message = message(code: 'license.created.message', args: [message(code: 'license.label', default: 'License'), licenseInstance.id])
  
        redirect controller: 'licenseDetails', action:'index', params:params, id:licenseInstance.id
        //redirect action: 'show', id: licenseInstance.id
        break
    }

  }

  def deleteLicense(params) {
    log.debug("deleteLicense id:${params.baselicense}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    if ( !checkUserIsMember(result.user, result.institution) ) {
      flash.error="You do not have permission to view ${result.institution.name}. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${result.institution.name}. Please request access on the profile page");
      return;
    }

    def license = License.get(params.baselicense)
    

    if ( license?.hasPerm("edit",result.user) ) {
      def current_subscription_status = RefdataCategory.lookupOrCreate('Subscription Status','Current');

      def subs_using_this_license = Subscription.findAllByOwnerAndStatus(license,current_subscription_status)
      
      if ( subs_using_this_license.size() == 0 ) {
        def deletedStatus = RefdataCategory.lookupOrCreate('License Status','Deleted');
        license.status = deletedStatus
        license.save(flush:true);
      }
      else {
        flash.error = "Unable to delete - The selected license has attached subscriptions marked as Current"
      }
    }
    else {
      log.warn("Attempt by ${result.user} to delete license ${result.license}without perms")
      flash.message = message(code:'license.delete.norights',default:'You do not have edit permission for the selected license.')
      redirect(url: request.getHeader('referer'))
      return
    }
    
    redirect action: 'currentLicenses', params: [shortcode:params.shortcode]
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def deleteDocuments() {
    def ctxlist = []
    
    log.debug("deleteDocuments ${params}");

    params.each { p ->
      if (p.key.startsWith('_deleteflag.') ) {
        def docctx_to_delete = p.key.substring(12);
        log.debug("Looking up docctx ${docctx_to_delete}");
        def docctx = DocContext.get(docctx_to_delete)
        docctx.status = RefdataCategory.lookupOrCreate('Document Context Status','Deleted');
      }
    }

    redirect controller: 'licenseDetails', action:'index', params:[shortcode:params.shortcode], id:params.licid, fragment:'docstab'
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def processAddSubscription() {

    def user = User.get(springSecurityService.principal.id)
    def institution = Org.findByShortcode(params.shortcode)

    if ( !checkUserIsMember(user, institution) ) {
      flash.error="You do not have permission to view ${institution.name}. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${institution.name}. Please request access on the profile page");
      return;
    }

    log.debug("processAddSubscription ${params}");

    def baseSubscription = Subscription.get(params.subOfferedId);

    if ( baseSubscription ) {
      log.debug("Copying base subscription ${baseSubscription.id} for org ${institution.id}");

      if ( ! baseSubscription.hasPerm("view",user) ) {
        flash.message = message(code:'noperm',default:'You do not have edit permission for the selected subscription.')
        redirect(url: request.getHeader('referer'))
        return
      }
      
      def subscriptionInstance = new Subscription(
                                     status:baseSubscription.status,
                                     type:RefdataCategory.lookupOrCreate('Subscription Type','Subscription Taken'),
                                     name:"Copy of ${baseSubscription.name}",
                                     identifier:baseSubscription.identifier,
                                     impId:null,
                                     startDate:baseSubscription.startDate,
                                     endDate:baseSubscription.endDate,
                                     instanceOf:baseSubscription,
                                     noticePeriod:baseSubscription.noticePeriod,
                                     dateCreated:new Date(),
                                     lastUpdated:new Date(),
                                     issueEntitlements: new java.util.TreeSet()
                                 );

      // Now copy reference data and issue entitlements
      if (!subscriptionInstance.save(flush: true)) {
        log.error("Problem saving license ${licenseInstance.errors}");
      }
      else {
        if ( params.createSubAction == 'copy' ) {
          log.debug("Copy issue entitlements");
          // These IE's are save on cascade
          int ic = 0;
          baseSubscription.issueEntitlements.each { bie ->
            log.debug("Adding issue entitlement... ${bie.id} [${ic++}]");
  
            new IssueEntitlement(status:bie.status,
                                 startDate:bie.startDate,
                                 startVolume:bie.startVolume,
                                 startIssue:bie.startIssue,
                                 endDate:bie.endDate,
                                 endVolume:bie.endVolume,
                                 endIssue:bie.endIssue,
                                 embargo:bie.embargo,
                                 coverageDepth:bie.coverageDepth,
                                 coverageNote:bie.coverageNote,
                                 coreStatus:bie.coreStatus,
                                 subscription:subscriptionInstance,
                                 tipp: bie.tipp).save();
          }
        }
  
        log.debug("Setting sub/org link");
        def subscriber_org_link = new OrgRole(org:institution, sub:subscriptionInstance, roleType: RefdataCategory.lookupOrCreate('Organisational Role','Subscriber')).save();

        log.debug("Adding packages");
        baseSubscription.packages.each { bp ->
          new SubscriptionPackage(subscription:subscriptionInstance, pkg:bp.pkg).save();
        }

        // Copy any org links
        baseSubscription.orgRelations.each { or ->
          new OrgRole(org:or.org, roleType:or.roleType, sub:subscriptionInstance).save();
        }

        log.debug("Save ok");
      }

      flash.message = message(code: 'subscription.created.message', args: [message(code: 'subscription.label', default: 'License'), subscriptionInstance.id])
      redirect controller: 'subscriptionDetails', action:'index', params:params, id:subscriptionInstance.id
    }
    else {
      flash.message = message(code: 'subscription.unknown.message')
      redirect action: 'addSubscription', params:params
    }
  }

  def availableLicenses() {
    // def sub = resolveOID(params.elementid);
    // OrgRole.findAllByOrgAndRoleType(result.institution, licensee_role).collect { it.lic }


    def user = User.get(springSecurityService.principal.id)
    def institution = Org.findByShortcode(params.shortcode)

    if ( !checkUserIsMember(user, institution) ) {
      flash.error="You do not have permission to view ${institution.name}. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${institution.name}. Please request access on the profile page");
      return;
    }

    def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');

    // Find all licenses for this institution...
    def result = [:]
    OrgRole.findAllByOrgAndRoleType(institution, licensee_role).each { it ->
      if ( it.lic?.status?.value != 'Deleted' ) {
        result["License:${it.lic?.id}"] = it.lic?.reference
      }
    }

    //log.debug("returning ${result} as available licenses");
    render result as JSON
  }

  def resolveOID(oid_components) {
    def result = null;
    def domain_class=grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${oid_components[0]}")
    if ( domain_class ) {
      result = domain_class.getClazz().get(oid_components[1])
    }
    result
  }

  def actionCurrentSubscriptions() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    def subscription = Subscription.get(params.basesubscription)

    if ( subscription.hasPerm("edit",result.user) ) {
      def derived_subs = Subscription.countByInstanceOf(subscription)
      
      if ( derived_subs == 0 ) {
        def deletedStatus = RefdataCategory.lookupOrCreate('Subscription Status','Deleted');
        subscription.status = deletedStatus
        subscription.save(flush:true);
      }
      else {
        flash.error = "Unable to delete - The selected license has attached subscriptions"
      }
    }
    else {
      log.warn("${result.user} attempted to delete subscription ${result.subscription} without perms")
      flash.message = message(code: 'subscription.delete.norights')
    }

    redirect action: 'currentSubscriptions', params: [shortcode:params.shortcode]
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def renewalsSearch() {

    log.debug("Search : ${params}");
    log.debug("Start year filters: ${params.startYear}");

    StringWriter sw = new StringWriter()
    def fq = null;
    boolean has_filter = false
  
    params.each { p ->
      if ( p.key.startsWith('fct:') && p.value.equals("on") ) {
        log.debug("start year ${p.key} : -${p.value}-");

        if ( !has_filter )
          has_filter = true
        else
          sw.append(" OR ")

        String[] filter_components = p.key.split(':');
            switch ( filter_components[1] ) {
              case 'consortiaName':
                sw.append('consortiaName')
                break;
              case 'startYear':
                sw.append('startYear')
                break;
              case 'contentProvider':
                sw.append('packages.cpname')
                break;
            }
            if ( filter_components[2].indexOf(' ') > 0 ) {
              sw.append(":'");
              sw.append(filter_components[2])
              sw.append("'");
            }
            else {
              sw.append(":");
              sw.append(filter_components[2])
            }
      }
    }

    if ( has_filter ) {
      fq = sw.toString();
      log.debug("Filter Query: ${fq}");
    }

    // Be mindful that the behavior of this controller is strongly influenced by the schema setup in ES.
    // Specifically, see KBPlus/import/processing/processing/dbreset.sh for the mappings that control field type and analysers
    // Internal testing with http://localhost:9200/kbplus/_search?q=subtype:'Subscription%20Offered'
    def result=[:]

    result.institution = Org.findByShortcode(params.shortcode)

    // Get hold of some services we might use ;)
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
    result.user = springSecurityService.getCurrentUser()

    if ( !checkUserIsMember(result.user, result.institution) ) {
      flash.error="You do not have permission to view ${result.institution.name}. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${result.institution.name}. Please request access on the profile page");
      return;
    }

    def shopping_basket = UserFolder.findByUserAndShortcode(result.user,'SOBasket') ?: new UserFolder(user:result.user, shortcode:'SOBasket').save();

    if ( params.addBtn ) {
      log.debug("Add item ${params.addBtn} to basket");
      def oid = "com.k_int.kbplus.Subscription:${params.addBtn}"
      shopping_basket.addIfNotPresent(oid)
      shopping_basket.save(flush:true);
    }
    else if ( params.clearBasket=='yes' ) {
      log.debug("Clear basket....");
      shopping_basket.items?.clear();
      shopping_basket.save(flush:true)
    }
    else if ( params.generate=='yes' ) {
      log.debug("Generate");
      generate(materialiseFolder(shopping_basket.items), result.institution)
      return
    }

    result.basket = materialiseFolder(shopping_basket.items)

    if (springSecurityService.isLoggedIn()) {

      try {

          params.max = Math.min(params.max ? params.int('max') : 10, 100)
          params.offset = params.offset ? params.int('offset') : 0

          //def params_set=params.entrySet()

          def query_str = buildRenewalsQuery(params)
          if ( fq ) 
            query_str = query_str + " AND ( " + fq + " ) "
          
          log.debug("query: ${query_str}");

          def search = esclient.search{
            indices "kbplus"
            source {
              from = params.offset
              size = params.max
              query {
                query_string (query: query_str)
              }
              facets {
                consortiaName {
                  terms {
                    field = 'consortiaName'
                  }
                }
                contentProvider {
                  terms {
                    field = 'packages.cpname'
                  }
                }
                startYear {
                  terms {
                    field = 'startYear'
                  }
                }
              }

            }

          }

          if ( search?.response ) {
            result.hits = search.response.hits
            result.resultsTotal = search.response.hits.totalHits

            // We pre-process the facet response to work around some translation issues in ES
            if ( search.response.facets != null ) {
              result.facets = [:]
              search.response.facets.facets.each { facet ->
                def facet_values = []
                facet.value.entries.each { fe ->
                  facet_values.add([term: fe.term,display:fe.term,count:"${fe.count}"])
                }
                result.facets[facet.key] = facet_values
              }
            }
          }
      }
      finally {
        try {
        }
        catch ( Exception e ) {
          log.error("problem",e);
        }
      }

    }  // If logged in

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def selectPackages() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)

    result.candidates = [:]
    def title_list = []
    def package_list = []

    result.titles_in_this_sub = result.subscriptionInstance.issueEntitlements.size();

    result.subscriptionInstance.issueEntitlements.each { e ->
      def title = e.tipp.title
      log.debug("Looking for packages offering title ${title.id} - ${title?.title}");

      title.tipps.each { t ->
        log.debug("  -> This title is provided by package ${t.pkg.id} on platform ${t.platform.id}");

        def title_idx = title_list.indexOf("${title.id}");
        def pkg_idx = package_list.indexOf("${t.pkg.id}:${t.platform.id}");

        if ( title_idx == -1 ) {
          log.debug("  -> Adding title ${title.id} to matrix result");
          title_list.add("${title.id}");
          title_idx = title_list.size();
        }

        if ( pkg_idx == -1 ) {
          log.debug("  -> Adding package ${t.pkg.id} to matrix result");
          package_list.add("${t.pkg.id}:${t.platform.id}");
          pkg_idx = package_list.size();
        }

        log.debug("  -> title_idx is ${title_idx} pkg_idx is ${pkg_idx}");

        def candidate = result.candidates["${t.pkg.id}:${t.platform.id}"]
        if ( !candidate ) {
          candidate = [:]
          result.candidates["${t.pkg.id}:${t.platform.id}"] = candidate;
          candidate.pkg=t.pkg.id
          candidate.platform=t.platform
          candidate.titlematch=0
          candidate.pkg = t.pkg
          candidate.pkg_title_count = t.pkg.tipps.size();
        }
        candidate.titlematch++;
        log.debug("  -> updated candidate ${candidate}");
      }
    }

    log.debug("titles list ${title_list}");
    log.debug("package list ${package_list}");

    log.debug("titles list size ${title_list.size()}");
    log.debug("package list size ${package_list.size()}");
    result
  }

  def buildRenewalsQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    sw.write("subtype:'Subscription Offered'")

    renewals_reversemap.each { mapping ->

      // log.debug("testing ${mapping.key}");

      if ( params[mapping.key] != null ) {
        if ( params[mapping.key].class == java.util.ArrayList) {
          params[mapping.key].each { p ->
                sw.write(" AND ")
                sw.write(mapping.value)
                sw.write(":")
                sw.write("\"${p}\"")
          }
        }
        else {
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *
          if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
            sw.write(" AND ")
            sw.write(mapping.value)
            sw.write(":")
            sw.write("\"${params[mapping.key]}\"")
          }
        }
      }
    }


    def result = sw.toString();
    result;
  }

  def materialiseFolder(f) {
    def result = []
    f.each {
      result.add(genericOIDService.resolveOID(it.referencedOid))
    }
    result
  }

  def generate(slist, inst) {
    def m = generateMatrix(slist)
    exportWorkbook(m, inst)
  }

  def generateMatrix(slist) {
    def titleMap = [:]
    def subscriptionMap = [:]

    log.debug("pre-pre-process");

    boolean first = true;

    def formatter = new java.text.SimpleDateFormat("yyyy/MM/dd")

    // Step one - Assemble a list of all titles and packages
    slist.each { sub ->

      def sub_info = [
        sub_idx : subscriptionMap.size(),
        sub_name : sub.name,
        sub_id : sub.id
      ]

      subscriptionMap[sub.id] = sub_info

      // For each subscription in the shopping basket
      sub.issueEntitlements.each { ie ->
        if ( ! (ie.status?.value=='Deleted')  ) {
          def title_info = titleMap[ie.tipp.title.id]
          if ( !title_info ) {
            // log.debug("Adding ie: ${ie}");
            title_info = [:]
            title_info.title_idx = titleMap.size()
            title_info.id = ie.tipp.title.id;
            title_info.issn = ie.tipp.title.getIdentifierValue('ISSN');
            title_info.eissn = ie.tipp.title.getIdentifierValue('eISSN');
            title_info.title = ie.tipp.title.title
            if ( first ) {
              if ( ie.startDate )
                title_info.current_start_date = formatter.format(ie.startDate)
              if ( ie.endDate )
                title_info.current_end_date = formatter.format(ie.endDate)
              title_info.current_embargo = ie.embargo
              title_info.current_depth = ie.coverageDepth
              title_info.current_coverage_note = ie.coverageNote
              title_info.is_core = ie.coreStatus?.value
              title_info.core_start_date = ie.coreStatusStart ? formatter.format(ie.coreStatusStart) : ''
              title_info.core_end_date = ie.coreStatusEnd ? formatter.format(ie.coreStatusEnd) : ''
              // log.debug("added title info: ${title_info}");
            }
            titleMap[ie.tipp.title.id] = title_info;
          }
        }
      }
      first=false
    }

    log.debug("Result will be a matrix of size ${titleMap.size()} by ${subscriptionMap.size()}");

    // Object[][] result = new Object[subscriptionMap.size()+1][titleMap.size()+1]
    Object[][] ti_info_arr = new Object[titleMap.size()][subscriptionMap.size()]
    Object[] sub_info_arr = new Object[subscriptionMap.size()]
    Object[] title_info_arr = new Object[titleMap.size()]

    subscriptionMap.values().each { v ->
      sub_info_arr[v.sub_idx] = v
    }

    titleMap.values().each { v ->
      title_info_arr[v.title_idx] = v
    }

    slist.each { sub ->
      def sub_info = subscriptionMap[sub.id]
      sub.issueEntitlements.each { ie ->
        def title_info = titleMap[ie.tipp.title.id]
        def ie_info = [:]
        log.debug("Adding tipp info ${ie.tipp.startDate} ${ie.tipp.derivedFrom}");
        ie_info.tipp_id = ie.tipp.id;
        ie_info.core = ie.coreStatus?.value
        ie_info.startDate_d = ie.tipp.startDate ?: ie.tipp.derivedFrom?.startDate
        ie_info.startDate = ie_info.startDate_d ? formatter.format(ie_info.startDate_d) : null
        ie_info.startVolume = ie.tipp.startVolume ?: ie.tipp.derivedFrom?.startVolume
        ie_info.startIssue = ie.tipp.startIssue ?: ie.tipp.derivedFrom?.startIssue
        ie_info.endDate_d = ie.endDate ?: ie.tipp.derivedFrom?.endDate
        ie_info.endDate = ie_info.endDate_d ? formatter.format(ie_info.endDate_d) : null
        ie_info.endVolume = ie.endVolume ?: ie.tipp.derivedFrom?.endVolume
        ie_info.endIssue = ie.endIssue ?: ie.tipp.derivedFrom?.endIssue
        ti_info_arr[title_info.title_idx][sub_info.sub_idx] = ie_info
      }
    }


    [ti_info:ti_info_arr,title_info:title_info_arr,sub_info:sub_info_arr]
  }

  def exportWorkbook(m, inst) {

    // read http://stackoverflow.com/questions/2824486/groovy-grails-how-do-you-stream-or-buffer-a-large-file-in-a-controllers-respon

    HSSFWorkbook workbook = new HSSFWorkbook();
 
    CreationHelper factory = workbook.getCreationHelper();

    //
    // Create two sheets in the excel document and name it First Sheet and
    // Second Sheet.
    //
    HSSFSheet firstSheet = workbook.createSheet("Renewals Worksheet");
    Drawing drawing = firstSheet.createDrawingPatriarch();

 
    // Cell style for a present TI
    HSSFCellStyle present_cell_style = workbook.createCellStyle();  
    present_cell_style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);  
    present_cell_style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  

    // Cell style for a core TI
    HSSFCellStyle core_cell_style = workbook.createCellStyle();  
    core_cell_style.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);  
    core_cell_style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  

    int rc=0;
    // header
    int cc=0;
    HSSFRow row = null;
    HSSFCell cell = null;

    // Blank rows
    row = firstSheet.createRow(rc++);
    row = firstSheet.createRow(rc++);
    cc=0;
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Subscriber ID"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Subscriber Name"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Subscriber Shortcode"));

    row = firstSheet.createRow(rc++);
    cc=0;
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("${inst.id}"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString(inst.name));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString(inst.shortcode));

    row = firstSheet.createRow(rc++);

    // Key
    row = firstSheet.createRow(rc++);
    cc=0;
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Key"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Title In Subscription"));
    cell.setCellStyle(present_cell_style);  
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Core Title"));
    cell.setCellStyle(core_cell_style);  
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Not In Subscription"));
    cell = row.createCell(11);
    cell.setCellValue(new HSSFRichTextString("Current Sub"));
    cell = row.createCell(12);
    cell.setCellValue(new HSSFRichTextString("Candidates ->"));
    

    row = firstSheet.createRow(rc++);
    cc=11
    m.sub_info.each { sub ->
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${sub.sub_id}"));
    }
    
    // headings
    row = firstSheet.createRow(rc++);
    cc=0;
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Title ID"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Title"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("ISSN"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("eISSN"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("current Start Date"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Current End Date"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Current Coverage Depth"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Current Coverage Note"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("IsCore?"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Core Start Date"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Core End Date"));

    m.sub_info.each { sub ->
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${sub.sub_name}"));

      // Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
      // link.setAddress("http://poi.apache.org/");
      // cell.setHyperlink(link);
    }

    m.title_info.each { title ->

      row = firstSheet.createRow(rc++);
      cc = 0;

      // Internal title ID
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.id}"));
      // Title
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.title?:''}"));

      // ISSN
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.issn?:''}"));

      // eISSN
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.eissn?:''}"));

      // startDate
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.current_start_date?:''}"));

      // endDate
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.current_end_date?:''}"));

      // coverageDepth
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.current_depth?:''}"));

      // embargo
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.current_coverage_note?:''}"));

      // IsCore
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.is_core?:''}"));

      // Core Start Date
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.core_start_date?:''}"));

      // Core End Date
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.core_end_date?:''}"));

      m.sub_info.each { sub ->
        cell = row.createCell(cc++);
        def ie_info = m.ti_info[title.title_idx][sub.sub_idx]
        if ( ie_info ) {
          if ( ( ie_info.core ) && ( ie_info.core != 'No' ) ) {
            cell.setCellValue(new HSSFRichTextString(""));
            cell.setCellStyle(core_cell_style);  
          }
          else {
            cell.setCellValue(new HSSFRichTextString(""));
            cell.setCellStyle(present_cell_style);  
          }
          addCellComment(row, cell,"${title.title} provided by ${sub.sub_name}\nStart Date:${ie_info.startDate?:'Not set'}\nStart Volume:${ie_info.startVolume?:'Not set'}\nStart Issue:${ie_info.startIssue?:'Not set'}\nEnd Date:${ie_info.endDate?:'Not set'}\nEnd Volume:${ie_info.endVolume?:'Not set'}\nEnd Issue:${ie_info.endIssue?:'Not set'}\nSelect Title by setting this cell to Y", drawing, factory);
        }

      }
    }
    row = firstSheet.createRow(rc++);
    cell = row.createCell(0);
    cell.setCellValue(new HSSFRichTextString("END"));

    firstSheet.autoSizeColumn(0); //adjust width of the first column
    firstSheet.autoSizeColumn(1); //adjust width of the first column
    firstSheet.autoSizeColumn(2); //adjust width of the first column
    firstSheet.autoSizeColumn(3); //adjust width of the first column
    for ( int i=0; i<m.sub_info.size(); i++ ) {
      firstSheet.autoSizeColumn(7+i); //adjust width of the second column
    }



    response.setHeader "Content-disposition", "attachment; filename='comparison.xls'"
    // response.contentType = 'application/xls'
    response.contentType = 'application/vnd.ms-excel'
    workbook.write(response.outputStream)
    response.outputStream.flush()
 
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def renewalsUpload() {
    def result = [:]

    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    if ( !checkUserIsMember(result.user, result.institution) ) {
      flash.error="You do not have permission to view ${result.institution.name}. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${result.institution.name}. Please request access on the profile page");
      return;
    }

    result.errors = []

    log.debug("upload");

    if ( request.method == 'POST' ) {
      def upload_mime_type = request.getFile("renewalsWorksheet")?.contentType
      def upload_filename = request.getFile("renewalsWorksheet")?.getOriginalFilename()
      log.debug("Uploaded worksheet type: ${upload_mime_type} filename was ${upload_filename}");
      def input_stream = request.getFile("renewalsWorksheet")?.inputStream
      processRenewalUpload(input_stream, upload_filename, result)
    }

    result
  }

  def processRenewalUpload(input_stream, upload_filename, result) {
    int SO_START_COL=12
    int SO_START_ROW=7
    log.debug("processRenewalUpload - opening upload input stream as HSSFWorkbook");
    if ( input_stream ) {
      HSSFWorkbook wb = new HSSFWorkbook(input_stream);
      HSSFSheet firstSheet = wb.getSheetAt(0);

      // Step 1 - Extract institution id, name and shortcode
      HSSFRow org_details_row = firstSheet.getRow(2)
      String org_name = org_details_row?.getCell(0)?.toString()
      String org_id = org_details_row?.getCell(1)?.toString()
      String org_shortcode = org_details_row?.getCell(2)?.toString()
      log.debug("Worksheet upload on behalf of ${org_name}, ${org_id}, ${org_shortcode}");

      def sub_info = []
      // Step 2 - Row 5 (6, but 0 based) contains package identifiers starting in column 4(5)
      HSSFRow package_ids_row = firstSheet.getRow(5)
      for (int i=SO_START_COL;((i<package_ids_row.getLastCellNum())&&(package_ids_row.getCell(i)));i++) {
        log.debug("Got package identifier: ${package_ids_row.getCell(i).toString()}");
        def sub_id = Long.parseLong(package_ids_row.getCell(i).toString())
        def sub_rec = Subscription.get(sub_id);
        if ( sub_rec ) {
          sub_info.add(sub_rec);
        }
        else  {
          log.error("Unable to resolve the package identifier in row 6 column ${i+5}, please check");
          return
        }
      }

      result.entitlements = []

      boolean processing = true
      // Step three, process each title row, starting at row 11(10)
      for (int i=SO_START_ROW;((i<firstSheet.getLastRowNum())&&(processing)); i++) {
        log.debug("processing row ${i}");

        HSSFRow title_row = firstSheet.getRow(i)
        // Title ID
        def title_id = title_row.getCell(0).toString()
        if ( title_id == 'END' ) {
          log.debug("Encountered END title");
          processing = false;
        }
        else {
          log.debug("Upload Process title: ${title_id}, num subs=${sub_info.size()}, last cell=${title_row.getLastCellNum()}");
          def title_id_long = Long.parseLong(title_id)
          def title_rec = TitleInstance.get(title_id_long);
          for ( int j=0; ( ((j+SO_START_COL)<title_row.getLastCellNum()) && (j<=sub_info.size() ) ); j++ ) {
            def resp_cell = title_row.getCell(j+SO_START_COL)
            if ( resp_cell ) {
              log.debug("  -> Testing col[${j+SO_START_COL}] val=${resp_cell.toString()}");

              def subscribe=resp_cell.toString()

              log.debug("Entry : sub:${subscribe}");
                
              if ( subscribe == 'Y' || subscribe == 'y' ) {
                log.debug("Add an issue entitlement from subscription[${j}] for title ${title_id_long}");
                if ( result.base_subscription ) {
                  // if ( result.base_subscription != sub_info[j] ) {
                  //   log.error("Critical error - Worksheet merges entitlements from 2 different subscriptions offered");
                  //   result.errors.add("Critical error - Worksheet merges entitlements from 2 different subscriptions offered");
                  // }
                }
                else {
                  result.base_subscription = sub_info[j]
                }

                def entitlement_info = [:]
                entitlement_info.title_id = title_id_long
                entitlement_info.subscribe = subscribe
                entitlement_info.base_entitlement = extractEntitlement(sub_info[j], title_id_long)

                entitlement_info.start_date = title_row.getCell(4)
                entitlement_info.end_date = title_row.getCell(5)
                entitlement_info.coverage = title_row.getCell(6)
                entitlement_info.coverage_note = title_row.getCell(7)
                entitlement_info.core_status = title_row.getCell(8)
                entitlement_info.core_start_date = title_row.getCell(9)
                entitlement_info.core_end_date = title_row.getCell(10)

                log.debug("Added entitlement_info ${entitlement_info}");
                result.entitlements.add(entitlement_info)
              }
            }
          }
        }
      }
    }
    else {
      log.error("Input stream is null");
    }
    log.debug("Done");

    result
  }

  def extractEntitlement(sub, title_id) {
    def result = sub.issueEntitlements.find { e -> e.tipp?.title?.id == title_id }
    if ( result == null ) {
      log.error("Failed to look up title ${title_id} in subscription ${sub.sub_name}");
    }
    result
  }

  def processRenewal() {
    def result = [:]

    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    if ( !checkUserIsMember(result.user, result.institution) ) {
      flash.error="You do not have permission to view ${result.institution.name}. Please request access on the profile page";
      response.sendError(401)
      // render(status: '401', text:"You do not have permission to access ${result.institution.name}. Please request access on the profile page");
      return;
    }

    log.debug("-> renewalsUpload params: ${params}");

    log.debug("entitlements...[${params.ecount}]");

    int ent_count = Integer.parseInt(params.ecount);

    def db_sub = Subscription.get(params.baseSubscription);

    def new_subscription = new Subscription(
                                 identifier: "${db_sub.identifier}:${result.institution.name}",
                                 status:RefdataCategory.lookupOrCreate('Subscription Status','Current'),
                                 impId:null,
                                 name: db_sub.name,
                                 startDate: db_sub.startDate,
                                 endDate: db_sub.endDate,
                                 instanceOf: db_sub,
                                 type: RefdataValue.findByValue('Subscription Taken') )

    if ( new_subscription.save() ) {
      // log.debug("New subscriptionT saved...");
      // Copy package links from SO to ST
      db_sub.packages.each { sopkg ->
        def new_package_link = new SubscriptionPackage(subscription:new_subscription, pkg:sopkg.pkg).save();
      }

      // assert an org-role
      def org_link = new OrgRole(org:result.institution,
                                 sub: new_subscription,
                                 roleType: RefdataCategory.lookupOrCreate('Organisational Role','Subscriber')).save();

      // Copy any links from SO
      db_sub.orgRelations.each { or ->
        if ( or.roleType?.value != 'Subscriber' ) {
          def new_or = new OrgRole(org: or.org, sub: new_subscription, roleType: or.roleType).save();
        }
      }
    }
    else {
      log.error("Problem saving new subscription, ${new_subscription.errors}");
    }

    new_subscription.save(flush:true);


    if ( !new_subscription.issueEntitlements ) {
      new_subscription.issueEntitlements = new java.util.TreeSet()
    }

    for ( int i=0; i<=ent_count; i++ ) {
      def entitlement = params.entitlements."${i}";
      log.debug("process entitlement[${i}]: ${entitlement} - TIPP id is ${entitlement.tipp_id}");

      def dbtipp = TitleInstancePackagePlatform.get(entitlement.tipp_id)

      if ( dbtipp ) {
        def original_entitlement = IssueEntitlement.get(entitlement.entitlement_id)
        def live_issue_entitlement = RefdataCategory.lookupOrCreate('Entitlement Issue Status', 'Live');
        def is_core = false

        def new_core_status = null;

        switch ( entitlement.core_status?.toUpperCase() ) {
          case 'Y':
          case 'YES':
            new_core_status = RefdataCategory.lookupOrCreate('CoreStatus','Yes');
            is_core = true;
            break;
          case 'P':
          case 'PRINT':
            new_core_status = RefdataCategory.lookupOrCreate('CoreStatus','Print');
            is_core = true;
            break;
          case 'E':
          case 'ELECTRONIC':
            new_core_status = RefdataCategory.lookupOrCreate('CoreStatus','Electronic');
            is_core = true;
            break;
          case 'P+E':
          case 'E+P':
            new_core_status = RefdataCategory.lookupOrCreate('CoreStatus','Print+Electronic');
            is_core = true;
            break;
          default:
            new_core_status = RefdataCategory.lookupOrCreate('CoreStatus','No');
            break;
        }

        def new_start_date = entitlement.start_date ? parseDate(entitlement.start_date, possible_date_formats)  : null
        def new_end_date = entitlement.end_date ?  parseDate(entitlement.end_date, possible_date_formats) : null
        def new_core_start_date = entitlement.core_start_date ? parseDate(entitlement.core_start_date, possible_date_formats) : null
        def new_core_end_date = entitlement.core_end_date ? parseDate(entitlement.core_end_date, possible_date_formats) : null


        // entitlement.is_core
        def new_ie =  new IssueEntitlement(subscription:new_subscription,
                                           status: live_issue_entitlement,
                                           tipp: dbtipp,
                                           startDate:new_start_date ?: dbtipp.startDate,
                                           startVolume:dbtipp.startVolume,
                                           startIssue:dbtipp.startIssue,
                                           endDate:new_end_date ?: dbtipp.endDate,
                                           endVolume:dbtipp.endVolume,
                                           endIssue:dbtipp.endIssue,
                                           embargo:dbtipp.embargo,
                                           coverageDepth:dbtipp.coverageDepth,
                                           coverageNote:dbtipp.coverageNote,
                                           coreStatus:new_core_status,
                                           coreStatusStart:new_core_start_date,
                                           coreStatusEnd:new_core_end_date
                                           )

        if ( new_ie.save() ) {
          log.debug("new ie saved");
        }
        else {
          new_ie.errors.each{ e ->
            log.error("Problem saving new ie : ${e}");
          }
        }
      }
      else {
        log.debug("Unable to locate tipp with id ${entitlement.tipp_id}");
      }
    }
    log.debug("done entitlements...");

    if ( new_subscription )
      redirect controller:'subscriptionDetails', action:'index', id:new_subscription.id
    else
      redirect action:'renewalsUpload', params:params
  }

  def addCellComment(row, cell, comment_text, drawing, factory) {

    // When the comment box is visible, have it show in a 1x3 space
    ClientAnchor anchor = factory.createClientAnchor();
    anchor.setCol1(cell.getColumnIndex());
    anchor.setCol2(cell.getColumnIndex()+7);
    anchor.setRow1(row.getRowNum());
    anchor.setRow2(row.getRowNum()+9);

    // Create the comment and set the text+author
    def comment = drawing.createCellComment(anchor);
    RichTextString str = factory.createRichTextString(comment_text);
    comment.setString(str);
    comment.setAuthor("KBPlus System");

    // Assign the comment to the cell
    cell.setCellComment(comment);
  }

  def checkUserIsMember(user, org) {
    def result = false;
    // def uo = UserOrg.findByUserAndOrg(user,org)
    def uoq = UserOrg.where {
      (user == user && org == org && ( status == 1 || status == 3 ))
    }

    if ( uoq.count() > 0 )
      result = true;

    result
  }

  def checkUserHasRole(user, org, role) {
    def uoq = UserOrg.createCriteria()
    def grants = uoq.list {
      eq('user',user)
      eq('org',org)
      formalRole {
        eq('authority',role)
      }
      or {
        eq('status',1);
        eq('status',3);
      }
    }

    if ( grants && grants.size() > 0 )
      return true

    return false

  }

  def parseDate(datestr, possible_formats) {
    def parsed_date = null;
    if ( datestr && ( datestr.toString().trim().length() > 0 ) ) {
      for(Iterator i = possible_formats.iterator(); ( i.hasNext() && ( parsed_date == null ) ); ) {
        try {
          parsed_date = i.next().parse(datestr.toString());
        }
        catch ( Exception e ) {
        }
      }
    }
    parsed_date
  }

}

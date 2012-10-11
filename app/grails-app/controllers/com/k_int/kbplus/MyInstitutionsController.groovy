package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class MyInstitutionsController {

  def springSecurityService
  def docstoreService
  def ESWrapperService
  def gazetteerService
  def alertsService

  def reversemap = ['subject':'subject', 'provider':'provid', 'studyMode':'presentations.studyMode','qualification':'qual.type','level':'qual.level' ]


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    // Work out what orgs this user has admin level access to
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.userAlerts = alertsService.getAllVisibleAlerts(result.user);
    
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

    def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
    def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');

    // def qry = "select l from License as l left outer join l.orgLinks ol where ( ( l.type = ? ) OR ( ol.org = ? and ol.roleType = ? ) ) AND l.status.value != 'Deleted'"
    // def qry = "select l from License as l left outer join l.orgLinks ol where ( ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted'"
    def qry = "select l from License as l where exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted'"

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      qry += " order by l.${params.sort} ${params.order}"
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

    def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
    def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');

    // def qry = "select l from License as l left outer join l.orgLinks ol where ( ( l.type = ? ) OR ( ol.org = ? and ol.roleType = ? ) ) AND l.status.value != 'Deleted'"
    // def qry = "select l from License as l left outer join l.orgLinks ol where l.type = ? AND l.status.value != 'Deleted'"
    def qry = "select l from License as l where ( ( l.type = ? ) OR ( exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) ) ) AND l.status.value != 'Deleted'"

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      qry += " order by l.${params.sort} ${params.order}"
    }

    result.licenses = License.executeQuery(qry, [template_license_type, result.institution, licensee_role] )
    // result.licenses = License.executeQuery(qry, [template_license_type])

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def currentSubscriptions() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : 10;
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;


    def base_qry = " from Subscription as s where exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) AND ( s.status.value != 'Deleted' ) "
    def qry_params = [result.institution]

    if ( params.q?.length() > 0 ) {
      base_qry += " and ( lower(s.name) like ? or exists ( select sp from SubscriptionPackage as sp where sp.subscription = s and ( lower(sp.pkg.name) like ? or lower(sp.pkg.contentProvider.name) like ? ) ) ) "
      qry_params.add("%${params.q.trim().toLowerCase()}%");
      qry_params.add("%${params.q.trim().toLowerCase()}%");
      qry_params.add("%${params.q.trim().toLowerCase()}%");
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += " order by ${params.sort} ${params.order}"
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

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : 10;
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;


    def base_qry = " from Subscription as s where s.type.value = 'Subscription Offered'"
    def qry_params = []

    if ( params.q?.length() > 0 ) {
      base_qry += " and ( lower(s.name) like ? or exists ( select sp from SubscriptionPackage as sp where sp.subscription = s and ( lower(sp.pkg.name) like ? or lower(sp.pkg.contentProvider.name) like ? ) ) ) "
      qry_params.add("%${params.q.trim().toLowerCase()}%");
      qry_params.add("%${params.q.trim().toLowerCase()}%");
      qry_params.add("%${params.q.trim().toLowerCase()}%");
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += " order by ${params.sort} ${params.order}"
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
    
    switch (request.method) {
      case 'GET':
        [licenseInstance: new License(params)]
        break
      case 'POST':
        def baseLicense = params.baselicense ? License.get(params.baselicense) : null;
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
    def license = License.get(params.baselicense)

    if ( hasAdminRights(result.user,license) ) {
      if ( ( license.subscriptions == null ) || ( license.subscriptions.size() == 0 ) ) {
        def deletedStatus = RefdataCategory.lookupOrCreate('License Status','Deleted');
        license.status = deletedStatus
        license.save(flush:true);
      }
      else {
        flash.error = "Unable to delete - The selected license has attached subscriptions"
      }
    }
    else {
      log.warn("Attempt by ${result.user} to delete license ${result.license}without perms")
      flash.message = message(code: 'license.delete.norights')
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

    log.debug("processAddSubscription ${params}");

    def baseSubscription = Subscription.get(params.subOfferedId);

    if ( baseSubscription ) {
      log.debug("Copying base subscription ${baseSubscription.id} for org ${institution.id}");

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
                                 coreTitle:bie.coreTitle,
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

  // Placeholder to determine if the supplied user has admin rights over the speciifed object
  def hasAdminRights(user, object) {
    def result = false;

    if ( object && user ) {
      def user_orgs = user.affiliations.collect { it.org }

      if ( object instanceof License ) {
        // Work out which org "owns" this license
        def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
        def or = OrgRole.findByLicAndRoleType(object, licensee_role)
        if ( or ) {
          // The license owner must be the users institution
          if ( user_orgs.contains(or.org) ) {
            result = true
          }
        }
      }
      else if ( object instanceof Subscription ) {
        def subscriber_role = RefdataCategory.lookupOrCreate('Organisational Role','Subscriber');
        def or = OrgRole.findBySubAndRoleType(object, subscriber_role)
        if ( or ) {
          // The license owner must be the users institution
          if ( user_orgs.contains(or.org) ) {
            result = true
          }
        }
        else {
        }
      }
      else {
        log.warn("Unknown has admin rights object : ${object?.class?.name}");
      }
    }

    result;
  }

  def actionCurrentSubscriptions() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    def subscription = Subscription.get(params.basesubscription)

    if ( hasAdminRights(result.user,subscription) ) {
      if ( 1==1 ) {
        def deletedStatus = RefdataCategory.lookupOrCreate('Subscription Status','Deleted');
        subscription.status = deletedStatus
        subscription.save(flush:true);
      }
      else {
        flash.error = "Unable to delete - The selected license has attached subscriptions"
      }
    }
    else {
      log.warn("Attempt by ${result.user} to delete subscription ${result.subscription} without perms")
      flash.message = message(code: 'subscription.delete.norights')
    }

    redirect action: 'currentSubscriptions', params: [shortcode:params.shortcode]
  }
}

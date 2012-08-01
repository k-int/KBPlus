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
    result.userAlerts = alertsService.getActiveAlerts(result.user);
    
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

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def manageAffiliations() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
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
  def licenses() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
    def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');

    result.licenses = License.executeQuery("select l from License as l left outer join l.orgLinks ol where ( ( l.type = ? ) OR ( ol.org = ? and ol.roleType = ? ) ) AND l.status.value != 'Deleted'",
                                              [template_license_type, result.institution, licensee_role] )

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def licensesOld() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
    def template_license_type = RefdataCategory.lookupOrCreate('License Type','Template');
    def model_licenses = License.findAllByType(template_license_type);

    // We want to find all org role objects for this instutution where role type is licensee
    result.licenses = []
    // result.licenses.addAll(model_licenses)

    model_licenses.each { ml ->
      if ( ml.status?.value != 'Deleted' )
        result.licenses.add(ml);
    }

    // Find all licenses for this institution...
    // result.licenses.addAll(OrgRole.findAllByOrgAndRoleType(result.institution, licensee_role).collect { it.lic } )
    OrgRole.findAllByOrgAndRoleType(result.institution, licensee_role).each { or ->
      if ( or.lic?.status?.value!='Deleted' ) {
        result.licenses.add(or.lic);
      }
    }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def currentSubscriptions() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    def sc = Subscription.createCriteria()
    result.subscriptions = sc.list {
      orgRelations {
        and {
          roleType {
            eq('value','Subscriber')
          }
          eq('org', result.institution)
        }
      }
    }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def addSubscription() {

    log.debug("Search Index, params.q=${params.q}, format=${params.format}")

    def result = [:]

    if ( (!params.q) || ( params.q.length()==0 ) )
      params.q = '*';

    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    // Get hold of some services we might use ;)

    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

    result.user = User.get(springSecurityService.principal.id)

    try {
      if ( params.q && params.q.length() > 0) {
  
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.offset = params.offset ? params.int('offset') : 0
  
        def query_str = buildQuery(params)
        log.debug("query: ${query_str}");
  
        def search = esclient.search{
          indices "kbplus"
          source {
            from = params.offset
            size = params.max
            query {
              query_string (query: query_str)
            }
          }
        }

        if ( search?.response ) {
          result.hits = search.response.hits
          result.resultsTotal = search.response.hits.totalHits
          log.debug("Search result: total:${result.resultsTotal}");
        }

      }
      else {
        log.debug("No query.. Show search page")
      }
    }
    finally {
      try {
      }
      catch ( Exception e ) {
        log.error("problem",e);
      }
    }

    result
  }

  def buildQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    if ( ( params != null ) && ( params.q != null ) )
        if(params.q.equals("*")){
            sw.write(params.q)
        }
        else{
            sw.write(params.q)
        }
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
  def processJoinRequest() {
    log.debug("processJoinRequest org with id ${params.org}");
    def user = User.get(springSecurityService.principal.id)
    def org = com.k_int.kbplus.Org.get(params.org)
    if ( ( org != null ) && ( params.role != null ) ) {
      def p = new UserOrg(dateRequested:System.currentTimeMillis(), 
                          status:0, 
                          org:org, 
                          user:user, 
                          role:params.role)
      p.save(flush:true)
    }
    redirect(action: "manageAffiliations")
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
    redirect action: 'licenseDetails', params:params, id:licenseInstance.id
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
          baseLicense.documents?.each { dctx ->
            DocContext ndc = new DocContext(owner:dctx.owner,
                                            license: licenseInstance,
                                            domain: dctx.domain,
                                            status: dctx.status,
                                            doctype: dctx.doctype).save()
          }
        }

        flash.message = message(code: 'license.created.message', args: [message(code: 'license.label', default: 'License'), licenseInstance.id])
        redirect action: 'licenseDetails', params:params, id:licenseInstance.id
        //redirect action: 'show', id: licenseInstance.id
        break
    }

  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def licenseDetails() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)
    result
  }

  def deleteLicense(params) {
    log.debug("deleteLicense id:${params.baselicense}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    def license = License.get(params.baselicense)

    if ( hasAdminRights(result.user,license) ) {
      def deletedStatus = RefdataCategory.lookupOrCreate('License Status','Deleted');
      license.status = deletedStatus
    }
    else {
      log.warn("Attempt by ${result.user} to delete license ${result.license}without perms")
      flash.message = message(code: 'license.delete.norights')
    }
    
    redirect action: 'licenses', params: [shortcode:params.shortcode]
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def subscriptionDetails() {
    log.debug("subscriptionDetails id:${params.id}");
    def result = [:]

    def paginate_after = params.paginate_after ?: 29;
    result.max = params.max ? Integer.parseInt(params.max) : 15;
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)
    result.subscriptionInstance = Subscription.get(params.id)

    def base_qry = null;

    def qry_params = [result.subscriptionInstance]

    if ( params.filter ) {
      base_qry = " from IssueEntitlement as ie left outer join ie.tipp.title.ids ids where ie.subscription = ? and ( ( ie.tipp.title.title like ? ) or ( ids.identifier.value like ? ) )"
      qry_params.add("%${params.filter}%")
      qry_params.add("%${params.filter}%")
    }
    else {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? "
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += "order by ${params.sort} ${params.order} "
    }
    // result.num_sub_rows = IssueEntitlement.countBySubscription(result.subscriptionInstance);
    result.num_sub_rows = IssueEntitlement.executeQuery("select count(ie) "+base_qry, qry_params )[0]

    // result.entitlements = IssueEntitlement.findAllBySubscription(result.subscriptionInstance, [max:result.max, offset:result.offset, sort:'tipp.title.title', order:'asc']);
    // result.entitlements = IssueEntitlement.findAllBySubscription(result.subscriptionInstance, [max:result.max, offset:result.offset, sort:params.sort, order:params.order]);
    result.entitlements = IssueEntitlement.executeQuery("select ie "+base_qry, qry_params, [max:result.max, offset:result.offset]);

    log.debug("subscriptionInstance returning...");
    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def uploadDocument() {
    log.debug("upload document....");

    def input_stream = request.getFile("upload_file")?.inputStream
    def original_filename = request.getFile("upload_file")?.originalFilename
    def l = License.get(params.licid);

    log.debug("uploadDocument ${params} upload file = ${original_filename}");

    if ( l && input_stream ) {
      def docstore_uuid = docstoreService.uploadStream(input_stream, original_filename, params.upload_title)
      log.debug("Docstore uuid is ${docstore_uuid}");

      if ( docstore_uuid ) {
        log.debug("Docstore uuid present (${docstore_uuid}) Saving info");
        def doc_content = new Doc(contentType:1,
                                  uuid: docstore_uuid,
                                  filename: original_filename,
                                  mimeType: request.getFile("upload_file")?.contentType,
                                  title: params.upload_title,
                                  type:RefdataCategory.lookupOrCreate('Document Type','License')).save()
  
        def doc_context = new DocContext(license:l,
                                         owner:doc_content,
                                         doctype:RefdataCategory.lookupOrCreate('Document Type','License')).save(flush:true);
      }
    }

    log.debug("Redirecting...");
    redirect action: 'licenseDetails', params:[shortcode:params.shortcode], id:params.licid, fragment:params.fragment
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

    redirect action: 'licenseDetails', params:[shortcode:params.shortcode], id:params.licid, fragment:'docstab'
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

        log.debug("Setting sub/org link");
        def subscriber_org_link = new OrgRole(org:institution, sub:subscriptionInstance, roleType: RefdataCategory.lookupOrCreate('Organisational Role','Subscriber')).save();
        log.debug("Adding packages");
        baseSubscription.packages.each { bp ->
          new SubscriptionPackage(subscription:subscriptionInstance, pkg:bp.pkg).save();
        }
        log.debug("Save ok");
      }

      flash.message = message(code: 'subscription.created.message', args: [message(code: 'subscription.label', default: 'License'), subscriptionInstance.id])
      redirect action: 'subscriptionDetails', params:params, id:subscriptionInstance.id
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
      result["License:${it.lic?.id}"] = it.lic?.reference
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

  def uploadNewNote() {
    def result=[:]
    log.debug("uploadNewNote ${params}");

    def user = User.get(springSecurityService.principal.id)
    def institution = Org.findByShortcode(params.shortcode)

    def l = License.get(params.licid);

    if ( l ) {
      def doc_content = new Doc(contentType:0,
                                content: params.licenceNote,
                                type:RefdataCategory.lookupOrCreate('Document Type','Note')).save()

      def alert = null;
      if ( params.licenceNoteShared ) {
        switch ( params.licenceNoteShared ) {
          case "0":
            break;
          case "1":
            alert = new Alert(sharingLevel:1, createdBy:user, org:institution).save();
            break;
          case "2":
            alert = new Alert(sharingLevel:2, createdBy:user, org:institution).save();
            break;
        }
      }

      def doc_context = new DocContext(license:l,
                                       owner:doc_content,
                                       doctype:RefdataCategory.lookupOrCreate('Document Type','Note'),
                                       alert:alert).save(flush:true);
    }

    log.debug("Redirect...");
    redirect action: 'licenseDetails', params:[shortcode:params.shortcode], id:params.licid, fragment:params.fragment
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
    }

    result;
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def subscriptionBatchUpdate() {
    def subscriptionInstance = Subscription.get(params.id)
    def formatter = new java.text.SimpleDateFormat("MM/dd/yyyy")

    params.each { p ->
      if (p.key.startsWith('_bulkflag.') ) {
        def ie_to_edit = p.key.substring(10);

        def ie = IssueEntitlement.get(ie_to_edit)

        if ( params.bulk_start_date && ( params.bulk_start_date.trim().length() > 0 ) ) {
          ie.startDate = formatter.parse(params.bulk_start_date)
        }

        if ( params.bulk_end_date && ( params.bulk_end_date.trim().length() > 0 ) ) {
          ie.endDate = formatter.parse(params.bulk_end_date)
        }

        if ( params.bulk_embargo && ( params.bulk_embargo.trim().length() > 0 ) ) {
          ie.embargo = params.bulk_embargo
        }

        if ( params.bulk_core && (params.bulk_core.trim().length() > 0 ) ) {
          ie.coreTitle = params.bulk_core
        }

        if ( ie.save(flush:true) ) {
        }
        else {
          log.error("Problem saving ${ie.errors}")
        }
      }
    }

 
    redirect action: 'subscriptionDetails', params:[shortcode:params.shortcode, id:subscriptionInstance?.id], id:subscriptionInstance.id
  }



}

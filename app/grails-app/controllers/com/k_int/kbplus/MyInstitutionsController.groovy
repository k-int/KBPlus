package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import com.k_int.kbplus.auth.UserOrg
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.apache.poi.hslf.model.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*
import com.k_int.custprops.PropertyDefinition
// import org.json.simple.JSONArray;
// import org.json.simple.JSONObject;
import java.text.SimpleDateFormat

class MyInstitutionsController {

    def springSecurityService
    def ESWrapperService
    def ESSearchService
    def gazetteerService
    def alertsService
    def genericOIDService
    def factService
    def zenDeskSyncService
    def exportService
    def transformerService
    def institutionsService
    def docstoreService
    static String INSTITUTIONAL_LICENSES_QUERY = " from License as l where exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted'"

    // Map the parameter names we use in the webapp with the ES fields
    def renewals_reversemap = ['subject': 'subject', 'provider': 'provid', 'pkgname': 'tokname']
    def reversemap = ['subject': 'subject', 'provider': 'provid', 'studyMode': 'presentations.studyMode', 'qualification': 'qual.type', 'level': 'qual.level']

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
        log.debug("index for user with id ${springSecurityService.principal.id} :: ${result.user}");

        if ( result.user ) {
          result.userAlerts = alertsService.getAllVisibleAlerts(result.user);
          result.staticAlerts = alertsService.getStaticAlerts(request);


          if ((result.user.affiliations == null) || (result.user.affiliations.size() == 0)) {
              redirect controller: 'profile', action: 'index'
          } else {
          }

        }
        else {
          log.error("Failed to find user in database");
        }

        result
    }
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def tipview() {
        log.debug("admin::tipview ${params}")
        def result = [:]
       
        result.user = User.get(springSecurityService.principal.id)
        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
        def current_inst = null
        if(params.shortcode) current_inst = Org.findByShortcode(params.shortcode);
        //Parameters needed for criteria searching
        def (tip_property, property_field) = (params.sort ?: 'title-title').split("-")
        def list_order = params.order ?: 'asc'


        def criteria = TitleInstitutionProvider.createCriteria();
        def results = criteria.list(max: result.max, offset:result.offset) {
              if (params.shortcode){
                institution{
                    idEq(current_inst.id)
                }
              }
              if (params.search_for == "institution") {
                institution {
                  ilike("name", "%${params.search_str}%")         
                }
              }
             if (params.search_for == "provider") {
                provider {
                  ilike("name", "%${params.search_str}%")         
                }
             }
             if (params.search_for == "title") {
                title {
                  ilike("title", "%${params.search_str}%")         
                }
             }
             if(params.filter == "core" || !params.filter){
               isNotEmpty('coreDates') 
             }else if(params.filter=='not'){
                isEmpty('coreDates')
             }
             "${tip_property}"{
                order(property_field,list_order)
             }
        }

        result.tips = results
        result.institution = current_inst
        result.editable = current_inst?.hasUserWithRole(result.user,'INST_ADM')
        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def dashboard() {
        // Work out what orgs this user has admin level access to
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.userAlerts = alertsService.getAllVisibleAlerts(result.user);
        result.staticAlerts = alertsService.getStaticAlerts(request);

        if ((result.user.affiliations == null) || (result.user.affiliations.size() == 0)) {
            redirect controller: 'profile', action: 'index'
        } else {
        }
        result
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def actionLicenses() {
        log.debug("actionLicenses :: ${params}")
        if (params['copy-licence']) {
            newLicense(params)
        } else if (params['delete-licence']) {
            deleteLicense(params)
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def currentLicenses() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)
        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to view ${result.institution.name}. Please request access on the profile page";
            response.sendError(401)
            return;
        }

        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) {
            result.is_admin = true
        } else {
            result.is_admin = false;
        }

        def date_restriction = null;
        def sdf = new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)
 
        if (params.validOn == null) {
            result.validOn = sdf.format(new Date(System.currentTimeMillis()))
            date_restriction = sdf.parse(result.validOn)
        } else if (params.validOn == '') {
            result.validOn = sdf.format(new Date(System.currentTimeMillis()))
        } else {
            result.validOn = params.validOn
     
            date_restriction = sdf.parse(params.validOn)
        }

        def prop_types_list = PropertyDefinition.findAll()
        result.custom_prop_types = prop_types_list.collectEntries{
            [(it.name) : it.type + "&&" + it.refdataCategory]
            //We do this for the interface, so we can display select box when we are working with refdata.
            //Its possible there is another way
        }

        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
        result.max = params.format ? 10000 : result.max
        result.offset = params.format? 0 : result.offset

        def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee');
        def template_license_type = RefdataCategory.lookupOrCreate('License Type', 'Template');

        def qry_params = [result.institution, licensee_role]

        def qry = INSTITUTIONAL_LICENSES_QUERY
        // def qry = "from License as l where exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted'"

        if ((params['keyword-search'] != null) && (params['keyword-search'].trim().length() > 0)) {
            qry += " and lower(l.reference) like ?"
            qry_params += "%${params['keyword-search'].toLowerCase()}%"
            result.keyWord = params['keyword-search'].toLowerCase()
        }
        if( (params.propertyFilter != null) && params.propertyFilter.trim().length() > 0 ) {
            def propDef = PropertyDefinition.findByName(params.propertyFilterType)
            def propQuery = buildPropertySearchQuery(params,propDef)
            qry += propQuery.query
            qry_params += propQuery.queryParam
            result.propertyFilterType = params.propertyFilterType
            result.propertyFilter = params.propertyFilter
        } 

        if (date_restriction) {
            qry += " and l.startDate <= ? and l.endDate >= ? "
            qry_params.add(date_restriction)
            qry_params.add(date_restriction)
        }

        if ((params.sort != null) && (params.sort.length() > 0)) {
            qry += " order by l.${params.sort} ${params.order}"
        } else {
            qry += " order by l.reference asc"
        }

        result.licenseCount = License.executeQuery("select count(l) ${qry}", qry_params)[0];
        result.licenses = License.executeQuery("select l ${qry}", qry_params, [max: result.max, offset: result.offset]);

        withFormat {
            html result

            json {
                response.setHeader("Content-disposition", "attachment; filename=\"${result.institution.name}_licences.json\"")
                response.contentType = "application/json"
                render (result as JSON)
            }
            csv {
                response.setHeader("Content-disposition", "attachment; filename=${result.institution.name}_licences.csv")
                response.contentType = "text/csv"

                def out = response.outputStream
                exportService.StreamOutCurrentLicencesCSV(out, result)
                out.close()
            }
        }
    }
    def buildPropertySearchQuery(params,propDef) {
        def result = [:]

        def query = " and exists ( select cp from l.customProperties as cp where cp.type.name = ? and  "
        def queryParam = [params.propertyFilterType];
        switch (propDef.type){
            case Integer.toString():
                query += "cp.intValue = ? "
                def value;
                try{
                 value =Integer.parseInt(params.propertyFilter)
                }catch(Exception e){
                    log.error("Exception parsing search value: ${e}")
                    value = 0
                }
                queryParam += value
                break;
            case BigDecimal.toString():
                query += "cp.decValue = ? "
                try{
                 value = new BigDecimal(params.propertyFilter)
                }catch(Exception e){
                    log.error("Exception parsing search value: ${e}")
                    value = 0.0
                }
                queryParam += value
                break;
            case String.toString():
                query += "cp.stringValue like ? "
                queryParam += params.propertyFilter
                break;
            case RefdataValue.toString():
                query += "cp.refValue.value like ? "
                queryParam += params.propertyFilter
                break;
            default:
                log.error("Error executing buildPropertySearchQuery. Definition type ${propDef.type} case not found. ")
        }
        query += ")"
        
        result.query = query
        result.queryParam = queryParam
        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def addLicense() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to view ${result.institution.name}. Please request access on the profile page";
            response.sendError(401)
            return;
        }

        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) {
            result.is_admin = true
        } else {
            result.is_admin = false;
        }

        def template_license_type = RefdataCategory.lookupOrCreate('License Type', 'Template');
        def qparams = [template_license_type]
        def public_flag = RefdataCategory.lookupOrCreate('YN', 'No');

       // This query used to allow institutions to copy their own licenses - now users only want to copy template licenses
        // (OS License specs)
        // def qry = "from License as l where ( ( l.type = ? ) OR ( exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) ) OR ( l.isPublic=? ) ) AND l.status.value != 'Deleted'"

        def query = "from License as l where l.type = ? AND l.status.value != 'Deleted'"

        if (params.filter) {
            query += " and lower(l.reference) like ?"
            qparams.add("%${params.filter.toLowerCase()}%")
        }

        //separately select all licences that are not public or are null, to test access rights.
        // For some reason that I could track, l.isPublic != 'public-yes' returns different results.
        def non_public_query = query + " and ( l.isPublic = ? or l.isPublic is null) "

        if ((params.sort != null) && (params.sort.length() > 0)) {
            query += " order by l.${params.sort} ${params.order}"
        } else {
            query += " order by sortableReference asc"
        }

        println qparams
        result.numLicenses = License.executeQuery("select count(l) ${query}", qparams)[0]
        result.licenses = License.executeQuery("select l ${query}", qparams,[max: result.max, offset: result.offset])

        //We do the following to remove any licences the user does not have access rights
        qparams += public_flag

        def nonPublic = License.executeQuery("select l ${non_public_query}", qparams)
        def no_access = nonPublic.findAll{ !it.hasPerm("view",result.user)  }

        result.licenses = result.licenses - no_access
        result.numLicenses = result.numLicenses - no_access.size()

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def currentSubscriptions() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        def date_restriction = null;
        def sdf = new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)

        if (params.validOn) {
            result.validOn = params.validOn
        } else {
            result.validOn = sdf.format(new Date(System.currentTimeMillis()))
        }
        date_restriction = sdf.parse(result.validOn)

        def dateBeforeFilter = null;
        def dateBeforeFilterVal = null;
        if(params.dateBeforeFilter && params.dateBeforeVal){
            if(params.dateBeforeFilter == "Renewal Date"){
                dateBeforeFilter = " and s.manualRenewalDate < ?"
            }else if (params.dateBeforeFilter == "End Date"){
                dateBeforeFilter = " and s.endDate < ?"
            }
            dateBeforeFilterVal =sdf.parse(params.dateBeforeVal)
        }

        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to access ${result.institution.name} pages. Please request access on the profile page";
            response.sendError(401)
            return;
        }

        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) {
            result.editable = true
        } else {
            result.editable = false;
        }

        def public_flag = RefdataCategory.lookupOrCreate('YN', 'Yes');

        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

        // def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) OR ( s.isPublic=? ) ) AND ( s.status.value != 'Deleted' ) "
        def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
        // def qry_params = [result.institution, public_flag]
        def qry_params = [result.institution]

        if (params.q?.length() > 0) {
            base_qry += " and ( lower(s.name) like ? or exists ( select sp from SubscriptionPackage as sp where sp.subscription = s and ( lower(sp.pkg.name) like ? ) ) ) "
            qry_params.add("%${params.q.trim().toLowerCase()}%");
            qry_params.add("%${params.q.trim().toLowerCase()}%");
        }

        if (date_restriction) {
            base_qry += " and s.startDate <= ? and s.endDate >= ? "
            qry_params.add(date_restriction)
            qry_params.add(date_restriction)
        }

        if(dateBeforeFilter ){
            base_qry += dateBeforeFilter
            qry_params.add(dateBeforeFilterVal)
        }

        if ((params.sort != null) && (params.sort.length() > 0)) {
            base_qry += " order by ${params.sort} ${params.order}"
        } else {
            base_qry += " order by s.name asc"
        }


        log.debug("current subs base query: ${base_qry} params: ${qry_params} max:${result.max} offset:${result.offset}");

        result.num_sub_rows = Subscription.executeQuery("select count(s) " + base_qry, qry_params)[0]
        result.subscriptions = Subscription.executeQuery("select s ${base_qry}", qry_params, [max: result.max, offset: result.offset]);
        result.date_restriction = date_restriction;

        withFormat {
            html result
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def addSubscription() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        def date_restriction = null;
        def sdf = new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)

        if (params.validOn == null) {
            result.validOn = sdf.format(new Date(System.currentTimeMillis()))
            date_restriction = sdf.parse(result.validOn)
        } else if (params.validOn == '') {
            result.validOn = sdf.format(new Date(System.currentTimeMillis()))
        } else {
            result.validOn = params.validOn
            date_restriction = sdf.parse(params.validOn)
        }

        // if ( !checkUserHasRole(result.user, result.institution, 'INST_ADM') ) {
        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have admin permissions to access ${result.institution.name} pages. Please request access on the profile page";
            response.sendError(401)
            result.is_admin = false;
            // render(status: '401', text:"You do not have permission to add subscriptions to ${result.institution.name}. Please request editor access on the profile page");
            return;
        }

        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) {
            result.is_admin = true
        } else {
            result.is_admin = false;
        }

        def public_flag = RefdataCategory.lookupOrCreate('YN', 'Yes');

        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

        // def base_qry = " from Subscription as s where s.type.value = 'Subscription Offered' and s.isPublic=?"
        def qry_params = []
        def base_qry = " from Package as p where lower(p.name) like ?"

        if (params.q == null) {
            qry_params.add("%");
        } else {
            qry_params.add("%${params.q.trim().toLowerCase()}%");
        }

        if (date_restriction) {
            base_qry += " and p.startDate <= ? and p.endDate >= ? "
            qry_params.add(date_restriction)
            qry_params.add(date_restriction)
        }

        // Only list subscriptions where the user has view perms against the org
        // base_qry += "and ( ( exists select or from OrgRole where or.org =? and or.user = ? and or.perms.contains'view' ) "

        // Or the user is a member of an org who has a consortial membership that has view perms
        // base_qry += " or ( 2==2 ) )"

        if ((params.sort != null) && (params.sort.length() > 0)) {
            base_qry += " order by ${params.sort} ${params.order}"
        } else {
            base_qry += " order by p.name asc"
        }

        result.num_pkg_rows = Package.executeQuery("select count(p) " + base_qry, qry_params)[0]
        result.packages = Package.executeQuery("select p ${base_qry}", qry_params, [max: result.max, offset: result.offset]);

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def emptySubscription() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)
        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) {
            result.editable = true
        } else {
            result.editable = false;
        }

        if (result.editable) {
            def cal = new java.util.GregorianCalendar()
            def sdf = new SimpleDateFormat('yyyy-MM-dd')

            cal.setTimeInMillis(System.currentTimeMillis())
            cal.set(Calendar.MONTH, Calendar.JANUARY)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            result.defaultStartYear = sdf.format(cal.getTime())
            cal.set(Calendar.MONTH, Calendar.DECEMBER)
            cal.set(Calendar.DAY_OF_MONTH, 31)
            result.defaultEndYear = sdf.format(cal.getTime())
            result.defaultSubIdentifier = java.util.UUID.randomUUID().toString()
            result
        } else {
            redirect action: 'currentSubscriptions', params: [shortcode: params.shortcode]
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def processEmptySubscription() {
        log.debug(params)
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) {

            def sdf = new SimpleDateFormat('yyyy-MM-dd')
            def startDate = sdf.parse(params.valid_from)
            def endDate = sdf.parse(params.valid_to)

            def new_sub = new Subscription(type: RefdataValue.findByValue("Subscription Taken"),
                    status: RefdataCategory.lookupOrCreate('Subscription Status', 'Current'),
                    name: params.newEmptySubName,
                    startDate: startDate,
                    endDate: endDate,
                    identifier: params.newEmptySubId,
                    isPublic: RefdataCategory.lookupOrCreate('YN', 'No'),
                    impId: java.util.UUID.randomUUID().toString())
            if (new_sub.save()) {
                def new_sub_link = new OrgRole(org: result.institution,
                        sub: new_sub,
                        roleType: RefdataCategory.lookupOrCreate('Organisational Role', 'Subscriber')).save();

                redirect controller: 'subscriptionDetails', action: 'index', id: new_sub.id
            } else {
                new_sub.errors.each { e ->
                    log.debug("Problem creating new sub: ${e}");
                }
                flash.error = new_sub.errors
                redirect action: 'emptySubscription', params: [shortcode: params.shortcode]
            }
        } else {
            redirect action: 'currentSubscriptions', params: [shortcode: params.shortcode]
        }
    }

    def buildQuery(params) {
        log.debug("BuildQuery...");

        StringWriter sw = new StringWriter()

        if (params?.q?.length() > 0)
            sw.write(params.q)
        else
            sw.write("*:*")

        reversemap.each { mapping ->

            // log.debug("testing ${mapping.key}");

            if (params[mapping.key] != null) {
                if (params[mapping.key].class == java.util.ArrayList) {
                    params[mapping.key].each { p ->
                        sw.write(" AND ")
                        sw.write(mapping.value)
                        sw.write(":")
                        sw.write("\"${p}\"")
                    }
                } else {
                    // Only add the param if it's length is > 0 or we end up with really ugly URLs
                    // II : Changed to only do this if the value is NOT an *
                    if (params[mapping.key].length() > 0 && !(params[mapping.key].equalsIgnoreCase('*'))) {
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

        if (!checkUserHasRole(user, org, 'INST_ADM')) {
            flash.error = "You do not have edit permission to access ${org.name} pages. Please request access on the profile page";
            response.sendError(401)
            // render(status: '401', text:"You do not have permission to access ${org.name}. Please request access on the profile page");
            return;
        }

        def license_type = RefdataCategory.lookupOrCreate('License Type', 'Actual')
        def license_status = RefdataCategory.lookupOrCreate('License Status', 'Current')
        def licenseInstance = new License(type: license_type, status: license_status)
        if (!licenseInstance.save(flush: true)) {
        } else {
            log.debug("Save ok");
            def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee')
            log.debug("adding org link to new license");
            org.links.add(new OrgRole(lic: licenseInstance, org: org, roleType: licensee_role));
            if (org.save(flush: true)) {
            } else {
                log.error("Problem saving org links to license ${org.errors}");
            }
        }
        flash.message = message(code: 'license.created.message', args: [message(code: 'licence', default: 'Licence'), licenseInstance.id])
        redirect controller: 'licenseDetails', action: 'index', params: params, id: licenseInstance.id
    }

    def newLicense(params) {
        def user = User.get(springSecurityService.principal.id)
        def org = Org.findByShortcode(params.shortcode)

        if (!checkUserHasRole(user, org, 'INST_ADM')) {
            flash.error = "You do not have permission to access ${org.name} pages. Please request access on the profile page";
            response.sendError(401)
            // render(status: '401', text:"You do not have permission to access ${org.name}. Please request access on the profile page");
            return;
        }
       
        def baseLicense = params.baselicense ? License.get(params.baselicense) : null;

        if (!baseLicense?.hasPerm("view", user)) {
            log.debug("return 401....");
            flash.error = "You do not have permission to view the selected license. Please request access on the profile page";
            response.sendError(401)

        }else{
            def copyLicence = institutionsService.copyLicence(params)
            if (copyLicence.hasErrors() ) {
                log.error("Problem saving license ${copyLicence.errors}");
                render view: 'editLicense', model: [licenseInstance: copyLicence]                        
            }else{
                flash.message = message(code: 'license.created.message', args: [message(code: 'licence', default: 'Licence'), copyLicence.id])
                redirect controller: 'licenseDetails', action: 'index', params: params, id: copyLicence.id
            }
        }
    }

    def deleteLicense(params) {
        log.debug("deleteLicense ${params}");
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to view ${result.institution.name}. Please request access on the profile page";
            response.sendError(401)
            // render(status: '401', text:"You do not have permission to access ${result.institution.name}. Please request access on the profile page");
            return;
        }

        def license = License.get(params.baselicense)


        if (license?.hasPerm("edit", result.user)) {
            def current_subscription_status = RefdataCategory.lookupOrCreate('Subscription Status', 'Current');

            def subs_using_this_license = Subscription.findAllByOwnerAndStatus(license, current_subscription_status)

            if (subs_using_this_license.size() == 0) {
                def deletedStatus = RefdataCategory.lookupOrCreate('License Status', 'Deleted');
                license.status = deletedStatus
                license.save(flush: true);
            } else {
                flash.error = "Unable to delete - The selected license has attached subscriptions marked as Current"
                redirect(url: request.getHeader('referer'))
                return
            }
        } else {
            log.warn("Attempt by ${result.user} to delete license ${result.license} without perms")
            flash.message = message(code: 'license.delete.norights', default: 'You do not have edit permission for the selected license.')
            redirect(url: request.getHeader('referer'))
            return
        }

        redirect action: 'currentLicenses', params: [shortcode: params.shortcode]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def deleteDocuments() {
        def ctxlist = []

        log.debug("deleteDocuments ${params}");

        params.each { p ->
            if (p.key.startsWith('_deleteflag.')) {
                def docctx_to_delete = p.key.substring(12);
                log.debug("Looking up docctx ${docctx_to_delete}");
                def docctx = DocContext.get(docctx_to_delete)
                docctx.status = RefdataCategory.lookupOrCreate('Document Context Status', 'Deleted');
            }
        }

        redirect controller: 'licenseDetails', action: 'index', params: [shortcode: params.shortcode], id: params.licid, fragment: 'docstab'
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def processAddSubscription() {

        def user = User.get(springSecurityService.principal.id)
        def institution = Org.findByShortcode(params.shortcode)

        if (!checkUserIsMember(user, institution)) {
            flash.error = "You do not have permission to view ${institution.name}. Please request access on the profile page";
            response.sendError(401)
            // render(status: '401', text:"You do not have permission to access ${institution.name}. Please request access on the profile page");
            return;
        }

        log.debug("processAddSubscription ${params}");

        def basePackage = Package.get(params.packageId);

        if (basePackage) {
            // 
            def add_entitlements = (params.createSubAction == 'copy' ? true : false)

            def new_sub = basePackage.createSubscription("Subscription Taken",
                    "A New subscription....",
                    "A New subscription identifier.....",
                    basePackage.startDate,
                    basePackage.endDate,
                    basePackage.getConsortia(),
                    add_entitlements)

            def new_sub_link = new OrgRole(org: institution, sub: new_sub, roleType: RefdataCategory.lookupOrCreate('Organisational Role', 'Subscriber')).save();

            // This is done by basePackage.createSubscription
            // def new_sub_package = new SubscriptionPackage(subscription: new_sub, pkg: basePackage).save();

            flash.message = message(code: 'subscription.created.message', args: [message(code: 'subscription.label', default: 'Package'), basePackage.id])
            redirect controller: 'subscriptionDetails', action: 'index', params: params, id: new_sub.id
        } else {
            flash.message = message(code: 'subscription.unknown.message',default: "Subscription not found")
            redirect action: 'addSubscription', params: params
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def currentTitles() {
        // define if we're dealing with a HTML request or an Export (i.e. XML or HTML) 
        boolean isHtmlOutput = !params.format || params.format.equals("html")

        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)
        result.transforms = grailsApplication.config.titlelistTransforms

        // Set Date Restriction
        def date_restriction = null;

        def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
        if (params.validOn == null) {
            result.validOn = sdf.format(new Date(System.currentTimeMillis()))
            date_restriction = sdf.parse(result.validOn)
        } else if (params.validOn == '') {
            result.validOn = "" //sdf.format(new Date(System.currentTimeMillis()))
        } else {
            result.validOn = params.validOn
            date_restriction = sdf.parse(params.validOn)
        }

        // Set is_admin
        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) result.is_admin = true
        else result.is_admin = false;

        // Set default order and sort
        if (!params.order) params.order = "asc"
        if (!params.sort) params.sort = "tipp.title.title"

        // Set offset and max
        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
 
        def filterSub = params.list("filterSub")
        if (filterSub.contains("all")) filterSub = null
        def filterPvd = params.list("filterPvd")
        if (filterPvd.contains("all")) filterPvd = null
        def filterHostPlat = params.list("filterHostPlat")
        if (filterHostPlat.contains("all")) filterHostPlat = null
        def filterOtherPlat = params.list("filterOtherPlat")
        if (filterOtherPlat.contains("all")) filterOtherPlat = null

        def limits = (isHtmlOutput) ? [max: result.max, offset: result.offset] : [offset: 0]

        def qry_params = ['institution': result.institution]
        // def sub_qry =  "select ie from IssueEntitlement as ie JOIN ie.subscription.orgRelations as o LEFT OUTER JOIN ie.tipp.additionalPlatforms as ap LEFT OUTER JOIN ie.tipp.pkg.orgs AS role WHERE ie.tipp.title = t and o.roleType.value = 'Subscriber' AND o.org = :institution AND ie.subscription.status.value != 'Deleted'"
        def sub_qry = "from IssueEntitlement as ie INNER JOIN ie.subscription.orgRelations as o "
        if (filterOtherPlat) {
            sub_qry += "INNER JOIN ie.tipp.additionalPlatforms as ap "
        }
        if (filterPvd) {
            sub_qry += "INNER JOIN ie.tipp.pkg.orgs AS role "
        }
        sub_qry += "WHERE o.roleType.value = 'Subscriber' "
        sub_qry += "AND o.org = :institution "
        sub_qry += "AND ie.subscription.status.value != 'Deleted' "
        sub_qry += "AND ie.status != 'Deleted'"

        if (date_restriction) {
            sub_qry += " AND ie.subscription.startDate <= :date_restriction AND ie.subscription.endDate >= :date_restriction "
            qry_params.date_restriction = date_restriction
        }
        result.date_restriction = date_restriction;

        if ((params.filter) && (params.filter.length() > 0)) {
            log.debug("Adding title filter ${params.filter}");
            sub_qry += " AND LOWER(ie.tipp.title.title) like :titlestr"
            qry_params.titlestr = "%${params.filter}%".toLowerCase();

        }

        if (filterSub) {
            sub_qry += " AND ie.subscription.id in ( :subs )"
            qry_params.subs = filterSub.collect(new ArrayList<Long>()) { Long.valueOf(it) }
        }

        if (filterOtherPlat) {
            sub_qry += " AND ap.id in ( :addplats )"
            qry_params.addplats = filterOtherPlat.collect(new ArrayList<Long>()) { Long.valueOf(it) }
        }

        if (filterHostPlat) {
            sub_qry += " AND ie.tipp.platform.id in ( :plats )"
            qry_params.plats = filterHostPlat.collect(new ArrayList<Long>()) { Long.valueOf(it) }
        }

        if (filterPvd) {
            sub_qry += " AND role.roleType.value = 'Content Provider'  AND role.org.id IN (:provider) "
            qry_params.provider = filterPvd.collect(new ArrayList<Long>()) { Long.valueOf(it) }
        }

        // First get a neat list of the titles from all subscriptions in this institution
        // def title_qry = "from TitleInstance as t where exists ( ${sub_qry} )"
        def title_qry = sub_qry

        def having_clause = params.filterMultiIE ? 'having count(ie) > 1' : ''

        def order_by_clause = ''
        if (params.sort == 'tipp.title.title') {
            if (params.order == 'asc') {
                order_by_clause = 'order by ie.tipp.title.title asc'
            } else {
                order_by_clause = 'order by ie.tipp.title.title desc'
            }
        } else {
            order_by_clause = 'order by ie.tipp.title.title asc'
        }

        result.titles = IssueEntitlement.executeQuery("SELECT ie.tipp.title, count(ie) ${title_qry} group by ie.tipp.title ${having_clause} ${order_by_clause}", qry_params, limits)

        log.debug("Get count...");
        def cr = IssueEntitlement.executeQuery("SELECT count(t) from TitleInstance as t where exists ( select count(ie) as tc ${title_qry} AND ie.tipp.title = t group by ie.tipp.title ${having_clause})", qry_params)
        result.num_ti_rows = cr[0]

        // Put Lists for the filters into result
        if (isHtmlOutput) {
            result = setFiltersLists(result, date_restriction)
        } else {
            result.entitlements = IssueEntitlement.executeQuery("select ie ${title_qry} order by ie.tipp.title.title", qry_params)
        }

        def filename = "titles_listing_${result.institution.shortcode}"
        withFormat {
            html {
                result
            }
            csv {
                response.setHeader("Content-disposition", "attachment; filename=${filename}.csv")
                response.contentType = "text/csv"

                def out = response.outputStream
                exportService.StreamOutTitlesCSV(out, result.entitlements)
                out.close()
            }
            json {
                def map = [:]
                exportService.addTitlesToMap(map, result.entitlements)
                def content = map as JSON

                response.setHeader("Content-disposition", "attachment; filename=\"${filename}.json\"")
                response.contentType = "application/json"

                render content
            }
            xml {
                def doc = exportService.buildDocXML("TitleList")
                exportService.addTitleListXML(doc, doc.getDocumentElement(), result.entitlements)

                if ((params.transformId) && (result.transforms[params.transformId] != null)) {
                    String xml = exportService.streamOutXML(doc, new StringWriter()).getWriter().toString();
                    transformerService.triggerTransform(result.user, filename, result.transforms[params.transformId], xml, response)
                } else { // send the XML to the user
                    response.setHeader("Content-disposition", "attachment; filename=\"${filename}.xml\"")
                    response.contentType = "text/xml"
                    exportService.streamOutXML(doc, response.outputStream)
                }
            }
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def currentTitlesOld() {
        def verystarttime = exportService.printStart("currentTitles")

        // define if we're dealing with a HTML request or an Export (i.e. XML or HTML) 
        boolean isHtmlOutput = !params.format || params.format.equals("html")

        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)
        result.transforms = grailsApplication.config.subscriptionTransforms

        // Check if user is member of the institution
        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to access ${result.institution?.name} pages. Please request access on the profile page";
            response.sendError(401)
            return;
        }

        // Set Date Restriction
        def date_restriction = null;

        def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
        if (params.validOn == null) {
            result.validOn = sdf.format(new Date(System.currentTimeMillis()))
            date_restriction = sdf.parse(result.validOn)
        } else if (params.validOn == '') {
            result.validOn = "" //sdf.format(new Date(System.currentTimeMillis()))
        } else {
            result.validOn = params.validOn
            date_restriction = sdf.parse(params.validOn)
        }

        // Set is_admin
        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) result.is_admin = true
        else result.is_admin = false;

        // Set default order and sort
        if (!params.order) params.order = "asc"
        if (!params.sort) params.sort = "tipp.title.title"

        // Set offset and max
        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

        // Put Lists for the filters into result
        if (isHtmlOutput) {
            result = setFiltersLists(result, date_restriction)

            // !!!! Not sure it does actually ever happen? !!!!
            if (result.subscriptions.isEmpty()) {
                flash.error = "Sorry, we could not find any Subscription for ${result.institution.name}";
                result.titles = []
                result.entitlements = []
                return result;
            }
        }

        // Build query for titles
        def qry_build = buildCurrentTitlesQuery(result.institution, date_restriction)
        def title_query = qry_build.query
        def qry_params = qry_build.parameters

        def title_query_grouping = "GROUP By ie.tipp.title "
        def title_query_ordering = "ORDER BY ie.tipp.title.title ${params.order} " //COLLATE utf8_unicode_ci
        def title_query_extra = "COUNT(ie.subscription) "
        if (params.filterMultiIE) title_query_grouping += "HAVING COUNT(ie) >= 2 "

        log.debug("Final query:\n${title_query.replaceAll("\\s+", " ")}{title_query_grouping}\nParams:${qry_params}")

        // Get Total number of Titles for HTML view
        // The following 2 lines should be reviewed.
        if (isHtmlOutput)
            result.num_ti_rows = IssueEntitlement.executeQuery("SELECT ie.tipp.title ${title_query} ${title_query_grouping}", qry_params).size()

        // Other cases we just need the entitlements list.
        if (isHtmlOutput || params.filterMultiIE) {
            def limits = (isHtmlOutput) ? [max: result.max, offset: result.offset] : [offset: 0]

            // MAX(CASE WHEN ie.endDate IS NULL THEN '~' ELSE ie.endDate END) should get the max date or a null string if there is any empty ie.ie_end_date
            // We need to do that as an empty string actually means 'up to the most current issue available'
            result.titles = IssueEntitlement.executeQuery(
                    "SELECT ie.tipp.title, MIN(ie.startDate), \
MAX(CASE WHEN ie.endDate IS NULL THEN '~' ELSE DATE_FORMAT(ie.endDate, '%Y-%m-%d') END), \
${title_query_extra} \
${title_query} ${title_query_grouping} ${title_query_ordering}",
                    qry_params, limits);

            // If nothing is return send flash error message
            if (result.titles?.isEmpty()) {
                flash.error = "Sorry, we could not find any Titles.";
                result.entitlements = []
                return result;
            }
        }

        // Query the entitlements list
        def ie_query = "SELECT ie ${title_query} "
        if (isHtmlOutput || params.filterMultiIE) {
            // Get list of titles displayed on the page
            def title_list = []
            result.titles.each() { ti -> title_list.add(ti[0]) }
            // Add it to the query
            qry_params.titles = title_list
            ie_query += "AND ie.tipp.title In (:titles) "
        }
        ie_query += "${title_query_ordering}"

        log.debug("IE Query: ${ie_query}");
        result.entitlements = IssueEntitlement.executeQuery(ie_query, qry_params);

        exportService.printDuration(verystarttime, "Querying")

        def filename = "titles_listing_${result.institution.shortcode}"
        withFormat {
            html result
            csv {
                response.setHeader("Content-disposition", "attachment; filename=${filename}.csv")
                response.contentType = "text/csv"

                def out = response.outputStream
                def starttime = exportService.printStart("Stream Out")
                exportService.StreamOutTitlesCSV(out, result.entitlements)
                exportService.printDuration(starttime, "Stream Out")
                out.close()

                exportService.printDuration(verystarttime, "Overall Time")
            }
            json {
                def starttime = exportService.printStart("Building Map")
                def map = [:]
                exportService.addTitlesToMap(map, result.entitlements)
                exportService.printDuration(starttime, "Building Map")
                starttime = exportService.printStart("Create JSON")
                def content = map as JSON

                response.setHeader("Content-disposition", "attachment; filename=\"${filename}.json\"")
                response.contentType = "application/json"

                starttime = exportService.printStart("Sending JSON")
                render content
                exportService.printDuration(starttime, "Sending JSON")
                exportService.printDuration(verystarttime, "Overall Time")
            }
            xml {
                def starttime = exportService.printStart("Building XML Doc")
                def doc = exportService.buildDocXML("TitleList")
                exportService.addTitleListXML(doc, doc.getDocumentElement(), result.entitlements)
                exportService.printDuration(starttime, "Building XML Doc")

                if ((params.transformId) && (result.transforms[params.transformId] != null)) {
                    String xml = exportService.streamOutXML(doc, new StringWriter()).getWriter().toString();
                    transformerService.triggerTransform(result.user, filename, result.transforms[params.transformId], xml, response)
                } else { // send the XML to the user
                    response.setHeader("Content-disposition", "attachment; filename=\"${filename}.xml\"")
                    response.contentType = "text/xml"
                    starttime = exportService.printStart("Sending XML")
                    exportService.streamOutXML(doc, response.outputStream)
                    exportService.printDuration(starttime, "Sending XML")
                }
                exportService.printDuration(verystarttime, "Overall Time")
            }
        }
    }

    /**
     * Add to the given result Map the list of values available for each filters.
     *
     * @param result - result Map which will be sent to the view page
     * @param date_restriction - 'Subscription valid on' date restriction as a String
     * @return the result Map with the added filter lists
     */
    private setFiltersLists(result, date_restriction) {
        // Query the list of Subscriptions
        def sub_params = [institution: result.institution]
        def sub_qry = """
Subscription AS s INNER JOIN s.orgRelations AS o 
WHERE o.roleType.value = 'Subscriber' 
AND o.org = :institution 
AND s.status.value != 'Deleted' """
        if (date_restriction) {
            sub_qry += "\nAND s.startDate <= :date_restriction AND s.endDate >= :date_restriction "
            sub_params.date_restriction = date_restriction
        }
        result.subscriptions = Subscription.executeQuery("SELECT s FROM ${sub_qry} ORDER BY s.name", sub_params);

        // Query the list of Providers
        result.providers = Subscription.executeQuery("\
SELECT Distinct(role.org) FROM SubscriptionPackage sp INNER JOIN sp.pkg.orgs AS role \
WHERE EXISTS ( FROM ${sub_qry} AND sp.subscription = s ) \
AND role.roleType.value = 'Content Provider' \
ORDER BY role.org.name", sub_params);

        // Query the list of Host Platforms
        result.hostplatforms = IssueEntitlement.executeQuery("""
SELECT distinct(ie.tipp.platform) 
FROM IssueEntitlement AS ie, ${sub_qry} 
AND s = ie.subscription 
ORDER BY ie.tipp.platform.name""", sub_params);

        // Query the list of Other Platforms
        result.otherplatforms = IssueEntitlement.executeQuery("""
SELECT distinct(p.platform) 
FROM IssueEntitlement AS ie 
  INNER JOIN ie.tipp.additionalPlatforms as p, 
  ${sub_qry}  
AND s = ie.subscription 
ORDER BY p.platform.name""", sub_params);

        return result
    }

    /**
     * This function will gather the different filters from the request parameters and 
     * will build the base of the query to gather all the information needed for the view page
     * according to the requested filtering.
     *
     * @param institution - the {@link #com.k_int.kbplus.Org Org} object representing the institution we're looking at
     * @param date_restriction - 'Subscription valid on' date restriction as a String
     * @return a Map containing the base query as a String and a Map containing the parameters to run the query
     */
    private buildCurrentTitlesQuery(institution, date_restriction) {
        def qry_map = [:]

        // Put multi parameters for filtering into Lists
        // Set the variables to null if filter is equal to 'all'
        def filterSub = params.list("filterSub")
        if (filterSub.contains("all")) filterSub = null
        def filterPvd = params.list("filterPvd")
        if (filterPvd.contains("all")) filterPvd = null
        def filterHostPlat = params.list("filterHostPlat")
        if (filterHostPlat.contains("all")) filterHostPlat = null
        def filterOtherPlat = params.list("filterOtherPlat")
        if (filterOtherPlat.contains("all")) filterOtherPlat = null

        def qry_params = [:]

        StringBuilder title_query = new StringBuilder()
        title_query.append("FROM IssueEntitlement AS ie ")
        // Join with Org table if there are any Provider filters
        if (filterPvd) title_query.append("INNER JOIN ie.tipp.pkg.orgs AS role ")
        // Join with the Platform table if there are any Host Platform filters 
        if (filterHostPlat) title_query.append("INNER JOIN ie.tipp.platform AS hplat ")
        title_query.append(", Subscription AS s INNER JOIN s.orgRelations AS o ")

        // Main query part
        title_query.append("\
  WHERE o.roleType.value = 'Subscriber' \
  AND o.org = :institution \
  AND s.status.value != 'Deleted' \
  AND s = ie.subscription ")
        qry_params.institution = institution

        // Subscription filtering
        if (filterSub) {
            title_query.append("\
  AND ( \
  ie.subscription.id IN (:subscriptions) \
  OR ( EXISTS ( FROM IssueEntitlement AS ie2 \
  WHERE ie2.tipp.title = ie.tipp.title \
  AND ie2.subscription.id IN (:subscriptions) \
  )))")
            qry_params.subscriptions = filterSub.collect(new ArrayList<Long>()) { Long.valueOf(it) }
        }

        // Title name filtering
        // Copied from SubscriptionDetailsController
        if (params.filter) {
            title_query.append("\
  AND ( ( Lower(ie.tipp.title.title) like :filterTrim ) \
  OR ( EXISTS ( FROM IdentifierOccurrence io \
  WHERE io.ti.id = ie.tipp.title.id \
  AND io.identifier.value like :filter ) ) )")
            qry_params.filterTrim = "%${params.filter.trim().toLowerCase()}%"
            qry_params.filter = "%${params.filter}%"
        }

        // Provider filtering
        if (filterPvd) {
            title_query.append("\
  AND role.roleType.value = 'Content Provider' \
  AND role.org.id IN (:provider) ")
            qry_params.provider = filterPvd.collect(new ArrayList<Long>()) { Long.valueOf(it) }
            //Long.valueOf(params.filterPvd)
        }
        // Host Platform filtering
        if (filterHostPlat) {
            title_query.append("AND hplat.id IN (:hostPlatform) ")
            qry_params.hostPlatform = filterHostPlat.collect(new ArrayList<Long>()) { Long.valueOf(it) }
            //Long.valueOf(params.filterHostPlat)
        }
        // Host Other filtering
        if (filterOtherPlat) {
            title_query.append("""
AND EXISTS ( 
  FROM IssueEntitlement ie2 
  WHERE EXISTS ( 
    FROM ie2.tipp.additionalPlatforms AS ap 
    WHERE ap.platform.id IN (:otherPlatform) 
  ) 
  AND ie2.tipp.title = ie.tipp.title 
) """)
            qry_params.otherPlatform = filterOtherPlat.collect(new ArrayList<Long>()) { Long.valueOf(it) }
            //Long.valueOf(params.filterOtherPlat)
        }
        // 'Subscription valid on' filtering 
        if (date_restriction) {
            title_query.append(" AND ie.subscription.startDate <= :date_restriction AND ie.subscription.endDate >= :date_restriction ")
            qry_params.date_restriction = date_restriction
        }

        title_query.append("AND ( ie.status.value != 'Deleted' ) ")

        qry_map.query = title_query.toString()
        qry_map.parameters = qry_params
        return qry_map
    }

    def availableLicenses() {
        // def sub = resolveOID(params.elementid);
        // OrgRole.findAllByOrgAndRoleType(result.institution, licensee_role).collect { it.lic }


        def user = User.get(springSecurityService.principal.id)
        def institution = Org.findByShortcode(params.shortcode)

        if (!checkUserIsMember(user, institution)) {
            flash.error = "You do not have permission to view ${institution.name}. Please request access on the profile page";
            response.sendError(401)
            // render(status: '401', text:"You do not have permission to access ${institution.name}. Please request access on the profile page");
            return;
        }

        def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee');

        // Find all licenses for this institution...
        def result = [:]
        OrgRole.findAllByOrgAndRoleType(institution, licensee_role).each { it ->
            if (it.lic?.status?.value != 'Deleted') {
                result["License:${it.lic?.id}"] = it.lic?.reference
            }
        }

        //log.debug("returning ${result} as available licenses");
        render result as JSON
    }

    def resolveOID(oid_components) {
        def result = null;
        def domain_class = grailsApplication.getArtefact('Domain', "com.k_int.kbplus.${oid_components[0]}")
        if (domain_class) {
            result = domain_class.getClazz().get(oid_components[1])
        }
        result
    }

    def actionCurrentSubscriptions() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        def subscription = Subscription.get(params.basesubscription)

        if (subscription.hasPerm("edit", result.user)) {
            def derived_subs = Subscription.countByInstanceOf(subscription)

            if (derived_subs == 0) {
                def deletedStatus = RefdataCategory.lookupOrCreate('Subscription Status', 'Deleted');
                subscription.status = deletedStatus
                subscription.save(flush: true);
            } else {
                flash.error = "Unable to delete - The selected license has attached subscriptions"
            }
        } else {
            log.warn("${result.user} attempted to delete subscription ${result.subscription} without perms")
            flash.message = message(code: 'subscription.delete.norights')
        }

        redirect action: 'currentSubscriptions', params: [shortcode: params.shortcode]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def renewalsSearch() {

        log.debug("renewalsSearch : ${params}");
        log.debug("Start year filters: ${params.startYear}");


        // Be mindful that the behavior of this controller is strongly influenced by the schema setup in ES.
        // Specifically, see KBPlus/import/processing/processing/dbreset.sh for the mappings that control field type and analysers
        // Internal testing with http://localhost:9200/kbplus/_search?q=subtype:'Subscription%20Offered'
        def result = [:]

        result.institution = Org.findByShortcode(params.shortcode)

        result.user = springSecurityService.getCurrentUser()

        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to view ${result?.institution?.name?:'The selected institution.'}. Please request access on the profile page";
            response.sendError(401)
            // render(status: '401', text:"You do not have permission to access ${result.institution.name}. Please request access on the profile page");
            return;
        }

        def shopping_basket = UserFolder.findByUserAndShortcode(result.user, 'SOBasket') ?: new UserFolder(user: result.user, shortcode: 'SOBasket').save();

        if (params.addBtn) {
            log.debug("Add item ${params.addBtn} to basket");
            def oid = "com.k_int.kbplus.Package:${params.addBtn}"
            shopping_basket.addIfNotPresent(oid)
            shopping_basket.save(flush: true);
        } else if (params.clearBasket == 'yes') {
            log.debug("Clear basket....");
            shopping_basket.items?.clear();
            shopping_basket.save(flush: true)
        } else if (params.generate == 'yes') {
            log.debug("Generate");
            generate(materialiseFolder(shopping_basket.items), result.institution)
            return
        }

        result.basket = materialiseFolder(shopping_basket.items)


        //Following are the ES stuff 
        try {
            StringWriter sw = new StringWriter()
            def fq = null;
            boolean has_filter = false
            //This handles the facets.
            params.each { p ->
                if (p.key.startsWith('fct:') && p.value.equals("on")) {
                    log.debug("start year ${p.key} : -${p.value}-");

                    if (!has_filter)
                        has_filter = true;
                    else
                        sw.append(" AND ");

                    String[] filter_components = p.key.split(':');
                    switch (filter_components[1]) {
                        case 'consortiaName':
                            sw.append('consortiaName')
                            break;
                        case 'startYear':
                            sw.append('startYear')
                            break;
                        case 'endYear':
                            sw.append('endYear')
                            break;
                        case 'cpname':
                            sw.append('cpname')
                            break;
                    }
                    if (filter_components[2].indexOf(' ') > 0) {
                        sw.append(":\"");
                        sw.append(filter_components[2])
                        sw.append("\"");
                    } else {
                        sw.append(":");
                        sw.append(filter_components[2])
                    }
                }
            }

            if (has_filter) {
                fq = sw.toString();
                log.debug("Filter Query: ${fq}");
            }
            params.sort = "sortname"
            params.rectype = "Package" // Tells ESSearchService what to look for
            if(params.pkgname) params.q = params.pkgname
            if(fq){
                if(params.q) parms.q += " AND ";
                params.q += " (${fq}) ";
            } 
            result += ESSearchService.search(params)
        }
        catch (Exception e) {
            log.error("problem", e);
        }

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

                if (title_idx == -1) {
                    // log.debug("  -> Adding title ${title.id} to matrix result");
                    title_list.add("${title.id}");
                    title_idx = title_list.size();
                }

                if (pkg_idx == -1) {
                    log.debug("  -> Adding package ${t.pkg.id} to matrix result");
                    package_list.add("${t.pkg.id}:${t.platform.id}");
                    pkg_idx = package_list.size();
                }

                log.debug("  -> title_idx is ${title_idx} pkg_idx is ${pkg_idx}");

                def candidate = result.candidates["${t.pkg.id}:${t.platform.id}"]
                if (!candidate) {
                    candidate = [:]
                    result.candidates["${t.pkg.id}:${t.platform.id}"] = candidate;
                    candidate.pkg = t.pkg.id
                    candidate.platform = t.platform
                    candidate.titlematch = 0
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

        // sw.write("subtype:'Subscription Offered'")
        sw.write("rectype:'Package'")

        renewals_reversemap.each { mapping ->

            // log.debug("testing ${mapping.key}");

            if (params[mapping.key] != null) {
                if (params[mapping.key].class == java.util.ArrayList) {
                    params[mapping.key].each { p ->
                        sw.write(" AND ")
                        sw.write(mapping.value)
                        sw.write(":")
                        sw.write("\"${p}\"")
                    }
                } else {
                    // Only add the param if it's length is > 0 or we end up with really ugly URLs
                    // II : Changed to only do this if the value is NOT an *
                    if (params[mapping.key].length() > 0 && !(params[mapping.key].equalsIgnoreCase('*'))) {
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
            def item_to_add = genericOIDService.resolveOID(it.referencedOid)
            if (item_to_add) {
                result.add(item_to_add)
            } else {
                flash.message = "Folder contains item that cannot be found";
            }
        }
        result
    }

    def generate(plist, inst) {
        try {
            def m = generateMatrix(plist, inst)
            exportWorkbook(m, inst)
        }
        catch (Exception e) {
            log.error("Problem", e);
            response.sendError(500)
        }
    }

    def generateMatrix(plist, inst) {

        def titleMap = [:]
        def subscriptionMap = [:]

        log.debug("pre-pre-process");

        boolean first = true;

        def formatter = new java.text.SimpleDateFormat("yyyy/MM/dd")

        // Add in JR1 and JR1a reports
        def c = new GregorianCalendar()
        c.setTime(new Date());
        def current_year = c.get(Calendar.YEAR)

        // Step one - Assemble a list of all titles and packages.. We aren't assembling the matrix
        // of titles x packages yet.. Just gathering the data for the X and Y axis
        plist.each { sub ->


            log.debug("pre-pre-process (Sub ${sub.id})");

            def sub_info = [
                    sub_idx : subscriptionMap.size(),
                    sub_name: sub.name,
                    sub_id : "${sub.class.name}:${sub.id}"
            ]

            subscriptionMap[sub.id] = sub_info

            // For each subscription in the shopping basket
            if (sub instanceof Subscription) {
                log.debug("Handling subscription: ${sub_info.sub_name}")
                sub_info.putAll([sub_startDate : sub.startDate ? formatter.format(sub.startDate):null,
                sub_endDate: sub.endDate ? formatter.format(sub.endDate) : null])               
                sub.issueEntitlements.each { ie ->
                    // log.debug("IE");
                    if (!(ie.status?.value == 'Deleted')) {
                        def title_info = titleMap[ie.tipp.title.id]
                        if (!title_info) {
                            log.debug("Adding ie: ${ie}");
                            title_info = [:]
                            title_info.title_idx = titleMap.size()
                            title_info.id = ie.tipp.title.id;
                            title_info.issn = ie.tipp.title.getIdentifierValue('ISSN');
                            title_info.eissn = ie.tipp.title.getIdentifierValue('eISSN');
                            title_info.title = ie.tipp.title.title
                            if (first) {
                                if (ie.startDate)
                                    title_info.current_start_date = formatter.format(ie.startDate)
                                if (ie.endDate)
                                    title_info.current_end_date = formatter.format(ie.endDate)
                                title_info.current_embargo = ie.embargo
                                title_info.current_depth = ie.coverageDepth
                                title_info.current_coverage_note = ie.coverageNote
                                def test_coreStatus =ie.coreStatusOn(new Date())
                                def formatted_date = formatter.format(new Date())
                                title_info.core_status = test_coreStatus?"True(${formatted_date})": test_coreStatus==null?"False(Never)":"False(${formatted_date})"
                                title_info.core_status_on = formatted_date
                                title_info.core_medium = ie.coreStatus


                                try {
                                    log.debug("get jusp usage");
                                    title_info.jr1_last_4_years = factService.lastNYearsByType(title_info.id,
                                            inst.id,
                                            ie.tipp.pkg.contentProvider.id, 'JUSP:JR1', 4, current_year)

                                    title_info.jr1a_last_4_years = factService.lastNYearsByType(title_info.id,
                                            inst.id,
                                            ie.tipp.pkg.contentProvider.id, 'JUSP:JR1a', 4, current_year)
                                }
                                catch (Exception e) {
                                    log.error("Problem collating JUSP report info for title ${title_info.id}", e);
                                }

                                // log.debug("added title info: ${title_info}");
                            }
                            titleMap[ie.tipp.title.id] = title_info;
                        }
                    }
                }
            } else if (sub instanceof Package) {
                log.debug("Adding package into renewals worksheet");
                sub.tipps.each { tipp ->
                    log.debug("Package tipp");
                    if (!(tipp.status?.value == 'Deleted')) {
                        def title_info = titleMap[tipp.title.id]
                        if (!title_info) {
                            // log.debug("Adding ie: ${ie}");
                            title_info = [:]
                            title_info.title_idx = titleMap.size()
                            title_info.id = tipp.title.id;
                            title_info.issn = tipp.title.getIdentifierValue('ISSN');
                            title_info.eissn = tipp.title.getIdentifierValue('eISSN');
                            title_info.title = tipp.title.title
                            titleMap[tipp.title.id] = title_info;
                        }
                    }
                }
            }

            first = false
        }

        log.debug("Result will be a matrix of size ${titleMap.size()} by ${subscriptionMap.size()}");

        // Object[][] result = new Object[subscriptionMap.size()+1][titleMap.size()+1]
        Object[][] ti_info_arr = new Object[titleMap.size()][subscriptionMap.size()]
        Object[] sub_info_arr = new Object[subscriptionMap.size()]
        Object[] title_info_arr = new Object[titleMap.size()]

        // Run through the list of packages, and set the X axis headers accordingly
        subscriptionMap.values().each { v ->
            sub_info_arr[v.sub_idx] = v
        }

        // Run through the titles and set the Y axis headers accordingly
        titleMap.values().each { v ->
            title_info_arr[v.title_idx] = v
        }

        // Fill out the matrix by looking through each sub/package and adding the appropriate cell info
        plist.each { sub ->
            def sub_info = subscriptionMap[sub.id]
            if (sub instanceof Subscription) {
                log.debug("Filling out renewal sheet column for an ST");
                sub.issueEntitlements.each { ie ->
                    if (!(ie.status?.value == 'Deleted')) {
                        def title_info = titleMap[ie.tipp.title.id]
                        def ie_info = [:]
                        // log.debug("Adding tipp info ${ie.tipp.startDate} ${ie.tipp.derivedFrom}");
                        ie_info.tipp_id = ie.tipp.id;
                        def test_coreStatus =ie.coreStatusOn(new Date())
                        def formatted_date = formatter.format(new Date())
                        ie_info.core_status = test_coreStatus?"True(${formatted_date})": test_coreStatus==null?"False(Never)":"False(${formatted_date})"                     
                        ie_info.core_status_on = formatted_date
                        ie_info.core_medium = ie.coreStatus
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
            } else if (sub instanceof Package) {
                log.debug("Filling out renewal sheet column for a package");
                sub.tipps.each { tipp ->
                    if (!(tipp.status?.value == 'Deleted')) {
                        def title_info = titleMap[tipp.title.id]
                        def ie_info = [:]
                        // log.debug("Adding tipp info ${tipp.startDate} ${tipp.derivedFrom}");
                        ie_info.tipp_id = tipp.id;
                        ie_info.startDate_d = tipp.startDate
                        ie_info.startDate = ie_info.startDate_d ? formatter.format(ie_info.startDate_d) : null
                        ie_info.startVolume = tipp.startVolume
                        ie_info.startIssue = tipp.startIssue
                        ie_info.endDate_d = tipp.endDate
                        ie_info.endDate = ie_info.endDate_d ? formatter.format(ie_info.endDate_d) : null
                        ie_info.endVolume = tipp.endVolume ?: tipp.derivedFrom?.endVolume
                        ie_info.endIssue = tipp.endIssue ?: tipp.derivedFrom?.endIssue

                        ti_info_arr[title_info.title_idx][sub_info.sub_idx] = ie_info
                    }
                }
            }
        }

        log.debug("Completed.. returning final result");

        def final_result = [
                ti_info     : ti_info_arr,                      // A crosstab array of the packages where a title occours
                title_info  : title_info_arr,                // A list of the titles
                sub_info    : sub_info_arr,
                current_year: current_year]                  // The subscriptions offered (Packages)

        return final_result
    }

    def exportWorkbook(m, inst) {
        try {
            log.debug("export workbook");

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

            int rc = 0;
            // header
            int cc = 0;
            HSSFRow row = null;
            HSSFCell cell = null;

            log.debug(m.sub_info.toString())

            // Blank rows
            row = firstSheet.createRow(rc++);
            row = firstSheet.createRow(rc++);
            cc = 0;
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Subscriber ID"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Subscriber Name"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Subscriber Shortcode"));
           
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Subscription Start Date"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Subscription End Date"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Copy Subscription Documents"));

            row = firstSheet.createRow(rc++);
            cc = 0;
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("${inst.id}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString(inst.name));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString(inst.shortcode));
            
            def subscription = m.sub_info.find{it.sub_startDate}
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("${subscription?.sub_startDate?:''}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("${subscription?.sub_endDate?:''}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("${subscription?.sub_id?:m.sub_info[0].sub_id}"));

            row = firstSheet.createRow(rc++);

            // Key
            row = firstSheet.createRow(rc++);
            cc = 0;
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
            cell = row.createCell(21);
            cell.setCellValue(new HSSFRichTextString("Current Sub"));
            cell = row.createCell(22);
            cell.setCellValue(new HSSFRichTextString("Candidates ->"));


            row = firstSheet.createRow(rc++);
            cc = 21
            m.sub_info.each { sub ->
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${sub.sub_id}"));
            }

            // headings
            row = firstSheet.createRow(rc++);
            cc = 0;
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Title ID"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Title"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("ISSN"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("eISSN"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Current Start Date"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Current End Date"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Current Coverage Depth"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Current Coverage Note"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Core Status"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Core Status Checked"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("Core Medium"));

            // USAGE History
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1\n${m.current_year - 4}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1a\n${m.current_year - 4}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1\n${m.current_year - 3}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1a\n${m.current_year - 3}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1\n${m.current_year - 2}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1a\n${m.current_year - 2}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1\n${m.current_year - 1}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1a\n${m.current_year - 1}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1\n${m.current_year}"));
            cell = row.createCell(cc++);
            cell.setCellValue(new HSSFRichTextString("JR1a\n${m.current_year}"));

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
                cell.setCellValue(new HSSFRichTextString("${title.title ?: ''}"));

                // ISSN
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.issn ?: ''}"));

                // eISSN
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.eissn ?: ''}"));

                // startDate
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.current_start_date ?: ''}"));

                // endDate
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.current_end_date ?: ''}"));

                // coverageDepth
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.current_depth ?: ''}"));

                // embargo
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.current_coverage_note ?: ''}"));

                // IsCore
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.core_status ?: ''}"));

                // Core Start Date
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.core_status_on ?: ''}"));

                // Core End Date
                cell = row.createCell(cc++);
                cell.setCellValue(new HSSFRichTextString("${title.core_medium ?: ''}"));

                // Usage Stats
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1_last_4_years[4] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1a_last_4_years[4] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1_last_4_years[3] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1a_last_4_years[3] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1_last_4_years[2] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1a_last_4_years[2] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1_last_4_years[1] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1a_last_4_years[1] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1_last_4_years[0] ?: '0'));
                cell = row.createCell(cc++);
                if (title.jr1_last_4_years)
                    cell.setCellValue(new HSSFRichTextString(title.jr1a_last_4_years[0] ?: '0'));

                m.sub_info.each { sub ->
                    cell = row.createCell(cc++);
                    def ie_info = m.ti_info[title.title_idx][sub.sub_idx]
                    if (ie_info) {
                        if ((ie_info.core_status) && (ie_info.core_status.contains("True"))) {
                            cell.setCellValue(new HSSFRichTextString(""));
                            cell.setCellStyle(core_cell_style);
                        } else {
                            cell.setCellValue(new HSSFRichTextString(""));
                            cell.setCellStyle(present_cell_style);
                        }
                        addCellComment(row, cell, "${title.title} provided by ${sub.sub_name}\nStart Date:${ie_info.startDate ?: 'Not set'}\nStart Volume:${ie_info.startVolume ?: 'Not set'}\nStart Issue:${ie_info.startIssue ?: 'Not set'}\nEnd Date:${ie_info.endDate ?: 'Not set'}\nEnd Volume:${ie_info.endVolume ?: 'Not set'}\nEnd Issue:${ie_info.endIssue ?: 'Not set'}\nSelect Title by setting this cell to Y", drawing, factory);
                    }

                }
            }
            row = firstSheet.createRow(rc++);
            cell = row.createCell(0);
            cell.setCellValue(new HSSFRichTextString("END"));

            // firstSheet.autoSizeRow(6); //adjust width of row 6 (Headings for JUSP Stats)
            Row jusp_heads_row = firstSheet.getRow(6);
            jusp_heads_row.setHeight((short) (jusp_heads_row.getHeight() * 2));

            firstSheet.autoSizeColumn(0); //adjust width of the first column
            firstSheet.autoSizeColumn(1); //adjust width of the first column
            firstSheet.autoSizeColumn(2); //adjust width of the first column
            firstSheet.autoSizeColumn(3); //adjust width of the first column
            for (int i = 0; i < m.sub_info.size(); i++) {
                firstSheet.autoSizeColumn(7 + i); //adjust width of the second column
            }



            response.setHeader "Content-disposition", "attachment; filename=comparison.xls"
            // response.contentType = 'application/xls'
            response.contentType = 'application/vnd.ms-excel'
            workbook.write(response.outputStream)
            response.outputStream.flush()
        }
        catch (Exception e) {
            log.error("Problem", e);
            response.sendError(500)
        }
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def renewalsUpload() {
        def result = [:]

        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to view ${result.institution.name}. Please request access on the profile page";
            response.sendError(401)
            return;
        } else if (!result.institution.hasUserWithRole(result.user, "INST_ADM")) {
            flash.error = "Renewals Upload screen is not available to read-only users."
            response.sendError(401)
            return;
        }

        result.errors = []

        log.debug("upload");

        if (request.method == 'POST') {
            def upload_mime_type = request.getFile("renewalsWorksheet")?.contentType
            def upload_filename = request.getFile("renewalsWorksheet")?.getOriginalFilename()
            log.debug("Uploaded worksheet type: ${upload_mime_type} filename was ${upload_filename}");
            def input_stream = request.getFile("renewalsWorksheet")?.inputStream
            if (input_stream.available() != 0) {
                processRenewalUpload(input_stream, upload_filename, result)
            } else {
                flash.error = "You haven't selected a worksheet for upload."
            }
        }

        result
    }

    def processRenewalUpload(input_stream, upload_filename, result) {
        int SO_START_COL = 22
        int SO_START_ROW = 7
        log.debug("processRenewalUpload - opening upload input stream as HSSFWorkbook");
        def user = User.get(springSecurityService.principal.id)

        if (input_stream) {
            HSSFWorkbook wb;
            try {
                wb = new HSSFWorkbook(input_stream);
            } catch (IOException e) {
                if (e.getMessage().contains("Invalid header signature")) {
                    flash.error = "Error creating workbook. Possible causes: document format, corrupted file."
                } else {
                    flash.error = "Error creating workbook."
                }
                log.debug("Error creating workbook from input stream. ", e)
                return result;
            }
            HSSFSheet firstSheet = wb.getSheetAt(0);

            // Step 1 - Extract institution id, name and shortcode
            HSSFRow org_details_row = firstSheet.getRow(2)
            String org_name = org_details_row?.getCell(0)?.toString()
            String org_id = org_details_row?.getCell(1)?.toString()
            String org_shortcode = org_details_row?.getCell(2)?.toString()
            String sub_startDate = org_details_row?.getCell(3)?.toString()
            String sub_endDate = org_details_row?.getCell(4)?.toString()
            String original_sub_id = org_details_row?.getCell(5)?.toString()
            def original_sub = null
            if(original_sub_id){
                original_sub = genericOIDService.resolveOID(original_sub_id)
                if(!original_sub.hasPerm("view",user)){
                    original_sub = null;  
                    flash.error = "Can't access original subscription documents. Please verify you have the required access rights."
                } 
            }
            result.additionalInfo = [sub_startDate:sub_startDate,sub_endDate:sub_endDate,sub_name:original_sub?.name?:'',sub_id:original_sub?.id?:'']

            log.debug("Worksheet upload on behalf of ${org_name}, ${org_id}, ${org_shortcode}");

            def sub_info = []
            // Step 2 - Row 5 (6, but 0 based) contains package identifiers starting in column 4(5)
            HSSFRow package_ids_row = firstSheet.getRow(5)
            for (int i = SO_START_COL; ((i < package_ids_row.getLastCellNum()) && (package_ids_row.getCell(i))); i++) {
                log.debug("Got package identifier: ${package_ids_row.getCell(i).toString()}");
                def sub_id = package_ids_row.getCell(i).toString()
                def sub_rec = genericOIDService.resolveOID(sub_id) // Subscription.get(sub_id);
                if (sub_rec) {
                    sub_info.add(sub_rec);
                } else {
                    log.error("Unable to resolve the package identifier in row 6 column ${i + 5}, please check");
                    return
                }
            }

            result.entitlements = []

            boolean processing = true
            // Step three, process each title row, starting at row 11(10)
            for (int i = SO_START_ROW; ((i < firstSheet.getLastRowNum()) && (processing)); i++) {
                // log.debug("processing row ${i}");

                HSSFRow title_row = firstSheet.getRow(i)
                // Title ID
                def title_id = title_row.getCell(0).toString()
                if (title_id == 'END') {
                    // log.debug("Encountered END title");
                    processing = false;
                } else {
                    // log.debug("Upload Process title: ${title_id}, num subs=${sub_info.size()}, last cell=${title_row.getLastCellNum()}");
                    def title_id_long = Long.parseLong(title_id)
                    def title_rec = TitleInstance.get(title_id_long);
                    for (int j = 0; (((j + SO_START_COL) < title_row.getLastCellNum()) && (j <= sub_info.size())); j++) {
                        def resp_cell = title_row.getCell(j + SO_START_COL)
                        if (resp_cell) {
                            // log.debug("  -> Testing col[${j+SO_START_COL}] val=${resp_cell.toString()}");

                            def subscribe = resp_cell.toString()

                            // log.debug("Entry : sub:${subscribe}");

                            if (subscribe == 'Y' || subscribe == 'y') {
                                // log.debug("Add an issue entitlement from subscription[${j}] for title ${title_id_long}");

                                def entitlement_info = [:]
                                entitlement_info.base_entitlement = extractEntitlement(sub_info[j], title_id_long)
                                if (entitlement_info.base_entitlement) {
                                    entitlement_info.title_id = title_id_long
                                    entitlement_info.subscribe = subscribe

                                    entitlement_info.start_date = title_row.getCell(4)
                                    entitlement_info.end_date = title_row.getCell(5)
                                    entitlement_info.coverage = title_row.getCell(6)
                                    entitlement_info.coverage_note = title_row.getCell(7)
                                    entitlement_info.core_status = title_row.getCell(10) // Moved from 8


                                    // log.debug("Added entitlement_info ${entitlement_info}");
                                    result.entitlements.add(entitlement_info)
                                } else {
                                    log.error("TIPP not found in package.");
                                    flash.error = "You have selected an invalid title/package combination for title ${title_id_long}";
                                }
                            }
                        }
                    }
                }
            }
        } else {
            log.error("Input stream is null");
        }
        log.debug("Done");

        result
    }

    def extractEntitlement(pkg, title_id) {
        def result = pkg.tipps.find { e -> e.title?.id == title_id }
        if (result == null) {
            log.error("Failed to look up title ${title_id} in package ${pkg.name}");
        }
        result
    }

    def processRenewal() {
        log.debug("-> renewalsUpload params: ${params}");
        def result = [:]

        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to view ${result.institution.name}. Please request access on the profile page";
            response.sendError(401)
            // render(status: '401', text:"You do not have permission to access ${result.institution.name}. Please request access on the profile page");
            return;
        }

        log.debug("entitlements...[${params.ecount}]");

        int ent_count = Integer.parseInt(params.ecount);
     
        def sub_startDate = params.subscription?.copyStart ? parseDate(params.subscription?.start_date,possible_date_formats) : null
        def sub_endDate = params.subscription?.copyEnd ? parseDate(params.subscription?.end_date,possible_date_formats): null
        def copy_documents = params.subscription?.copy_docs && params.subscription.copyDocs

        def new_subscription = new Subscription(
                identifier: java.util.UUID.randomUUID().toString(),
                status: RefdataCategory.lookupOrCreate('Subscription Status', 'Current'),
                impId: java.util.UUID.randomUUID().toString(),
                name: "Unset: Generated by import",
                startDate: sub_startDate,
                endDate: sub_endDate,
                // instanceOf: db_sub,
                type: RefdataValue.findByValue('Subscription Taken'))
        log.debug("New Sub: ${new_subscription.startDate}  - ${new_subscription.endDate}")
        def packages_referenced = []
        Date earliest_start_date = null
        Date latest_end_date = null

        if (new_subscription.save()) {
            // assert an org-role
            def org_link = new OrgRole(org: result.institution,
                    sub: new_subscription,
                    roleType: RefdataCategory.lookupOrCreate('Organisational Role', 'Subscriber')).save();

            // Copy any links from SO
            // db_sub.orgRelations.each { or ->
            //   if ( or.roleType?.value != 'Subscriber' ) {
            //     def new_or = new OrgRole(org: or.org, sub: new_subscription, roleType: or.roleType).save();
            //   }
            // }
        } else {
            log.error("Problem saving new subscription, ${new_subscription.errors}");
        }

        new_subscription.save(flush: true);
        if(copy_documents){   
            String subOID =  params.subscription.copy_docs
            def sourceOID = "${new_subscription.getClass().getName()}:${subOID}"
            docstoreService.copyDocuments(sourceOID,"${new_subscription.getClass().getName()}:${new_subscription.id}")
        }

        if (!new_subscription.issueEntitlements) {
            new_subscription.issueEntitlements = new java.util.TreeSet()
        }

        for (int i = 0; i <= ent_count; i++) {
            def entitlement = params.entitlements."${i}";
            log.debug("process entitlement[${i}]: ${entitlement} - TIPP id is ${entitlement.tipp_id}");

            def dbtipp = TitleInstancePackagePlatform.get(entitlement.tipp_id)

            if (!packages_referenced.contains(dbtipp.pkg)) {
                packages_referenced.add(dbtipp.pkg)
                def new_package_link = new SubscriptionPackage(subscription: new_subscription, pkg: dbtipp.pkg).save();
                if ((earliest_start_date == null) || (dbtipp.pkg.startDate < earliest_start_date))
                    earliest_start_date = dbtipp.pkg.startDate
                if ((latest_end_date == null) || (dbtipp.pkg.endDate > latest_end_date))
                    latest_end_date = dbtipp.pkg.endDate
            }

            if (dbtipp) {
                def live_issue_entitlement = RefdataCategory.lookupOrCreate('Entitlement Issue Status', 'Live');
                def is_core = false

                def new_core_status = null;

                switch (entitlement.core_status?.toUpperCase()) {
                    case 'Y':
                    case 'YES':
                        new_core_status = RefdataCategory.lookupOrCreate('CoreStatus', 'Yes');
                        is_core = true;
                        break;
                    case 'P':
                    case 'PRINT':
                        new_core_status = RefdataCategory.lookupOrCreate('CoreStatus', 'Print');
                        is_core = true;
                        break;
                    case 'E':
                    case 'ELECTRONIC':
                        new_core_status = RefdataCategory.lookupOrCreate('CoreStatus', 'Electronic');
                        is_core = true;
                        break;
                    case 'P+E':
                    case 'E+P':
                    case 'PRINT+ELECTRONIC':
                    case 'ELECTRONIC+PRINT':
                        new_core_status = RefdataCategory.lookupOrCreate('CoreStatus', 'Print+Electronic');
                        is_core = true;
                        break;
                    default:
                        new_core_status = RefdataCategory.lookupOrCreate('CoreStatus', 'No');
                        break;
                }

                def new_start_date = entitlement.start_date ? parseDate(entitlement.start_date, possible_date_formats) : null
                def new_end_date = entitlement.end_date ? parseDate(entitlement.end_date, possible_date_formats) : null


                // entitlement.is_core
                def new_ie = new IssueEntitlement(subscription: new_subscription,
                        status: live_issue_entitlement,
                        tipp: dbtipp,
                        startDate: new_start_date ?: dbtipp.startDate,
                        startVolume: dbtipp.startVolume,
                        startIssue: dbtipp.startIssue,
                        endDate: new_end_date ?: dbtipp.endDate,
                        endVolume: dbtipp.endVolume,
                        endIssue: dbtipp.endIssue,
                        embargo: dbtipp.embargo,
                        coverageDepth: dbtipp.coverageDepth,
                        coverageNote: dbtipp.coverageNote,
                        coreStatus: new_core_status
                )

                if (new_ie.save()) {
                    log.debug("new ie saved");
                } else {
                    new_ie.errors.each { e ->
                        log.error("Problem saving new ie : ${e}");
                    }
                }
            } else {
                log.debug("Unable to locate tipp with id ${entitlement.tipp_id}");
            }
        }
        log.debug("done entitlements...");
 
        new_subscription.startDate = sub_startDate ?: earliest_start_date
        new_subscription.endDate = sub_endDate ?: latest_end_date
        new_subscription.save()

        if (new_subscription)
            redirect controller: 'subscriptionDetails', action: 'index', id: new_subscription.id
        else
            redirect action: 'renewalsUpload', params: params
    }

    def addCellComment(row, cell, comment_text, drawing, factory) {

        // When the comment box is visible, have it show in a 1x3 space
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex() + 7);
        anchor.setRow1(row.getRowNum());
        anchor.setRow2(row.getRowNum() + 9);

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
            (user == user && org == org && (status == 1 || status == 3))
        }

        if (uoq.count() > 0)
            result = true;

        result
    }

    def checkUserHasRole(user, org, role) {
        def uoq = UserOrg.createCriteria()

        def grants = uoq.list {
          and {
            eq('user', user)
            eq('org', org)
            formalRole {
                eq('authority', role)
            }
            or {
                eq('status', 1);
                eq('status', 3);
            }
          }
        }

        if (grants && grants.size() > 0)
            return true

        return false

    }

    def parseDate(datestr, possible_formats) {
        def parsed_date = null;
        if (datestr && (datestr.toString().trim().length() > 0)) {
            for (Iterator i = possible_formats.iterator(); (i.hasNext() && (parsed_date == null));) {
                try {
                    parsed_date = i.next().parse(datestr.toString());
                }
                catch (Exception e) {
                }
            }
        }
        parsed_date
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def instdash() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to access ${result.institution.name} pages. Please request access on the profile page";
            response.sendError(401)
            return;
        }

        if (checkUserHasRole(result.user, result.institution, 'INST_ADM')) {
            result.is_admin = true
        } else {
            result.is_admin = false;
        }

        def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")
        getTodoForInst(result)

        //.findAllByOwner(result.user,sort:'ts',order:'asc')


        def announcement_type = RefdataCategory.lookupOrCreate('Document Type', 'Announcement')
        result.recentAnnouncements = Doc.findAllByType(announcement_type, [max: 10, sort: 'dateCreated', order: 'desc'])

        result.forumActivity = zenDeskSyncService.getLatestForumActivity()

        result
    }

    def getTodoForInst(result){
        def lic_del = RefdataCategory.lookupOrCreate('License Status', 'Deleted');
        def sub_del = RefdataCategory.lookupOrCreate('Subscription Status', 'Deleted');
        def pkg_del = RefdataCategory.lookupOrCreate( 'Package Status', 'Deleted' );
        result.num_todos = PendingChange.executeQuery("select count(distinct pc.oid) from PendingChange as pc left outer join pc.license as lic left outer join lic.status as lic_status left outer join pc.subscription as sub left outer join sub.status as sub_status left outer join pc.pkg as pkg left outer join pkg.packageStatus as pkg_status where pc.owner = ? and ((lic_status is null or lic_status!=?) and (sub_status is null or sub_status!=?) and (pkg_status is null or pkg_status!=?))", [result.institution,lic_del,sub_del,pkg_del])[0]

        log.debug("Count3=${result.num_todos}");

        def change_summary = PendingChange.executeQuery("select distinct(pc.oid), count(pc), min(pc.ts), max(pc.ts) from PendingChange as pc left outer join pc.license as lic left outer join lic.status as lic_status left outer join pc.subscription as sub left outer join sub.status as sub_status left outer join pc.pkg as pkg left outer join pkg.packageStatus as pkg_status where pc.owner = ? and ((lic_status is null or lic_status!=?) and (sub_status is null or sub_status!=?) and (pkg_status is null or pkg_status!=?)) group by pc.oid", [result.institution,lic_del,sub_del,pkg_del], [max: result.max?:100, offset: result.offset?:0]);
        result.todos = []

        change_summary.each { cs ->
            log.debug("Change summary row : ${cs}");
            def item_with_changes = genericOIDService.resolveOID(cs[0])
            result.todos.add([
                    item_with_changes: item_with_changes,
                    oid              : cs[0],
                    num_changes      : cs[1],
                    earliest         : cs[2],
                    latest           : cs[3],
            ]);
        }
        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def todo() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
        getTodoForInst(result)

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def announcements() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;


        def announcement_type = RefdataCategory.lookupOrCreate('Document Type', 'Announcement')
        result.recentAnnouncements = Doc.findAllByType(announcement_type, [max: result.max, sort: 'dateCreated', order: 'desc'])
        result.num_announcements = result.recentAnnouncements.size()

        // result.num_sub_rows = Subscription.executeQuery("select count(s) "+base_qry, qry_params )[0]
        // result.subscriptions = Subscription.executeQuery("select s ${base_qry}", qry_params, [max:result.max, offset:result.offset]);


        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def changeLog() {
        def result = [:]

        def exporting = ( params.format == 'csv' ? true : false )

        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        result.institutional_objects = []

        if ( exporting ) {
          result.max = 1000000;
          result.offset = 0;
        }
        else {
          result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;
          result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
        }

        PendingChange.executeQuery('select distinct(pc.license) from PendingChange as pc where pc.owner = ?',[result.institution]).each {
          result.institutional_objects.add(['com.k_int.kbplus.License:'+it.id,"${message(code:'licence')}: "+it.reference]);
        }
        PendingChange.executeQuery('select distinct(pc.subscription) from PendingChange as pc where pc.owner = ?',[result.institution]).each {
          result.institutional_objects.add(['com.k_int.kbplus.Subscription:'+it.id,"${message(code:'subscription')}: "+it.name]);
        }

        if ( params.restrict == 'ALL' )
          params.restrict=null

        def base_query = " from PendingChange as pc where owner = ?";
        def qry_params = [result.institution]
        if ( ( params.restrict != null ) && ( params.restrict.trim().length() > 0 ) ) {
          def o =  genericOIDService.resolveOID(params.restrict)
          if ( o != null ) {
            if ( o instanceof License ) {
              base_query += ' and license = ?'
            }
            else {
              base_query += ' and subscription = ?'
            }
            qry_params.add(o)
          }
        }

        result.num_changes = PendingChange.executeQuery("select count(pc) "+base_query, qry_params)[0];


        withFormat {
            html {
            result.changes = PendingChange.executeQuery("select pc "+base_query+"  order by ts desc", qry_params, [max: result.max, offset:result.offset])
                result
            }
            csv {
                def dateFormat = new SimpleDateFormat("YYYY-MM-dd")
                def changes = PendingChange.executeQuery("select pc "+base_query+"  order by ts desc", qry_params)
                response.setHeader("Content-disposition", "attachment; filename=${result.institution.name}_changes.csv")
                response.contentType = "text/csv"

                def out = response.outputStream
                out.withWriter { w ->
                  w.write('Date,ChangeId,Actor, SubscriptionId,LicenceId,Description\n')
                  changes.each { c ->
                    def line = "\"${dateFormat.format(c.ts)}\",\"${c.id}\",\"${c.user?.displayName?:''}\",\"${c.subscription?.id ?:''}\",\"${c.license?.id?:''}\",\"${c.desc}\"\n".toString()
                    w.write(line)
                  }
                }
                out.close()
            }

        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def finance() {
        def result         = [:]
        result.user        = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to view ${result.institution.name}. Please request access on the profile page";
            response.sendError(401)
            return;
        }

        render view: 'financeActions', model: result, params: [shortcode: params.shortcode]
    }


}

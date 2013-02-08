package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader
import java.text.SimpleDateFormat



class UploadController {

  def springSecurityService
  def sessionFactory
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
  
  def possible_date_formats = [
    new SimpleDateFormat('dd/MM/yyyy'),
    new SimpleDateFormat('yyyy/MM/dd'),
    new SimpleDateFormat('dd/MM/yy'),
    new SimpleDateFormat('yyyy/MM'),
    new SimpleDateFormat('yyyy')
  ];

  @Secured(['ROLE_ADMIN', 'KBPLUS_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def so() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    
    if ( request.method == 'POST' ) {
      def upload_mime_type = request.getFile("soFile")?.contentType
      def upload_filename = request.getFile("soFile")?.getOriginalFilename()
      log.debug("Uploaded so type: ${upload_mime_type} filename was ${upload_filename}");
      if ( validateStream(request.getFile("soFile")?.inputStream, upload_filename ) ) {
        def input_stream = request.getFile("soFile")?.inputStream
        processUploadSO(input_stream, upload_filename, result)
      }
    }

    result
  }
  
  def validateStream(input_stream, upload_filename) {

    log.debug("Validating Stream");

    CSVReader r = new CSVReader( new InputStreamReader(input_stream) )

    String [] nl;

    String [] so_name_line = r.readNext()
    String [] so_identifier_line = r.readNext()
    String [] so_provider_line = r.readNext()
    String [] so_package_identifier_line = r.readNext()
    String [] so_package_name_line = r.readNext()
    String [] so_agreement_term_start_yr_line = r.readNext()
    String [] so_agreement_term_end_yr_line = r.readNext()
    String [] so_consortium_line = r.readNext()
    String [] so_num_prop_id_cols_line = r.readNext()
    int num_prop_id_cols = Integer.parseInt(so_num_prop_id_cols_line[1] ?: "0");
    String [] so_num_platforms_listed_line = r.readNext()
    int num_platforms_listed = Integer.parseInt(so_num_platforms_listed_line[1] ?: "0");
    String [] so_header_line = r.readNext()

    if ( num_platforms_listed == 0 ) {
      num_platforms_listed = 1
    }

    def normalised_identifier = so_identifier_line[1].trim().toLowerCase().replaceAll('-','_')
    def norm_pkg_identifier = "${so_provider_line[1].trim()}:${so_package_identifier_line[1].trim()}"
    norm_pkg_identifier = norm_pkg_identifier.toLowerCase().replaceAll('-','_');
    
    if ( ( normalised_identifier == null ) || ( normalised_identifier.trim().length() == 0 ) ) {
      log.error("No subscription offered identifier");
      flash.error="Problem processing ${upload_filename} : No usable subscription offered identifier";      
      return false
    }
    
    if ( ( norm_pkg_identifier == null ) || ( norm_pkg_identifier.length() == 0 ) ) {
      log.error("No usable package identifier");
      flash.error="Problem processing ${upload_filename} : No usable package identifier";      
      return false
    }
    
    def sub = Subscription.findByIdentifier(normalised_identifier)
    if ( sub != null ) {
      log.error("Sub ${normalised_identifier} already exists");
      flash.error="Problem processing ${upload_filename} : Unable to process file - Subscription with ID ${normalised_identifier} already exists in database";
      return false
    }

    def pkg = Package.findByIdentifier(norm_pkg_identifier);
    if ( pkg != null ) {
      log.error("Package ${norm_pkg_identifier} already exists");
      flash.error="Problem processing ${upload_filename} : Unable to process file - Subscription with ID ${normalised_identifier} already exists in database";
      return false
    }

    def issns_so_far = []
    def eissns_so_far = []
    

    while ((nl = r.readNext()) != null) {
      boolean has_data = false
      nl.each {
        if ( ( it != null ) && ( it.trim() != '' ) )
          has_data = true;
      }

      if ( !has_data )
        continue;
      else
        log.debug("has data");

      if ( present(nl[1]) && ( nl[1].trim().length() > 8 ) ) {
        def issn_to_add = nl[1].trim();
        if ( issns_so_far.contains(issn_to_add) ) {
          flash.error="Problem processing ${upload_filename} : The ISSN ${issn_to_add} appears to be repeated in the TIPP rows";
          return false
        }
        else {
          issns_so_far.add(issn_to_add)
        }
      }

      if ( present(nl[2]) && ( nl[2].trim().length() > 8 ) ) {
        def eissn_to_add = nl[2].trim();
        if ( eissns_so_far.contains(eissn_to_add) ) {
          flash.error="Problem processing ${upload_filename} : The rISSN ${eissn_to_add} appears to be repeated in the TIPP rows";
          return false
        }
        else {
          eissns_so_far.add(eissn_to_add)
        }
      }


      if ( present(nl[0] ) ) {

        def parsed_start_date = parseDate(nl[3],possible_date_formats)
        def parsed_end_date = parseDate(nl[6],possible_date_formats)

        def host_platform_url = null;
        for ( int i=0; i<num_platforms_listed; i++ ) {

          int position = 15+num_prop_id_cols+(i*3)   // Offset past any proprietary identifiers.. This needs a test case.. it's fraught with danger

          log.debug("Processing ${i}th platform entry. Arr len = ${nl.length} position=${position}");

          if ( ( nl.size() >= position+3 ) && ( nl[position] ) && ( nl[position].length() > 0 ) ) {
            def platform_role = nl[position+1]
            def platform_url = nl[position+2]
            println("Process platform name:${nl[position]} / type:${platform_role} / url:${platform_url}");

            if ( platform_role.trim() == 'host' ) {
              host_platform_url = platform_url
            }
            else {
            }
          }
        }
        if ( host_platform_url==null || host_platform_url.trim().length() == 0 ) {
          log.error("At least one tipp row has no host platform specified. Please correct and re-upload")
          flash.error="Problem processing ${upload_filename} : At least one tipp row has no host platform specified. Please correct and re-upload"
          return false
        }
      }
    }

    true
  }


  def processUploadSO(input_stream, upload_filename, result) {

    def prepared_so = [:]

    CSVReader r = new CSVReader( new InputStreamReader(input_stream) )
    
    String [] nl;

    String [] so_name_line = r.readNext()
    String [] so_identifier_line = r.readNext()
    String [] so_provider_line = r.readNext()
    String [] so_package_identifier_line = r.readNext()
    String [] so_package_name_line = r.readNext()
    String [] so_agreement_term_start_yr_line = r.readNext()
    String [] so_agreement_term_end_yr_line = r.readNext()
    String [] so_consortium_line = r.readNext()
    String [] so_num_prop_id_cols_line = r.readNext()
    int num_prop_id_cols = Integer.parseInt(so_num_prop_id_cols_line[1] ?: "0");
    String [] so_num_platforms_listed_line = r.readNext()
    int num_platforms_listed = Integer.parseInt(so_num_platforms_listed_line[1] ?: "0");
    String [] so_header_line = r.readNext()

    log.debug("Read column headings: ${so_header_line}");

    if ( num_platforms_listed == 0 ) {
      num_platforms_listed = 1
      println("**WARNING** num_platforms_listed = 0, defaulting to 1!");
    }
    
    def content_provider_org = Org.findByName(so_provider_line[1]) ?: new Org(name:so_provider_line[1],impId:java.util.UUID.randomUUID().toString()).save();
    
    def normalised_identifier = so_identifier_line[1].trim().toLowerCase().replaceAll('-','_')
    def norm_pkg_identifier = "${so_provider_line[1].trim()}:${so_package_identifier_line[1].trim()}"
    norm_pkg_identifier = norm_pkg_identifier.toLowerCase().replaceAll('-','_');

    log.debug("Normalised package identifier is ${norm_pkg_identifier}");
    
    log.debug("Processing subscription ${so_identifier_line[1]} normalised to ${normalised_identifier}");
    
    def pkg = Package.findByIdentifier(norm_pkg_identifier);
    
    prepared_so.provider = content_provider_org
    prepared_so.sub = [:]
    prepared_so.sub.identifier = normalised_identifier;
    prepared_so.sub.name = so_name_line[1];
    prepared_so.sub.start_date_str = so_agreement_term_start_yr_line[1]
    prepared_so.sub.end_date_str=so_agreement_term_end_yr_line[1]
    prepared_so.sub.start_date = parseDate(so_agreement_term_start_yr_line[1],possible_date_formats)
    prepared_so.sub.end_date = parseDate(so_agreement_term_end_yr_line[1],possible_date_formats)
    prepared_so.pkg_id = norm_pkg_identifier
    prepared_so.titles = []


    def pkg_type = RefdataCategory.lookupOrCreate('PackageTypes','Unknown');
    def cp_role = RefdataCategory.lookupOrCreate('Organisational Role','Content Provider');

    log.debug("Process consortium");

    def consortium = null;
    if ( ( so_consortium_line[1] != null ) && ( so_consortium_line[1].length() > 0 ) )  {
        prepared_so.cons = Org.findByName(so_consortium_line[1]) ?: new Org(name:so_consortium_line[1]).save();
    }


    log.debug("Create package");
    // We have validated the package and so information, and made sure all titles exist..
    // Add a package
    def new_pkg = new Package(identifier:prepared_so.pkg_id,
                              name:so_package_name_line[1],
                              type:null,
                              contentProvider:prepared_so.provider,
                              impId:java.util.UUID.randomUUID().toString());

    if ( new_pkg.save(flush:true) ) {
      //log.debug("New package ${pkg.identifier} saved");
      // Content Provider?
      log.debug("Package [${new_pkg.id}] with identifier ${new_pkg.identifier} created......");
      if ( prepared_so.provider ) {
        OrgRole.assertOrgPackageLink(prepared_so.provider, new_pkg, cp_role);
      }
    }
    else {
      log.error("Problem saving new package");
      new_pkg.errors.each { pe ->
        log.error("Problem saving package: ${pe}");
      }
      flash.error="Problem saving new package ${new_pkg.errors}";
      return
    }


    log.debug("processing titles");
    // Title info
    while ((nl = r.readNext()) != null) {
      boolean has_data = false
      nl.each {
        if ( ( it != null ) && ( it.trim() != '' ) )
          has_data = true;
      }

      if ( !has_data )
        continue;
      else
        log.debug("has data");

      if ( present(nl[0] ) ) {
        def title=[:]
        title.additional_platforms = []
        println "**Processing pub title:${nl[0]}, print identifier ${nl[1]} (${num_prop_id_cols} prop cols, ${num_platforms_listed} plat cols)"
        def title_identifiers = [];
        
        def publisher = null
        if ( present(nl[13]) ) {
          println("Publisher name: ${nl[13]}")
          publisher = Org.findByName(nl[13]) ?: new Org(name:nl[13]).save();
        }
        else {
          log.debug("No publisher...");
        }

        if ( present(nl[1]) && ( nl[1].trim().length() > 8 ) )
          title_identifiers.add([value:nl[1].trim(), namespace:'ISSN'])

        if ( present(nl[2]) && ( nl[2].trim().length() > 8 ) )
          title_identifiers.add([value:nl[2].trim(), namespace:'eISSN'])

        if ( present(nl[14]) )
          title_identifiers.add([value:nl[14].trim(), namespace:'DOI'])

        title.title_identifiers = title_identifiers;

        // Process identifiers in the row.
        for ( int i=0; i<num_platforms_listed; i++ ) {
          int position = 15+num_prop_id_cols+(i*3)   // Offset past any proprietary identifiers.. This needs a test case.. it's fraught with danger

          if ( ( nl.size() >= position+3 ) &&
               ( nl[position] ) &&
               ( nl[position].length() > 0 ) ) {

            def platform_role = nl[position+1]
            def platform_url = nl[position+2]

            def parsed_start_date = parseDate(nl[3],possible_date_formats)
            def parsed_end_date = parseDate(nl[6],possible_date_formats)

            def platform = Platform.lookupOrCreatePlatform(name:nl[position],
                                                           type:nl[position+1],
                                                           primaryUrl:platform_url)

            if ( !platform ) {
              flash.error = "Problem processing ${upload_filename} : unable to identify a platform for entry with title ${nl[0]}"
              return
            }

            title.startDateString = nl[3]
            title.startDate = parsed_start_date
            title.startVolume = nl[4]
            title.startIssue = nl[5]
            title.endDateString = nl[6]
            title.endDate = parsed_end_date
            title.endVolume = nl[7]
            title.endIssue = nl[8]
            title.title_id = nl[9]
            title.embargo = nl[10]
            title.coverageDepth = nl[11]
            title.coverageNote = nl[12]

            println("Process platform name:${nl[position]} / type:${platform_role} / url:${platform_url}");

            if ( platform_role.trim() == 'host' ) {
              title.platform = platform;
              title.host_platform_url = platform_url
            }
            else {
              title.additional_platforms.add([plat:platform, role:platform_role, url:platform_url])
            }
          }
        }

        if ( title_identifiers.size() == 0 ) {
          log.error("Upload contains a title with no identifier");
          flash.error="Problem processing ${upload_filename} : Title ${nl[0]} has no usable identifiers. File not imported. Please fix and re-upload";
          return;
        }

        // Lookup or create title instance        
        title.title = lookupOrCreateTitleInstance(title_identifiers,nl[0],publisher);
        title.pkg = new_pkg
        prepared_so.titles.add(title)
      }
    }

    log.debug("Adding titles");
    // Add titles to the new package
    prepared_so.titles.each { t ->
      if ( t.title && t.pkg && t.platform ) {

        log.debug("Processing new so, looking for tipp. title:${t.title.id}, pkg:${t.pkg.id}, plat:${t.platform.id}");
      
        def dbtipp = TitleInstancePackagePlatform.findByPkgAndPlatformAndTitle(t.pkg,t.platform,t.title)
        if ( dbtipp == null ) {
          dbtipp = new TitleInstancePackagePlatform(pkg:t.pkg,
                                                    platform:t.platform,
                                                    title:t.title,
                                                    startDate:t.startDate,
                                                    startVolume:t.startVolume,
                                                    startIssue:t.startIssue,
                                                    endDate:t.endDate,
                                                    endVolume:t.endVolume,
                                                    endIssue:t.endIssue,
                                                    embargo:t.embargo,
                                                    coverageDepth:t.coverageDepth,
                                                    coverageNote:t.coverageNote,
                                                    hostPlatformURL:t.host_platform_url,
                                                    impId:java.util.UUID.randomUUID().toString(),
                                                    ids:[])
  
          if ( ! dbtipp.save() ) {
            log.error("ERROR Saving tipp");
            dbtipp.errors.each { err ->
              log.error("  -> ${err}");
            }
          }
          else {
            log.debug("new TIPP Save OK ${dbtipp.id}");
          }
        }
        else {
          log.error("TIPP already exists!! This should never be the case as we are creating a new package!!!");
        }
      }
      else { 
        log.error("One of title(${t.title}), package(${t.pkg}) or platform(${t.platform}) are missing");
      }
    }

    // Create an SO by creating a header and copying the tipps from this package into IE's
    log.debug("Copying Package TIPPs into issue entitlements");
    def new_pkg_id = new_pkg.id;
    new_pkg.discard();
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()

    result.user = User.get(springSecurityService.principal.id)

    def reloaded_pkg = Package.get(new_pkg_id);
    reloaded_pkg.updateNominalPlatform();
    reloaded_pkg.save(flush:true);

    def new_sub = reloaded_pkg.createSubscription('Subscription Offered', 
                                             prepared_so.sub.name, 
                                             prepared_so.sub.identifier, 
                                             prepared_so.sub.start_date, 
                                             prepared_so.sub.end_date, 
                                             prepared_so.cons) 
    
    log.debug("Completed New package is ${new_pkg.id}");

    result.new_pkg_id = new_pkg_id
    result.new_sub_id = new_sub?.id
  }
  
  def lookupOrCreateTitleInstance(identifiers,title,publisher) {
    log.debug("lookupOrCreateTitleInstance ${identifiers}, ${title}, ${publisher}");
    def result = TitleInstance.lookupOrCreate(identifiers, title);
    if ( !result.getPublisher() ) {
      def pub_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Publisher');
      OrgRole.assertOrgTitleLink(publisher, result, pub_role);
      result.save();
    }
    log.debug("Done: ${result}");
    result;
  }
  
  def parseDate(datestr, possible_formats) {
    def parsed_date = null;
    for(Iterator i = possible_formats.iterator(); ( i.hasNext() && ( parsed_date == null ) ); ) {
      try {
        parsed_date = i.next().parse(datestr);
      }
      catch ( Exception e ) {
      }
    }
    parsed_date
  }
  
  def present(v) {
    if ( ( v != null ) &&
         ( v.trim().length() > 0 ) &&
         ( ! ( v.trim().equalsIgnoreCase('n/a') ) ) &&
         ( ! ( v.trim().equalsIgnoreCase('-') ) ) )
      return true

    return false
  }

}

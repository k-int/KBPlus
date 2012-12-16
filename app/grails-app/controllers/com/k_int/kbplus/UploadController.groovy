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
  
  def possible_date_formats = [
    new SimpleDateFormat('dd/MM/yyyy'),
    new SimpleDateFormat('yyyy/MM/dd'),
    new SimpleDateFormat('dd/MM/yy'),
    new SimpleDateFormat('yyyy/MM'),
    new SimpleDateFormat('yyyy')
  ];

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def so() { 
    log.debug("so");    
    
    if ( request.method == 'POST' ) {
      def upload_mime_type = request.getFile("soFile")?.contentType
      log.debug("Uploaded so type: ${upload_mime_type}");
      def input_stream = request.getFile("soFile")?.inputStream
      processUploadSO(input_stream)
    }
  }
  
  def processUploadSO(input_stream) {

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
    
    def content_provider_org = Org.findByName(so_provider_line[1]) ?: new Org(name:so_provider_line[1]).save();
    
    def normalised_identifier = so_identifier_line[1].trim().toLowerCase().replaceAll('-','_')
    def norm_pkg_identifier = "${so_provider_line[1].trim()}:${so_package_identifier_line[1].trim()}"
    norm_pkg_identifier = norm_pkg_identifier.toLowerCase().replaceAll('-','_');

    log.debug("Normalised package identifier is ${norm_pkg_identifier}");
    
    log.debug("Processing subscription ${so_identifier_line[1]} normalised to ${normalised_identifier}");
    
    if ( ( normalised_identifier == null ) || ( normalised_identifier.trim().length() == 0 ) ) {
      log.error("No subscription offered identifier");
      flash.error="No usable subscription offered identifier";      
      return
    }
    
    if ( ( norm_pkg_identifier == null ) || ( norm_pkg_identifier.length() == 0 ) ) {
      log.error("No usable package identifier");
      flash.error="No usable package identifier";      
      return
    }
    
    def sub = Subscription.findByIdentifier(normalised_identifier)
    if ( sub != null ) {
      log.error("Sub ${normalised_identifier} already exists");
      flash.error="Unable to process file - Subscription with ID ${normalised_identifier} already exists in database";
      return
    }

    def pkg = Package.findByIdentifier(norm_pkg_identifier);
    if ( pkg != null ) {
      log.error("Package ${norm_pkg_identifier} already exists");
      flash.error="Unable to process file - Subscription with ID ${normalised_identifier} already exists in database";
      return
    }
    
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

    log.debug("Package created, save...");
    if ( new_pkg.save(flush:true) ) {
      //log.debug("New package ${pkg.identifier} saved");
      // Content Provider?
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

          if ( ( nl.size() >= position+2 ) &&
               ( nl[position] ) &&
               ( nl[position].length() > 0 ) ) {

            def platform_role = nl[position+1]
            def platform_url = nl[position+2]

            def platform = Platform.lookupOrCreatePlatform(name:nl[position],
                                                           type:nl[position+1],
                                                           primaryUrl:platform_url)


            println("Process platform ${nl[position]} / ${platform_role} / ${platform_url}");

            if ( platform_role.trim() == 'host' ) {
              title.platform = platform;
              title.host_platform_url = platform_url
            }
            else {
              title.additional_platforms.add([plat:platform, role:platform_role, url:platform_url])
            }
          }
        }

        // Lookup or create title instance        
        title.title = lookupOrCreateTitleInstance(title_identifiers,nl[0],publisher);
        title.pkg = new_pkg
        title.platform = null
        prepared_so.titles.add(title)
      }
    }

    log.debug("Adding titles");
    // Add titles to the new package
    prepared_so.titles.each { t ->
      if ( t.title && t.pkg && t.platform ) {
      
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
                                                    hostPlatformURL:t.hostPlatformURL,
                                                    impId:java.util.UUID.randomUUID().toString(),
                                                    ids:[])
  
          if ( ! dbtipp.save() ) {
            log.error("ERROR Saving tipp");
            dbtipp.errors.each { err ->
              log.error("  -> ${err}");
            }
          }
          else {
            log.debug("TIPP Save OK ${tipp._id}");
          }
        }
      }
    }

    // Create an SO    
    log.debug("Completed");
  }
  
  def lookupOrCreateTitleInstance(identifiers,title,publisher) {
    log.debug("lookupOrCreateTitleInstance ${identifiers}, ${title}, ${publisher}");
    def result = TitleInstance.lookupOrCreate(identifiers, title);
    if ( !result.getPublisher() ) {
      def pub_role = lookupOrCreateRefdataEntry('Organisational Role', 'Publisher');
      OrgRole.assertOrgTitleLink(publisher, result, pub_role);
      result.save();
    }
    log.debug("Done: ${result}");
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

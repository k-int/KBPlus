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
  
  def csv_column_config = [
    'ID':[coltype:'map'],
    'publication_title':[coltype:'simple'],
    'date_first_issue_online':[coltype:'simple'],
    'num_first_vol_online':[coltype:'simple'],
    'num_first_issue_online':[coltype:'simple'],
    'date_last_issue_online':[coltype:'simple'],
    'num_last_vol_online':[coltype:'simple'],
    'num_last_issue_online':[coltype:'simple'],
    'title_id':[coltype:'simple'],
    'embargo_info':[coltype:'simple'],
    'coverage_depth':[coltype:'simple'],
    'coverage_notes':[coltype:'simple'],
    'publisher_name':[coltype:'simple'],
    'platform':[coltype:'map']
  ];

  def possible_date_formats = [
    new SimpleDateFormat('dd/MM/yyyy'),
    new SimpleDateFormat('yyyy/MM/dd'),
    new SimpleDateFormat('dd/MM/yy'),
    new SimpleDateFormat('yyyy/MM'),
    new SimpleDateFormat('yyyy')
  ];

  @Secured(['ROLE_ADMIN', 'KBPLUS_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def reviewSO() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    
    if ( request.method == 'POST' ) {
      def upload_mime_type = request.getFile("soFile")?.contentType
      def upload_filename = request.getFile("soFile")?.getOriginalFilename()
      log.debug("Uploaded so type: ${upload_mime_type} filename was ${upload_filename}");
      result.validationResult = readSubscriptionOfferedCSV(request.getFile("soFile")?.inputStream, upload_filename )
	    result.validationResult.processFile=true
	    // validate(result.validationResult)
	    render result as JSON
    }
    else {
      return result
    }
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

  def readSubscriptionOfferedCSV(input_stream, upload_filename) {

    def result = [:]
  	result.processFile=true
  	
    // File level messages
	  result.messages=[]

    log.debug("Reading Stream");

    CSVReader r = new CSVReader( new InputStreamReader(input_stream) )

    String [] nl;

    processCsvLine(r.readNext(),'soName',1,result,'str',null,true)
    processCsvLine(r.readNext(),'soIdentifier',1,result,'str',null,false)
    processCsvLine(r.readNext(),'soProvider',1,result,'str',null,false)
    processCsvLine(r.readNext(),'soPackageIdentifier',1,result,'str',null,true)
    processCsvLine(r.readNext(),'soPackageName',1,result,'str',null,true)
    processCsvLine(r.readNext(),'aggreementTermStartYear',1,result,'date',null,true)
    processCsvLine(r.readNext(),'aggreementTermEndYear',1,result,'date',null,true)
    processCsvLine(r.readNext(),'consortium',1,result,'str',null,false)
    
    result['soName'].messages=['This is an soName message','And so is this'];
    
    result.soHeaderLine = r.readNext()

    result.tipps = []
    while ((nl = r.readNext()) != null) {
      // result.tipps.add(tipp_row)
      result.tipps.add(readTippRow(result.soHeaderLine, nl))
    }

    return result;
  }
  
  def readTippRow(cols, nl) {
    def result = [:]
    for ( int i=0; i<nl.length; i++ ) {
      log.debug("Process column ${cols[i]} value is ${nl[i]}");
      def column_components = cols[i].split('\\.')
      log.debug("result of split is ${column_components}");
      def column_name = column_components[0]
      def column_defn = csv_column_config[column_name]
      if ( column_defn ) {
        log.debug("Got col def: ${column_defn}");
        switch ( column_defn.coltype ) {
          case 'simple': 
            result[column_name] = nl[i]
            break;
          case 'map':
            log.debug("Processing map, col components are ${column_components}");
            if ( result[column_name] == null )
              result[column_name] = [:]
            
            // If this is a simple map, like id.issn or id.eissn just set the value
            if ( column_components.length == 2 ) {
              log.debug("Assigning simple map value ${column_components[1]} = ${nl[i]}");
              result[column_name][column_components[1]] = nl[i];    
            }
            else {
              log.debug("Processing structured map entry");
              // We have an object like platform.host:1.name, platform.host:2.name
              if ( result[column_name][column_components[1]] == null ) {
                // We're setting up the object. Add new object to platform map, keyed as second part of col name
                result[column_name][column_components[1]]=[:]
                def column_types = column_components[1].split(':');
                // Add a coltype to the values, so platform.host.name gets a coltype of "host"
                result[column_name][column_components[1]].coltype=column_types[0]
              }
              result[column_name][column_components[1]][column_components[2]] = nl[i]
            }
            break;
        } 
      }
      else {
        // Unknown column
      }
    }
    return result;
  }
  
  def processCsvLine(csv_line,field_name,col_num,result_map,parseAs,defval,isMandatory) {  
    log.debug("  processCsvLine ${csv_line} ${field_name} ${col_num}... mandatory=${isMandatory}");
	  def result = [:]
  	result.messages=[]
	  result.origValue = csv_line[col_num]

    if ( ( col_num <= csv_line.length ) && ( csv_line[col_num] != null ) ) {      
      switch(parseAs) {
        case 'int':
          result.value = Integer.parseInt(result.origValue?:defval)
          break;
        case 'date':
		      result.value = parseDate(result.origValue,possible_date_formats)
  		    break;
        case 'str':
        default:
          result.value = result.origValue
          break;
      }
      result_map[field_name] = result
    }
	
	  
    if ( ( result.value == null ) || ( result.value.toString().trim() == '' ) ) {
	    if ( isMandatory ) {
	      result_map.processFile=false
		    result_map[field_name] = [messages:["Missing mandatory property: ${field_name}"]]
	    }
      else {
	      result_map[field_name] = [messages:["Missing property: ${field_name}"]]	  
	    }
    }
    else {
    }
    
 	  log.debug("result = ${result}");
  }
  
  def validate(upload) {
	  
	  def result = generateAndValidateSubOfferedIdentifier(upload) &&
	               generateAndValidatePackageIdentifier(upload) &&
				         validateNumPlatforms(upload) &&
      				   validateConsortia(upload) &&
			      	   validateColumnHeadings(upload)
			      	   
		if ( upload.processFile ) {
		  validateTipps(upload)
		}
		else {
		}
  }
  
  def validateTipps(upload) {
  
    def id_list = []
    
    int counter = 0;
    upload.tipps.each { tipp ->
      if ( ( tipp.publication_title.origValue == null ) || ( tipp.publication_title.origValue.trim() == '' ) ) {
        tipp.messages.add("Title (row ${counter}) must not be empty");
        upload.processFile=false;
      }
      
      //if ( ! atLeastOneOf(tipp,['print_identifier', 'online_identifier', 'DOI', 'Proprietary_ID.isbn']) ) {
      //  tipp.messages.add("Title (row ${counter}) must reference at least one identifier");
      //  upload.processFile=false;
      //}
      
      if ( ! validISSN(tipp.print_identifier?.origValue) ) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid ISSN");
        upload.processFile=false;
      }
      
      if ( ! validISSN(tipp.online_identifier?.origValue) ) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid eISSN");
        upload.processFile=false;
      }

      if ( ! validISSN(tipp.online_identifier?.origValue) ) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid eISSN");
        upload.processFile=false;
      }

      if ( ! validISBN(tipp."Proprietary_ID.isbn"?.origValue) ) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid ISBN");
        upload.processFile=false;
      }
      
      ["print_identifier", "online_identifier", "DOI", "Proprietary_ID.isbn"].each { idtype ->
        if ( ( tipp[idtype] ) && ( tipp[idtype].origValue.trim() != '' ) ) {
          if ( id_list.contains(tipp[idtype].origValue) ) {
            tipp.messages.add("Title (row ${counter}) contains a repeated ${idtype} - ${tipp[idtype].origValue}");
            upload.processFile=false;
          }
          else {
            id_list.add(tipp[idtype].origValue)
          }
        }
      }

      if ( ( tipp_row.host_platform_url.origValue == null ) || ( tipp_row.host_platform_url.origValue.trim() == '' ) ) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid host platform");
        upload.processFile=false;
      }
      
      tipp.platforms?.each { plat ->
        if ( ( plat.role.toLowerCase().trim() != 'host' ) &&
             ( plat.role.toLowerCase().trim() != 'administrative' ) ) {
          tipp.messages.add("Title (row ${counter}) Containts a non-host or admin platform");
          upload.processFile=false;          
        }
      }
      
      if ( tipp.parsed_start_date == null ) {
        tipp.messages.add("Title (row ${counter}) Invalid start date");
        upload.processFile=false;                  
      }
      
      if ( tipp.parsed_end_date == null ) {
        tipp.messages.add("Title (row ${counter}) Invalid end date");
        upload.processFile=false;                  
      }
      
      if ( ( tipp.coverage_depth?.origValue != null ) &&
           ( tipp.coverage_depth.origValue != '' ) &&
           ( ! ['fulltext','selected articles','abstracts'].contains(tipp.coverage_depth.origValue.toLowerCase()) ) ) {
        tipp.messages.add("coverage depth must be one of fulltext, selected articles or abstracts");
        upload.processFile=false;                             
      }

      counter++
    }
    
    return true;
  }
  
  def validISSN(issn_string) {
    def result = true;
    if ( ( issn_string ) && ( issn_string.trim() != '' ) ) {
      // Check issn_string matches regexp "[0-9]{4}-[0-9]{3}[0-9X]"
      if ( issn_string ==~ '[0-9]{4}-[0-9]{3}[0-9X]' ) {
        // Matches, all is good.
      }
      else {
        result = false
      }
    }
    return result;
  }

  def validISBN(isbn_string) {
    def result = true;
    if ( ( isbn_string ) && ( isbn_string.trim() != '' ) ) {
      // Check issn_string matches regexp "[0-9]{4}-[0-9]{3}[0-9X]"
      if ( isbn_string ==~ '97(8|9))?[0-9]{9}[0-9X])' ) {
        // Matches, all is good.
      }
      else {
        result = false
      }
    }
    return result;
  }

  def generateAndValidateSubOfferedIdentifier(upload) {
	  // Create normalised SO ID
	  upload.normalisedSoIdentifier = upload['soIdentifier'].value.trim().toLowerCase().replaceAll('-','_')
	  if ( ( upload.normalisedSoIdentifier == null ) || ( upload.normalisedSoIdentifier.trim().length() == 0 ) ) {
		  log.error("No subscription offered identifier");
		  upload['soIdentifier'].messages.add("Unable to use this identifier")
		  upload.processFile=false
	  }
	  else {
		  // Generated identifier is valid, check one does not exist already
		  if ( Subscription.findByIdentifier(upload.normalisedSoIdentifier) ) {
			  upload['soIdentifier'].messages.add("Subscription identifier already present")
			  upload.processFile=false
		  }
	  }
 	  return true
  }
  
  def generateAndValidatePackageIdentifier(upload) {
      upload.normPkgIdentifier = "${upload.soProvider.value.trim()}:${upload.soPackageIdentifier.value.trim()}".toLowerCase().replaceAll('-','_');
	  if ( ( upload.normPkgIdentifier == null ) || ( upload.normPkgIdentifier.trim().length() == 0 ) ) {
		  log.error("No package identifier");
		  upload['soPackageIdentifier'].messages.add("Unable to use this identifier")
		  upload.processFile=false
	  }
	  else {
		  // Generated identifier is valid, check one does not exist already
		  if ( Package.findByIdentifier(upload.normPkgIdentifier) ) {
			  upload['soPackageIdentifier'].messages.add("Package identifier already present")
			  upload.processFile=false
		  }
	  }


	  return true
  }

  def validateNumPlatforms(upload) {
	  if ( ( upload.numPlatformsListed.value == null ) ||
		   ( upload.numPlatformsListed.value <= 0 ) ) {
	    upload.numPlatformsListed.messages.add("Must be >= 0")
		upload.processFile=false
	  }
	return true
  }
    
  def validateConsortia(upload) {
	if ( ( upload.consortium ) && ( upload.consortium.value ) ) {
	  if ( Org.findByName(upload.consortium.value) == null ) {
		upload.consortium.messages.add("Unable to locate org with name ${upload.consortium.value}")
		upload.processFile=false
	  }
	}
	return true
  }

  def validateColumnHeadings(upload) {
	return checkColumnHeader(upload, 0,'publication_title') &&
	       checkColumnHeader(upload, 1,'print_identifier') &&
		     checkColumnHeader(upload, 2,'online_identifier') &&
 		     checkColumnHeader(upload, 3,'date_first_issue_online') &&
		     checkColumnHeader(upload, 4,'num_first_vol_online') &&
		     checkColumnHeader(upload, 5,'num_first_issue_online') &&
		     checkColumnHeader(upload, 6,'date_last_issue_online') &&
		     checkColumnHeader(upload, 7,'num_last_vol_online') &&
		     checkColumnHeader(upload, 8,'num_last_issue_online') &&
		     checkColumnHeader(upload, 9,'title_id') &&
		     checkColumnHeader(upload,10,'embargo_info') &&
		     checkColumnHeader(upload,11,'coverage_depth') &&
		     checkColumnHeader(upload,12,'coverage_notes') &&
		     checkColumnHeader(upload,13,'publisher_name') &&
		     checkColumnHeader(upload,14,'DOI') &&
		     checkProprietaryIds(upload) &&
		     checkPlatforms(upload)
		   
  }
  
  def checkColumnHeader(upload,col_position,col_name) {
    if ( upload.soHeaderLine[col_position] == col_name )
	  return true
	else {
	  upload.messages.add("Expected column ${col_name} at position ${col_position}. Found ${upload.soHeaderLine[col_position]}")
	  upload.processFile=false
	  return false
	}
  }
  
  def checkProprietaryIds(upload) {
	return true
  }
  
  def checkPlatforms(upload) {
	return true
  }
  // result.soName = [origvalue:so_name_line[1]]

}

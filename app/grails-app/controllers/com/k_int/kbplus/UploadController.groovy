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

import org.mozilla.universalchardet.UniversalDetector;
import org.apache.commons.io.input.BOMInputStream


class UploadController {

  def springSecurityService
  def sessionFactory
  def packageIngestService
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
  
  def csv_column_config = [
    'id':[coltype:'map'],
    'tippid':[coltype:'map'],
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
    [regexp:'[0-9]{2}/[0-9]{2}/[0-9]{4}', format: new SimpleDateFormat('dd/MM/yyyy')],
    [regexp:'[0-9]{4}/[0-9]{2}/[0-9]{2}', format: new SimpleDateFormat('yyyy/MM/dd')],
    [regexp:'[0-9]{2}/[0-9]{2}/[0-9]{2}', format: new SimpleDateFormat('dd/MM/yy')],
    [regexp:'[0-9]{4}/[0-9]{2}', format: new SimpleDateFormat('yyyy/MM')],
    [regexp:'[0-9]{4}', format: new SimpleDateFormat('yyyy')]
  ];

  @Secured(['ROLE_ADMIN', 'KBPLUS_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def reviewSO() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    
    if ( request.method == 'POST' ) {


      result.validationResult = readSubscriptionOfferedCSV(request)

      validate(result.validationResult)
      if ( result.validationResult.processFile == true ) {
        // log.debug("Passed first phase validation, continue...");
        processUploadSO(result.validationResult)
      }
    }
    else {
    }
    
    return result
  }
  
  def processUploadSO(upload) {

    def new_pkg_id = null
    log.debug("Content provider value is ${upload.soProvider.value}");

    def content_provider_org = Org.findByName(upload.soProvider.value) 
    if ( content_provider_org == null ) {
      log.debug("content_provider_org is present and set to ${content_provider_org}");
      content_provider_org = new Org(name:upload.soProvider.value,impId:java.util.UUID.randomUUID().toString()).save();    
      incrementStatsCounter(upload,'Content Provider Org Created');
    }
    else {
      log.debug("Matched ${content_provider_org} using name ${upload.soProvider.value}");
      incrementStatsCounter(upload,'Content Provider Org Matched');
    }
    
    def pkg_type = RefdataCategory.lookupOrCreate('PackageTypes','Unknown');
    def cp_role = RefdataCategory.lookupOrCreate('Organisational Role','Content Provider');
    def tipp_current = RefdataCategory.lookupOrCreate('TIPP Status','Current');
    
    def consortium = null;
    if ( upload.consortium != null )  {
      consortium = Org.findByName(upload.consortium.value) ?: new Org(name:upload.consortium.value).save();
    }


    def new_pkg = null;

    if ( upload.incremental ) {
      log.debug("Processing incremental load....");
      new_pkg = Package.findByIdentifier(upload.normPkgIdentifier);
      new_pkg_id = new_pkg.id
    }
    else {
      new_pkg = new Package(identifier: upload.normPkgIdentifier,
                            name: upload.soPackageName.value,
                            type: pkg_type,
                            contentProvider: content_provider_org,
                            startDate: upload.aggreementTermStartYear?.value, 
                            endDate: upload.aggreementTermEndYear?.value, 
                            impId: java.util.UUID.randomUUID().toString());

      if ( new_pkg.save(flush:true, failOnError:true) ) {

        log.debug("Package [${new_pkg.id}] with identifier ${new_pkg.identifier} created......");

        if ( upload.consortiumOrg ) {                              
          def sc_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Package Consortia');
          def or = new OrgRole(org: consortium, pkg:new_pkg, roleType:sc_role).save();
        }

        // Content Provider?
        if ( content_provider_org ) {
          log.debug("Linking to org as content provider");
          def cp_or = new OrgRole(org: content_provider_org, pkg:new_pkg, roleType:cp_role).save();
        }
        else {
          log.debug("No content provider org");
        }
        new_pkg_id = new_pkg.id
      }
      else {
        log.error("Problem saving new package");
        upload.nessages.add("Problem saving new package");
        new_pkg.errors.each { pe ->
          log.error("Problem saving package: ${pe}");
          upload.nessages.add("Problem saving package: ${pe}");
        }
        flash.error="Problem saving new package ${new_pkg.errors}";
        return
      }
    }

    log.debug("processing titles");
    // Title info
    upload.tipps.each { tipp ->
    
      def publisher = null;
      if ( tipp.publisher_name && ( tipp.publisher_name.trim() != '' ) )  {
        publisher = Org.findByName(tipp.publisher_name)
        if ( publisher == null ) {
          publisher = new Org(name:tipp.publisher_name).save();
          incrementStatsCounter(upload,'Publisher Org Created');
        }
        else {
          incrementStatsCounter(upload,'Publisher Org Matched');
        }
      }

      tipp.host_platform = null;
      tipp.additional_platforms = []

      // Process identifiers in the row.
      tipp.platform.values().each { pl ->
        def platform = Platform.lookupOrCreatePlatform(name:pl.name, primaryUrl:pl.url)
        if ( pl.coltype == 'host' ) {
          tipp.host_platform = platform
          tipp.host_platform_url = pl.url;
        }
        else {
          tipp.additional_platforms.add([plat:platform, role:pl.coltype, url:pl.url])
        }
      }
          
      // log.debug("Lookup or create title ${tipp.id}");

      tipp.title_obj = lookupOrCreateTitleInstance(tipp.id,tipp.publication_title,publisher);
      
      if ( tipp.title_obj && tipp.host_platform && new_pkg ) {
        // Got all the components we need to create a tipp
        def dbtipp = TitleInstancePackagePlatform.findByPkgAndPlatformAndTitle(new_pkg,tipp.host_platform,tipp.title_obj)
        if ( dbtipp == null ) {
          incrementStatsCounter(upload,'TIPP Created');
           
          dbtipp = new TitleInstancePackagePlatform(pkg:new_pkg,
                                                    platform:tipp.host_platform,
                                                    title:tipp.title_obj,
                                                    startDate:tipp.parsedStartDate,
                                                    startVolume:tipp.num_first_vol_online,
                                                    startIssue:tipp.num_first_issue_online,
                                                    endDate:tipp.parsedEndDate,
                                                    endVolume:tipp.num_last_vol_online,
                                                    endIssue:tipp.num_last_issue_online,
                                                    embargo:tipp.embargo_info,
                                                    coverageDepth:tipp.coverage_depth,
                                                    coverageNote:tipp.coverage_notes,
                                                    hostPlatformURL:tipp.host_platform_url,
                                                    impId:java.util.UUID.randomUUID().toString(),
                                                    status:tipp_current,
                                                    ids:[])
  
          if ( ! dbtipp.save() ) {
            log.error("ERROR Saving tipp");
            dbtipp.errors.each { err ->
              log.error("  -> ${err}");
              tipp.messages.add("Problem saving tipp: ${err}");
              tipp.messages.add([type:'alert-error',message:"Problem creating new tipp: ${dbtipp.id}"]);
            }
          }
          else {
            // log.debug("new TIPP Save OK ${dbtipp.id}");
            tipp.messages.add([type:'alert-success',message:"New tipp created: ${dbtipp.id}"]);
            tipp.additional_platforms.each { ap ->
              PlatformTIPP pt = new PlatformTIPP(tipp:dbtipp,platform:ap.plat,titleUrl:ap.url,rel:ap.role)
            }
            // Really not happy with this as a way forward, hoping for feedback from OS
            tipp.tippid.each { tippid ->
              def canonical_id = Identifier.lookupOrCreateCanonicalIdentifier(tippid.key, tippid.value)
              def new_io = new IdentifierOccurrence(identifier:canonical_id,tipp:dbtipp).save()
              tipp.messages.add([type:'alert-success',message:"tipp identifier created: ${tippid.key}:${tippid.value} ${new_io.id}"]);
            }
          }

        }
        else {
          log.error("TIPP already exists!! this can happen in incrementals... just ignore now");
          tipp.messages.add([type:'alert-error',message:"WARNING: An existing tipp record was located. This can happen in incremental imports. This row has been ignored"]);
        }        
      }
      else {
        tipp.messages.add([type:'alert-error',message:"WARNING: Dataload failed, at least one of title, package or platform was null"]);
      }
    }

    log.debug("Completed New package is ${new_pkg.id}");

    upload.new_pkg_id = new_pkg_id
  }
    
  def lookupOrCreateTitleInstance(identifiers,title,publisher) {
    // log.debug("lookupOrCreateTitleInstance ${identifiers}, ${title}, ${publisher}");
    def result = TitleInstance.lookupOrCreateViaIdMap(identifiers, title);
    if ( !result.getPublisher() ) {
      def pub_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Publisher');
      OrgRole.assertOrgTitleLink(publisher, result, pub_role);
      result.save();
    }

    // ToDo: Check to see that all the identifiers we have

    // log.debug("Done: ${result}");
    result;
  }
  
  def parseDate(datestr, possible_formats) {
    def parsed_date = null;
    for(Iterator i = possible_formats.iterator(); ( i.hasNext() && ( parsed_date == null ) ); ) {
      try {
        def date_format_info = i.next();

        if ( datestr ==~ date_format_info.regexp ) {
          def formatter = date_format_info.format
          parsed_date = formatter.parse(datestr);
          java.util.Calendar c = new java.util.GregorianCalendar();
          c.setTime(parsed_date)
          if ( ( 0 <= c.get(java.util.Calendar.MONTH) ) && ( c.get(java.util.Calendar.MONTH) <= 11 ) ) {
            // Month is valid
          }
          else {
            // Invalid date
            parsed_date = null
          // log.debug("Parsed ${datestr} using ${formatter.toPattern()} : ${parsed_date}");
          }
        }
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

  def readSubscriptionOfferedCSV(request) {

    def result = [:]
    result.processFile=true
    result.incremental=false

    def upload_mime_type = request.getFile("soFile")?.contentType
    def upload_filename = request.getFile("soFile")?.getOriginalFilename()
    log.debug("Uploaded so type: ${upload_mime_type} filename was ${upload_filename}");

    def charset = checkCharset(request.getFile("soFile")?.inputStream)

    def input_stream = new BOMInputStream(request.getFile("soFile")?.inputStream)

    // File level messages
    result.messages=[]

    log.debug("Reading Stream");

    if  ( ( charset != null ) && ( ! charset.equals('UTF-8') ) ) {
      result.messages.add("** WARNING: Detected input character stream encoding: ${charset}. Expected UTF-8");
    }

    CSVReader r = null
    if ( params.docstyle?.equals("tsv") ) {
      log.debug("Processing TSV");
      r = new CSVReader( new InputStreamReader(input_stream, java.nio.charset.Charset.forName('UTF-8') ), (char)'\t' )
    }
    else {
      log.debug("Processing CSV");
      r = new CSVReader( new InputStreamReader(input_stream, java.nio.charset.Charset.forName('UTF-8') ) )
    }

    String [] nl;

    def possible_header_properties = [
      [ key:'so name', name:'soName', type:'str', mandatory:false ],
      [ key:'so identifier', name:'soIdentifier', type:'str', mandatory:false ],
      [ key:'provider', name:'soProvider', type:'str', mandatory:false ],
      [ key:'package identifier', name:'soPackageIdentifier', type:'str', mandatory:true ],
      [ key:'package name', name:'soPackageName', type:'str', mandatory:true ],
      [ key:'agreement term start year', name:'aggreementTermStartYear', type:'date', mandatory:true ],
      [ key:'agreement term end year', name:'aggreementTermEndYear', type:'date', mandatory:true ],
      [ key:'consortium', name:'consortium', type:'str', mandatory:false ],
    ]

    // Read rows whilst we are processing header properties defined above, as soon as we encounter a row that doesn't
    // contain a header property, move on to the column definitions.
    nl = r.readNext()
    def header_conf = possible_header_properties.find{ it.key == nl[0].toLowerCase() }
    while( header_conf != null ) {
      processCsvLine(nl,header_conf.name,1,result,header_conf.type,null,header_conf.mandatory)
      nl = r.readNext()
      header_conf = possible_header_properties.find{ it.key == nl[0].toLowerCase() }
    }
    
    result.soHeaderLine = []
    nl.each { h ->
      result.soHeaderLine.add(h.toLowerCase());
    }
    

    result.tipps = []
    while ((nl = r.readNext()) != null) {
      // result.tipps.add(tipp_row)
      result.tipps.add(readTippRow(result.soHeaderLine, nl))
    }

    return result;
  }
  
  def readTippRow(cols, nl) {
    def result = [:]

    result.messages = []
    result.row = []
    
    for ( int i=0; i<nl.length; i++ ) {
      result.row.add(nl[i]);
      if ( ( nl[i] != null ) && ( nl[i].trim() != '' ) ) {
        def column_components = cols[i].split('\\.')
        def column_name = column_components[0]
        def column_defn = csv_column_config[column_name]
        //log.debug("Process ${cols[i]} ${column_name} : ${column_defn}")
        if ( column_defn ) {
          switch ( column_defn.coltype ) {
            case 'simple': 
              result[column_name] = nl[i]
              break;
            case 'map':
              if ( result[column_name] == null )
                result[column_name] = [:]
            
              // If this is a simple map, like id.issn or id.eissn just set the value
              if ( column_components.length == 2 ) {
                result[column_name][column_components[1]] = nl[i].trim();    
              }
              else {
                // We have an object like platform.host:1.name, platform.host:2.name
                if ( result[column_name][column_components[1]] == null ) {
                  // We're setting up the object. Add new object to platform map, keyed as second part of col name
                  result[column_name][column_components[1]]=[:]
                  def column_types = column_components[1].split(':');
                  // Add a coltype to the values, so platform.host.name gets a coltype of "host"
                  result[column_name][column_components[1]].coltype=column_types[0]
                }
                result[column_name][column_components[1]][column_components[2]] = nl[i].trim();
              }
              break;
          } 
        }
        else {
          // Unknown column
        }
      }
    }
    return result;
  }
  
  def processCsvLine(csv_line, field_name, col_num,result_map, parseAs, defval, isMandatory) {  
    log.debug("  processCsvLine ${csv_line} ${field_name} ${col_num}... mandatory=${isMandatory}");
    def result = [:]
    result.messages = []
    result.origValue = csv_line[col_num]

    if ( ( col_num <= csv_line.length ) && ( csv_line[col_num] != null ) ) {      
      switch(parseAs) {
        case 'int':
          result.value = Integer.parseInt(result.origValue?:defval)
          break;
        case 'date':
          result.value = parseDate(result.origValue,possible_date_formats)
          log.debug("Parse date, ${result.origValue}, result = ${result.value}");
          break;
        case 'str':
        default:
          result.value = result.origValue
          break;
      }
      result_map[field_name] = result
    }
  
    
    if ( ( result.value == null ) || ( result.value.toString().trim() == '' ) ) {
      log.debug("Mandatory flag set, checking value");
      if ( isMandatory ) {
        log.debug("Mandatory property is null.. error");
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
    
    def result = generateAndValidatePackageIdentifier(upload) &&
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
    
      // log.debug("Validate tipp: ${tipp}");

      if ( ( tipp.publication_title == null ) || 
           ( tipp.publication_title.trim() == '' ) ) {
        tipp.messages.add("Title (row ${counter}) must not be empty");
        upload.processFile=false;
      }
      
      //if ( ! atLeastOneOf(tipp,['print_identifier', 'online_identifier', 'DOI', 'Proprietary_ID.isbn']) ) {
      //  tipp.messages.add("Title (row ${counter}) must reference at least one identifier");
      //  upload.processFile=false;
      //}
      
      // log.debug("tipp id = ${tipp.id}");
            
      if (!tipp.id) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid ID (at least one of \"id.issn\", \"id.isbn\", \"id.eissn\" required, found: ${tipp.id})");
        upload.processFile=false;
      }
            
      if ( !validISSN(tipp.id?.issn) ) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid ISSN (Column should be id.issn, value in file was ${tipp.id?.issn})");
        upload.processFile=false;
      }
      
      if ( ! validISSN(tipp.id?.eissn) ) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid eISSN  (Column name should be id.eissn, value in file was ${tipp.id?.eissn})");
        upload.processFile=false;
      }

      if ( ! validISBN(tipp.id?.isbn) ) {
        tipp.messages.add("Title (row ${counter}) does not contain a valid ISBN (Column name should be id.isbn,value in file was ${tipp.id?.issn})");
        upload.processFile=false;
      }
      
      if ( tipp.id ) {
        ["eissn", "issn", "doi", "isbn" ].each { idtype ->
          if ( ( tipp.id[idtype] ) && ( tipp.id[idtype] != '' ) ) {
            if ( id_list.contains(tipp.id[idtype]) ) {
              tipp.messages.add("Title (row ${counter}) contains a repeated ${idtype} - ${tipp.id[idtype]}");
              upload.processFile=false;
            }
            else {
              id_list.add(tipp.id[idtype])
            }
          }
        }
      }

      def matched_titles = findTitleIdentifierIntersection(tipp.id)
      if ( matched_titles.size() > 1 ) {
        tipp.messages.add("Mix of identifiers in row ${counter} match more than one title:(${matched_titles}). Please correct. identifiers:${tipp.id}");
        upload.processFile=false;
      }
      else {
        if ( ( matched_titles.size() == 1 ) && ( tipp.publication_title != null ) ) {
          checkTitleFingerprintMatch(matched_titles[0], tipp.publication_title, tipp,upload)
        }
      }

      // Git issue 419 - Allow tipps without a start date (:-/)
      tipp.parsedStartDate = parseDate(tipp.date_first_issue_online,possible_date_formats)
      
      if ( tipp.date_last_issue_online && ( tipp.date_last_issue_online.trim() != '' ) ) {
        tipp.parsedEndDate = parseDate(tipp.date_last_issue_online,possible_date_formats)
        if ( tipp.parsedEndDate == null ) {
          tipp.messages.add("Invalid tipp date_last_issue_online");
          upload.processFile=false;
        }
      }
      
      if ( ( tipp.coverage_depth != null ) &&
           ( tipp.coverage_depth.trim() != '' ) &&
           ( ! ( ['fulltext','selected articles','abstracts'].contains(tipp.coverage_depth.toLowerCase())) ) ) {
        tipp.messages.add("coverage depth must be one of fulltext, selected articles or abstracts");
        upload.processFile=false;                             
      }
      else {
        // log.debug("${tipp.coverage_depth} is valid");
        //  tipp.messages.add("Missing tipp coverage depth");
        //  upload.processFile=false;                                     

      }
      
      if ( tipp.platform ) {
        def plat = tipp.platform.values().find { it.coltype=='host' }
        if ( plat && plat.url && ( plat.url.trim() != '' && plat.name && plat.name.trim() != '' ) ) {
          // Cool - found platform
        }
        else {
          tipp.messages.add("Error in Platform Information - Unable to locate host platform OR platform url/name not specified");
          upload.processFile=false;
        }
      }
      else {
        tipp.messages.add("No platform information for tipp");
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
      if ( isbn_string ==~ '(97(8|9))?[0-9]{9}[0-9X]' ) {
        // Matches, all is good.
      }
      else {
        result = false
      }
    }
    return result;
  }

  def generateAndValidatePackageIdentifier(upload) {

    if ( upload.soProvider?.value && upload.soPackageIdentifier?.value ) {
      upload.normPkgIdentifier = "${upload.soProvider.value?.trim()}:${upload.soPackageIdentifier.value?.trim()}"
                                      .toLowerCase()
                                      .replaceAll('-','_')
                                      .replaceAll('&','_')
                                      .replaceAll(' ','_')
                                      .replaceAll(':','_');
      if ( ( upload.normPkgIdentifier == null ) || ( upload.normPkgIdentifier.trim().length() == 0 ) ) {
        log.error("No package identifier");
        upload['soPackageIdentifier'].messages.add("Unable to use this identifier")
        upload.processFile=false
      }
      else {
        // Generated identifier is valid, check one does not exist already
        if ( Package.findByIdentifier(upload.normPkgIdentifier) ) {
          upload['soPackageIdentifier'].messages.add("Package identifier already present - Assuming incremental import")
          // upload.processFile=false
          upload.incremental=true
        }
      }
    }
    else {
      log.error("No package identifier");
      upload['soPackageIdentifier'].messages.add("Unable to use this identifier")
      upload.processFile=false
    }

    return true
  }

  def validateConsortia(upload) {
  if ( ( upload.consortium ) && ( upload.consortium.value ) ) {
    upload.consortiumOrg = Org.findByName(upload.consortium.value)
    if ( upload.consortiumOrg == null ) {
      upload.consortium.messages.add("Unable to locate org with name ${upload.consortium.value}")
      upload.processFile=false
    }
  }
  return true
  }

  def validateColumnHeadings(upload) {
    def cols_so_far = []
    int col = 0;
    upload.soHeaderLine.each { p ->
      if ( p.trim() != '' ) {
        if ( cols_so_far.contains(p) ) {
          upload.messages.add("Field ${p} (col ${col}) seems to be repeated in the header. This should never happen. If you are, for example, adding multiple admin platforms, please use the format platform.first.name, platform.second.name instead of simply repeating platform.name. This allows us to join together urls with corresponding values");
          upload.process_file=false;
        }
        else {
          cols_so_far.add(p);
        }
      }
      col++
    }       
  }

  def findTitleIdentifierIntersection(idlist) {
    def matched_title_ids = []
    def identifiers_to_check = ["eissn", "issn", "doi", "isbn"]
    idlist.each { id ->
      if ( identifiers_to_check.contains(id.key) ) {
        def title = TitleInstance.findByIdentifier([[namespace:id.key,value:id.value]])
        // log.debug("found title ${title}");
        if ( title != null ) {
          if ( matched_title_ids.contains(title.id) )  {
            //log.debug("Matched titles already contains that title");
          }
          else {
            //log.debug("Adding matched title ${title.id} to matching titles list");
            matched_title_ids.add(title.id)
          }
        }
        else {
          log.debug("no title by identifier match for ${id.key} : ${id.value}");
        }
      }
      else {
        log.debug("Not checking identifier of type ${id.key}");
      }
    }

    // log.debug("Identifiers matched the following title ids: ${matched_title_ids}");
    return matched_title_ids;
  }
  
  def checkTitleFingerprintMatch(matched_title_id, title_from_import_file, tipp, upload) {
    // log.debug("checkTitleFingerprintMatch ${matched_title_id}, ${title_from_import_file}");
    def title_instance = TitleInstance.get(matched_title_id)
    def generated_key_title = TitleInstance.generateKeyTitle(title_from_import_file)
    // log.debug("checkTitleFingerprintMatch ${matched_title_id}, ${title_from_import_file} == ${generated_key_title}");

    if ( ! title_instance.keyTitle.equals(generated_key_title) ) {
      tipp.messages.add("Matched title identifier does not pass title fingerprint match. titleid=${matched_title_id}, \"${title_instance.keyTitle}\" != \"${generated_key_title}\"");
      upload.processFile=false;
    }
  }

  def incrementStatsCounter(result, counter) {
    if ( result.stats == null ) {
      result.stats = [:]
    }

    if ( result.stats[counter] == null ) {
      result.stats[counter] = 1
    }
    else {
      result.stats[counter]++
    }
  }

  def public static checkCharset(file_input_stream) {

    def result = null;

    byte[] buf = new byte[4096];

    // (1)
    UniversalDetector detector = new UniversalDetector(null);

    // (2)
    int nread;
    while ((nread = file_input_stream.read(buf)) > 0 && !detector.isDone()) {
      detector.handleData(buf, 0, nread);
    }
    // (3)
    detector.dataEnd();

    // (4)
    String encoding = detector.getDetectedCharset();
    if (encoding != null) {
      result = encoding; 
      System.out.println("Detected encoding = " + encoding);
      if ( encoding.equals('WINDOWS-1252') ) {
      }
    } else {
      System.out.println("No encoding detected.");
    }

    // (5)
    detector.reset();

    result
  }

}

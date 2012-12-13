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
    
    def org = Org.findByName(so_provider_line[1]) ?: new Org(name:so_provider_line[1]).save();
    
    def normalised_identifier = so_identifier_line[1].trim().toLowerCase().replaceAll('-','_')
    def norm_pkg_identifier = so_package_identifier_line[1].trim().toLowerCase().replaceAll('-','_');
    
    log.debug("Processing subscription ${so_identifier_line[1]} normalised to ${normalised_identifier}");
    
    if ( ( normalised_identifier == null ) || ( normalised_identifier.trim().length() == 0 ) ) {
      flash.error="No usable subscription offered identifier";      
      return
    }
    
    if ( ( norm_pkg_identifier == null ) || ( norm_pkg_identifier.length() == 0 ) ) {
      flash.error="No usable package identifier";      
      return
    }
    
    def sub = Subscription.findByIdentifier(normalised_identifier)
    if ( sub != null ) {
      flash.error="Unable to process file - Subscription with ID ${normalised_identifier} already exists in database";
      return
    }

    def pkg = Package.findByIdentifier(norm_pkg_identifier);
    if ( pkg != null ) {
      flash.error="Unable to process file - Subscription with ID ${normalised_identifier} already exists in database";
      return
    }
    
    prepared_so.provider = org
    prepared_so.sub = [:]
    prepared_so.sub.identifier = normalised_identifier;
    prepared_so.sub.name = so_name_line[1];
    prepared_so.sub.start_date_str = so_agreement_term_start_yr_line[1]
    prepared_so.sub.end_date_str=so_agreement_term_end_yr_line[1]
    prepared_so.sub.start_date = parseDate(so_agreement_term_start_yr_line[1],possible_date_formats)
    prepared_so.sub.end_date = parseDate(so_agreement_term_end_yr_line[1],possible_date_formats)
    prepared_so.pkg_id = norm_pkg_identifier


    def consortium = null;
    if ( ( so_consortium_line[1] != null ) && ( so_consortium_line[1].length() > 0 ) )  {
        prepared_so.cons = Org.findByName(so_consortium_line[1]) ?: new Org(name:so_consortium_line[1]).save();
    }

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
        println "**Processing pub title:${nl[0]}, print identifier ${nl[1]} (${num_prop_id_cols} prop cols, ${num_platforms_listed} plat cols)"
        
      }
    }
    
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

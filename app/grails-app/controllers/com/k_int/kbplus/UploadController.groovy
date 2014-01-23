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
  def reviewPackage() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    
    if ( request.method == 'POST' ) {

      def upload_mime_type = request.getFile("soFile")?.contentType
      def upload_filename = request.getFile("soFile")?.getOriginalFilename()
      log.debug("Uploaded so type: ${upload_mime_type} filename was ${upload_filename}");
      def charset = checkCharset(request.getFile("soFile")?.inputStream)
      def input_stream = request.getFile("soFile")?.inputStream

      result.validationResult = packageIngestService.readPackageCSV(upload_mime_type, upload_filename, charset, input_stream)

      packageIngestService.validate(result.validationResult)

      if ( result.validationResult.processFile == true ) {
        // log.debug("Passed first phase validation, continue...");
        packageIngestService.processUploadPackage(result.validationResult)
      }
    }
    else {
    }
    
    return result
  }
}

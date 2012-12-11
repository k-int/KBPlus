package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

class PackageDetailsController {

    def springSecurityService

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def list() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      params.max = Math.min(params.max ? params.int('max') : 10, 100)
      result.packageInstanceList=Package.list(params)
      result.packageInstanceTotal=Package.count()
      result
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def create() {
      def user = User.get(springSecurityService.principal.id)

      switch (request.method) {
        case 'GET':
          [packageInstance: new Package(params), user:user]
          break
        case 'POST':
          def providerName = params.contentProviderName
          def packageName = params.packageName
          def identifier = params.identifier

          def contentProvider = Org.findByName(providerName);
          def existing_pkg = Package.findByIdentifier(identifier);

          if ( contentProvider && existing_pkg==null ) {
            log.debug("Create new package, content provider = ${contentProvider}, identifier is ${identifier}");
            Package new_pkg = new Package(identifier:identifier, 
                                          contentProvider:contentProvider,
                                          name:packageName,
                                          impId:java.util.UUID.randomUUID().toString());
            if ( new_pkg.save(flush:true) ) {
              redirect action: 'edit', id:new_pkg.id
            }
            else {
              new_pkg.errors.each { e ->
                log.error("Problem: ${e}");
              }
              render view: 'create', model: [packageInstance: new_pkg, user:user]
            }
          }
          else {
            render view: 'create', model: [packageInstance: packageInstance, user:user]
            return
          }

          // flash.message = message(code: 'default.created.message', args: [message(code: 'package.label', default: 'Package'), packageInstance.id])
          // redirect action: 'show', id: packageInstance.id
          break
      }
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def show() {
      def packageInstance = Package.get(params.id)
      if (!packageInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
        redirect action: 'list'
        return
      }

      [packageInstance: packageInstance]
    }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def uploadTitles() {
    def pkg = Package.get(params.id)
    def upload_mime_type = request.getFile("titleFile")?.contentType
    log.debug("Uploaded content type: ${upload_mime_type}");
    def input_stream = request.getFile("titleFile")?.inputStream

    if ( upload_mime_type=='application/vnd.ms-excel' ) {
      attemptXLSLoad(pkg,input_stream);
    }
    else {
      attemptCSVLoad(pkg,input_stream);
    }

    redirect action:'show', id:params.id
  }

  def attemptXLSLoad(pkg,stream) {
    log.debug("attemptXLSLoad");
    HSSFWorkbook wb = new HSSFWorkbook(stream);
    HSSFSheet hssfSheet = wb.getSheetAt(0);

    attemptv1XLSLoad(pkg,hssfSheet);
  }

  def attemptCSVLoad(pkg,stream) {
    log.debug("attemptCSVLoad");
    attemptv1CSVLoad(pkg,stream);
  }

  def attemptv1XLSLoad(pkg,hssfSheet) {

    log.debug("attemptv1XLSLoad");
    def extracted = [:]
    extracted.rows = []

    int row_counter = 0;
    Iterator rowIterator = hssfSheet.rowIterator();
    while (rowIterator.hasNext()) {
      switch(row_counter++){
        case 0:
          break;
        case 1:
          break;
        case 2:
          break;
        case 3:
          // Record header row
          log.debug("Header");
          break;
        default:
          // A real data row
          HSSFRow hssfRow = (HSSFRow) rowIterator.next();
         
          def row_info = [
            issn:hssfRow.getCell(0)?.toString(),
            eissn:hssfRow.getCell(1)?.toString(),
            date_first_issue_online:hssfRow.getCell(2)?.toString(),
            num_first_volume_online:hssfRow.getCell(3)?.toString(),
            num_first_issue_online:hssfRow.getCell(4)?.toString(),
            date_last_issue_online:hssfRow.getCell(5)?.toString(),
            date_first_volume_online:hssfRow.getCell(6)?.toString(),
            date_first_issue_online:hssfRow.getCell(7)?.toString(),
            embargo:hssfRow.getCell(8)?.toString(),
            coverageDepth:hssfRow.getCell(9)?.toString(),
            coverageNote:hssfRow.getCell(10)?.toString(),
            platformUrl:hssfRow.getCell(11)?.toString()
          ]

          extracted.rows.add(row_info);
          log.debug("datarow: ${row_info}");
          break;
      }
    }
    
    processExractedData(pkg,extracted);
  }

  def attemptv1CSVLoad(pkg,stream) {
    log.debug("attemptv1CSVLoad");
    def extracted = [:]
    processExractedData(pkg,extracted);
  }

  def processExractedData(pkg, extracted_data) {
    log.debug("processExractedData...");
  }
}

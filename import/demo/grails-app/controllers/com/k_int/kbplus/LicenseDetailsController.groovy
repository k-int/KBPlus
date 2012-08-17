package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class LicenseDetailsController {

  def springSecurityService
  def docstoreService
  def ESWrapperService
  def gazetteerService
  def alertsService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    // result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)

    if ( result.license.isEditableBy(result.user) ) {
      result.editable = true
    }
    else {
      result.editable = false
    }

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def links() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    // result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def notes() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    // result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def documents() {
    log.debug("licenseDetails id:${params.id}");
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    // result.institution = Org.findByShortcode(params.shortcode)
    result.license = License.get(params.id)
    result
  }



  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def uploadDocument() {
    log.debug("upload document....");

    def user = User.get(springSecurityService.principal.id)

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
                                  type:RefdataCategory.lookupOrCreate('Document Type',params.doctype)).save()

        def doc_context = new DocContext(license:l,
                                         owner:doc_content,
                                         user: user,
                                         doctype:RefdataCategory.lookupOrCreate('Document Type',params.doctype)).save(flush:true);
      }
    }

    log.debug("Redirecting...");
    redirect controller: 'licenseDetails', action:'index', id:params.licid, fragment:params.fragment
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def deleteDocuments() {
    def ctxlist = []

    log.debug("deleteDocuments ${params}");

    params.each { p ->
      if (p.key.startsWith('_deleteflag.') ) {
        def docctx_to_delete = p.key.substring(12);
        log.debug("Looking up docctx ${docctx_to_delete} for delete");
        def docctx = DocContext.get(docctx_to_delete)
        docctx.status = RefdataCategory.lookupOrCreate('Document Context Status','Deleted');
      }
    }

    redirect controller: 'licenseDetails', action:'index', params:[shortcode:params.shortcode], id:params.licid, fragment:'docstab'
  }

}

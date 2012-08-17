package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;


class DocWidgetController {

  def springSecurityService
  def docstoreService
  def ESWrapperService
  def gazetteerService
  def alertsService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def createNote() { 
    log.debug("Create note referer was ${request.getHeader('referer')} or ${request.request.RequestURL}");
    def user = User.get(springSecurityService.principal.id)
    def domain_class=grailsApplication.getArtefact('Domain',params.ownerclass)

    if ( domain_class ) {
      def instance = domain_class.getClazz().get(params.ownerid)
      if ( instance ) {
        log.debug("Got owner instance ${instance}");

        def doc_content = new Doc(contentType:0,
                                  content: params.licenceNote,
                                  type:RefdataCategory.lookupOrCreate('Document Type','Note'),
                                  user:user).save()

        def alert = null;
        if ( params.licenceNoteShared ) {
          switch ( params.licenceNoteShared ) {
            case "0":
              break;
            case "1":
              alert = new Alert(sharingLevel:1, createdBy:user).save();
              break;
            case "2":
              alert = new Alert(sharingLevel:2, createdBy:user).save();
              break;
          }
        }

        def doc_context = new DocContext("${params.ownertp}":instance,
                                         owner:doc_content,
                                         doctype:RefdataCategory.lookupOrCreate('Document Type',params.doctype),
                                         alert:alert).save(flush:true);
      }
      else {
        log.debug("no instance");
      }
    }
    else {
      log.debug("no type");
    }

    redirect(url: request.getHeader('referer'))
    // redirect(url: request.request.RequestURL)
    // request.request.RequestURL
    // request.getHeader('referer') 
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def uploadDocument() {
    log.debug("upload document....");

    def input_stream = request.getFile("upload_file")?.inputStream
    def original_filename = request.getFile("upload_file")?.originalFilename

    def user = User.get(springSecurityService.principal.id)

    def domain_class=grailsApplication.getArtefact('Domain',params.ownerclass)

    if ( domain_class ) {
      def instance = domain_class.getClazz().get(params.ownerid)
      if ( instance ) {
        log.debug("Got owner instance ${instance}");

        if ( input_stream ) {
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

            def doc_context = new DocContext("${params.ownertp}":instance,
                                             owner:doc_content,
                                             user:user,
                                             doctype:RefdataCategory.lookupOrCreate('Document Type',params.doctype)).save(flush:true);
          }
        }
        
      }
      else {
        log.error("Unable to locate document owner instance for class ${params.ownerclass}:${params.ownerid}");
      }
    }
    else {
      log.warn("Unable to locate domain class when processing generic doc upload. ownerclass was ${params.ownerclass}");
    }

    redirect(url: request.getHeader('referer'))
  }
}

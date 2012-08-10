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

    def domain_class=grailsApplication.getArtefact('Domain',params.ownerclass)

    if ( domain_class ) {
      def instance = domain_class.getClazz().get(params.ownerid)
      if ( instance ) {
        log.debug("Got owner instance ${instance}");

        def user = User.get(springSecurityService.principal.id)

        def doc_content = new Doc(contentType:0,
                                content: params.licenceNote,
                                type:RefdataCategory.lookupOrCreate('Document Type','Note')).save()

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
                                         doctype:RefdataCategory.lookupOrCreate('Document Type','Note'),
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
}

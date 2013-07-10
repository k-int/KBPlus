package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hslf.model.*;
import java.text.SimpleDateFormat




class ApiController {

  def springSecurityService

  def index() { 
  }

  def uploadBibJson() {
    def result=[:]
    log.debug("uploadBibJson");
    log.debug("Auth request from ${request.getRemoteAddr()}");
    if ( request.getRemoteAddr() == '127.0.0.1' ) {
      if ( request.method.equalsIgnoreCase("post")) {
        result.message = "Working...";
        def candidate_identifiers = []
        request.JSON.identifier.each { i ->
          if ( i.type=='ISSN' || i.type=='eISSN' || i.type=='DOI' ) {
            candidate_identifiers.add([namespace:i.type, value:i.id]);
          }
        }
        if ( candidate_identifiers.size() >  0 ) {
          log.debug("Lookup using ${candidate_identifiers}");
          def title = TitleInstance.findByIdentifier(candidate_identifiers)
          if ( title != null ) {
            log.debug("Located title ${title}");
            result.matchedTitleId=title.id
            if ( title.getIdentifierValue('JUSP') != null ) {
              result.message = "JUSP ID already present against title";
            }
            else {
              def jid = request.JSON.identifier.find { it.type=='JUSP' }
              if ( jid != null ) {
                result.message = "Adding JUSP ID ${jid.id}to title";
                // def new_jusp_id = Identifier.lookupOrCreateCanonicalIdentifier('JUSP',"${}");
              }
              else {
                result.message = "Unable to locate JID in BibJson record";
              }
            }
          }
          else {
            result.message = "Unable to locate title on matchpoints : ${candidate_identifiers}";
          }
        }
        else {
          result.message = "No matchable identifiers. ${request.JSON.identifier}";
        }
        
      }
      else {
        result.message = "non post";
      }
    }
    else {
      result.message = "uploadBibJson only callable from 127.0.0.1";
    }
    render result as JSON
  }
}

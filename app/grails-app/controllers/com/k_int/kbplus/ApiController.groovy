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
import com.k_int.kbplus.auth.*;
import grails.plugins.springsecurity.Secured
import grails.converters.*




class ApiController {

  def springSecurityService


  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
  }

  // @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
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
            log.debug("Located title ${title}  Current identifiers: ${title.ids}");
            result.matchedTitleId=title.id
            if ( title.getIdentifierValue('jusp') != null ) {
              result.message = "jusp ID already present against title";
            }
            else {
              log.debug("Adding jusp Identifier to title");
              def jid = request.JSON.identifier.find { it.type=='jusp' }
              log.debug("Add identifier identifier ${jid}");
              if ( jid != null ) {
                result.message = "Adding jusp ID ${jid.id}to title";
                def new_jusp_id = Identifier.lookupOrCreateCanonicalIdentifier('jusp',"${jid.id}");
                def new_io = new IdentifierOccurrence(identifier:new_jusp_id, ti:title).save(flush:true);
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


  // Assert a core status against a title/institution. Creates TitleInstitutionProvider objects
  // For all known combinations.
  def assertCore() {
    // Params:     inst - [namespace:]code  Of an org [mandatory]
    //            title - [namespace:]code  Of a title [mandatory]
    //         provider - [namespace:]code  Of an org [optional]
    log.debug("assertCore(${params})");
    def result = [:]
    if ( request.getRemoteAddr() == '127.0.0.1' ) {
      if ( ( params.inst?.length() > 0 ) && ( params.title?.length() > 0 ) ) {
        def inst = Org.lookupByIdentifierString(params.inst);
        def title = TitleInstance.lookupByIdentifierString(params.title);
        def provider = params.provider ? Org.lookupByIdentifierString(params.provider) : null;
        def year = params.year

        log.debug("assertCore ${params.inst}:${inst} ${params.title}:${title} ${params.provider}:${provider}");

        if ( title && inst ) {

          def sdf = new java.text.SimpleDateFormat('yyyy-MM-ddThh:mm:ss');

          if ( provider ) {
          }
          else {
            log.debug("Calculating all known providers for this title");
            def providers = TitleInstancePackagePlatform.executeQuery('''select distinct orl.org 
from TitleInstancePackagePlatform as tipp join tipp.pkg.orgs as orl
where tipp.title = ? and orl.roleType.value=?''',[title,'Content Provider']);

            providers.each {
              log.debug("Title ${title} is provided by ${it}");
              def tiinp = TitleInstitutionProvider.findByTitleAndInstitutionAndprovider(title, inst, it) 
              if ( tiinp == null ) {
                log.debug("Creating new TitleInstitutionProvider");
                tiinp = new TitleInstitutionProvider(title:title, institution:inst, provider:it).save(flush:true, failOnError:true)
              }

              log.debug("Got tiinp:: ${tiinp}");
              def startDate = sdf.parseDate("${year}-01-01T00:00:00");
              def endDate = sdf.parseDate("${year}-12-31T23:59:59");
              tiinp.extendCoreExtent(givenStartDate, givenEndDate);
            }
          }
        }
      }
      else {
        result.message="ERROR: missing mandatory parameter: inst or title";
      }
    }
    else {
      result.message="ERROR: this call is only usable from within the KB+ system network"
    }
    render result as JSON
  }
}

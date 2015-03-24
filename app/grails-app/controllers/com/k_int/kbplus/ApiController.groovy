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


  // @Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    log.debug("API");
  }

  // @Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
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
  // @Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
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
        def year = params.year?.trim()

        log.debug("assertCore ${params.inst}:${inst} ${params.title}:${title} ${params.provider}:${provider}");

        if ( title && inst ) {

          def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

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
              def startDate = sdf.parse("${year}-01-01T00:00:00");
              def endDate = sdf.parse("${year}-12-31T23:59:59");
              tiinp.extendCoreExtent(startDate, endDate);
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
  /*
  * Create a CSV containing all JUSP title IDs with the institution they belong to
  */
  def fetchAllTips(){

    def jusp_ti_inst = TitleInstitutionProvider.executeQuery("""
   select jusp_institution_id.identifier.value, jusp_title_id.identifier.value, dates,tip_ti.id, 
   (select jusp_provider_id.identifier.value from tip_ti.provider.ids as jusp_provider_id where jusp_provider_id.identifier.ns.ns='juspsid' )
    from TitleInstitutionProvider tip_ti
      join tip_ti.institution.ids as jusp_institution_id,
    TitleInstitutionProvider tip_inst
      join tip_inst.title.ids as jusp_title_id,
    TitleInstitutionProvider tip_date
      join tip_date.coreDates as dates
    where jusp_title_id.identifier.ns.ns='jusp'
        and tip_ti = tip_inst
        and tip_inst = tip_date
        and jusp_institution_id.identifier.ns.ns='jusplogin' order by jusp_institution_id.identifier.value 
     """)

    def date = new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)
    date = date.format(new Date())
    response.setHeader("Content-disposition", "attachment; filename=kbplus_jusp_export_${date}.csv")
    response.contentType = "text/csv"
    def out = response.outputStream
    def currentTip = null
    def dates_concat = ""
    out.withWriter { writer ->
      writer.write("JUSP Institution ID,JUSP Title ID,JUSP Provider, Core Dates\n")
      Iterator iter = jusp_ti_inst.iterator()
      while(iter.hasNext()){
        def it = iter.next()
        if(currentTip == it[3]){
          dates_concat += ", ${it[2]}"
        }else if(currentTip){
          writer.write("\"${dates_concat}\"\n\"${it[0]}\",\"${it[1]}\",\"${it[4]?:''}\",")
          dates_concat = "${it[2]}"
          currentTip = it[3]
        }else{
          writer.write("\"${it[0]}\",\"${it[1]}\",\"${it[4]?:''}\",")
          dates_concat = "${it[2]}"
          currentTip = it[3]
        }
        if (!iter.hasNext()){
          writer.write("\"${dates_concat}\"\n")
        }
      }

       writer.flush()
       writer.close()
     }
     out.close()   
  }

  // Accept a single mandatorty parameter which is the namespace:code for an institution
  // If found, return a JSON report of each title for that institution
  // Also accept an optional parameter esn [element set name] with values full of brief[the default]
  // Example:  http://localhost:8080/demo/api/institutionTitles?orgid=jusplogin:shu
  def institutionTitles() {

    def result = [:]
    result.titles = []

    if ( params.orgid ) {
      def name_components = params.orgid.split(':')
      if ( name_components.length == 2 ) {
        // Lookup org by ID
        def orghql = "select org from Org org where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.org = org and id.ns = ns and io.identifier = id and ns.ns = ? and id.value like ? )"
        def orgs = Org.executeQuery(orghql, [name_components[0],name_components[1]])
        if ( orgs.size() == 1 ) {
          def org = orgs[0]

          def today = new Date()

          // Find all TitleInstitutionProvider where institution = org
          def titles = TitleInstitutionProvider.executeQuery('select tip.title.title, tip.title.id, count(cd) from TitleInstitutionProvider as tip left join tip.coreDates as cd where tip.institution = ? and cd.startDate < ? and cd.endDate > ?',
                                                             [org, today, today]);
          titles.each { tip ->
            result.titles.add([title:tip[0], tid:tip[1], isCore:tip[2]]);
          }
        }
        else {
          log.message="Unable to locate Org with ID ${params.orgid}";
        }
      }
      else {
        result.message="Invalid orgid. Format orgid as namespace:value, for example jusplogin:shu"
      }
    }

    render result as JSON
  }
}


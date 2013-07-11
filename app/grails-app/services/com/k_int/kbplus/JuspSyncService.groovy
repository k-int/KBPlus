package com.k_int.kbplus

import org.codehaus.groovy.grails.commons.ApplicationHolder
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.JSON
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset
import org.apache.http.*
import org.apache.http.protocol.*

class JuspSyncService {

  def executorService
  def factService

  def doSync() {
    log.debug("JuspSyncService::doSync");


//     def future = executorService.submit({ internalDoSync() } as java.util.concurrent.Callable)
//     log.debug("doSync returning");
//   }

//   def internalDoSync() {

    def jusp_api = ApplicationHolder.application.config.JuspApiUrl

    if ( ( jusp_api == null ) || ( jusp_api == '' ) ) {
      log.error("JUSP API Not set in config");
      return
    }

    // Select all public packages where there is currently no forumId
    def jusp_api_endpoint = new RESTClient(jusp_api)

    def c = new GregorianCalendar()
    c.add(Calendar.MONTH,-2)

    // Remember months are zero based!
    def most_recent_closed_period = "${c.get(Calendar.YEAR)}-${String.format('%02d',c.get(Calendar.MONTH)+1)}"


    // Select distinct list of Title+Provider (TIPP->Package-CP->ID[jusplogin] with jusp identifiers
    // def q = "select distinct tipp.title, po.org, tipp.pkg from TitleInstancePackagePlatform as tipp " +
    //           "join tipp.pkg.orgs as po where po.roleType.value='Content Provider' " +
    //           "and exists ( select tid from tipp.title.ids as tid where tid.identifier.ns.ns = 'jusp' ) " +
    //           "and exists ( select oid from po.org.ids as oid where oid.identifier.ns.ns = 'juspsid' ) "
    def q = "select distinct ie.tipp.title, po.org, orgrel.org, jusptid from IssueEntitlement as ie " +
              "join ie.tipp.pkg.orgs as po " +
              "join ie.subscription.orgRelations as orgrel "+
              "join ie.tipp.title.ids as jusptid where jusptid.identifier.ns.ns = 'jusp' "+
              "and po.roleType.value='Content Provider' "+
              "and exists ( select oid from po.org.ids as oid where oid.identifier.ns.ns = 'juspsid' ) " +
              "and orgrel.roleType.value = 'Subscriber' " +
              "and exists ( select sid from orgrel.org.ids as sid where sid.identifier.ns.ns = 'jusplogin' ) "

    log.debug("JUSP Sync Task - Running query ${q}");

    def l1 = IssueEntitlement.executeQuery(q)

    l1.each { to ->
      // log.debug("Processing titile/provider pair: ${to[0].title}, ${to[1].name}");
      // def jusp_title_id = to[0].getIdentifierValue('jusp')
      def jusp_supplier_id = to[1].getIdentifierByType('juspsid').value
      def jusp_login = to[2].getIdentifierByType('jusplogin').value
      def jusp_title_id = to[3].identifier.value
      // log.debug(" -> Title jusp id: ${jusp_title_id}");
      // log.debug(" -> Suppllier jusp id: ${jusp_supplier_id}");
      // log.debug(" -> Subscriber jusp id: ${jusp_login}");
      def csr = JuspTripleCursor.findByTitleIdAndSupplierIdAndJuspLogin(jusp_title_id,jusp_supplier_id,jusp_login)
      if ( csr == null ) {
        csr = new JuspTripleCursor(titleId:jusp_title_id,supplierId:jusp_supplier_id,juspLogin:jusp_login,haveUpTo:null)
      }

      if ( ( csr.haveUpTo == null ) || ( csr.haveUpTo < most_recent_closed_period ) ) {
        log.debug("Cursor for ${jusp_title_id}(${to[0].id}):${jusp_supplier_id}(${to[1].id}):${jusp_login}(${to[2].id}) is ${csr.haveUpTo} and is null or < ${most_recent_closed_period}");
        def from_period = csr.haveUpTo ?: '1800-01'
        log.debug("https://www.jusp.mimas.ac.uk/api/v1/Journals/Statistics/?jid=${jusp_title_id}&sid=${jusp_supplier_id}&loginid=${jusp_login}&startrange=${from_period}&endrange=${most_recent_closed_period}&granularity=monthly");
        try {
          jusp_api_endpoint.get( 
                                 path : 'api/v1/Journals/Statistics/',
                                 contentType: JSON,
                                 query: [
                                         jid:jusp_title_id,
                                         sid:jusp_supplier_id,
                                         loginid:jusp_login,
                                         startrange:from_period,
                                         endrange:most_recent_closed_period,
                                         granularity:'monthly'] ) { resp, json ->
            // log.debug("Result: ${resp}, ${json}");
            if ( json ) {
              if ( json.ReportPeriods != null ) {
                log.debug("Report Periods present: ${json.ReportPeriods}");
                json.ReportPeriods.each { p ->
                  def fact = [:]
                  fact.start=p.Start
                  fact.end=p.End
                  p.Reports.each { r ->
                    fact.type = "JUSP:${r.key}"
                    fact.value = r.value
                    fact.facets = [ title:to[0].id, supplier:to[1].id, subscriber:to[2].id ]
                    factService.registerFact(fact);
                    // log.debug("registerFact: ${fact}");
                  }
                }
              }
              else {
                // log.debug("No report periods");
              }
            }
          }
        }
        catch ( Exception e ) {
          log.error("Problem fetching JUSP data: ${e}");
        }
        finally {
        }
      }

      csr.save(flush:true);
    }

 
    
    // Set update date point
    // For each org with a JUSP ID
      // For each title
        // Fetch all data between org last update date
        // Update/create any records
      
    log.debug("internalDoSync exit");
  }
}

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
import java.text.SimpleDateFormat


class JuspSyncService {

  static transactional = false
  def FIXED_THREAD_POOL_SIZE = ApplicationHolder.application.config.juspThreadPoolSize ?: 10
  def executorService
  def factService
  def sessionFactory
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

  static int submitCount=0
  static int completedCount=0
  static int newFactCount=0
  static int totalTime=0
  static int queryTime=0
  static int exceptionCount=0
  static long syncStartTime=0
  static int syncElapsed=0
  static def activityHistogram = [:]

  // Change to static just to be super sure
  static boolean running = false;

  def synchronized doSync() {
    log.debug("JuspSyncService::doSync ${this.hashCode()}");

    if ( this.running == true ) {
      log.debug("Skipping sync.. task already running");
      return
    }

    log.debug("Mark JuspSyncTask as running...");
    this.running = true

    submitCount=0
    completedCount=0
    newFactCount=0
    totalTime=0
    queryTime=0
    syncStartTime=System.currentTimeMillis()
    log.debug("Launch jusp sync at ${syncStartTime} ( ${System.currentTimeMillis()} )");
    syncElapsed=0
    activityHistogram = [:]

    def future = executorService.submit({ internalDoSync() } as java.util.concurrent.Callable)
  }

  def internalDoSync() {

    def ftp = null
    try {
      log.debug("create thread pool");
      ftp = java.util.concurrent.Executors.newFixedThreadPool(FIXED_THREAD_POOL_SIZE)

      def jusp_api = ApplicationHolder.application.config.JuspApiUrl
      if ( ( jusp_api == null ) || ( jusp_api == '' ) ) {
        log.error("JUSP API Not set in config");
        return
      }

      def c = new GregorianCalendar()
      c.add(Calendar.MONTH,-2)

      // Remember months are zero based - hence the +1 in this line!
      def most_recent_closed_period = "${c.get(Calendar.YEAR)}-${String.format('%02d',c.get(Calendar.MONTH)+1)}"

      def start_time = System.currentTimeMillis()

      // Select distinct list of Title+Provider (TIPP->Package-CP->ID[jusplogin] with jusp identifiers
      // def q = "select distinct tipp.title, po.org, tipp.pkg from TitleInstancePackagePlatform as tipp " +
      //           "join tipp.pkg.orgs as po where po.roleType.value='Content Provider' " +
      //           "and exists ( select tid from tipp.title.ids as tid where tid.identifier.ns.ns = 'jusp' ) " +
      //           "and exists ( select oid from po.org.ids as oid where oid.identifier.ns.ns = 'juspsid' ) "
      // def q = "select distinct ie.tipp.title, po.org, orgrel.org, jusptid from IssueEntitlement as ie " +

      // Get a distinct list of titles ids, the content provider, subscribing organisation and the jusp title identifier
      def q = "select distinct ie.tipp.title.id, po.org.id, orgrel.org.id, jusptid.id from IssueEntitlement as ie " +
              "join ie.tipp.pkg.orgs as po " +
              "join ie.subscription.orgRelations as orgrel "+
              "join ie.tipp.title.ids as jusptid where jusptid.identifier.ns.ns = 'jusp' "+
              "and po.roleType.value='Content Provider' "+
              "and exists ( select oid from po.org.ids as oid where oid.identifier.ns.ns = 'juspsid' ) " +
              "and orgrel.roleType.value = 'Subscriber' " +
              "and exists ( select sid from orgrel.org.ids as sid where sid.identifier.ns.ns = 'jusplogin' ) "

      log.debug("JUSP Sync Task - Running query ${q}");

      def l1 = IssueEntitlement.executeQuery(q)

      queryTime = System.currentTimeMillis() - start_time

      log.debug("JUSP Sync query completed....");

      l1.each { to ->
        // log.debug("Submit job ${++submitCount} to fixed thread pool");
        ftp.submit( { processTriple(to[0],to[1],to[2],to[3], most_recent_closed_period) } as java.util.concurrent.Callable )
      }
    }
    catch ( Exception e ) {
      log.error("Problem",e);
    }
    finally {
      log.debug("internalDoSync complete");
      if ( ftp != null ) {
        ftp.shutdown()
        if ( ftp.awaitTermination(6,java.util.concurrent.TimeUnit.HOURS) ) {
          log.debug("FTP cleanly terminated");
        }
        else {
          log.debug("FTP still running.... Calling shutdown now to terminate any outstanding requests");
          ftp.shutdownNow()
        }
      }
      log.debug("Mark JuspSyncTask as not running...");
      this.running = false
    }
  }

  def processTriple(a,b,c,d,most_recent_closed_period) {

    def jusp_api = ApplicationHolder.application.config.JuspApiUrl

    // REST endpoint for JUSP
    def jusp_api_endpoint = new RESTClient(jusp_api)

    def juspTimeStampFormat = new SimpleDateFormat('yyyy-MM')

    def start_time = System.currentTimeMillis();

    Fact.withNewTransaction { status ->

      //log.debug("processTriple");

      def title_inst = TitleInstance.get(a);
      def supplier_inst = Org.get(b);
      def org_inst = Org.get(c);
      def title_io_inst = IdentifierOccurrence.get(d);

      //log.debug("Processing titile/provider/org triple: ${title_inst.title}, ${supplier_inst.name}, ${org_inst.name}");

      def jusp_supplier_id = supplier_inst.getIdentifierByType('juspsid').value
      def jusp_login = org_inst.getIdentifierByType('jusplogin').value
      def jusp_title_id = title_io_inst.identifier.value

      // log.debug(" -> Title jusp id: ${jusp_title_id}");
      // log.debug(" -> Suppllier jusp id: ${jusp_supplier_id}");
      // log.debug(" -> Subscriber jusp id: ${jusp_login}");

      def csr = JuspTripleCursor.findByTitleIdAndSupplierIdAndJuspLogin(jusp_title_id,jusp_supplier_id,jusp_login)
      if ( csr == null ) {
        csr = new JuspTripleCursor(titleId:jusp_title_id,supplierId:jusp_supplier_id,juspLogin:jusp_login,haveUpTo:null)
      }

      if ( ( csr.haveUpTo == null ) || ( csr.haveUpTo < most_recent_closed_period ) ) {
        def from_period = csr.haveUpTo ?: '1800-01'
        //log.debug("Cursor for ${jusp_title_id}(${title_inst.id}):${jusp_supplier_id}(${supplier_inst.id}):${jusp_login}(${org_inst.id}) is ${csr.haveUpTo} and is null or < ${most_recent_closed_period}. Will be requesting data from ${from_period}");
        try {
          //log.debug("Making JUSP API Call");
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

            //log.debug("Got JUSP Result");
            if ( json ) {
              def cal = new GregorianCalendar();
              if ( json.ReportPeriods != null ) {
                if ( json.ReportPeriods.equals(net.sf.json.JSONNull) ) {
                  log.debug("report periods jsonnull");
                }
                else {
                  // log.debug("Report Periods present: ${json.ReportPeriods}");
                  json.ReportPeriods.each { p ->
                    if ( p instanceof net.sf.json.JSONNull ) {
                      // Safely ignore
                    }
                    else {
                      def fact = [:]
                      fact.from=juspTimeStampFormat.parse(p.Start)
                      fact.to=juspTimeStampFormat.parse(p.End)
                      cal.setTime(fact.to)
                      fact.reportingYear=cal.get(Calendar.YEAR)
                      fact.reportingMonth=cal.get(Calendar.MONTH)+1
                      p.Reports.each { r ->
                        fact.type = "JUSP:${r.key}"
                        fact.value = r.value
                        fact.uid = "${jusp_title_id}:${jusp_supplier_id}:${jusp_login}:${p.End}:${r.key}"
                        fact.title = title_inst
                        fact.supplier = supplier_inst
                        fact.inst =  org_inst
                        fact.juspio =  title_io_inst
                        if ( factService.registerFact(fact) ) {
                          ++newFactCount
                        }
                      }
                    }
                  }
                }
              }
              else {
                // log.debug("No report periods");
              }
            }
          }
          // log.debug("Update csr");
          csr.haveUpTo=most_recent_closed_period
          csr.save(flush:true);
        }
        catch ( Exception e ) {
          log.error("Problem fetching JUSP data",e);
          log.error("URL giving error(${e.message}): https://www.jusp.mimas.ac.uk/api/v1/Journals/Statistics/?jid=${jusp_title_id}&sid=${jusp_supplier_id}&loginid=${jusp_login}&startrange=${from_period}&endrange=${most_recent_closed_period}&granularity=monthly");
          exceptionCount++
        }
        finally {
        }
      }

      csr.save(flush:true);
      cleanUpGorm();
      def elapsed = System.currentTimeMillis() - start_time;
      totalTime+=elapsed
      incrementActivityHistogram();
      // log.debug("jusp triple completed and updated.. ${completedCount} tasks completed out of ${submitCount}. Elasped=${elapsed}. Average=${totalTime/completedCount}");
    }
  }


  def cleanUpGorm() {
    // log.debug("Clean up GORM");
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }

  public static synchronized void incrementActivityHistogram() {
    def sdf = new SimpleDateFormat('yyyy/MM/dd HH:mm')
    def col_identifier = sdf.format(new Date())

    completedCount++

    if ( activityHistogram[col_identifier] == null ) {
      activityHistogram[col_identifier] = new Long(1)
    }
    else {
      activityHistogram[col_identifier]++
    }

    syncElapsed = System.currentTimeMillis() - syncStartTime
  }


}

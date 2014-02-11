package com.k_int.kbplus

import com.k_int.goai.OaiClient
import java.text.SimpleDateFormat

class GlobalSourceSyncService {

  def executorService

  def runAllActiveSyncTasks() {
    // def future = executorService.submit({ internalRunAllActiveSyncTasks() } as java.util.concurrent.Callable)
    internalRunAllActiveSyncTasks()
  }

  def internalRunAllActiveSyncTasks() {

     log.debug("Batch job running...");

     def jobs = GlobalRecordSource.findAll() 
     jobs.each { sync_job ->
       log.debug(sync_job);
       // String identifier
       // String name
       // String type
       // Date haveUpTo
       // String uri
       // String listPrefix
       // String fullPrefix
       // String principal
       // String credentials
       switch ( sync_job.type ) {
         case 'OAI':
           doOAISync(sync_job)
           break;
         default:
           log.error("Unhandled sync job type: ${sync_job.type}");
           break;
       }
     }
  }

  def private doOAISync(sync_job) {
    def future = executorService.submit({ internalOAISync(sync_job.id) } as java.util.concurrent.Callable)
  }
 
  def internalOAISync(sync_job_id) {
    def sync_job = GlobalRecordSource.get(sync_job_id)
    def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    println("doOAISync(${sync_job})");
    try {
      def date = new Date()
      def oai_client = new OaiClient(host:sync_job.uri)
      def max_timestamp = 0
      oai_client.getChangesSince(date, 'oai_dc') { rec ->
        log.debug("Processing a record ${rec}");
        log.debug(rec.header.identifier)
        log.debug(rec.header.datestamp)
        def qryparams = [sync_job.id, rec.header.identifier.text()]
        def record_timestamp = sdf.parse(rec.header.datestamp.text())
        log.debug("Find: ${qryparams}");
        def existing_record_info = GlobalRecordInfo.executeQuery('select r from GlobalRecordInfo as r where r.source.id = ? and r.identifier = ?',qryparams);
        if ( existing_record_info == 1 ) {
          log.debug("dbg found");
        }
        else {
          log.debug("dbg not found");
          // Because we don't know about this record, we can't possibly be already tracking it. Just create a local tracking record.
          existing_record_info = new GlobalRecordInfo(
                                                      ts:record_timestamp,
                                                      identifier:rec.header.identifier.text(), 
                                                      source: sync_job).save()
        }
	log.debug("--");
      }
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }

    // sync_job.maxTimestamp=oai_client.maxTimestamp
    // save
  }

  def parseDate(datestr, possible_formats) {
    def parsed_date = null;
    if ( datestr && ( datestr.toString().trim().length() > 0 ) ) {
      for(Iterator i = possible_formats.iterator(); ( i.hasNext() && ( parsed_date == null ) ); ) {
        try {
          parsed_date = i.next().parse(datestr.toString());
        }
        catch ( Exception e ) {
        }
      }
    }
    parsed_date
  }

}

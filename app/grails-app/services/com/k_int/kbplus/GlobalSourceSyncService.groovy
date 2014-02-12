package com.k_int.kbplus

import com.k_int.goai.OaiClient
import java.text.SimpleDateFormat

class GlobalSourceSyncService {

  def static rectypes = [
    [ name:'Package', method:'syncPackage' ]
  ]

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
           log.debug("start internal sync");
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
    log.debug("internalOAISync ${sync_job} records from ${sync_job.uri} since ${sync_job.haveUpTo} using oai_dc");
    def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    try {
      def date = sync_job.haveUpTo ?: new Date(0)
      def oai_client = new OaiClient(host:sync_job.uri)
      def max_timestamp = 0
      log.debug("Collect changes since ${date}");
      oai_client.getChangesSince(date, 'oai_dc') { rec ->
        log.debug("Processing a record ${rec}");
        log.debug(rec.header.identifier)
        log.debug(rec.header.datestamp)
        log.debug("metadata: "+rec.metadata.dc.text())
        log.debug("title: "+rec.metadata.dc.title)
        def qryparams = [sync_job.id, rec.header.identifier.text()]
        def record_timestamp = sdf.parse(rec.header.datestamp.text())
        log.debug("Find: ${qryparams}");
        def existing_record_info = GlobalRecordInfo.executeQuery('select r from GlobalRecordInfo as r where r.source.id = ? and r.identifier = ?',qryparams);
        if ( existing_record_info.size() == 1 ) {
          log.debug("dbg found");
        }
        else {
          log.debug("dbg not found");
          // Because we don't know about this record, we can't possibly be already tracking it. Just create a local tracking record.
          existing_record_info = new GlobalRecordInfo(
                                                      ts:record_timestamp,
                                                      name:rec.metadata.dc.title.text(),
                                                      identifier:rec.header.identifier.text(), 
                                                      source: sync_job,
                                                      rectype:0).save()
        }
        if ( record_timestamp.getTime() > max_timestamp ) {
          max_timestamp = record_timestamp.getTime()
          log.debug("Max timestamp is now ${record_timestamp}");
        }
	log.debug("--");
      }

      log.debug("Updating sync job max timestamp");
      sync_job.haveUpTo=new Date(max_timestamp)
      sync_job.save();
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }

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

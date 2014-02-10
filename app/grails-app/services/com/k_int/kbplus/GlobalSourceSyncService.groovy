package com.k_int.kbplus

import com.k_int.goai.OaiClient

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
    def future = executorService.submit({ internalOAISync(sync_job) } as java.util.concurrent.Callable)
  }
 
  def internalOAISync(sync_job) {
    println("doOAISync(${sync_job})");
    def date = new Date()
    def oai_client = new OaiClient(host:sync_job.uri)
    oai_client.getChangesSince(date, 'oai_dc') {
      println("Processing a record");
    }

    // sync_job.maxTimestamp=oai_client.maxTimestamp
    // save
  }
}

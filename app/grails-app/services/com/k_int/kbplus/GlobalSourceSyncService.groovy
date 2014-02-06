package com.k_int.kbplus

class GlobalSourceSyncService {

  def executorService

  def runAllActiveSyncTasks() {
    def future = executorService.submit({ internalRunAllActiveSyncTasks() } as java.util.concurrent.Callable)
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
    println("doOAISync");

  }
}

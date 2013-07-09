package com.k_int.kbplus

class JuspSyncService {

  def executorService

  def doSync() {
    log.debug("JuspSyncService::doSync");
    def future = executorService.submit({ internalDoSync() } as java.util.concurrent.Callable)
    log.debug("doSync returning");
  }

  def internalDoSync() {
    log.debug("JUSP Sync Task");
    // Set update date point
    // For each org with a JUSP ID
      // For each title
        // Fetch all data between org last update date
        // Update/create any records
      
  }
}

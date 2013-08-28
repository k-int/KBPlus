package com.k_int.kbplus

class AppinfoController {

  def zenDeskSyncService
  def juspSyncService

  def index() { 

    def result = [:]

    result.juspSyncService=[:]
    result.juspSyncService.running=juspSyncService.running

    result.juspSyncService.submitCount=juspSyncService.submitCount
    result.juspSyncService.completedCount=juspSyncService.completedCount
    result.juspSyncService.newFactCount=juspSyncService.newFactCount
    result.juspSyncService.totalTime=juspSyncService.totalTime
    result.juspSyncService.threads=juspSyncService.FIXED_THREAD_POOL_SIZE
    result.juspSyncService.queryTime=juspSyncService.queryTime


    result;
  }
}

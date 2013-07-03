package com.k_int.kbplus.batch



class JuspSyncJob {

  def juspSyncService

  static triggers = {
    // Delay 20 seconds, run every 10 mins.
    // Cron:: Min Hour DayOfMonth Month DayOfWeek Year
    // Example - every 10 mins 0 0/10 * * * ? 
    // At 5 past 2am on the first of every month - Sync JUSP Stats
    cron name:'cronTrigger', startDelay:20000, cronExpression: "5 2 1 * * ?"
  }

  def execute() {
    log.debug("JuspSyncJob");
      juspSyncService.doSync() {
  }

}

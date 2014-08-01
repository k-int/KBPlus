package com.k_int.kbplus.batch

import org.codehaus.groovy.grails.commons.ApplicationHolder

class GlobalDataSyncJob {

  def globalSourceSyncService

  static triggers = {
    // Delay 20 seconds, run every 10 mins.
    // Cron:: Min Hour DayOfMonth Month DayOfWeek Year
    // Example - every 10 mins 0 0/10 * * * ? 
    // At 5 past 5am every day
    cron name:'globalDataSyncTrigger', startDelay:20000, cronExpression: "0 5 5 * * ?"
    // cronExpression: "s m h D M W Y"
    //                  | | | | | | `- Year [optional]
    //                  | | | | | `- Day of Week, 1-7 or SUN-SAT, ?
    //                  | | | | `- Month, 1-12 or JAN-DEC
    //                  | | | `- Day of Month, 1-31, ?
    //                  | | `- Hour, 0-23
    //                  | `- Minute, 0-59
    //                  `- Second, 0-59
  }

  def execute() {
    log.debug("GlobalDataSyncJob");
    if ( ApplicationHolder.application.config.KBPlusMaster == true ) {
      log.debug("This server is marked as KBPlus master. Running GlobalDataSyncJob batch job");
      globalSourceSyncService.runAllActiveSyncTasks()
    }
    else {
      log.debug("This server is NOT marked as KBPlus master. NOT Running GlobalDataSyncJob SYNC batch job");
    }
  }

}

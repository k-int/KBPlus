package com.k_int.kbplus.batch

import org.codehaus.groovy.grails.commons.ApplicationHolder



class NotificationsJob {

  def zenDeskSyncService
  def changeNotificationService

  static triggers = {
    // Delay 20 seconds, run every 10 mins.
    // Cron:: Min Hour DayOfMonth Month DayOfWeek Year
    // Example - every 10 mins 0 0/10 * * * ? 
    // At 5 past 2am every day...
    cron name:'notificationsTrigger', startDelay:20000, cronExpression: "5 2 * * * ?"
  }

  def execute() {
    log.debug("NotificationsJob");
    if ( ApplicationHolder.application.config.KBPlusMaster == true ) {
      log.debug("This server is marked as KBPlus master. Running ZENDESK sync batch job");
      zenDeskSyncService.doSync()
      changeNotificationService.aggregateAndNotifyChanges();
    }
    else {
      log.debug("This server is NOT marked as KBPlus master. NOT Running ZENDESK SYNC batch job");
    }
  }

}

package com.k_int.kbplus.batch



class NotificationsJob {

  def zenDeskSyncService

  static triggers = {
    // Delay 20 seconds, run every 10 mins.
    // Cron:: Min Hour DayOfMonth Month DayOfWeek Year
    // Example - every 10 mins 0 0/10 * * * ? 
    // At 5 past 2am every day...
    cron name:'notificationsTrigger', startDelay:20000, cronExpression: "5 2 * * * ?"
  }

  def execute() {
    log.debug("NotificationsJob");
    zenDeskSyncService.doSync()
  }

}

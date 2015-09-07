package com.k_int.kbplus.batch

class IndexUpdateJob {

  def dataloadService

  static triggers = {
    // Delay 120 seconds, run every 10 mins.
    cron name:'cronTrigger', startDelay:190000, cronExpression: "0 0/10 * * * ?"
  }

  def execute() {
    log.debug("****Running Index Update Job****")

    dataloadService.doFTUpdate()
  }
}

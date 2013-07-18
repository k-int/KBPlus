package demo



class IndexUpdateJob {

  def mongoService 
  def ESWrapperService
  def dataloadService

  static triggers = {
    // Delay 20 seconds, run every 10 mins.
    cron name:'cronTrigger', startDelay:20000, cronExpression: "0 0/10 * * * ?"
  }

  def execute() {
    dataloadService.doFTUpdate()
  }
}

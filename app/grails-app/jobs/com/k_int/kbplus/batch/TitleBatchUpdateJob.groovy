package com.k_int.kbplus.batch

import com.k_int.kbplus.TitleInstance
import groovy.time.TimeCategory
import org.hibernate.ScrollMode

/*
* This job is only run once on system startup, and is responsible for generating sort titles on TitleInstance
*/
class TitleBatchUpdateJob {


  static triggers = {
  	simple name:'TitleBatchUpdateJob', startDelay:20000, repeatInterval:30000, repeatCount:0  
  }

  def execute() {
  	def event = "TitleBatchUpdateJob"
  	def startTime = printStart(event)
  	def counter = 0
    TitleInstance.withSession { session ->
       def scroll_res = session.createCriteria(TitleInstance).scroll(ScrollMode.FORWARD_ONLY)
       while (scroll_res.next()) {
          def title = scroll_res.get(0)
          updateTitle(title)
          counter ++ 
          if(counter == 500){
          	cleanUpGorm(session)
          	counter = 0
          }
       }
    }
    printDuration(startTime,event)
  }

  def cleanUpGorm(session) {
    log.debug("Batch of titles, clean up GORM");
    session.flush()
    session.clear()
  }

  def updateTitle(title){
  	// Instead of trigger update, do this, as if nothing changes Hibernate will not bother saving.
    TitleInstance.generateNormTitle(title.title)
    TitleInstance.generateKeyTitle(title.title)
    TitleInstance.generateSortTitle(title.title)
  	title.save()
  }
	
   def printStart(event){
   		def starttime = new Date();
   		log.debug("******* Start ${event}: ${starttime} *******")
   		return starttime
   }

	def printDuration(starttime, event){
		use(groovy.time.TimeCategory) {
			def duration = new Date() - starttime
			log.debug("******* End ${event}: ${new Date()} *******")
			log.debug("Duration: ${(duration.hours*60)+duration.minutes}m ${duration.seconds}s")
		}
	}

}


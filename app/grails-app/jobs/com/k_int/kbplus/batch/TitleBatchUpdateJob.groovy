package com.k_int.kbplus.batch

import com.k_int.kbplus.TitleInstance
import groovy.time.TimeCategory
import org.hibernate.ScrollMode

/*
* This job is only run once on system startup, and is responsible for generating sort titles on TitleInstance
*/
class TitleBatchUpdateJob {


  static triggers = {
  	simple name:'TitleBatchUpdateJob', startDelay:120000, repeatInterval:30000, repeatCount:0  
  }

  def execute() {
  	def event = "TitleBatchUpdateJob"
  	def startTime = printStart(event)
  	def counter = 0
    try{
      TitleInstance.auditable = false
      TitleInstance.withSession { session ->
         def scroll_res = session.createCriteria(TitleInstance).scroll(ScrollMode.FORWARD_ONLY)
         while (scroll_res.next()) {
            def title = scroll_res.get(0)
            if(updateTitle(title)) {
              counter ++ ;
            }
            if(counter == 500){
            	cleanUpGorm(session)
            	counter = 0
            }
         }
         cleanUpGorm(session)
      }
      printDuration(startTime,event)
    }catch( Exception e ) {
      log.error(e)
    }finally{
      //always reset the status
      TitleInstance.auditable = false
    }

  }

  def cleanUpGorm(session) {
    log.debug("Batch of titles, clean up GORM");
    session.flush()
    session.clear()
  }

  def updateTitle(title){
  	// Instead of trigger update, do this, as if nothing changes Hibernate will not bother saving.
    if(title.sortTitle) return null;
    title.normTitle = TitleInstance.generateNormTitle(title.title)
    title.keyTitle  = TitleInstance.generateKeyTitle(title.title)
    title.sortTitle = TitleInstance.generateSortTitle(title.title)
  	title.save()
    return 1
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


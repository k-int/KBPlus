package com.k_int.kbplus.batch

import com.k_int.kbplus.Package
import groovy.time.TimeCategory
import org.hibernate.ScrollMode

/*
* This job is only run once on system startup, and is responsible for generating sort names on Package
*/
class PackageBatchUpdateJob {


  static triggers = {
  	simple name:'PackageBatchUpdateJob', startDelay:60000, repeatInterval:30000, repeatCount:0  
  }

  def execute() {
  	def event = "PackageBatchUpdateJob"
  	def startTime = printStart(event)
  	def counter = 0
    try{
      Package.auditable = false
      Package.withSession { session ->
         def scroll_res = session.createCriteria(Package).scroll(ScrollMode.FORWARD_ONLY)
         while (scroll_res.next()) {
            def pkg = scroll_res.get(0)
            if(updatePackage(pkg)){
              counter ++ 
            }
            if(counter == 500){
            	cleanUpGorm(session)
            	counter = 0
            }
         }
         cleanUpGorm(session)
      }
      printDuration(startTime,event)

    }catch( Exception e ) {log.error(e)}
    finally{
      Package.auditable=true
    }
  }

  def cleanUpGorm(session) {
    log.debug("Batch of packages, clean up GORM");
    session.flush()
    session.clear()
  }

  def updatePackage(pkg){
  	// Instead of trigger update, do this, as if nothing changes Hibernate will not bother saving.
     if(pkg.sortName) return null;
     pkg.sortName = Package.generateSortName(pkg.name)
     pkg.save()
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


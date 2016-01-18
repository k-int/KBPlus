package com.k_int.kbplus.batch
import com.k_int.kbplus.TitleInstance
import com.k_int.kbplus.Package
import groovy.time.TimeCategory
import org.hibernate.ScrollMode

/*
* This job is only run once on system startup, and is responsible for initializing various fields
* First run on unpopulated data takes up to 45min, then only few seconds if run again
*/
class BatchTouchJob {


  static triggers = {
    simple name:'BatchTouchJob', startDelay:50000, repeatInterval:30000, repeatCount:0  
  }

  def execute() {
    log.debug("BatchTouchJob::execute");

    //The following will only make changes to objects when required. If fields are populated they will skip
    //Make sure all classes have impIDs, as they are the key used for ES
    impIdJob();
    //Make sure all packages have sort name, again used by ES 
    pkgBatchUpdate()
    //Generate norm,sort,and key title for TitleInstances,used by ES and app sorting.
    titleBatchUpdate()
  }

  def titleBatchUpdate() {

    log.debug("BatchTouchJob::titleBatchUpdate");

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

  def updateTitle(title){
    // Instead of trigger update, do this, as if nothing changes Hibernate will not bother saving.
    if(title.sortTitle) return null;
    title.normTitle = TitleInstance.generateNormTitle(title.title)
    title.keyTitle  = TitleInstance.generateKeyTitle(title.title)
    title.sortTitle = TitleInstance.generateSortTitle(title.title)
    title.save()
    return 1
  }

  def impIdJob(){
    log.debug("BatchTouchJob::impIdJob");
    def event = "BatchImpIdJob"
    def startTime = printStart(event)
    def counter = 0
    def classList = [com.k_int.kbplus.Package,com.k_int.kbplus.Org,com.k_int.kbplus.License,com.k_int.kbplus.Subscription,com.k_int.kbplus.Platform,com.k_int.kbplus.TitleInstance] 
    classList.each{ currentClass ->
      def auditable_store = null
      try{
        if(currentClass.hasProperty('auditable'))  {
          auditable_store = currentClass.auditable
          currentClass.auditable = false ;
        }
        currentClass.withSession { session ->
           def scroll_res = session.createCriteria(currentClass).scroll(ScrollMode.FORWARD_ONLY)
           while (scroll_res.next()) {
              def obj = scroll_res.get(0)
              if(updateObject(obj)){
                counter ++ 
              }
              if(counter == 500){
                cleanUpGorm(session)
                counter = 0
              }
           }
           cleanUpGorm(session)
        }

      }catch( Exception e ) {log.error(e)}
      finally{
        if(currentClass.hasProperty('auditable')) currentClass.auditable = auditable_store?:true ;
      }
      
    }
    printDuration(startTime,event)
  }

  def pkgBatchUpdate() {
    log.debug("BatchTouchJob::pkgBatchUpdate");
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


  def updatePackage(pkg){
    // Instead of trigger update, do this, as if nothing changes Hibernate will not bother saving.
     if(pkg.sortName) return null;
     pkg.sortName = Package.generateSortName(pkg.name)
     pkg.save()
     return 1

  }
  def cleanUpGorm(session) {
    log.debug("clean up GORM");
    session.flush()
    session.clear()
  }

  def updateObject(obj){
    if(obj.impId) return null;
    obj.impId = java.util.UUID.randomUUID().toString();
    obj.save()
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

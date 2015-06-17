package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.web.JSONBuilder
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent
import com.k_int.kbplus.auth.User
import static java.util.concurrent.TimeUnit.*
import static grails.async.Promises.*


class DataManagerController {

  def springSecurityService 

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def result =[:]
    def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")

    result.pendingChanges = PendingChange.executeQuery("select pc from PendingChange as pc where pc.pkg is not null and ( pc.status is null or pc.status = ? ) order by ts desc", [pending_change_pending_status]);

    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def changeLog() { 

    def result =[:]
    log.debug("changeLog ${params}");
    def formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")

    def exporting = params.format == 'csv' ? true : false

    if ( exporting ) {
      result.max = 10000
      params.max = 10000
      result.offset = 0
    }
    else {
      def user = User.get(springSecurityService.principal.id)
      result.max = params.max ? Integer.parseInt(params.max) : user.defaultPageSize
      params.max = result.max
      result.offset = params.offset ? Integer.parseInt(params.offset) : 0;
    }

    if ( params.startDate == null ) {
      def cal = new java.util.GregorianCalendar()
      cal.setTimeInMillis(System.currentTimeMillis())
      cal.set(Calendar.DAY_OF_MONTH,1)
      params.startDate=formatter.format(cal.getTime())
    }
    if ( params.endDate == null ) { params.endDate = formatter.format(new Date()) }
    if ( ( params.creates == null ) && ( params.updates == null ) ) {
      params.creates='Y'
    }
    if(params.startDate > params.endDate){
      flash.error = "From Date cannot be after To Date."
      return
    }
    def base_query = "from org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent as e where e.className in (:l) AND e.lastUpdated >= :s AND e.lastUpdated <= :e AND e.eventName in (:t)"

    def types_to_include = []
    if ( params.packages=="Y" ) types_to_include.add('com.k_int.kbplus.Package');
    if ( params.licenses=="Y" ) types_to_include.add('com.k_int.kbplus.License');
    if ( params.titles=="Y" ) types_to_include.add('com.k_int.kbplus.TitleInstance');
    if ( params.tipps=="Y" ) types_to_include.add('com.k_int.kbplus.TitleInstancePackagePlatform');
    // com.k_int.kbplus.Subscription                 |
    // com.k_int.kbplus.IdentifierOccurrence         |

    def events_to_include=[]
    if ( params.creates=="Y" ) events_to_include.add('INSERT');
    if ( params.updates=="Y" ) events_to_include.add('UPDATE');
    
    result.actors = []
    def actors_dms = []
    def actors_users = []
    def all_types = [ 'com.k_int.kbplus.Package','com.k_int.kbplus.License','com.k_int.kbplus.TitleInstance','com.k_int.kbplus.TitleInstancePackagePlatform' ]
    def auditActors = AuditLogEvent.executeQuery('select distinct(al.actor) from AuditLogEvent as al where al.className in ( :l  )',[l:all_types])
    def formal_role = com.k_int.kbplus.auth.Role.findByAuthority('INST_ADM')
    def rolesMa = com.k_int.kbplus.auth.UserOrg.executeQuery("select distinct(userorg.user.username) from UserOrg as userorg where userorg.formalRole = (:formal_role) and userorg.user.username in (:actors)",[formal_role:formal_role,actors:auditActors])
    auditActors.each {
      def u = User.findByUsername(it)
      
      if ( u != null ) {
        if(rolesMa.contains(it)){
          actors_dms.add([it, u.displayName]) 
        }else{
          actors_users.add([it, u.displayName]) 
        }
      }
    }
    actors_dms.sort{it[1]}
    actors_users.sort{it[1]}

   result.actors = actors_dms.plus(actors_users)

    log.debug("${params}");
    if ( types_to_include.size() == 0 ) {
      types_to_include.add('com.k_int.kbplus.Package')
      params.packages="Y"
    }

    def start_date = formatter.parse(params.startDate)
    def end_date = formatter.parse(params.endDate)

    final long hoursInMillis = 60L * 60L * 1000L;
    end_date = new Date(end_date.getTime() + (24L * hoursInMillis - 2000L)); 

    def query_params = ['l':types_to_include,'s':start_date,'e':end_date, 't':events_to_include]
   

    def filterActors = params.findAll{it.key.startsWith("change_actor_")}
    if(filterActors) {
      def multipleActors = false;
      def condition = "AND ( "
      filterActors.each{        
          if(multipleActors){
            condition = "OR"
          }
          if ( it == "change_actor_PEOPLE" ) {
            base_query += " ${condition} e.actor <> \'system\' AND e.actor <> \'anonymousUser\' "
            multipleActors = true
          }
          else if(it.key != 'change_actor_ALL' && it.key != 'change_actor_PEOPLE'){
            def paramKey = it.key.replaceAll("[^A-Za-z]", "")//remove things that can cause problems in sql
            base_query += " ${condition} e.actor = :${paramKey} "
            query_params."${paramKey}" = it.key.split("change_actor_")[1]
            multipleActors = true
          }     
      } 
      base_query += " ) "  
    }
  
  

    if ( types_to_include.size() > 0 ) {
  
      def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[max:result.max,offset:0]
      result.historyLines = AuditLogEvent.executeQuery('select e '+base_query+' order by e.lastUpdated desc', 
                                                       query_params, limits);
      result.num_hl = AuditLogEvent.executeQuery('select count(e) '+base_query,
                                                 query_params)[0];
      result.formattedHistoryLines = []
      result.historyLines.each { hl ->
  
        def line_to_add = [:]
        def linetype = null
  
        switch(hl.className) {
          case 'com.k_int.kbplus.License':
            def license_object = License.get(hl.persistedObjectId);
            def licence_name = license_object.licenseType ?"${license_object.licenseType}: ": ""
            licence_name += license_object.reference != null?license_object.reference:"**No reference**"
            line_to_add = [ link: createLink(controller:'licenseDetails', action: 'index', id:hl.persistedObjectId),
                            name: licence_name,
                            lastUpdated: hl.lastUpdated,
                            actor: User.findByUsername(hl.actor), 
                            propertyName: hl.propertyName,
                            oldValue: hl.oldValue,
                            newValue: hl.newValue
                          ]
            linetype = 'Licence'
            break;
          case 'com.k_int.kbplus.Subscription':
            break;
          case 'com.k_int.kbplus.Package':
            def package_object = Package.get(hl.persistedObjectId);
            line_to_add = [ link: createLink(controller:'packageDetails', action: 'show', id:hl.persistedObjectId),
                            name: package_object.name,
                            lastUpdated: hl.lastUpdated,
                            propertyName: hl.propertyName,
                            actor: User.findByUsername(hl.actor),
                            oldValue: hl.oldValue,
                            newValue: hl.newValue
                          ]
            linetype = 'Package'
            break;
          case 'com.k_int.kbplus.TitleInstancePackagePlatform':
            def tipp_object = TitleInstancePackagePlatform.get(hl.persistedObjectId);
            if ( tipp_object != null ) {
              line_to_add = [ link: createLink(controller:'tipp', action: 'show', id:hl.persistedObjectId),
                              name: tipp_object.title?.title + " / "+tipp_object.pkg?.name,
                              lastUpdated: hl.lastUpdated,
                              propertyName: hl.propertyName,
                              actor: User.findByUsername(hl.actor),
                              oldValue: hl.oldValue,
                              newValue: hl.newValue
                            ]
              linetype = 'TIPP'
            }
            else {
              log.debug("Cleaning up history line that relates to a deleted item");
              hl.delete(); 
            }
            break;
          case 'com.k_int.kbplus.TitleInstance':
            def title_object = TitleInstance.get(hl.persistedObjectId);
            line_to_add = [ link: createLink(controller:'titleDetails', action: 'show', id:hl.persistedObjectId),
                            name: title_object.title,
                            lastUpdated: hl.lastUpdated,
                            propertyName: hl.propertyName,
                            actor: User.findByUsername(hl.actor),
                            oldValue: hl.oldValue,
                            newValue: hl.newValue
                          ]
            linetype = 'Title'
            break;
          case 'com.k_int.kbplus.IdentifierOccurrence':
            break;
          default:
            log.error("Unexpected event class name found ${hl.className}")
            break;
        }

        switch ( hl.eventName ) {
          case 'INSERT':
            line_to_add.eventName= "New ${linetype}"
            break;
          case 'UPDATE':
            line_to_add.eventName= "Updated ${linetype}"
            break;
          case 'DELETE':
            line_to_add.eventName= "Deleted ${linetype}"
            break;
          default:
            line_to_add.eventName= "Unknown ${linetype}"
            break;
        }

        if(line_to_add.eventName.contains('null')){
          log.error("We have a null line in DM change log and we exclude it from output...${hl}");
        }else{
          result.formattedHistoryLines.add(line_to_add);
        }
      }
  
    }
    else {
      result.num_hl = 0
    }


    withFormat {
      html {
        result
      }
      csv {
        if(result.formattedHistoryLines.size() == 10000 ){
          //show some error somehow
        }
        response.setHeader("Content-disposition", "attachment; filename=DMChangeLog.csv")
        response.contentType = "text/csv"
        def out = response.outputStream
        def actors_list = getActorNameList(params)
        out.withWriter { w ->
        w.write('Start Date, End Date, Change Actors, Packages, Licences, Tittles, TIPPs, New Items, Updates\n')
        w.write("\"${params.startDate}\", \"${params.endDate}\", \"${actors_list}\", ${params.packages}, ${params.licenses}, ${params.titles}, ${params.tipps}, ${params.creates}, ${params.updates} \n")
        w.write('Timestamp,Name,Event,Property,Actor,Old,New,Link\n')
          result.formattedHistoryLines.each { c ->
            if(c.eventName){
            def line = "\"${c.lastUpdated}\",\"${c.name}\",\"${c.eventName}\",\"${c.propertyName}\",\"${c.actor?.displayName}\",\"${c.oldValue}\",\"${c.newValue}\",\"${c.link}\"\n"
            w.write(line)
              
            }
          }
        }
        out.close()
      }
    }
  }

  def getActorNameList(params) {
    def actors = []
    def filterActors = params.findAll{it.key.startsWith("change_actor_")}
    if(filterActors) {

      if ( params.change_actor_PEOPLE == 'Y' ) {
        actors += "All Real Users"
      }
      if(params.change_actor_ALL == "Y"){
        actors += "All Including System"
      }
      filterActors.each{      
          if(it.key != 'change_actor_ALL' && it.key != 'change_actor_PEOPLE'){
            def paramKey = it.key.replaceAll("[^A-Za-z]", "")//remove things that can cause problems in sql
            def username = it.key.split("change_actor_")[1]
            def user = User.findByUsername(username)
            if(user){
              actors += user.displayName
              
            }
          }     
      }     
    }
    return actors
  }

  @Secured(['ROLE_ADMIN', 'KBPLUS_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def deletedTitleManagement() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)

    result.max = params.max ? Integer.parseInt(params.max) : result.user.defaultPageSize;

    def paginate_after = params.paginate_after ?: ( (2*result.max)-1);
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    def deleted_title_status =  RefdataCategory.lookupOrCreate( 'Title Status', 'Deleted' );
    def qry_params = [deleted_title_status]

    def base_qry = " from TitleInstance as t where ( t.status = ? )"

    result.titleInstanceTotal = Subscription.executeQuery("select count(t) "+base_qry, qry_params )[0]

    result.titleList = Subscription.executeQuery("select t ${base_qry}", qry_params, [max:result.max, offset:result.offset]);

    result
  }

  @Secured(['ROLE_ADMIN', 'KBPLUS_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def expungeDeletedTitles() {

    log.debug("expungeDeletedTitles.. Create async task..");

    def p = TitleInstance.async.task {
      def l = TitleInstance.executeQuery('select t.id from TitleInstance t where t.status.value=?',['Deleted']);
      def ctr = 0;
      l.each { ti_id -> 
        TitleInstance.withNewTransaction {
          log.debug("Expunging title [${ctr++}] ${ti_id}");
          TitleInstance.expunge(ti_id);
        }
      }
      return "expungeDeletedTitles Completed - ${ctr} titles expunged"
    }

    log.debug("Got promise : ${p}. ${p.class.name}");

    p.onError { Throwable err ->
	log.debug("An error occured ${err.message}")
    }

    p.onComplete { result ->
        log.debug("Promise returned $result")
    }

    log.debug("expungeDeletedTitles.. Returning");

    redirect(controller:'home')
  }
  
}


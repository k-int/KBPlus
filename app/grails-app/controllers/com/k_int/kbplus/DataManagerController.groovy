package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.web.JSONBuilder
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent
import com.k_int.kbplus.auth.User

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
    def all_types = [ 'com.k_int.kbplus.Package','com.k_int.kbplus.License','com.k_int.kbplus.TitleInstance','com.k_int.kbplus.TitleInstancePackagePlatform' ]
    AuditLogEvent.executeQuery('select distinct(al.actor) from AuditLogEvent as al where al.className in ( :l  ) order by al.actor',[l:all_types]).each {
      def u = User.findByUsername(it)
      if ( u != null ) {
        result.actors.add([it,u.displayName]);
      }
    }

   result.actors.sort{it[1]}

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
      def condition = "and"

      if ( params.change_actor_PEOPLE == 'Y' ) {
        base_query += " ${condition} ( e.actor <> \'system\' AND e.actor <> \'anonymousUser\' )"
        multipleActors = true
      }
      filterActors.each{
          if(multipleActors){
            condition = "or"
          }
          if(it.key != 'change_actor_ALL' && it.key != 'change_actor_PEOPLE'){
            def paramKey = it.key.replaceAll("[^A-Za-z]", "")//remove things that can cause problems in sql
            base_query += " ${condition} e.actor = :${paramKey} "
            query_params."${paramKey}" = it.key.split("change_actor_")[1]
            multipleActors = true
          }     
      }     
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
            line_to_add = [ link: createLink(controller:'licenseDetails', action: 'index', id:hl.persistedObjectId),
                            name: license_object.reference != null?license_object.reference:"**No reference**",
                            lastUpdated: hl.lastUpdated,
                            actor: User.findByUsername(hl.actor), 
                            propertyName: hl.propertyName,
                            oldValue: hl.oldValue,
                            newValue: hl.newValue
                          ]
            linetype = 'License'
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
        result.formattedHistoryLines.add(line_to_add);
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
        out.withWriter { w ->
        w.write('Timestamp,Name,Event,Property,Actor,Old,New,Link\n')
          result.formattedHistoryLines.each { c ->
            def line = "\"${c.lastUpdated}\",\"${c.name}\",\"${c.eventName}\",\"${c.propertyName}\",\"${c.actor?.displayName}\",\"${c.oldValue}\",\"${c.newValue}\",\"${c.link}\"\n"
            w.write(line)
          }
        }
        out.close()
      }

    }

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
}


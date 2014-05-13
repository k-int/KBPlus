package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.web.JSONBuilder
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent
import com.k_int.kbplus.auth.User

class DataManagerController {

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

    result.max = params.max ? Integer.parseInt(params.max) : 25
    params.max = result.max
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

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
    


    log.debug("${params}");
    if ( types_to_include.size() == 0 ) {
      types_to_include.add('com.k_int.kbplus.Package')
      params.packages="Y"
    }

    if ( types_to_include.size() > 0 ) {
  
      def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]
      def start_date = formatter.parse(params.startDate)
      def end_date = formatter.parse(params.endDate)
  
      result.historyLines = AuditLogEvent.executeQuery("select e from org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent as e where e.className in (:l) AND e.lastUpdated >= :s AND e.lastUpdated <= :e AND e.eventName in (:t) order by e.lastUpdated desc", 
                                                       ['l':types_to_include,'s':start_date,'e':end_date, 't':events_to_include], limits);
      result.num_hl = AuditLogEvent.executeQuery("select count(e) from org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent as e where className in (:l) AND e.lastUpdated >= :s AND e.lastUpdated <= :e AND e.eventName in (:t)", 
                                                 ['l':types_to_include,'s':start_date,'e':end_date,'t':events_to_include])[0];
  
      result.formattedHistoryLines = []
      result.historyLines.each { hl ->
  
        def line_to_add = [:]
        def linetype = null
  
        switch(hl.className) {
          case 'com.k_int.kbplus.License':
            def license_object = License.get(hl.persistedObjectId);
            line_to_add = [ link: createLink(controller:'licenseDetails', action: 'show', id:hl.persistedObjectId),
                            name: license_object.toString(),
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
                            name: package_object.toString(),
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
            line_to_add = [ link: createLink(controller:'tippDetails', action: 'show', id:hl.persistedObjectId),
                            name: tipp_object.toString(),
                            lastUpdated: hl.lastUpdated,
                            propertyName: hl.propertyName,
                            actor: User.findByUsername(hl.actor),
                            oldValue: hl.oldValue,
                            newValue: hl.newValue
                          ]
            linetype = 'TIPP'
            break;
          case 'com.k_int.kbplus.TitleInstance':
            def title_object = TitleInstance.get(hl.persistedObjectId);
            line_to_add = [ link: createLink(controller:'titleDetails', action: 'show', id:hl.persistedObjectId),
                            name: title_object.toString(),
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

    result
  }
}


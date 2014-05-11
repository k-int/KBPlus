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

    // com.k_int.kbplus.License                      |
    // com.k_int.kbplus.Subscription                 |
    // com.k_int.kbplus.Package                      |
    // com.k_int.kbplus.TitleInstancePackagePlatform |
    // com.k_int.kbplus.TitleInstance                |
    // com.k_int.kbplus.IdentifierOccurrence         |
    def qry_params=['com.k_int.kbplus.Package']

    result.max = params.max ? Integer.parseInt(params.max) : 25
    params.max = result.max
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    def limits = (!params.format||params.format.equals("html"))?[max:result.max, offset:result.offset]:[offset:0]

    result.historyLines = AuditLogEvent.executeQuery("select e from org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent as e where className=? order by e.lastUpdated desc", qry_params, limits);

    result.formattedHistoryLines = []
    result.historyLines.each { hl ->

      def line_to_add = [:]

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
          break;
        case 'com.k_int.kbplus.TitleInstancePackagePlatform':
          break;
        case 'com.k_int.kbplus.TitleInstance':
          break;
        case 'com.k_int.kbplus.IdentifierOccurrence':
          break;
      }
      switch ( hl.eventName ) {
        case 'INSERT':
          line_to_add.eventName= "New"
          break;
        case 'UPDATE':
          line_to_add.eventName= "Updated"
          break;
        case 'DELETE':
          line_to_add.eventName= "Deleted"
          break;
        default:
          line_to_add.eventName= "Unknown"
          break;
      }
      result.formattedHistoryLines.add(line_to_add);
    }

    result.num_hl = AuditLogEvent.executeQuery("select count(e) from org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent as e where className=?", qry_params)[0];

    result
  }
}


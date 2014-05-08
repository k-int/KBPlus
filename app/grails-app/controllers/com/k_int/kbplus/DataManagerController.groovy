package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.web.JSONBuilder

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
    def qry_params=['com.k_int.kbplus.Package']
    // com.k_int.kbplus.License                      |
    // com.k_int.kbplus.Subscription                 |
    // com.k_int.kbplus.Package                      |
    // com.k_int.kbplus.TitleInstancePackagePlatform |
    // com.k_int.kbplus.TitleInstance                |
    // com.k_int.kbplus.IdentifierOccurrence         |

    result.historyLines = AuditLogEvent.executeQuery("select e from AuditLogEvent as e where className=? order by id desc", qry_params);

    result
  }
}


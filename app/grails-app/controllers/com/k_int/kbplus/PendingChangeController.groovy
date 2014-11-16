package com.k_int.kbplus

import grails.plugins.springsecurity.Secured

class PendingChangeController {

 def genericOIDService
 def pendingChangeService


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def accept() {
    log.debug("Accept");
    pendingChangeService.performAccept(params.id,request);
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def reject() {
    log.debug("Reject");
    pendingChangeService.performReject(params.id,request);
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def acceptAll() {
    def owner = genericOIDService.resolveOID(params.id)

    def changes_to_accept = []
    def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")
    def pendingChanges = PendingChange.executeQuery("select pc.id from PendingChange as pc where ( license=:owner or subscription=:owner or pkg=:owner ) and ( pc.status is null or pc.status =:status ) order by ts asc", [owner:owner, status:pending_change_pending_status ]);
    
    pendingChanges.each { pc ->
      pendingChangeService.performAccept(pc,request)
    }

    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def rejectAll() {
    def owner = genericOIDService.resolveOID(params.id)

    def changes_to_reject = []
    def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")
    def pendingChanges = PendingChange.executeQuery("select pc.id from PendingChange as pc where ( license=:owner or subscription=:owner or pkg=:owner ) and ( pc.status is null or pc.status =:status ) ", [owner:owner, status:pending_change_pending_status ]);
    
    pendingChanges.each { pc ->
      pendingChangeService.performReject(pc,request)
    }

    redirect(url: request.getHeader('referer'))
  }  
}

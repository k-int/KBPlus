package com.k_int.kbplus

import grails.plugins.springsecurity.Secured

class PendingChangeController {

 def genericOIDService
 def pendingChangeService
 def executorWrapperService

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
    def pendingChanges = owner.pendingChanges.findAll {(it.status == pending_change_pending_status) || it.status == null}
    pendingChanges = pendingChanges.collect{it.id}
    def user= [user:request.user]
    executorWrapperService.processClosure({
      pendingChanges.each { pc ->
        pendingChangeService.performAccept(pc,user)
      }
    },owner)

    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def rejectAll() {
    def owner = genericOIDService.resolveOID(params.id)

    def changes_to_reject = []
    def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")
    def pendingChanges = owner.pendingChanges.findAll {(it.status == pending_change_pending_status) || it.status == null}
    pendingChanges = pendingChanges.collect{it.id}
    
    def user= [user:request.user]
    executorWrapperService.processClosure({
      pendingChanges.each { pc ->
        pendingChangeService.performReject(pc,user)
      }
    },owner)

    redirect(url: request.getHeader('referer'))
  }  
}

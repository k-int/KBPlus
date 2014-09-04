package com.k_int.kbplus

import grails.plugins.springsecurity.Secured

class PendingChangeController {

 def genericOIDService
 def pendingChangeService


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def accept() {
    log.debug("Accept");
    def change = PendingChange.get(params.id);
    pendingChangeService.performAccept(change,request);
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def reject() {
    log.debug("Reject");
    def change = PendingChange.get(params.id);
    pendingChangeService.performReject(change,request);
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def acceptAll() {
    def owner = genericOIDService.resolveOID(params.id)

    def changes_to_accept = []

    owner.pendingChanges.each { pc ->
      changes_to_accept.add(pc)
    }

    changes_to_accept.each { pc ->
      pendingChangeService.performAccept(pc,request)
    }

    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def rejectAll() {
    def owner = genericOIDService.resolveOID(params.id)

    def changes_to_reject = []

    owner.pendingChanges.each { pc ->
      changes_to_reject.add(pc)
    }

    changes_to_reject.each { pc ->
      pendingChangeService.performReject(pc,request)
    }

    redirect(url: request.getHeader('referer'))
  }

  
}

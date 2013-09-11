package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import grails.converters.*

class PendingChangeController {

 def genericOIDService


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def accept() {
    log.debug("Accept");
    def change = PendingChange.get(params.id);
    performAccept(change);
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def reject() {
    log.debug("Reject");
    def change = PendingChange.get(params.id);
    performReject(change);
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def acceptAll() {
    def owner = genericOIDService.resolveOID(params.id)
    owner.pendingChanges.each { pc ->
      performAccept(pc)
    }
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def rejectAll() {
    def owner = genericOIDService.resolveOID(params.id)
    owner.pendingChanges.each { pc ->
      performReject(pc)
    }
    redirect(url: request.getHeader('referer'))
  }

  private void performAccept(change) {
    def parsed_change_info = JSON.parse(change.changeDoc)
    log.debug("Process change ${parsed_change_info}");
    switch ( parsed_change_info.changeType ) {
      case 'TIPPDeleted' :
        // "changeType":"TIPPDeleted","tippId":"com.k_int.kbplus.TitleInstancePackagePlatform:6482"}
      
        break;
      default:
        log.error("Unhandled change type : ${pc.changeDoc}");
        break;
    }

  }

  private void performReject(change) {
    change.delete();
  }
}

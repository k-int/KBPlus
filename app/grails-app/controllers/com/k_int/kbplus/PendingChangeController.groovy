package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import grails.converters.*
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.transaction.TransactionStatus

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

    def changes_to_accept = []


    owner.pendingChanges.each { pc ->
      changes_to_accept.add(pc)
    }

    changes_to_accept.each { pc ->
      performAccept(pc)
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
      performReject(pc)
    }

    redirect(url: request.getHeader('referer'))
  }

  private boolean performAccept(change) {
    def result = true
    PendingChange.withNewTransaction { TransactionStatus status ->
      try {
        def parsed_change_info = JSON.parse(change.changeDoc)
        log.debug("Process change ${parsed_change_info}");
        switch ( parsed_change_info.changeType ) {
          case 'TIPPDeleted' :
            // "changeType":"TIPPDeleted","tippId":"com.k_int.kbplus.TitleInstancePackagePlatform:6482"}
            def sub_to_change = change.subscription
            def tipp = genericOIDService.resolveOID(parsed_change_info.tippId)
            def ie_to_update = IssueEntitlement.findBySubscriptionAndTipp(sub_to_change,tipp)
            if ( ie_to_update != null ) {
              ie_to_update.status = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Deleted');
              ie_to_update.save();
            }
            break;
          case 'PropertyChange' :
            // def target_object = change.license ? change.license : change.subscription
            if ( ( parsed_change_info.changeTarget != null ) && ( parsed_change_info.changeTarget.length() > 0 ) ) {
              def target_object = genericOIDService.resolveOID(parsed_change_info.changeTarget);
              if ( target_object ) {
                // Work out if parsed_change_info.changeDoc.prop is an association - If so we will need to resolve the OID in the value
                def domain_class = ApplicationHolder.application.getArtefact('Domain',target_object.class.name);
                def prop_info = domain_class.getPersistentProperty(parsed_change_info.changeDoc.prop)
                if ( prop_info.isAssociation() ) {
                  log.debug("Setting association for ${parsed_change_info.changeDoc.prop} to ${parsed_change_info.changeDoc.new}");
                  target_object[parsed_change_info.changeDoc.prop] = genericOIDService.resolveOID(parsed_change_info.changeDoc.new)
                }
                else if ( prop_info.getType() == java.util.Date ) {
                  log.debug("Date processing.... parse \"${parsed_change_info.changeDoc.new}\"");
                  def df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // yyyy-MM-dd'T'HH:mm:ss.SSSZ 2013-08-31T23:00:00Z
                  def d = df.parse(parsed_change_info.changeDoc.new)
                  target_object[parsed_change_info.changeDoc.prop] = d
                }
                else {
                  log.debug("Setting value for ${parsed_change_info.changeDoc.prop} to ${parsed_change_info.changeDoc.new}");
                  target_object[parsed_change_info.changeDoc.prop] = parsed_change_info.changeDoc.new
                }
                target_object.save()
              }
            }
            break;
          case 'TIPPEdit':
            // A tipp was edited, the user wants their change applied to the IE
            break;
          default:
            log.error("Unhandled change type : ${pc.changeDoc}");
            break;
        }
        change.license?.pendingChanges?.remove(change)
        change.license?.save();
        change.subscription?.pendingChanges?.remove(change)
        change.subscription?.save();
        change.delete();
      }
      catch ( Exception e ) {
        log.error("Problem accepting change",e);
        result = false;
      }
      return result
    }
  }

  private void performReject(change) {
    PendingChange.withNewTransaction { TransactionStatus status ->
      change.delete();
    }
  }
}

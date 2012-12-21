package com.k_int.kbplus.mixins

import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.*;
import com.k_int.kbplus.auth.*;

public class PendingChangeMixin {

  def processAcceptChange(params, targetObject) {
    def user = User.get(springSecurityService.principal.id)

    if ( ! targetObject.hasPerm("edit",user) ) {
      render status: 401
      return
    }

    def pc = PendingChange.get(params.changeid)

    targetObject[pc.updateProperty] = pc.updateValue
    targetObject.save(flush:true)

    expungePendingChange(targetObject, pc);
  }

  def processRejectChange(params, targetObject) {
    def user = User.get(springSecurityService.principal.id)

    if ( ! targetObject.hasPerm("edit",user) ) {
      render status: 401
      return
    }

    def pc = PendingChange.get(params.changeid)
    expungePendingChange(targetObject, pc);
  }


  def expungePendingChange(targetObject, pc) {
    log.debug("Expunging pending change, looking up change context doc=${pc.doc?.id}, targetObject=${targetObject.id}");

    // def this_change_ctx = DocContext.findByOwnerAndLicense(pc.doc, license)
    def this_change_ctx_qry
    if ( targetObject instanceof Subscription ) 
      this_change_ctx_qry = DocContext.where { owner == pc.doc && subscription == targetObject }
    else
      this_change_ctx_qry = DocContext.where { owner == pc.doc && license == targetObject }

    def this_change_ctx = this_change_ctx_qry.find()

    pc.delete(flush:true);

    if ( this_change_ctx ) {
      log.debug("Delete change context between targetObject and change description document");
      this_change_ctx.alert.delete();
      this_change_ctx.delete(flush:true);

      def remaining_contexts = DocContext.findAllByOwner(pc.doc)
      if ( remaining_contexts.size() == 0 ) {
        log.debug("Change doc has no remaining contexts, delete it");
        pc.doc.delete();
      }
      else {
        log.debug("Change document still referenced by ${remaining_contexts.size()} contexts");
      }
    }
    else {
      log.debug("No change context found");
    }
  }


}

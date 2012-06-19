package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import grails.plugins.springsecurity.Secured

class AdminController {

  def index() { }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def manageAffiliationRequests() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)

    // List all pending requests...
    result.pendingRequests = UserOrg.findAllByStatus(0, [sort:'dateRequested'])
    result
  }


  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def actionAffiliationRequest() {
    log.debug("actionMembershipRequest");
    def req = UserOrg.get(params.req);
    def user = User.get(springSecurityService.principal.id)
    if ( req != null ) {
      switch(params.act) {
        case 'approve':
          req.status = 1;
          break;
        case 'deny':
          req.status = 2;
          break;
        default:
          log.error("FLASH UNKNOWN CODE");
          break;
      }
      // req.actionedBy = user
      req.dateActioned = System.currentTimeMillis();
      req.save(flush:true);
    }
    else {
      log.error("FLASH");
    }
    redirect(action: "manageAffiliationRequests")
  }

}

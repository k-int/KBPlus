package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import grails.plugins.springsecurity.Secured
import grails.converters.*

class AdminController {

  def springSecurityService
  def dataloadService

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
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


  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def reconcile() {
    def result = [:]
    result.recon_status = dataloadService.getReconStatus();
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def startReconciliation() {
    def test = RefdataCategory.findByDesc('isCoreTitle');
    if ( test != null ) {
      log.debug("Starting reconciliation process");
      flash.message="Reconciliation started";
      dataloadService.requestReconciliation();
    }
    else {
      flash.message="Reconciliation not started - Is the reference data set up?";
    }
    redirect(action:'reconcile');
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def showAffiliations() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.users = User.list()

    withFormat {
      html {
        render(view:'showAffiliations',model:result)
      }
      json {
        def r2 = []
        result.users.each { u ->
          def row = [:]
          row.username = u.username
          row.display = u.display
          row.instname = u.instname
          row.instcode = u.instcode
          row.email = u.email
          row.shibbScope = u.shibbScope
          row.enabled = u.enabled
          row.accountExpired = u.accountExpired
          row.accountLocked = u.accountLocked
          row.passwordExpired = u.passwordExpired
          row.affiliations = []
          u.affiliations.each { ua ->
            row.affiliations.add( [org: ua.org.shortcode, status: ua.status, role:ua.role] )
          }
          r2.add(row)
        }
        render r2 as JSON
      }
    }
  }
  
  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def allNotes() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    def sc = DocContext.createCriteria()
    result.alerts = sc.list {
      alert {
        gt('sharingLevel', -1)
      }
    }

    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def dataCleanse() {
    // Sets nominal platform
    dataloadService.dataCleanse()
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def titleAugment() {
    // Sets nominal platform
    dataloadService.titleAugment()
  }


  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def licenseLink() {
    if ( ( params.sub_identifier ) && ( params.lic_reference.length() > 0 ) ) {
    }
  }
}

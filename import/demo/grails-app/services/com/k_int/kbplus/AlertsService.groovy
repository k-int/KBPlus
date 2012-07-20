package com.k_int.kbplus

import com.k_int.kbplus.auth.*;

class AlertsService {


  /**
   *  Return a list composed of [ { base-object, [alert, alert, alert] }, ...]
   *  for each alert visible to this user.
   */
  def getActiveAlerts(user) {
    def result = new java.util.ArrayList()

    // Firstly.. Find all license notes that this user can see
    
    // List all institutions this user is attached to
    user.affiliations.each { ua ->
      def org = ua.org;

      // For each institution, iterate through all licenses attached to that institution
      def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
      OrgRole.findAllByOrgAndRoleType(ua.org, licensee_role).each { or ->
        def notes = []
        if ( or.lic?.status?.value!='Deleted' ) {
          // license under question is in or.lic - See if there are any notes attached to that license that this user can see
          // Alert is globally visible or the alert 
          or.lic.documents.each { lic_doc_ctx ->
            if ( ( lic_doc_ctx.alert ) && ( alertIsVisible(user, lic_doc_ctx))) {
              notes.add(lic_doc_ctx)
            }
          }
        }
        if ( notes.size() > 0 ) {
          result.add ( [rootObj:or.lic, notes:notes] )
        }
      }
    }

    log.debug("result of getActiveAlerts: ${result}");

    result
  }

  def alertIsVisible(user, doc_ctx) {
    def result = true;
    if (doc_ctx && user) {
      switch ( doc_ctx.alert.sharingLevel ) {
        case 1:
          // Private - If user org and org of note creator
          user.affiliations.each { ua ->
            result = result || ( ua.org.id == doc_ctx.alert?.org?.id )
          }
          break;
        case 2:
          // Share with JC Only.. If user has org of JC, then visible = true
          def jc_org = Org.findByShortcode('JISC_Collections')
          user.affiliations.each { ua ->
            result = result || ( ua.org.id == jc_org.id )
          }
          break;
        case 3:
          result = true;
          break;
      }
    }
    result
  }
}

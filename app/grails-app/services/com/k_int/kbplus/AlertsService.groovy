package com.k_int.kbplus

import com.k_int.kbplus.auth.*;

class AlertsService {


  def getAllVisibleAlerts(user) {
    def result = new java.util.ArrayList()
    def roots = [:]

    def all_public_notes = DocContext.findAll("from DocContext as ctx where ctx.alert.sharingLevel=2")

    log.debug("all public notes: ${all_public_notes}");

    all_public_notes.each { pn ->
      log.debug("Processing: ${pn}");

      def info = null;
      if ( pn.license ) {
        info = roots["license:${pn.license.id}"]
        if ( !info ) {
          info = [:]
          roots["license:${pn.license.id}"] = info
          info.rootObj = pn.license
          info.notes = [:]
        }
      }
      else if (pn.subscription) {
        info = roots["subscription:${pn.subscription.id}"]
        if ( !info ) {
          info = [:]
          roots["subscription:${pn.subscription.id}"] = info
          info.rootObj = pn.subscription
          info.notes = [:]
        }
      }
      else {
        log.error("unhandled type");
      }

      if (info.notes[pn.id]) {
        // The note is already present under this object, skip
        log.debug("${pn.id} already present");
      }
      else {
        log.debug("Adding note ${pn.id}");
        info.notes[pn.id] = pn
      }
    }

    // Thats all public ones - Add any private notes / objects


    // Now turn the roots map into the array list structure that getActiveAlerts produces so the view doesn't have to change
    roots.values().each { r->
      log.debug("Processing ${r}");
      def notes_to_add = []
      r.notes.values().each { n ->
        notes_to_add.add(n);
      }
      result.add([rootObj:r.rootObj,notes:notes_to_add])
    }
    
    // return
    result
  }

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
        case 0:
          // Private - If user org and org of note creator
          user.affiliations.each { ua ->
            result = result || ( ua.org.id == doc_ctx.alert?.org?.id )
          }
          break;
        case 1:
          // Share with JC Only.. If user has org of JC, then visible = true
          def jc_org = Org.findByShortcode('JISC_Collections')
          user.affiliations.each { ua ->
            result = result || ( ua.org.id == jc_org.id )
          }
          break;
        case 2:
          result = true;
          break;
      }
    }
    result
  }
}

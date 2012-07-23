package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import grails.plugins.springsecurity.Secured
import grails.converters.*



class NewDataloadController {

  def executorService
  def ESWrapperService
  def mongoService
  def springSecurityService
  def genericReconciler

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')
    handleChangesSince(mdb,'orgs',0,com.k_int.kbplus.processing.OrgsProcessing.orgs_reconciliation_ruleset)
  }

  def handleChangesSince(db,
                         collname,
                         timestamp,
                         ruleset) {

    def cursor = db."${collname}".find().sort(lastmod:1)
    cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
    cursor.each { item ->
      def local_copy = db."${collname}_localcopy".findOne([_id:item._id])
      if ( local_copy ) {
        log.debug("Got local copy");
        if ( item.equals(local_copy.original) ) {
          log.debug("No change detected in source item since last processing");
        }
        else {
          log.debug("Record has changed... process");
          genericReconciler.reconcile(item, local_copy, ruleset);
        }
      }
      else {
        log.debug("No local copy found");
        def copy_item = [
          _id:item._id,
          original:item
        ]
        db."${collname}_localcopy".save(copy_item);
        genericReconciler.reconcile(item, null, ruleset);
      }
    }
  }
}

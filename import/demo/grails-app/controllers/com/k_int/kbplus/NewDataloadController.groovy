package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import grails.plugins.springsecurity.Secured
import grails.converters.*



class NewDataloadController {

  def executorService
  def ESWrapperService
  def mongoService
  def springSecurityService
  def genericReconcilerService

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
      def mongo_collection = db."${collname}_localcopy"

      def local_copy = mongo_collection.findOne([_id:item._id])
      if ( local_copy ) {
        log.debug("Got local copy");
        if ( item.equals(local_copy.current_copy) ) {
          log.debug("No change detected in source item since last processing");
          // genericReconcilerService.reconcile(db, item, local_copy, ruleset);
        }
        else {
          log.debug("Record has changed... process");
          genericReconcilerService.reconcile(mongo_collection, item, local_copy, ruleset);
        }
      }
      else {
        log.debug("No local copy found");
        def historic_item_info = [
          _id:item._id,
          current_copy:null,
          conflict:false,
          pending_queue:[]
        ]
        mongo_collection.save(historic_item_info);
        genericReconcilerService.reconcile(mongo_collection, item, historic_item_info, ruleset);
      }
    }
    log.debug("handleChangesSince for ${collname} completed normally");
  }
}

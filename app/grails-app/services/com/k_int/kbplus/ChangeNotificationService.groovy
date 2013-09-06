package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import com.k_int.kbplus.*;

class ChangeNotificationService {

  def executorService
  def genericOIDService

  // N,B, This is critical for this service as it's called from domain object OnChange handlers
  static transactional = false;

  def notifyLicenseChange(l, propname, oldvalue, newvalue, note, type) {
    log.debug("notifyLicenseChange...${l},type=${type}  create future");

    def future = executorService.submit({
      log.debug("inside submitted job");
      processLicenseChange(l,propname,oldvalue,newvalue,note,type)
    } as java.util.concurrent.Callable)

  }

  def notifySubscriptionChange(l, propname, oldvalue, newvalue, note, type) {
    log.debug("notifySubscriptionChange...${l} create future");

    def future = executorService.submit({
      try {
        log.debug("inside submitted job - notify subscription change ${propname} - ${oldvalue} to ${newvalue}....... waiting");
        processSubscriptionChange(l,propname,oldvalue,newvalue,note,type)
      }
      catch ( Throwable t ) {
        log.error("problem processing sub change notification",t);
      }
      finally {
        log.debug("Call to processSubscriptionChange completed");
      }
    } as java.util.concurrent.Callable)

  }

  /**
   *  type "S"imple or "R"eference
   */ 
  def processLicenseChange(l, propname, oldvalue, newvalue, note, type) {
    log.debug("processLicenseChange...(type=${type})");

    License lic_being_changed = License.get(l);

    try {

      if ( hasDerivedLicenses(lic_being_changed) ) {
        Doc change_doc = new Doc(title:'Template Change notification',
                                 contentType:2,
                                 content:'The template license for this actual license has changed. You can accept the changes').save();

        lic_being_changed.outgoinglinks.each { ol ->
          def derived_licence = ol.toLic;
          log.debug("Notify license ${ol.toLic.id} of change");

          Alert a = new Alert(sharingLevel:2).save(flush:true)

          DocContext ctx = new DocContext(owner:change_doc, 
                                          license:derived_licence,
                                          alert:a).save(flush:true);

          PendingChange pc = new PendingChange(license:derived_licence,
                                               doc:change_doc,
                                               changeType:type,
                                               updateProperty:propname, 
                                               updateValue: newvalue,
                                               updateReason:"The template used to derive this licence has changed")
          if ( pc.save(flush:true) ) {
          }
          else {
            log.error("Problem saving pending change: ${pc.errors}");
          }

        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem processng change notification",e);
    }
    finally {
      log.debug("processChange completed");
    }
  }

  def processSubscriptionChange(l, propname, oldvalue, newvalue, note, type) {

    Subscription sub_being_changed = Subscription.get(l);

    try {

      log.debug("Does the edited sub have derived subs? ${hasDerivedSubscriptions(sub_being_changed)}");

      if ( hasDerivedSubscriptions(sub_being_changed) ) {
        Doc change_doc = new Doc(title:'Template Change notification',
                                 contentType:2,
                                 content:'The template subscription for this sub has changed. You can accept the changes').save();

        sub_being_changed.derivedSubscriptions?.each { st ->
          log.debug("Adding note for derived ST: ${st.id}");
          Alert a = new Alert(sharingLevel:2).save(flush:true)

          DocContext ctx = new DocContext(owner:change_doc,
                                          subscription:st,
                                          alert:a).save(flush:true);

          PendingChange pc = new PendingChange(subscription:st,
                                               doc:change_doc,
                                               changeType:type,
                                               updateProperty:propname,
                                               updateValue: type=='R' ? "${newvalue.class.name}:${newvalue.id}" : newvalue,
                                               updateReason:"The template used to derive this subscription has changed").save(flush:true);

        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem processng change notification",e);
    }
    finally {
      log.debug("processChange completed");
    }
  }


  /**
   *  Taken this simple test out to a function of it's own in preparation for license link types when
   *  this test will likely become more involved.
   */
  def hasDerivedLicenses(lic) {
    def result = false;
    if ( lic.outgoinglinks?.size() > 0 )
      result = true;
    result;
  }

  def hasDerivedSubscriptions(sub) {
    def result = false;
    if ( sub.derivedSubscriptions?.size() > 0 )
      result = true;
    result;
  }


  def broadcastEvent(contextObjectOID, 
                     changeDetailDocument) {
    log.debug("broadCastEvent");

    def contextObject = genericOIDService.resolveOID(contextObjectOID);

    if ( contextObject.metaClass.respondsTo(contextObject, 'getNotificationEndpoints') ) {
      // Does the objct have a zendesk URL, or any other comms URLs for that matter?
      // How do we decouple Same-As links? Only the object should know about what
      // notification services it's registered with? What about the case where we're adding
      // a new thing? Whats registered?
      contextObject.getNotificationEndpoints.each { ne ->
        switch ( ne.service ) {
          case 'zendesk-forum': 
            log.debug("Send zendesk forum notification for ${ne.remoteid} - ${changeDetailDocument}");
            break;
          default:
            break;
        }
      }
    }
  }

  /**
   *  An object has changed. Because we don't want to do heavy work of calculating dependent objects in the thread doing the DB
   *  commit, responsibility for handling the change is delegated to this method. However, the source object is the seat of
   *  knowledge for what dependencies there are (For example, a title change should propagate to all packages using that title).
   *  Therefore, we get a new handle to the object
   */
  def notifyChangeEvent(changeDocument) {
    log.debug("notifyChangeEvent(${changeDocument})");
    def future = executorService.submit({
      def contextObject = genericOIDService.resolveOID(contextObjectOID);
      contextObject.notifyDependencies(changeDocument)
    } as java.util.concurrent.Callable)
  }
}

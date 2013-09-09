package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import com.k_int.kbplus.*;
import grails.converters.*


class ChangeNotificationService {

  def executorService
  def genericOIDService
  def zenDeskSyncService

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


  def broadcastEvent(contextObjectOID, changeDetailDocument) {
    log.debug("broadcastEvent(${contextObjectOID},${changeDetailDocument})");

    
    def contextObject = genericOIDService.resolveOID(contextObjectOID);

    def jsonChangeDocument = changeDetailDocument as JSON
    def new_queue_item = new ChangeNotificationQueueItem(oid:contextObjectOID, 
                                                         changeDocument:jsonChangeDocument.toString(),
                                                         ts:new Date())
    if ( new_queue_item.save() ) {
      log.debug("Pending change saved ok");
    }
    else {
      log.error(new_queue_item.errors);
    }

  }


  // Gather together all the changes for a give context object, formate them into an aggregated document
  // notify any registered channels
  def aggregateAndNotifyChanges() {
    def future = executorService.submit({
      internalAggregateAndNotifyChanges();
    } as java.util.concurrent.Callable)
  }


  def internalAggregateAndNotifyChanges() {

    try {
      def pendingOIDChanges = ChangeNotificationQueueItem.executeQuery("select distinct c.oid from ChangeNotificationQueueItem as c order by c.oid");

      pendingOIDChanges.each { poidc ->
    
        log.debug("Consider pending changes for ${poidc}");
        def contextObject = genericOIDService.resolveOID(poidc);
        def pendingChanges = ChangeNotificationQueueItem.executeQuery("select c from ChangeNotificationQueueItem as c where c.oid = ? order by c.ts asc",[poidc]);
        StringWriter sw = new StringWriter();
        sw.write("<p>Changes on ${new Date().toString()}</p><p><ul>");
        def pc_delete_list = []

        pendingChanges.each { pc ->
          log.debug("Process pending change ${pc}");    
          def parsed_event_info = JSON.parse(pc.changeDocument)
          def change_template = ContentItem.findByKey("ChangeNotification.${parsed_event_info.event}")
          if ( change_template != null ) {
            log.debug("Found change template... ${change_template.content}");
            // groovy.util.Eval.x(r, 'x.' + rh.property)
            def event_props = [o:contextObject, evt:parsed_event_info]

            // Use doStuff to cleverly render change_template with variable substitution 
            log.debug("Make engine");
            def engine = new groovy.text.GStringTemplateEngine()
            log.debug("createTemplate..");
            def tmpl = engine.createTemplate(change_template.content).make(event_props)
            log.debug("Write to string writer");
            sw.write("<li>");
            sw.write(tmpl.toString());
            sw.write("</li>");
          }
          else {
            sw.write("<li>Unable to find template for change event \"ChangeNotification.${parsed_event_info.event}\". Event info follows\n\n${pc.changeDocument}</li>");
          }
          pc_delete_list.add(pc)
        }
        sw.write("</ul></p>");

        if ( contextObject != null ) {
          if ( contextObject.metaClass.respondsTo(contextObject, 'getNotificationEndpoints') ) {
            log.debug("  -> looking at notification endpoints...");
            // Does the objct have a zendesk URL, or any other comms URLs for that matter?
              // How do we decouple Same-As links? Only the object should know about what
            // notification services it's registered with? What about the case where we're adding
            // a new thing? Whats registered?
            contextObject.notificationEndpoints.each { ne ->
              log.debug("  -> consider ${ne}");
              switch ( ne.service ) {
                case 'zendesk.forum': 
                  log.debug("Send zendesk forum notification for ${ne.remoteid}");
                  zenDeskSyncService.postTopicCommentInForum(sw.toString(),
                                                             ne.remoteid.toString(), 
                                                             "Changes related to ${contextObject.toString()}".toString(),
                                                             'System generated alerts and notifications will appear as comments under this topic');
                  break;
                default:
                  break;
              }
            }
          }
          else {
          }
        }

        log.debug("Delete reported changes...");
        // If we got this far, all is OK, delete any pending changes
        pc_delete_list.each { pc ->
          pc.delete()
        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem",e);
    }
    finally {
      log.debug("aggregateAndNotifyChanges completed");
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
      try {
        log.debug("inside executor task submission... ${changeDocument.OID}");
        def contextObject = genericOIDService.resolveOID(changeDocument.OID);
        contextObject.notifyDependencies(changeDocument)
      }
      catch ( Exception e ) {
        log.error("Problem with event transmission",e);
      }
    } as java.util.concurrent.Callable)
  }
}

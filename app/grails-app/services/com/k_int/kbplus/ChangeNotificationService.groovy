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

  def broadcastEvent(contextObjectOID, changeDetailDocument) {
    // log.debug("broadcastEvent(${contextObjectOID},${changeDetailDocument})");

    
    def contextObject = genericOIDService.resolveOID(contextObjectOID);

    def jsonChangeDocument = changeDetailDocument as JSON
    def new_queue_item = new ChangeNotificationQueueItem(oid:contextObjectOID, 
                                                         changeDocument:jsonChangeDocument.toString(),
                                                         ts:new Date())
    if ( new_queue_item.save() ) {
      // log.debug("Pending change saved ok");
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


  // Sum up all pending changes by OID and write a unified message
  def internalAggregateAndNotifyChanges() {

    try {
      def pendingOIDChanges = ChangeNotificationQueueItem.executeQuery("select distinct c.oid from ChangeNotificationQueueItem as c order by c.oid");

      pendingOIDChanges.each { poidc ->
    
        // log.debug("Consider pending changes for ${poidc}");
        def contextObject = genericOIDService.resolveOID(poidc);

        if ( contextObject == null ) {
          log.warn("Pending changes for a now deleted item.. nuke them!");
          ChangeNotificationQueueItem.executeUpdate("delete ChangeNotificationQueueItem c where c.oid = :oid", [oid:poidc])
        }

        def pendingChanges = ChangeNotificationQueueItem.executeQuery("select c from ChangeNotificationQueueItem as c where c.oid = ? order by c.ts asc",[poidc]);
        StringWriter sw = new StringWriter();

        if ( contextObject ) {
          if ( contextObject.metaClass.respondsTo(contextObject, 'getURL') ) {
            sw.write("<p>Changes on <a href=\"${contextObject.getURL()}\">${contextObject.toString()}</a> ${new Date().toString()}</p><p><ul>");
          }
          else  {
            sw.write("<p>Changes on ${contextObject.toString()} ${new Date().toString()}</p><p><ul>");
          }
        }
        else {
        }
        def pc_delete_list = []

        pendingChanges.each { pc ->
          // log.debug("Process pending change ${pc}");    
          def parsed_event_info = JSON.parse(pc.changeDocument)
          def change_template = ContentItem.findByKey("ChangeNotification.${parsed_event_info.event}")
          if ( change_template != null ) {
            // log.debug("Found change template... ${change_template.content}");
            // groovy.util.Eval.x(r, 'x.' + rh.property)
            def event_props = [o:contextObject, evt:parsed_event_info]
            if ( parsed_event_info.OID != null && parsed_event_info.OID.length() > 0 ) {
              event_props.OID = genericOIDService.resolveOID(parsed_event_info.OID);
            }

            // Use doStuff to cleverly render change_template with variable substitution 
            // log.debug("Make engine");
            def engine = new groovy.text.GStringTemplateEngine()
            // log.debug("createTemplate..");
            def tmpl = engine.createTemplate(change_template.content).make(event_props)
            // log.debug("Write to string writer");
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
            // log.debug("  -> looking at notification endpoints...");
            def announcement_content = sw.toString();
            // Does the objct have a zendesk URL, or any other comms URLs for that matter?
              // How do we decouple Same-As links? Only the object should know about what
            // notification services it's registered with? What about the case where we're adding
            // a new thing? Whats registered?
            contextObject.notificationEndpoints.each { ne ->
              // log.debug("  -> consider ${ne}");
              switch ( ne.service ) {
                case 'zendesk.forum': 
                  if ( ne.remoteid != null ) {
                    // log.debug("Send zendesk forum notification for ${ne.remoteid}");
                    zenDeskSyncService.postTopicCommentInForum(announcement_content,
                                                               ne.remoteid.toString(), 
                                                               "Changes related to ${contextObject.toString()}".toString(),
                                                               'System generated alerts and notifications will appear as comments under this topic');
                  }
                  else {
                    log.warn("Context object has no forum... ${poidc}");
                  }
                  break;
                case 'announcements':
                  def announcement_type = RefdataCategory.lookupOrCreate('Document Type','Announcement')
                  // result.recentAnnouncements = Doc.findAllByType(announcement_type,[max:10,sort:'dateCreated',order:'desc'])
                  def newAnnouncement = new Doc(title:'Automated Announcement',
                                                type:announcement_type,
                                                content:announcement_content,
                                                dateCreated:new Date(),
                                                user:User.findByUsername('admin')).save();

                  break;
                default:
                  break;
              }
            }
          }
          else {
          }
        }

        // log.debug("Delete reported changes...");
        // If we got this far, all is OK, delete any pending changes
        pc_delete_list.each { pc ->
          // log.debug("Deleting reported change ${pc.id}");
          pc.delete()
        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem",e);
    }
    finally {
      // log.debug("aggregateAndNotifyChanges completed");
    }
 
  }

  /**
   *  An object has changed. Because we don't want to do heavy work of calculating dependent objects in the thread doing the DB
   *  commit, responsibility for handling the change is delegated to this method. However, the source object is the seat of
   *  knowledge for what dependencies there are (For example, a title change should propagate to all packages using that title).
   *  Therefore, we get a new handle to the object
   */
  def notifyChangeEvent(changeDocument) {
    // log.debug("notifyChangeEvent(${changeDocument})");
    def future = executorService.submit({
      try {
        log.debug("inside executor task submission... ${changeDocument.OID}");
        def contextObject = genericOIDService.resolveOID(changeDocument.OID);
        log.debug("Context object: ${contextObject}")
        contextObject?.notifyDependencies(changeDocument)
      }
      catch ( Exception e ) {
        log.error("Problem with event transmission for ${changeDocument.OID}",e);
      }
    } as java.util.concurrent.Callable)
  }



  def registerPendingChange(prop, target, desc, objowner, changeMap ) {
    log.debug("Register pending change ${prop} ${target.class.name}:${target.id}");
    def new_pending_change = new PendingChange()
    new_pending_change[prop] = target;
    def jsonChangeDocument = changeMap as JSON
    new_pending_change.changeDoc = jsonChangeDocument.toString();
    new_pending_change.desc = desc
    new_pending_change.owner = objowner
    new_pending_change.oid = "${target.class.name}:${target.id}"
    new_pending_change.ts = new Date();
    if ( new_pending_change.save(flush:true) ) {
    }
    else {
      log.error("Problem saving pending change: ${new_pending_change.errors}");
    }
  }

}

package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import com.k_int.kbplus.*;

class ChangeNotificationService {

  def executorService

  // N,B, This is critical for this service as it's called from domain object OnChange handlers
  static transactional = false;

  def notifyLicenseChange(l, propname, oldvalue, newvalue, note) {
    log.debug("notifyLicenseChange...${l} create future");

    def future = executorService.submit({
      log.debug("inside submitted job");
      processLicenseChange(l,propname,oldvalue,newvalue,note)
    } as java.util.concurrent.Callable)

  }

  def processLicenseChange(l, propname, oldvalue, newvalue, note) {
    log.debug("processChange...");

    License lic_being_changed = License.get(l);

    try {

      if ( hasDerivedLicenses(lic_being_changed) ) {
        Doc change_doc = new Doc(title:'Template Change notification',
                                 contentType:1,
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
                                               updateProperty:propname, 
                                               updateValue:newvalue,
                                               updateReason:"The template used to derive this licence has changed").save(flush:true);

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

}

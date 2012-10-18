package com.k_int.kbplus

import com.k_int.kbplus.auth.*;

class ChangeNotificationService {

  // N,B, This is critical for this service as it's called from domain object OnChange handlers
  static transactional = false;

  def notifyLicenseChange(l, propname, oldvalue, newvalue, note) {
    log.debug("notifyLicenseChange...");

        // Doc change_doc = new Doc(title:'Template Change notification',contentType:1,content:'The template license for this actual license has changed. You can accept the changes').save();

        // outgoinglinks.each { ol ->
        //   def derived_licence = ol.toLic;
        //   log.debug("Notify license ${ol.toLic.id} of change");
        //   Alert a = new Alert(sharingLevel:2).save(flush:true)
        //   DocContext ctx = new DocContext(owner:change_doc, 
        //                                   license:derived_licence,
        //                                   alert:a).save(flush:true);

        //   PendingChange pc = new PendingChange(//  license:derived_licence,
        //                                        updateProperty:cp, 
        //                                        updateValue:newMap[cp],
        //                                        updateReason:'Template Edited').save(flush:true);
        // }

        // log.debug("licenseUrl has changed - Notify any licenses derived from this one. Change description is ${changeDescription}");

  }

}

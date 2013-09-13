package com.k_int.kbplus

import com.k_int.kbplus.auth.User;

class ChangeNotificationQueueItem {

  String oid
  String changeDocument
  Date ts

  static mapping = {
               oid column:'cnqi_oid'
    changeDocument column:'cnqi_change_document', type:'text'
                ts column:'cnqi_ts'
  }

  static constraints = {
    oid(nullable:false, blank:false);
    changeDocument(nullable:false, blank:false);
    ts(nullable:false, blank:false);
  }

}

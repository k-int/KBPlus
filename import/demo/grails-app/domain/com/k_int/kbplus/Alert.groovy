package com.k_int.kbplus

class Alert {

  int sharingLevel

  static mapping = {
    sharingLevel column:'dc_alert_fk'
  }

  static constraints = {
    sharingLevel(nullable:false, blank:false);
  }

}

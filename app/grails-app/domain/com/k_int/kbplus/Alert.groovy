package com.k_int.kbplus

import com.k_int.kbplus.auth.User;

class Alert {

  int sharingLevel
  long createTime = System.currentTimeMillis()
  User createdBy
  Org org
  SortedSet comments


  static hasMany = [ comments : Comment ]


  static mapping = {
    sharingLevel column:'al_sharing_level'
    createTime column:'al_create_time'
    createdBy column:'al_user_fk'
    org column:'al_org_fk'
  }

  static constraints = {
    sharingLevel(nullable:false, blank:false);
    createTime(nullable:false, blank:false);
    createdBy(nullable:true, blank:false);
    org(nullable:true, blank:false);
  }

}

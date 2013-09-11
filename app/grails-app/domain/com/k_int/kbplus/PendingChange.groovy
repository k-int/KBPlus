package com.k_int.kbplus

class PendingChange {

  Subscription subscription
  License license
  TitleInstancePackagePlatform tipp

  String changeDoc
  static mapping = {
      subscription column:'pc_sub_fk'
           license column:'pc_lic_fk'
              tipp column:'pc_tipp_fk', type:'text'
               doc column:'pc_change_doc'
  }

  static constraints = {
    subscription(nullable:true, blank:false);
    license(nullable:true, blank:false);
    tipp(nullable:true, blank:false);
    changeDoc(nullable:true, blank:false);
  }
}

package com.k_int.kbplus

class PendingChange {

  Subscription subscription
  License license
  Date ts
  Org owner
  String oid
  String changeDoc
  String desc

  static mapping = {
      subscription column:'pc_sub_fk'
           license column:'pc_lic_fk'
               oid column:'pc_oid'
               doc column:'pc_change_doc'
                ts column:'pc_ts'
             owner column:'pc_owner'
              desc column:'pc_desc'
  }

  static constraints = {
    subscription(nullable:true, blank:false);
    license(nullable:true, blank:false);
    changeDoc(nullable:true, blank:false);
    ts(nullable:true, blank:false);
    owner(nullable:true, blank:false);
    oid(nullable:true, blank:false);
    desc(nullable:true, blank:false);
  }
}

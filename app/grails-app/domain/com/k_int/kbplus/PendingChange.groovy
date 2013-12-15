package com.k_int.kbplus

class PendingChange {

  Subscription subscription
  License license
  SystemObject systemObject
  Date ts
  Org owner
  String oid
  String changeDoc
  String desc
  RefdataValue status

  static mapping = {
      systemObject column:'pc_sys_obj'
      subscription column:'pc_sub_fk'
           license column:'pc_lic_fk'
               oid column:'pc_oid'
         changeDoc column:'pc_change_doc', type:'text'
                ts column:'pc_ts'
             owner column:'pc_owner'
              desc column:'pc_desc', type:'text'
            status column:'pc_status_rdv_fk'
  }

  static constraints = {
    systemObject(nullable:true, blank:false);
    subscription(nullable:true, blank:false);
    license(nullable:true, blank:false);
    changeDoc(nullable:true, blank:false);
    ts(nullable:true, blank:false);
    owner(nullable:true, blank:false);
    oid(nullable:true, blank:false);
    desc(nullable:true, blank:false);
    status(nullable:true, blank:false);
  }
}

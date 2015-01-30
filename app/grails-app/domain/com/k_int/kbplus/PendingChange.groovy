package com.k_int.kbplus

import com.k_int.kbplus.auth.User

class PendingChange {

  Subscription subscription
  License license
  SystemObject systemObject
  Package pkg
  Date ts
  Org owner
  String oid
  String changeDoc
  String desc
  RefdataValue status
  Date actionDate
  User user


  static mapping = {
      systemObject column:'pc_sys_obj'
      subscription column:'pc_sub_fk'
           license column:'pc_lic_fk'
               pkg column:'pc_pkg_fk'
               oid column:'pc_oid', index:'pending_change_oid_idx'
         changeDoc column:'pc_change_doc', type:'text'
                ts column:'pc_ts'
             owner column:'pc_owner'
              desc column:'pc_desc', type:'text'
            status column:'pc_status_rdv_fk'
        actionDate column:'pc_action_date'
              user column:'pc_action_user_fk'
              sort "ts":"asc"
  }

  static constraints = {
    systemObject(nullable:true, blank:false);
    subscription(nullable:true, blank:false);
    license(nullable:true, blank:false);
    changeDoc(nullable:true, blank:false);
    pkg(nullable:true, blank:false);
    ts(nullable:true, blank:false);
    owner(nullable:true, blank:false);
    oid(nullable:true, blank:false);
    desc(nullable:true, blank:false);
    status(nullable:true, blank:false);
    actionDate(nullable:true, blank:false);
    user(nullable:true, blank:false);
  }
}

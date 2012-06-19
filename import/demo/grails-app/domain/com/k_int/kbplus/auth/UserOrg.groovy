package com.k_int.kbplus.auth

import com.k_int.kbplus.Org

class UserOrg {

    Long dateRequested
    Long dateActioned
    int status  // 0=Pending, 1=Approved, 2=Rejected, 3=AutoApproved
    String role

    static belongsTo = [org: com.k_int.kbplus.Org, user:com.k_int.kbplus.auth.User]

    static constraints = {
      dateActioned(nullable:true)
      dateRequested(nullable:true)
    }
}


package com.k_int.kbplus

import com.k_int.kbplus.auth.*;

class OrgPermShare {

  Perm perm
  RefdataValue role

  static mapping = {
    cache true
  }

  static constraints = {
    perm blank: false, unique: true, nullable:false
    role blank: false, unique: true, nullable:false
  }
}

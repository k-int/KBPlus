package com.k_int.kbplus

import com.k_int.kbplus.auth.*;

class OrgPermShare {

  Perm perm
  RefdataValue rdv

  static mapping = {
    cache true
  }

  static constraints = {
    perm blank: false, nullable:false
    rdv blank: false, nullable:false
  }

  public static def assertPermShare(perm, rdv) {
    def result = OrgPermShare.findByPermAndRdv(perm,rdv);

    if ( result == null ) {
      result = new OrgPermShare(perm:perm, rdv:rdv).save();
    }

    result
  }
}

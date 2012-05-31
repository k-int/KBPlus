package com.k_int.kbplus

class OrgRole {

  static belongsTo = [
    org:Org
  ]

  RefdataValue roleType

  // For polymorphic joins based on "Target Context"
  Package pkg
  Subscription sub
  License lic
  TitleInstance title

  static mapping = {
          id column:'or_id'
     version column:'or_version'
         org column:'or_org_fk', index:'or_org_rt_idx'
    roleType column:'or_roletype_fk', index:'or_org_rt_idx'
         pkg column:'or_pkg_fk'
         sub column:'or_sub_fk'
         lic column:'or_lic_fk'
       title column:'or_title_fk'
  }

  static constraints = {
    roleType(nullable:true, blank:false)
    pkg(nullable:true, blank:false)
    sub(nullable:true, blank:false)
    lic(nullable:true, blank:false)
    title(nullable:true, blank:false)
  }
}

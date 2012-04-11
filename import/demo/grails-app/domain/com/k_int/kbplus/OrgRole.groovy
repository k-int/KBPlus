package com.k_int.kbplus

class OrgRole {

  static belongsTo = [
    org:Org,
    roleType:RefdataValue
  ]

  static mapping = {
          id column:'or_id'
     version column:'or_version'
         org column:'or_org_fk'
    roleType column:'or_roletype_fk'
  }

  static constraints = {
  }
}

package com.k_int.kbplus

class RefdataValue {

  String value
  String icon

  static belongsTo = [
    owner:RefdataCategory
  ]

  
  // We wish some refdata items to model a sharing of permission from the owner of an object to a particular
  // organisation. For example, an organisation taking out a license (Via an OrgRole link) needs to be editable by that org.
  // Therefore, we would like all OrgRole links of type "Licensee" to convey
  // permissions of "EDIT" and "VIEW" indicating that anyone who has the corresponding rights via their
  // connection to that org can perform the indicated action.
  // Object Side = Share Permission, User side == grant permission
  Set sharedPermissions = []

  static hasMany = [
   sharedPermissions:OrgPermShare
  ]

  static mapping = {
         id column:'rdv_id'
    version column:'rdv_version'
      owner column:'rdv_owner', index:'rdv_entry_idx'
      value column:'rdv_value', index:'rdv_entry_idx'
       icon column:'rdv_icon'
  }

  static constraints = {
    icon(nullable:true)
  }
}

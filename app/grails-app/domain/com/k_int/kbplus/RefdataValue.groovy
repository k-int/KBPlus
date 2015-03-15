package com.k_int.kbplus
import com.k_int.ClassUtils

class RefdataValue {

  String value

  // N.B. This used to be ICON but in the 2.x series this was changed to be a css class which denotes an icon
  // Please stick with the change to store css classes in here and not explicit icons
  String icon
  //For cases were we want to present a specific group of values, eg Licence/Sub related
  String group

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
      group column:'rdv_group'
  }

  static constraints = {
    icon(nullable:true)
    group(nullable:true, blank:false)
  }

  static def refdataFind(params) {
    def result = [];
    def ql = null;
    ql = RefdataValue.findAllByValueIlike("%${params.q}%",params)

    if ( ql ) {
      ql.each { id ->
        result.add([id:"${id.class.name}:${id.id}",text:"${id.value}"])
      }
    }

    result
  }

  static def refdataCreate(value) {
    // return new RefdataValue(value:value);
    return null;
  }

  public String toString() {
    value
  }
  /**
  * Equality should be decided like this, although we currently got duplicates
  * refdatavalue for same string value
  **/
  @Override
  public boolean equals (Object o) {
    def obj = ClassUtils.deproxy(o)
    if (obj != null) {
      if ( obj instanceof RefdataValue ) {
        return obj.id == id
      }
    }
    return false
  }
}

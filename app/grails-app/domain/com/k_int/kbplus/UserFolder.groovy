package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import javax.persistence.Transient

class UserFolder {

  String shortcode
  String name
  List items=[]

  static belongsTo = [
    user:User
  ]

  static hasMany = [
    items:FolderItem
  ]

  static mapping = {
          id column:'uf_id'
     version column:'uf_version'
        user column:'uf_owner_id'
   shortcode column:'uf_shortcode'
        name column:'uf_name'
       items cascade: 'all-delete-orphan'
  }

  static constraints = {
    shortcode(nullable:true, blank:true)
    name(nullable:true, blank:true)
  }


  @Transient
  def addIfNotPresent(oid) {
    def present = false;
    items.each { 
      if ( it.referencedOid && ( it.referencedOid == oid ) ) {
        present = true
      }
    }

    if ( !present ) {
      items.add(new FolderItem(folder:this,referencedOid:oid))
    }
  }


}

package com.k_int.kbplus

import com.k_int.kbplus.auth.User

class UserFolder {

  String shortcode
  String name
  List items

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
  }

  static constraints = {
    shortcode(nullable:true, blank:true)
    name(nullable:true, blank:true)
  }
}

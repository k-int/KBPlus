package com.k_int.kbplus

import javax.persistence.Transient

/*
* With the current implementation only one SystemAdmin should exist.
* SystemAdmin is created in Bootstrap, only when SystemAdmin.list().first() is null
*/
class SystemAdmin {

  static hasMany = [customProperties: SystemAdminCustomProperty]
  String name;

  @Transient
  def refresh(){
    customProperties.each{
      def prop_name = it.type.name
      grails.util.Holders.config.put(prop_name,it.getValue())
    }
  }
}
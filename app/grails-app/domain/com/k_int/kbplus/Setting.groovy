package com.k_int.kbplus

class Setting {

  String name
  int tp = 0;  // 0=string, 1=boolean
  String defvalue
  String value

  static mapping = {
             id column:'set_id'
           name column:'set_name'
             tp column:'set_type'
       defvalue column:'set_defvalue'
          value column:'set_value'
  }

  
  static constraints = {
    name(nullable:false, blank:false);
    tp(nullable:false, blank:false);
    defvalue(nullable:true, blank:true, maxSize:1024);
    value(nullable:true, blank:true,maxSize:1024);
  }

}

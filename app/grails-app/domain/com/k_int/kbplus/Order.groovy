package com.k_int.kbplus

class Order {

  String orderNumber
  Org owner

  static mapping = {
             table 'kbplus_ord'
                id column:'ord_id'
           version column:'ord_version'
       orderNumber column:'ord_number'
             owner column:'ord_owner'
  }

  static constraints = {
    orderNumber(nullable:false, blank:false);
          owner(nullable:false, blank:false);
  }

}

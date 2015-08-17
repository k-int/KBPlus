package com.k_int.kbplus

import javax.persistence.Transient

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


    @Transient
    static def refdataFind(params) {
        def owner  = Org.findByShortcode(params.shortcode)
        def result = [];
        def ql     = null;
        if (owner)
            ql = Order.findAllByOwnerAndOrderNumberIlike(owner,"%${params.q}%",params)

        if ( ql ) {
            ql.each { id ->
                result.add([id:"${id.class.name}:${id.id}",text:"${id.orderNumber}"])
            }
        }

        result
    }
}

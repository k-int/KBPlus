package com.k_int.kbplus

import com.k_int.kbplus.auth.User

class Reminder {

    User    user     //Linked to
    Boolean active   //Is in use or disabled via user
    Integer amount   //e.g. 3 days before
    RefdataValue  method   //email
    RefdataValue  unit     //day, week, month
    RefdataValue  trigger  //Subscription manual renewal date

    static constraints = {
        method  nullable: false, blank: false
        unit    nullable: false, blank: false
        trigger nullable: false, blank: false
        amount  nullable: false, blank: false
    }
}

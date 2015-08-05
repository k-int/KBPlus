package com.k_int.kbplus

import com.k_int.kbplus.auth.User

/**
 * @author Ryan@k-int.com
 */
class Reminder {

    User    user     //Linked to
    Boolean active //Is in use or disabled via user
    Integer amount   //e.g. 3 days before
    RefdataValue  reminderMethod   //email
    RefdataValue  unit     //day, week, month
    RefdataValue  trigger  //Subscription manual renewal date

    Date lastUpdated
    Date lastRan  //i.e. successful email operation

    static constraints = {
        reminderMethod  nullable: false, blank: false
        unit    nullable: false, blank: false
        trigger nullable: false, blank: false
        amount  nullable: false, blank: false
        active  nullable: false, blank: false
        lastRan nullable: true, blank:false
    }

    static mapping = {
        sort lastUpdated: "desc"
    }
}

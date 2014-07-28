package com.k_int.kbplus.abstract_domain

import com.k_int.custprops.PropertyDefinition
import com.k_int.kbplus.License
import com.k_int.kbplus.RefdataValue

/**
 * Created by ioannis on 26/06/2014.
 * Custom properties must always follow the naming convention: Owner + CustomProperty, where owner is the
 * name of owner class and be under com.k_int.kbplus . For example LicenceCustomProperty , SubscriptionCustomProperty.
 * Relevant code in PropertyDefinition, createPropertyValue
 */
abstract class CustomProperty implements Serializable{

    String stringValue
    Integer intValue
    BigDecimal decValue
    RefdataValue refValue
    String note = ""

    static constraints = {
        stringValue(nullable: true)
        intValue(nullable: true)
        decValue(nullable: true)
        refValue(nullable: true)
        note(nullable: true)
    }

}

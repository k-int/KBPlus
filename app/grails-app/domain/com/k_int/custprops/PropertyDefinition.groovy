package com.k_int.custprops

import com.k_int.kbplus.LicenceCustomProp
import com.k_int.kbplus.License
import com.k_int.kbplus.RefdataValue
import com.k_int.kbplus.Subscription

import javax.persistence.Transient
import javax.validation.UnexpectedTypeException

class PropertyDefinition {

    String name
    String desc
    String type
    String refdataCategory
    @Transient
    public static final String[] validTypes = [Integer.toString(), String.toString(), RefdataValue.toString(), BigDecimal.toString()]

    static constraints = {
        name(nullable: false, blank: false)
        desc(nullable: false, blank: true)
        type(nullable: false, blank: false)
        refdataCategory(nullable:true)
    }

    static mapping = {
        id column: 'td_id'
        desc column: 'td_description'
        name column: 'td_name', index: 'td_name_idx'
        type column: 'td_type', index: 'td_type_idx'
    }

    private static def typeIsValid(value) {
        if (validTypes.contains(value)) {
            return true;
        } else {
            //log.error("Provided custom prop type ${value.getClass()} is not valid. Allowed types are ${validTypes}")
            throw new UnexpectedTypeException()
        }
    }
    static def lookupOrCreateProp(name, value, owner) {
        typeIsValid(value.getClass().toString())
        lookupOrCreateProp(name, value, '',owner)
    }

    static def lookupOrCreateProp(id, owner){
        if(id instanceof String){
            id = id.toLong()
        }
        def cat = PropertyDefinition.get(id)
        println "PropertyDefinition is "+cat
        createPropertyValue(owner, cat, "")
    }

    static def lookupOrCreateProp(name, value, desc, owner) {
        typeIsValid(value.getClass().toString())
        def cat = lookupOrCreateType(name, value.getClass().toString(), desc)

        createPropertyValue(owner, cat, value)
    }

    private static void createPropertyValue(owner, PropertyDefinition cat, val) {
        def result

        if (owner instanceof License) {
            result = LicenceCustomProp.findByOwnerAndValueToString(cat,val)

            if (!result) {
                def newProp = new LicenceCustomProp(owner: cat,licence: owner);
                newProp.setNote("")
                newProp.setValueToString("")
                newProp.save(flush: true)
                result = newProp
            }
        } else if (owner instanceof Subscription) {

        }
        result
    }

    static def lookupOrCreateType(name, type, desc) {
        typeIsValid(type)
        def cat = PropertyDefinition.findByNameAndTypeAndDesc(name, type, desc);
        if (!cat) {
            cat = new PropertyDefinition(name: name, type: type, desc: desc).save();
        }
        cat
    }
    static def refdataFind(params) {
        def result = []
        def ql = null

        ql = PropertyDefinition.findAllByNameIlike("${params.q}%",params)
        if ( ql ) {
            ql.each { id ->
                result.add([id:"${id.class.name}:${id.id}",text:"${id.name}"])
            }
        }
        result
    }
}


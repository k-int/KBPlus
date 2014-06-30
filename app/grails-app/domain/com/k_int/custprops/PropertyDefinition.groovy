package com.k_int.custprops

import com.k_int.kbplus.RefdataValue
import com.k_int.kbplus.abstract_domain.CustomProperty

import javax.persistence.Transient
import javax.validation.UnexpectedTypeException

class PropertyDefinition {

    String name
    String descr
    String type
    String refdataCategory
    @Transient
    public static final String[] validTypes = [Integer.toString(), String.toString(), RefdataValue.toString(), BigDecimal.toString()]

    static constraints = {
        name(nullable: false, blank: false)
        descr(nullable: false, blank: true)
        type(nullable: false, blank: false)
        refdataCategory(nullable:true)
    }

    static mapping = {
        id column: 'td_id'
        descr column: 'td_description'
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

    static def lookupOrCreateProp(id, owner){
        if(id instanceof String){
            id = id.toLong()
        }
        def type = get(id)
        createPropertyValue(owner, type)
    }

    private static CustomProperty createPropertyValue(owner, PropertyDefinition type) {
        String classString = owner.getClass().toString()
        def ownerClassName = classString.substring(classString.lastIndexOf(".")+1)
        ownerClassName="com.k_int.kbplus."+ownerClassName+"CustomProperty"
        def newProp = Class.forName(ownerClassName).newInstance(type: type,owner: owner)
        newProp.setNote("")
        newProp.save(flush:true)
        newProp
    }

    static def lookupOrCreateType(name, typeClass, descr) {
        typeIsValid(typeClass)
        def type = findByNameAndTypeAndDescr(name, typeClass, descr);
        if (!type) {
            type = new PropertyDefinition(name: name, type: typeClass, descr: descr)
            type.save()
        }
        type
    }
    static def refdataFind(params) {
        def result = []
        def  ql = findAllByNameIlike("${params.q}%",params)
        if ( ql ) {
            ql.each { prop ->
                result.add([id:"${prop.id}",text:"${prop.name}"])
            }
        }
        result
    }
}


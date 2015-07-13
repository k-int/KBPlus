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

    //Map keys can change and they wont affect any of the functionality
    @Transient
    static def validTypes = ["Number":Integer.toString(), 
                             "Text":String.toString(), 
                             "Refdata":RefdataValue.toString(), 
                             "Decimal":BigDecimal.toString()]

    static constraints = {
        name(nullable: false, blank: false, unique:true)
        descr(nullable: true, blank: false)
        type(nullable: false, blank: false)
        refdataCategory(nullable:true)
    }

    static mapping = {
                      id column: 'pd_id'
                   descr column: 'pd_description'
                    name column: 'pd_name', index: 'td_name_idx'
                    type column: 'pd_type', index: 'td_type_idx'
         refdataCategory column: 'pd_rdc', index: 'td_type_idx'
                      sort name: 'desc'
    }

    private static def typeIsValid(value) {
        if (validTypes.containsValue(value)) {
            return true;
        } else {
            println "Provided custom prop type ${value.getClass()} is not valid. Allowed types are ${validTypes}"
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
    /**
    * @param owner: The class that will hold the property, e.g Licence
    **/
    private static CustomProperty createPropertyValue(owner, PropertyDefinition type) {
        String classString = owner.getClass().toString()
        def ownerClassName = classString.substring(classString.lastIndexOf(".")+1)
        ownerClassName="com.k_int.kbplus."+ownerClassName+"CustomProperty"
        def newProp = Class.forName(ownerClassName).newInstance(type: type,owner: owner)
        newProp.setNote("")
        owner.customProperties.add(newProp)
        newProp.save(flush:true)
        newProp
    }

    static def lookupOrCreateType(name, typeClass, descr) {
        typeIsValid(typeClass)
        def type = findByNameAndType(name, typeClass);
        if (!type) {
            print("No PropertyDefinition type match found for ${typeClass}. Creating new.")
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

  @Transient
  def countOccurrences(cls) {
    def qparams = [this]
    def qry = 'select count(c) from '+cls+" as c where c.type = ?"
    return (PropertyDefinition.executeQuery(qry,qparams))[0]; 
  }

  @Transient
  def removeProperty() {
    log.debug("Remove");
    PropertyDefinition.executeUpdate('delete from com.k_int.kbplus.LicenseCustomProperty c where c.type = ?',[this])
    PropertyDefinition.executeUpdate('delete from com.k_int.kbplus.SubscriptionCustomProperty c where c.type = ?',[this])
    this.delete();
  }
}


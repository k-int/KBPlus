package com.k_int.kbplus

import com.k_int.custprops.PropertyDefinition
import javax.validation.UnexpectedTypeException

class LicenceCustomProp{

  String stringValue
  Integer intValue
  BigDecimal decValue
  RefdataValue refValue
  String valueToString = "";
  String note = ""

  static belongsTo = [
	owner: PropertyDefinition,
	licence: License
  ]
  static constraints = {
      stringValue(nullable:true)
      intValue(nullable:true)
      decValue(nullable:true)
      refValue(nullable:true)
      valueToString(nullable:true)
      note(nullable:true)
  }

	static mapping = {
      id column:'lcp_id'
      owner column:'lcp_owner', index:'lcp_entry_idx'
      note column: 'lcp_note'
   	  licence column:'lcp_licence'
  	}

    def setValue(val){
        if(val.getClass()==owner.type) {
            switch (val.getClass()) {
                case Integer:
                    intValue = val
                    valueToString = val.toString()
                    break;
                case String:
                    stringValue = val
                    valueToString = val.toString()
                    break;
                case RefdataValue:
                    refValue = val
                    valueToString = val.toString()
                    break;
                case BigDecimal:
                    decValue = val
                    valueToString = val.toString()
                    break;
                default:
                    throw new UnexpectedTypeException()
            }
        }
    }
  	public String toString(){
  		return valueToString
  	}
}
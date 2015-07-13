package com.k_int.kbplus

import com.k_int.custprops.PropertyDefinition
import com.k_int.kbplus.abstract_domain.CustomProperty
import javax.persistence.Transient

/**Org custom properties are used to store Org related settings and options**/
class OrgCustomProperty extends CustomProperty {
  
  static belongsTo = [
      type : PropertyDefinition,
      owner: Org
  ]
  PropertyDefinition type
  Org owner
}

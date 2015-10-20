package com.k_int.kbplus

import com.k_int.custprops.PropertyDefinition
import com.k_int.kbplus.abstract_domain.CustomProperty
import javax.persistence.Transient

/**SystemAdmin custom properties are used to store system related settings and options**/
class SystemAdminCustomProperty extends CustomProperty {
  
  static belongsTo = [
      type : PropertyDefinition,
      owner: SystemAdmin
  ]
  PropertyDefinition type
  SystemAdmin owner
}

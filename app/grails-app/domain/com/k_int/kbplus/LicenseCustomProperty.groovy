package com.k_int.kbplus

import com.k_int.custprops.PropertyDefinition
import com.k_int.kbplus.abstract_domain.CustomProperty

class LicenseCustomProperty extends CustomProperty {

    static belongsTo = [
            type   : PropertyDefinition,
            owner: License
    ]
}
package com.k_int.kbplus

import javax.persistence.Transient

class CostItemGroup {

  RefdataValue      budgetcode
  CostItem          costItem

  static mapping = {
                id column:'cig_id'
           version column:'cig_version'
       budgetcode  column:'cig_budgetcode_fk'
          costItem column:'cig_costItem_fk'
  }

  static constraints = {
      budgetcode  nullable: true, blank: false
      costItem    nullable: true, blank: false
  }

    @Transient
    static def refdataFind(params) {
        def result     = [];
        def qryResults = null
        def searchTerm = (params.q ? params.q.toLowerCase() : '' ) + "%"
        def owner      = RefdataCategory.findByDesc("budgetcode_"+params.shortcode)

        if (owner)
            qryResults = RefdataValue.findAllByOwnerAndValueIlike(owner,searchTerm)

        qryResults?.each { rdv ->
            result.add([id:rdv.id, text:rdv.value])
        }
        result
    }


}

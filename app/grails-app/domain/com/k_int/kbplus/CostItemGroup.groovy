package com.k_int.kbplus

import javax.persistence.Transient

/**
 * This M to N domain is being used to represent the different budget codes
 * Cost item has many budget codes and budget codes have many cost items ... simples
 * Lookup for GSP is used RefDataCategory, i.e. budgetcode_JISC_COLLECTIONS and then searching RefDataValue for the code
 * Creation of CostItemGroup is dealt with inside FinanceController - createBudgetCodes()
 */
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
        def owner      = RefdataCategory.findByDesc("budgetcode_${params.shortcode}")

        if (!owner) //First run ever...
            new RefdataCategory(desc: "budgetcode_${params.shortcode}").save()

        if (owner)
            qryResults = RefdataValue.findAllByOwnerAndValueIlike(owner,searchTerm)

        qryResults?.each { rdv ->
            result.add([id:rdv.id, text:rdv.value])
        }
        result
    }


}

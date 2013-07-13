package com.k_int.kbplus

class FactService {

  static transactional = false;

    def registerFact(fact) {

      if ( ( fact.type == null ) || 
           ( fact.type == '' ) ) 
        return

      Fact.withTransaction { status ->
        def fact_type_refdata_value = RefdataCategory.lookupOrCreate('FactType',fact.type);

        // Are we updating an existing fact?
        if ( fact.uid != null ) {
          def current_fact = Fact.findByFactTypeAndFactUid(fact_type_refdata_value,fact.uid)
          if ( current_fact == null ) {
            log.debug("Create new fact..");
            current_fact = new Fact(factType:fact_type_refdata_value, 
                                    factFrom:fact.from,
                                    factTo:fact.to,
                                    factValue:fact.value,
                                    factUid:fact.uid,
                                    relatedTitle:fact.title,
                                    supplier:fact.supplier,
                                    inst:fact.inst)
            if ( current_fact.save(flush:true) ) {
            }
            else {
              log.error("Problem saving fact: ${current_fact.errors}");
            }
          }
          else {
            log.debug("update existing fact ${current_fact.id}");
          }
        }
      }
    }
}

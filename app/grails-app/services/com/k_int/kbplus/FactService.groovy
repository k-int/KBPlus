package com.k_int.kbplus

class FactService {

    def registerFact(fact) {

      log.debug("registerFact: ${fact.type} ${fact.start} ${fact.end} ${fact.value} ${fact.facets}");
    }
}

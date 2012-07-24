package com.k_int.kbplus

class GenericReconcilerService {

  static transactional = false

  def grailsApplication

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Reconciler Init");
  }

  def reconcile(s1,s2,ruleset) {
    log.debug("GenericReconcilerService::reconcile");
    processNode(s1, s2, ruleset);
  }

  def processNode(s1, s2, ruleset) {
    log.debug("processNode");
    def domainObject = resolve(s1, ruleset)
  }

  def resolve(node, ruleset) {
    def domainClass = grailsApplication.getArtefact('Domain',ruleset.domainClass).clazz
    def result = null;
    def lookup_rules_i = ruleset.recordMatching.iterator();
    while ( result==null && lookup_rules_i.hasNext() ) {
      def lookup_rule = lookup_rules_i.next();
      def lookupQry = domainClass.createCriteria()
      result = lookupQry.list {
        createLookupQuery(node, lookup_rule, lookupQry)
      }

      //result = lookupQry.get {
      //  eq("impId","jkdsh")
      //}
    }

    log.debug("${result}")
    if ( result ) {
      log.debug("  -> found");
    }
    else { 
      log.debug("  -> Not found");
    }

    result
  }

  def createLookupQuery(node, lookup_rule, criteria) {
    log.debug("Lookup: ${lookup_rule}");
    def result = null
    if ( lookup_rule.matchingType == 'simpleCorrespondence' ) {
      result = criteria.and {
          lookup_rule.pairs.each { p ->
            termClause(node,p, criteria)
          }
        }
    }

    log.debug("createLookupQuery Result = ${result}");
    result
  }

  def termClause(node, term_def, criteria) {
    log.debug("termClause ${term_def.targetProperty} - ${node[term_def.sourceProperty]}");
    def result = criteria.eq(term_def.targetProperty, node[term_def.sourceProperty].toString())
    log.debug("result of termClause is ${result}");
    result
  }
}

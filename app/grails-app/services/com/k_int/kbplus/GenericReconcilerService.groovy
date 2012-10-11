package com.k_int.kbplus

import org.springframework.context.*

class GenericReconcilerService {

  static transactional = false

  def grailsApplication
  ApplicationContext applicationContext

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Reconciler Init");
  }

  def reconcile(mongo_collection, latest_record,saved_historic_info,ruleset) {
    log.debug("GenericReconcilerService::reconcile");
    processNode(mongo_collection, latest_record,saved_historic_info, ruleset);
  }

  def processNode(mongo_collection, latest_record, historic_info, ruleset) {
    log.debug("processNode");
    def domain_object = resolve(latest_record, ruleset)
    log.debug("Result of resolve: ${domain_object}");
    // If there are pending conflicts, just queue up this change

    // Otherwise, check to see if this change would cause a conflict. This applies recursively to all "Local" nodes
    if ( checkForConflicts(domain_object, historic_info.current_copy, latest_record, ruleset) ) {
      // Conflicts were detected, queue up this new object
      log.debug("Conflicts detected. Queue up record");
      historic_info['pending_queue'].add([record:latest_record])
      historic_info.conflict = true;
      mongo_collection.save(historic_info);
    }
    else {
      applyChanges(mongo_collection, latest_record, historic_info, ruleset, domain_object);
    }
  }

  def checkForConflicts(domainObject, originalObject, newObject, ruleset) {
    log.debug("checkForConflicts");
    def result = false
 
    // There can only be conflicts if we have an original object and a database object
    if ( originalObject && domainObject ) {
      ruleset.standardProcessing.each { p ->
        // Currently, only check if the rule is for a simple property copy
        if ( p.targetProperty ) {
          // For each standard processing rule
          if ( originalObject[p.sourceProperty] == newObject[p.sourceProperty] ) {
            // Property has not changed from remote side, leave it alone
          }
          else { // The database value must be == to the original object value, or something has changed and we have a conflict
            if ( originalObject[p.sourceProperty] == domainObject[p.targetProperty] ) {
              // All systems are go, the value has changed, but the old value is the same as the value we
              // have in the database. Therefore, we can update the db to the new value from the datasource
            }
            else {
              // Oh dear, the data source is trying to update a value, but what we have in the db currently
              // is not the same as the previous state of the record. Probably a conflict. Ask the user
              result = true
            }
          }
        }
      }
    }
    else {
      log.debug("No domain object or saved copy.... no conflicts detected");
    }

    log.debug("Result of checkForConflicts: ${result}");
    return result
  }


  /**
   *  Apply all changes from latest record to the domain object. Completed result should be
   *  updated database, and updated historic record. All should complete as a transaction if possible.
   */
  def applyChanges(mongo_collection, latest_record, saved_historic_info, ruleset, domain_object) {
    def original_object = saved_historic_info.current_copy
    log.debug("applyChanges");
    if ( domain_object ) {
      // Existing domain object
      log.debug("Got existing domain object");
    }
    else {
      // We are going to need to create a new domain object and then fill it with data
      log.debug("Create new domain object");
      def domainClass = grailsApplication.getArtefact('Domain',ruleset.domainClass).clazz
      domain_object = domainClass.newInstance();
    }

    if ( domain_object ) {

      // Iterate through the merge rules in the ruleset applying them
      ruleset.standardProcessing.each { p ->
        if ( original_object )
          log.debug("test (${p.sourceProperty}) orig-${original_object[p.sourceProperty]} == latest-${latest_record[p.sourceProperty]}");
        else 
          log.debug("No original copy.. set values");

        if ( ( original_object ) && ( original_object[p.sourceProperty] == latest_record[p.sourceProperty] ) ) {
          // Property has not changed from remote side, leave it alone
        } 
        else {
          if ( p.targetProperty ) {
            // Update the domain object
            log.debug("Setting ${p.targetProperty} to \"${latest_record[p.sourceProperty]}\"");
            domain_object[p.targetProperty] = latest_record[p.sourceProperty]
          }
          else if ( p.processingClosure ) {
            p.processingClosure(latest_record[p.sourceProperty], applicationContext, domain_object)
          }
        }
      }

      if ( domain_object.save() ) {
        log.debug("Domain object saved OK.. Updating latest_record in reconciliation database");
        saved_historic_info.current_copy = latest_record;
        mongo_collection.save(saved_historic_info)
      }
      else {
        log.error("Problem saving domain object ${domain_object}");
        domain_object.errors.each { doe ->
          log.error(doe);
        }
      }
    }
    else {
      throw new RuntimeException("Problem trying to import record - No domain object located or created. ${latest_record}");
    }
  }

  /** Try to look up a domain object that corresponds with a JSON document
   * @param node - The root of the JSON object
   * @param ruleset - The ruleset containing record matching rules
   * 
   * Resolve takes a json object and a ruleset and attempts to look up a domain object
   * that relates to that node. The ruleset must specify an array of recordMatching clauses
   * each clause can have different configs, but "simpleCorrespondence" is the only one implemented
   * currently. In simple correspondence mode, each clause in an individual recordMatching section
   * must have a value in the source json object, and the values from the json object must match
   * the conjunction of query terms from the database.
   */
  def resolve(node, ruleset) {
    def domainClass = grailsApplication.getArtefact('Domain',ruleset.domainClass).clazz
    def result = null;
    def lookup_rules_i = ruleset.recordMatching.iterator();
    while ( result==null && lookup_rules_i.hasNext() ) {
      def lookup_rule = lookup_rules_i.next();
      def lookupQry = domainClass.createCriteria()

      if ( lookupRuleIsApplicable(node, lookup_rule) ) {
        result = lookupQry.get { 
          createLookupQuery(node, lookup_rule, lookupQry)
        }
      }
    }

    result
  }

  def lookupRuleIsApplicable(node, lookup_rule) {
    def result = true
    // In simple correspondence, all properties in the rule must be present in the source JSON object
    if ( lookup_rule.matchingType == 'simpleCorrespondence' ) {
      lookup_rule.pairs.each { p ->
        if ( node[p.sourceProperty] == null )
          result = false;
      }
    }

    result
  }

  def createLookupQuery(node, lookup_rule, criteria) {
    def result = null

    if ( lookup_rule.matchingType == 'simpleCorrespondence' ) {
      result = criteria.and {
        lookup_rule.pairs.each { p ->
          termClause(node,p, criteria)
        }
      }
    }

    result
  }

  def termClause(node, term_def, criteria) {
    def result = null;
    if ( node[term_def.sourceProperty] ) {
      result = criteria.eq(term_def.targetProperty, node[term_def.sourceProperty].toString())
    }
    result
  }
}

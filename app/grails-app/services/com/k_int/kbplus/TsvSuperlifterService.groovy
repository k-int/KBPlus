package com.k_int.kbplus

import java.text.SimpleDateFormat
import org.springframework.transaction.annotation.*
import au.com.bytecode.opencsv.CSVReader


class TsvSuperlifterService {

  def genericOIDService
  def executorService

  def load(input_stream, config, testRun) {
    def result = [:]
    result.log = []

    def ctr = 0;
    def start_time = System.currentTimeMillis()

    CSVReader r = new CSVReader( new InputStreamReader(input_stream, java.nio.charset.Charset.forName('UTF-8') ), '\t' as char )
    String[] nl;
    String[] columns;
    def colmap = [:]
    def first = true

    while ((nl = r.readNext()) != null) {


     def row_information = [ messages:[], error:false]

     def elapsed = System.currentTimeMillis() - start_time

      if ( first ) {
        first = false; // header
        log.debug('Header :'+nl);
        columns=nl
        result.columns = columns;
        // Set up colmap
        int i=0;
        columns.each {
          colmap.put(it,new Integer(i++));
        }
      }
      else {
        row_information.rownum=ctr;
        row_information.rawValues = nl;
        result.log.add(row_information)

        // The process of matching column values to domain objects can result in objects being located or not. Sometimes, several columns may
        // be needed to look up a domain object. Once identified domain objects are put into locatedObjects
        // locatedObjects
        def locatedObjects = [:]
    
        log.debug(nl);

        // We need to see if we can identify any existing domain objects which match the current row in the TSV.
        // We do this using the config.header.targetObjectIdentificationHeuristics list which contains a list of
        // column conjunctions.

        // Cycle through config.header.targetObjectIdentificationHeuristics
        config.header.targetObjectIdentificationHeuristics.each { toih ->
          // For this type [Instances of toih.cls] which will go into locatedObjects with key [toih.ref] iterate over all the
          // diferent ways we have to try and locate such instances. Stop when we have a match -- OR -- carry on and see if we have a conflict?
          def located_objects = []
          toih.heuristics.each { toih_heuristic ->
            // Each heuristic is a conjunction of properties
            def o = locateDomainObject(toih, toih_heuristic, nl, locatedObjects, colmap);
            if ( ( o != null ) && ( o.size() == 1 ) ) {
              row_information.messages.add("Located instance of ${toih.cls} : ${o[0]}");
              located_objects.add(o);
            }
          }

          if ( located_objects.size() == 1 ) {
            row_information.messages.add("Located unique item for ${toih.ref} :: ${located_objects[0]}");
            locatedObjects[toih.ref] = located_objects[0]
          }
          else if ( located_objects.size() > 1 ) {
            row_information.messages.add("Multiple items located for ${toih.ref}. ERROR");
            row_information.error = true;
          }
          else {
            row_information.messages.add("No domain objects located for ${toih.ref} - Check for create instruction");
          }
        }
      }
      ctr++
    }

    result
  }

  private def locateDomainObject(toih, toih_heuristic, nl, locatedObjects, colmap) {
    // try to look up instances of toih.cls using the given heuristic
    def result = null;

    switch ( toih_heuristic.type ) {
      case 'simpleLookup' :
        def qry_params = [:]
        def base_qry = "select i from ${toih.cls} as i where "
        toih_heuristic.criteria.each { clause ->
          // iterate through each clause in the conjunction of clauses that might identify a domian object
          switch ( clause.srcType ) {
            case 'col' :
              base_qry += "i.${clause.domainProperty} = :${clause.colname}"
              qry_params.put(clause.colname,nl[colmap[clause.colname]]);
              break;
          }
        }
        result = TitleInstance.executeQuery(base_qry,qry_params)
        log.debug("Lookup ${toih.ref} using ${base_qry} and params ${qry_params} result:${result}");
        break;

      case 'hql' :
        //  hql: 'select o from Org as o join o.ids as id where id.ns.ns = :jcns and id.value = :orgId',
        // values : [ jcns : [type:'static', value:'JC'], orgId: [type:'column', colname:'InstitutionId'] ] 
        log.debug("HQL Lookup");
        def qry_params=[:]
        toih_heuristic.values.each { k, v ->
          switch ( v.type ) {
            case 'static':
              qry_params[k] = v.value;
              break;
            case 'column':
              qry_params[k] = nl[colmap[v.colname]]
              break;
          }
        }
        log.debug("HQL : ${toih_heuristic.hql}, ${qry_params}");
        result = TitleInstance.executeQuery(toih_heuristic.hql, qry_params);
        break;

      default:
        log.debug("Unhandled heuristic type");
        break;
    }

    return result;
  }
}

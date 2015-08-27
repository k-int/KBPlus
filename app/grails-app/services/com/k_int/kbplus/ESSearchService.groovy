package com.k_int.kbplus
import org.elasticsearch.client.*
import org.elasticsearch.client.Client


class ESSearchService{
// Map the parameter names we use in the webapp with the ES fields
  def reversemap = ['subject':'subject', 
                    'provider':'provid',
                    'type':'rectype',
                    'endYear':'endYear',
                    'startYear':'startYear',
                    'consortiaName':'consortiaName',
                    'cpname':'cpname',
                    'availableToOrgs':'availableToOrgs',
                    'isPublic':'isPublic',
                    'lastModified':'lastModified']

  def ESWrapperService
  def grailsApplication

  def search(params){
    search(params,reversemap)
  }
  def search(params, field_map){
    // log.debug("Search Index, params.coursetitle=${params.coursetitle}, params.coursedescription=${params.coursedescription}, params.freetext=${params.freetext}")
    log.debug("ESSearchService::search - ${params}")

	 def result = [:]

   Client esclient = ESWrapperService.getClient()
  
    try {
      if ( (params.q && params.q.length() > 0) || params.rectype) {
  
        params.max = Math.min(params.max ? params.int('max') : 15, 100)
        params.offset = params.offset ? params.int('offset') : 0

        def query_str = buildQuery(params,field_map)
        log.debug("index:${grailsApplication.config.aggr.es.index} query: ${query_str}");
        if (params.tempFQ) //add filtered query
        {
            query_str = query_str + " AND ( " + params.tempFQ + " ) "
            params.remove("tempFQ") //remove from GSP access
            log.debug("ESSearchService::search -  Adding to query, appending filtered query: ${query_string}")
        }

  
        def search = esclient.search{
          indices grailsApplication.config.aggr.es.index ?: "kbplus"
          source {
            from = params.offset
            size = params.max
            sort = params.sort?[
              ("${params.sort}".toString()) : [ 'order' : (params.order?:'asc') ]
            ] : []

            query {
              query_string (query: query_str)
            }
            facets {
              consortiaName {
                terms {
                  field = 'consortiaName'
                  size = 25
                }
              }
              cpname {
                terms {
                  field = 'cpname'
                  size = 25
                }
              }
              type {
                terms {
                  field = 'rectype'
                }
              }
              startYear {
                terms {
                  field = 'startYear'
                  size = 25
                }
              }
              endYear {
                terms {
                  field = 'endYear'
                  size = 25
                }
              }
            }

          }

        }.actionGet()

        if ( search ) {
          def search_hits = search.hits
          result.hits = search_hits.hits
          result.resultsTotal = search_hits.totalHits

          // We pre-process the facet response to work around some translation issues in ES
          if ( search.getFacets()) {
            result.facets = [:]
            search.getFacets().facets().each { facet ->
              def facet_values = []
              for(entry in facet){
                facet_values.add([term:entry.getTerm(),display:entry.getTerm(),count:entry.getCount()])
              }

              result.facets[facet.getName()] = facet_values
            }
          }
        }
      }
      else {
        log.debug("No query.. Show search page")
      }
    }
    finally {
      try {
      }
      catch ( Exception e ) {
        log.error("problem",e);
      }
    }
    result
  }

  def buildQuery(params,field_map) {
    log.debug("BuildQuery... with params ${params}. ReverseMap: ${field_map}");

    StringWriter sw = new StringWriter()

    if ( ( params != null ) && ( params.q != null ) ){
        if(params.q.equals("*")){ // What was supposed to happen here?
            sw.write(params.q)
        }
        else{
            sw.write(params.q)
        }
    }else{
      sw.write("*:*")
    }
      
    if(params?.rectype){sw.write(" AND rectype:'${params.rectype}'")} 

    field_map.each { mapping ->

      if ( params[mapping.key] != null ) {
        log.debug("Found...");
        if ( params[mapping.key].class == java.util.ArrayList) {

          sw.write(" AND ( ( ( NOT _type:\"com.k_int.kbplus.Subscription\" ) AND ( NOT _type:\"com.k_int.kbplus.License\" )) OR ( ")

          params[mapping.key].each { p ->  
                sw.write(mapping.value)
                sw.write(":")
                sw.write("\"${p}\"")
                if(p == params[mapping.key].last()) {
                  sw.write(" ) ) ")
                }else{
                  sw.write(" OR ")
                }
          }
        }
        else {
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *
          if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
            sw.write(" AND ")
            sw.write(mapping.value)
            sw.write(":")
            if(params[mapping.key].startsWith("[") && params[mapping.key].endsWith("]")){
              sw.write("${params[mapping.key]}")
            }else{
              sw.write("\"${params[mapping.key]}\"")
            }
          }
        }
      }
    }

    def result = sw.toString();
    result;
  }
}

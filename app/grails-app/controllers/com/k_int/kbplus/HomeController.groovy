package com.k_int.kbplus

import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;

class HomeController {

  def ESWrapperService
  def gazetteerService
  def springSecurityService

  // Map the parameter names we use in the webapp with the ES fields
  def reversemap = ['subject':'subject', 'provider':'provid', 'studyMode':'presentations.studyMode','qualification':'qual.type','level':'qual.level' ]
  
  def index() { 
    // log.debug("Search Index, params.coursetitle=${params.coursetitle}, params.coursedescription=${params.coursedescription}, params.freetext=${params.freetext}")
    log.debug("Search Index, params.q=${params.q}, format=${params.format}")

    def pagename = 'index'
    def result = [:]

    // Get hold of some services we might use ;)
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
    // result.user = User.get(springSecurityService.principal.id)
    result.user = springSecurityService.getCurrentUser()

    if (springSecurityService.isLoggedIn()) {
  
      try {
        if ( params.q && params.q.length() > 0) {
    
          params.max = Math.min(params.max ? params.int('max') : 10, 100)
          params.offset = params.offset ? params.int('offset') : 0
    
          //def params_set=params.entrySet()
          
          def query_str = buildQuery(params)
          log.debug("query: ${query_str}");
    
          def search = esclient.search{
            indices "kbplus"
            source {
              from = params.offset
              size = params.max
              query {
                query_string (query: query_str)
              }
              facets {
                type {
                  terms {
                    field = 'rectype'
                  }
                }
              }
  
            }
  
          }
  
          if ( search?.response ) {
            result.hits = search.response.hits
            result.resultsTotal = search.response.hits.totalHits
  
            // We pre-process the facet response to work around some translation issues in ES
            if ( search.response.facets != null ) {
              result.facets = [:]
              search.response.facets.facets.each { facet ->
                def facet_values = []
                facet.value.entries.each { fe ->
                  facet_values.add([term: fe.term,display:fe.term,count:"${fe.count}"])
                }
                result.facets[facet.key] = facet_values
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
  
    }  // If logged in

    withFormat {
      html {
        render(view:'index',model:result)
      }
      rss {
        renderRSSResponse(result)
      }
      atom {
        renderATOMResponse( result,params.max )
      }
      xml {
        render result as XML
      }
      json {
        render result as JSON
      }
    }
  }

  def buildQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    if ( ( params != null ) && ( params.q != null ) )
        if(params.q.equals("*")){
            sw.write(params.q)
        }
        else{
            sw.write(params.q)
        }
    else
      sw.write("*:*")
      
    reversemap.each { mapping ->

      // log.debug("testing ${mapping.key}");

      if ( params[mapping.key] != null ) {
        if ( params[mapping.key].class == java.util.ArrayList) {
          params[mapping.key].each { p ->  
                sw.write(" AND ")
                sw.write(mapping.value)
                sw.write(":")
                sw.write("\"${p}\"")
          }
        }
        else {
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *
          if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
            sw.write(" AND ")
            sw.write(mapping.value)
            sw.write(":")
            sw.write("\"${params[mapping.key]}\"")
          }
        }
      }
    }

    def result = sw.toString();
    result;
  }

  def renderRSSResponse(results) {

    def output_elements = buildOutputElements(results.hits)

    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)

    xml.rss(version: '2.0') {
      channel {
        title("KBPlus")
        description("KBPlus")
        "opensearch:totalResults"(results.hits.totalHits)
        // "opensearch:startIndex"(results.search_results.results.start)
        "opensearch:itemsPerPage"(10)
        output_elements.each { i ->  // For each record
          entry {
            i.each { tuple ->   // For each tuple in the record
              "${tuple[0]}"("${tuple[1]}")
            }
          }
        }
      }
    }

    render(contentType:"application/rss+xml", text: writer.toString())
  }


  def renderATOMResponse(results,hpp) {

    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)

    def output_elements = buildOutputElements(results.hits)

    xml.feed(xmlns:'http://www.w3.org/2005/Atom') {
        // add the top level information about this feed.
        title("KBPlus")
        description("KBPlus")
        "opensearch:totalResults"(results.hits.totalHits)
        // "opensearch:startIndex"(results.search_results.results.start)
        "opensearch:itemsPerPage"("${hpp}")
        // subtitle("Serving up my content")
        //id("uri:uuid:xxx-xxx-xxx-xxx")
        link(href:"http://a.b.c")
        author {
          name("KBPlus")
        }
        //updated sdf.format(new Date());

        // for each entry we need to create an entry element
        output_elements.each { i ->
          entry {
            i.each { tuple ->
                "${tuple[0]}"("${tuple[1]}")
            }
          }
        }
    }

    render(contentType:'application/xtom+xml', text: writer.toString())
  }

  def buildOutputElements(searchresults) {
    // Result is an array of result elements
    def result = []

    searchresults.hits?.each { doc ->
      ////  log.debug("adding ${doc} ${doc.source.title}");
      def docinfo = [];

      docinfo.add(['dc.title',doc.source.title])
      docinfo.add(['dc.description',doc.source.description])
      docinfo.add(['dc.identifier',doc.source._id])
      result.add(docinfo)
    }
    result
  }

  def count = {
      
    def result = [:]
    // Get hold of some services we might use ;)
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

    
    if ( params.q && params.q.length() > 0)
    {
        def query_str = buildQuery(params)
        log.debug("count query: ${query_str}");
                       
        def search = esclient.count{
            indices "courses"
            types "course"
            query {
              query_string (query: query_str)
            }
        }
        
        result.hits = search.response.count
    }
    
    render result as JSON
  }

}

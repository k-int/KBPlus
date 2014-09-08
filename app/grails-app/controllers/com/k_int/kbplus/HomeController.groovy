package com.k_int.kbplus

import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;

class HomeController {

  def springSecurityService
  def ESSearchService
  
 
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)

    if ( result.user?.defaultDash != null ) {
      redirect(controller:'myInstitutions', action:'dashboard', params:[shortcode:result.user.defaultDash.shortcode]);
    }
    else {
      if ( result.user.affiliations.size() == 1 ) {
        result.user.defaultDash = result.user.affiliations.first().org
        result.user.save();
        redirect(controller:'myInstitutions', action:'dashboard',
         params:[shortcode:result.user.defaultDash.shortcode]);
      }
      else {
        flash.message="Please select an institution to use as your default home dashboard"
        redirect(controller:'profile', action:'index')
      }
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def search() { 

    def result = [:]
  
    result.user = springSecurityService.getCurrentUser()
    params.max = result.user.defaultPageSize

    if (springSecurityService.isLoggedIn()) {
      result =  ESSearchService.search(params)     
    }  
    withFormat {
      html {
        render(view:'search',model:result)
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

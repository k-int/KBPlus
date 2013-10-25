package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.apache.log4j.*
import java.text.SimpleDateFormat
import com.k_int.kbplus.*;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class TitleDetailsController {

  def springSecurityService
  def ESWrapperService

  def title_qry_reversemap = ['subject':'subject', 'provider':'provid', 'pkgname':'tokname' ]


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def findTitleMatches() { 
    // find all titles by n_title proposedTitle
    def result=[:]
    if ( params.proposedTitle ) {
      // def proposed_title_key = com.k_int.kbplus.TitleInstance.generateKeyTitle(params.proposedTitle)
      // result.titleMatches=com.k_int.kbplus.TitleInstance.findAllByKeyTitle(proposed_title_key)
      def normalised_title = com.k_int.kbplus.TitleInstance.generateNormTitle(params.proposedTitle)
      result.titleMatches=com.k_int.kbplus.TitleInstance.findAllByNormTitleLike("${normalised_title}%")
    }
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def createTitle() {
    log.debug("Create new title for ${params.title}");
    def new_title = new TitleInstance(title:params.title, impId:java.util.UUID.randomUUID().toString())
    
    if ( new_title.save(flush:true) ) {
      log.debug("New title id is ${new_title.id}");
      redirect ( action:'edit', id:new_title.id);
    }
    else {
      log.error("Problem creating title: ${new_title.errors}");
      flash.message = "Problem creating title: ${new_title.errors}"
      redirect ( action:'findTitleMatches' )
    }
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def edit() {
    def result = [:]

    if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') )
      result.editable=true
    else
      result.editable=false

    result.ti = TitleInstance.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def show() {
    def result = [:]

    if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') )
      result.editable=true
    else
      result.editable=false

    result.ti = TitleInstance.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {

    log.debug("titleSearch : ${params}");

    StringWriter sw = new StringWriter()
    def fq = null;
    boolean has_filter = false

    params.each { p ->
      if ( p.key.startsWith('fct:') && p.value.equals("on") ) {
        log.debug("start year ${p.key} : -${p.value}-");

        if ( !has_filter )
          has_filter = true
        else
          sw.append(" AND ")

        String[] filter_components = p.key.split(':');

        switch ( filter_components[1] ) { 
          case 'consortiaName':
                sw.append('consortiaName')
                break;
          case 'startYear':
                sw.append('startYear')
                break;
          case 'cpname':
                sw.append('cpname')
                break;
        }

        if ( filter_components[2].indexOf(' ') > 0 ) { 
          sw.append(":\"");
          sw.append(filter_components[2])
          sw.append("\"");
        }
        else {
          sw.append(":");
          sw.append(filter_components[2])
        }
      }
    }

    if ( has_filter ) {
      fq = sw.toString();
      log.debug("Filter Query: ${fq}");
    }

    // Be mindful that the behavior of this controller is strongly influenced by the schema setup in ES.
    // Specifically, see KBPlus/import/processing/processing/dbreset.sh for the mappings that control field type and analysers
    // Internal testing with http://localhost:9200/kbplus/_search?q=subtype:'Subscription%20Offered'
    def result=[:]

    // Get hold of some services we might use ;)
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
    result.user = springSecurityService.getCurrentUser()

    if (springSecurityService.isLoggedIn()) {
      try {

        params.max = Math.min(params.max ? params.int('max') : result.user.defaultPageSize, 100)
        params.offset = params.offset ? params.int('offset') : 0
 
        def query_str = buildTitleQuery(params)
        if ( fq )
          query_str = query_str + " AND ( " + fq + " ) "

        log.debug("query: ${query_str}");
        result.es_query = query_str;
 
        def search = esclient.search{
          indices "kbplus"
          source {
            from = params.offset
            size = params.max
            sort = [
              'sortname' : [ 'order' : 'asc' ]
            ]
            query {
              query_string (query: query_str)
            }
            facets {
            }
          }
        }

        if ( search?.response ) {
          log.debug("Found ${search.response.hits} titles");
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
        else {
          log.error("No search response for title search");
        }
      }
      finally {
        try {
        }
        catch ( Exception e ) {
          log.error("problem",e);
        }
      }
    }
    result
  }

  def buildTitleQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    // sw.write("subtype:'Subscription Offered'")
    sw.write("rectype:'Title'")

    title_qry_reversemap.each { mapping ->

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

}

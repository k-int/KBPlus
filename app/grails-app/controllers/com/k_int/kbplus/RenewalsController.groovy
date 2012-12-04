package com.k_int.kbplus


import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class RenewalsController {

  // Map the parameter names we use in the webapp with the ES fields
  def reversemap = ['subject':'subject', 
                    'provider':'provid', 
                    'pkgname':'name'
                   ]


  def springSecurityService
  def ESWrapperService

  def index() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def search() {

    log.debug("Search : ${params}");
    // Be mindful that the behavior of this controller is strongly influenced by the schema setup in ES.
    // Specifically, see KBPlus/import/processing/processing/dbreset.sh for the mappings that control field type and analysers
    // Internal testing with http://localhost:9200/kbplus/_search?q=subtype:'Subscription%20Offered'
    def result=[:]

    // Get hold of some services we might use ;)
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
    result.user = springSecurityService.getCurrentUser()

    def shopping_basket = UserFolder.findByUserAndShortcode(result.user,'SOBasket') ?: new UserFolder(user:result.user, shortcode:'SOBasket').save();

    if (springSecurityService.isLoggedIn()) {

      try {

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
                consortia {
                  terms {
                    field = 'consortiaName'
                  }
                }
                contentProvider {
                  terms {
                    field = 'packages.cpname'
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
      finally {
        try {
        }
        catch ( Exception e ) {
          log.error("problem",e);
        }
      }

    }  // If logged in

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def selectPackages() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)

    result.candidates = [:]
    def title_list = []
    def package_list = []

    result.titles_in_this_sub = result.subscriptionInstance.issueEntitlements.size();

    result.subscriptionInstance.issueEntitlements.each { e ->
      def title = e.tipp.title
      log.debug("Looking for packages offering title ${title.id} - ${title?.title}");

      title.tipps.each { t ->
        log.debug("  -> This title is provided by package ${t.pkg.id} on platform ${t.platform.id}");

        def title_idx = title_list.indexOf("${title.id}");
        def pkg_idx = package_list.indexOf("${t.pkg.id}:${t.platform.id}");

        if ( title_idx == -1 ) {
          log.debug("  -> Adding title ${title.id} to matrix result");
          title_list.add("${title.id}");
          title_idx = title_list.size();
        }

        if ( pkg_idx == -1 ) {
          log.debug("  -> Adding package ${t.pkg.id} to matrix result");
          package_list.add("${t.pkg.id}:${t.platform.id}");
          pkg_idx = package_list.size();
        }

        log.debug("  -> title_idx is ${title_idx} pkg_idx is ${pkg_idx}");

        def candidate = result.candidates["${t.pkg.id}:${t.platform.id}"]
        if ( !candidate ) {
          candidate = [:]
          result.candidates["${t.pkg.id}:${t.platform.id}"] = candidate;
          candidate.pkg=t.pkg.id
          candidate.platform=t.platform
          candidate.titlematch=0
          candidate.pkg = t.pkg
          candidate.pkg_title_count = t.pkg.tipps.size();
        }
        candidate.titlematch++;
        log.debug("  -> updated candidate ${candidate}");
      }
    }

    log.debug("titles list ${title_list}");
    log.debug("package list ${package_list}");

    log.debug("titles list size ${title_list.size()}");
    log.debug("package list size ${package_list.size()}");
    result
  }

  def buildQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    sw.write("subtype:'Subscription Offered'")

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

}

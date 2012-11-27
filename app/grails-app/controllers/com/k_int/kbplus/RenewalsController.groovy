package com.k_int.kbplus


import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class RenewalsController {

  def springSecurityService
  def ESWrapperService

  def index() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
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
}

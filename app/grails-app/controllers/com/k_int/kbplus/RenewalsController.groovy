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

  def selectPackages() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)

    result.candidates = [:]

    result.subscriptionInstance.issueEntitlements.each { e ->
      def title = e.tipp.title
      log.debug("Looking for packages offering title ${title.id} - ${title?.title}");
      title.tipps.each { t ->
        log.debug("  -> This title is provided by package ${t.pkg.id} on platform ${t.platform.id}");
        def candidate = result.candidates["${t.pkg.id}:${t.platform.id}"]
        if ( !candidate ) {
          candidate = [:]
          result.candidates["${t.pkg.id}:${t.platform.id}"] = candidate;
          candidate.pkg=t.pkg.id
          candidate.platform=t.platform.id
          candidate.titlematch=0
        }
        candidate.titlematch++;
        log.debug("updated candidate ${candidate}");
      }
    }

    result
  }
}

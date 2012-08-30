package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class AlertController {

  def springSecurityService

  def commentsFragment() { 
    def result = [:]
    if ( params.id ) {
      result.alert = Alert.get(params.id)
    }
    result
  }

  def addComment() {
    log.debug("Adding comment ${params.newcomment} on alert ${params.alertid}");
    def user = User.get(springSecurityService.principal.id)
    if ( params.alertid ) {
      def alert = Alert.get(params.alertid)
      Comment c = new Comment(commentDate:new Date(), comment:params.newcomment, by:user, alert: alert)
      if ( ! c.save(flush:true) ) {
        c.errors.each { ce ->
          log.error("Problem saving commentk ${ce}");
        }
      }
    }
    redirect(url: request.getHeader('referer'))
  }
}

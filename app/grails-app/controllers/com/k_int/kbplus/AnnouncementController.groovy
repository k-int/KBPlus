package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;
import java.text.SimpleDateFormat

class AnnouncementController {

  def springSecurityService
  def alertsService
  def genericOIDService


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def createAnnouncement() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    flash.message="Annoucement Created"
    redirect(action:'index')
  }

}

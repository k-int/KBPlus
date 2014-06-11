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


  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    def announcement_type = RefdataCategory.lookupOrCreate('Document Type','Announcement')
    result.recentAnnouncements = Doc.findAllByType(announcement_type,[max:10,sort:'dateCreated',order:'desc'])
    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def createAnnouncement() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    flash.message="Announcement Created"
    def announcement_type = RefdataCategory.lookupOrCreate('Document Type','Announcement')
    def new_announcement = new Doc(title:params.subjectTxt, 
                                   content:params.annTxt, 
                                   user:result.user, 
                                   type:announcement_type, 
                                   contentType:0 ).save(flush:true)
    redirect(action:'index')
  }

}

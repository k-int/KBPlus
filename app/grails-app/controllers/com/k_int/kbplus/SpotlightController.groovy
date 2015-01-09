package com.k_int.kbplus
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class SpotlightController {
  def ESSearchService
  def springSecurityService
  def dataloadService


  def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()


  def index() { 
    log.debug("spotlight::index");
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def managePages(){
    def result = [:]
    result.user = springSecurityService.getCurrentUser()

    def isAdmin = SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')

    if ( isAdmin  ) {
       request.setAttribute("editable","true")
    }

    def newPage

    if(request.getMethod() == "POST"){
      if(params.newCtrl && params.newAction && params.newAlias){
        newPage = 
        new SitePage(controller:params.newCtrl,action:params.newAction, alias:params.newAlias).save(flush:true)
      }else{
        flash.error=message(code: 'spotlight.new.missingprop') 
      }      
    }else if(params.id){
       newPage = SitePage.get(params.id)
       newPage.delete(flush:true)
    }

    if(newPage){
      if(newPage.hasErrors()){
          log.error(newPage.errors)
          result.newPage = newPage
        }else{
          updateSiteES()
        }
    }

    result.pages = SitePage.findAll()

    result

  }
  def updateSiteES(){
      dataloadService.updateSiteMapping()
  }
  def search() { 
    log.debug("spotlight::search");
    def result = [:]
    def filtered
    def query = params.query
    result.user = springSecurityService.getCurrentUser()
    params.max = result.user.defaultPageSize ?: 15
   
    if(! query){
      return result
    }

    if (springSecurityService.isLoggedIn()) {
      if (query.startsWith("\$") && query.length() > 2 && query.indexOf(" ") != -1 ) {
        def filter = query.substring(0,query.indexOf(" "))
        switch (filter) {
          case "\$t":
            params.type = "title"
            query = query.replace("\$t  ","")
            filtered = "Title Instance"
            break
          case "\$pa":
            params.type = "package"
            query = query.replace("\$pa ","")
            filtered = "Package"
            break
          case "\$p":
            params.type = "package"
            query = query.replace("\$p ","")
            filtered = "Package"
            break
          case "\$pl":
            params.type = "platform"
            query = query.replace("\$pl ","")
            filtered = "Platform"
            break;
          case "\$s":
            params.type = "subscription"
            query = query.replace("\$s ","")     
            filtered = "Subscription"     
            break
          case "\$o":
            params.type = "organisation"
            query = query.replace("\$o ","")
            filtered = "Organisation"
            break
          case "\$l":
            params.type = "license"
            query = query.replace("\$l ","")
            filtered = "License"
            break
          case "\$a":
            params.type = "action"
            query = query.replace("\$a ","")
            filtered = "Action"
        }

      }
      params.q = query
      params.availableToOrgs = result.user.getAuthorizedOrgs().collect{it.id}

      if(query.startsWith("\$")){
        if( query.length()> 2){
        result = ESSearchService.search(params)
        }
      }else{
        result = ESSearchService.search(params)
      }
      result.filtered = filtered
        // result?.facets?.type?.pop()?.term
    }
    result
  }

  def getActionLinks(q) {
    def result = []
    result = allActions
    return result;
  }
}

package ediauthtest

import grails.plugins.springsecurity.Secured



class SecuredController {

  def springSecurityService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    log.debug("secured::index()");
  }
}

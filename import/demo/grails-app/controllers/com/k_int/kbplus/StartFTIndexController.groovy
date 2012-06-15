package com.k_int.kbplus

import grails.plugins.springsecurity.Secured

class StartFTIndexController {

  def dataloadService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    log.debug("manual start full text index");
    dataloadService.updateFTIndexes();
    log.debug("redirecting to home...");
    redirect(controller:'home')
  }
}

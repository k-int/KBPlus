package com.k_int.kbplus

class SpotlightController {

  def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()

  def allActions = [
    [ linktext:'My Institutions', url:g.createLink(controller:'myInstitutions') ]
  ]

  def index() { 
    log.debug("spotlight::index");
  }

  def search() { 
    log.debug("spotlight::search");
    def result = [:]
    result.resultCategories = []

    def actionLinks = getActionLinks(params.q)
    if ( ( actionLinks ) && ( actionLinks.size() > 0 ) ) {
      result.resultCategories.add([name:'Actions', results:actionLinks]);
    }

    result
  }

  def getActionLinks(q) {
    def result = []
    result = allActions
    return result;
  }
}

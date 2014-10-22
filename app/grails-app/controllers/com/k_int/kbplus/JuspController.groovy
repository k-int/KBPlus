package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import com.k_int.kbplus.auth.User
import grails.plugins.springsecurity.Secured
import grails.converters.*
import com.k_int.custprops.PropertyDefinition
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil

class JuspController {

  def index() {
    log.debug("JuspController");
  }

  def titleInfo() {
    log.debug("JuspController::TitleInfo ${params}");
    // Pass in a Jusp Title Identifier and a Jusp Institition Identifier and a Jusp Content Provider
    // to get back a form which will allow the user to edit the Core status of the identified tipp as a Jusp Title

  }

}

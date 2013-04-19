package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class TippController {

 def springSecurityService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def show() { 
    def result = [:]

    result.user = User.get(springSecurityService.principal.id)
    result.editable = true

    result.tipp = TitleInstancePackagePlatform.get(params.id)
    result.titleInstanceInstance = result.tipp.title

    if (!result.titleInstanceInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
      redirect action: 'list'
      return
    }

    result

  }
}

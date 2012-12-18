package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class TippController {

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def show() { 
    def tipp = TitleInstancePackagePlatform.get(params.id)
    def titleInstanceInstance = tipp.title

    if (!titleInstanceInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
      redirect action: 'list'
      return
    }

    [tipp:tipp, titleInstanceInstance: titleInstanceInstance]

  }
}

package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import grails.gorm.*

class UserDetailsController {

    def springSecurityService

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def list() {

      def result = [:]
      result.user = User.get(springSecurityService.principal.id)

      params.max = Math.min(params.max ? params.int('max') : 10, 100)
      def results = null;
      def count = null;

      def criteria = new DetachedCriteria(User).build {
        if ( params.name && params.name != '' ) {
          or {
            ilike('username',"%${params.name}%")
            ilike('display',"%${params.name}%")
            ilike('instname',"%${params.name}%")
          }
        }
      }

      result.users = criteria.list(params)
      result.total = criteria.count()

      result
    }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def edit() {
    def result = [:]
    def userInstance = User.get(params.id)
    if (!userInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'Org'), params.id])
        redirect action: 'list'
        return
    }
    result.ui = userInstance
    result
  }    

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def pub() {
    def result = [:]
    def userInstance = User.get(params.id)
    result.ui = userInstance
    result
  }    

  
}

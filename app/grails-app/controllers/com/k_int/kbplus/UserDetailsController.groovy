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
    def genericOIDService

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
      println params.authority
      def criteria = new DetachedCriteria(User).build {
        if ( params.name && params.name != '' ) {
          or {
            ilike('username',"%${params.name}%")
            ilike('display',"%${params.name}%")
            ilike('instname',"%${params.name}%")
          }
        }
        if(params.authority){
          def filter_role = Role.get(params.authority.toLong())
          if(filter_role){
              roles{
                eq('role',filter_role)
              }
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
    result.user = User.get(springSecurityService.principal.id)
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
    result.user = User.get(springSecurityService.principal.id)
    def userInstance = User.get(params.id)
    result.ui = userInstance
    result
  }    

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def create() {
    switch (request.method) {
      case 'GET':
        [orgInstance: new Org(params)]
        break
      case 'POST':
        def userInstance = new User(params)
        if (!userInstance.save(flush: true)) {
          render view: 'create', model: [userInstance: userInstance]
          return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect action: 'show', id: userInstance.id
        break
    }
  }
  
  
}

package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;


class TitleInstanceController {

    def springSecurityService
    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      params.max = Math.min(params.max ? params.int('max') : 10, 100)
      result.titleInstanceInstanceList=TitleInstance.list(params)
      result.titleInstanceInstanceTotal=TitleInstance.count()
      result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
		switch (request.method) {
		case 'GET':
        	[titleInstanceInstance: new TitleInstance(params)]
			break
		case 'POST':
	        def titleInstanceInstance = new TitleInstance(params)
	        if (!titleInstanceInstance.save(flush: true)) {
	            render view: 'create', model: [titleInstanceInstance: titleInstanceInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), titleInstanceInstance.id])
	        redirect action: 'show', id: titleInstanceInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
        def titleInstanceInstance = TitleInstance.get(params.id)
        if (!titleInstanceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
            redirect action: 'list'
            return
        }

        [titleInstanceInstance: titleInstanceInstance]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
		switch (request.method) {
		case 'GET':
	        def titleInstanceInstance = TitleInstance.get(params.id)
	        if (!titleInstanceInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [titleInstanceInstance: titleInstanceInstance]
			break
		case 'POST':
	        def titleInstanceInstance = TitleInstance.get(params.id)
	        if (!titleInstanceInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (titleInstanceInstance.version > version) {
	                titleInstanceInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'titleInstance.label', default: 'TitleInstance')] as Object[],
	                          "Another user has updated this TitleInstance while you were editing")
	                render view: 'edit', model: [titleInstanceInstance: titleInstanceInstance]
	                return
	            }
	        }

	        titleInstanceInstance.properties = params

	        if (!titleInstanceInstance.save(flush: true)) {
	            render view: 'edit', model: [titleInstanceInstance: titleInstanceInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), titleInstanceInstance.id])
	        redirect action: 'show', id: titleInstanceInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def titleInstanceInstance = TitleInstance.get(params.id)
        if (!titleInstanceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
            redirect action: 'list'
            return
        }

        try {
            titleInstanceInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

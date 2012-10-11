package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;



class TitleInstancePackagePlatformController {

    def springSecurityService
    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
      def result=[:]
      result.user = User.get(springSecurityService.principal.id)
      params.max = Math.min(params.max ? params.int('max') : 10, 100)
      result.titleInstancePackagePlatformInstanceList=TitleInstancePackagePlatform.list(params)
      result.titleInstancePackagePlatformInstanceTotal=TitleInstancePackagePlatform.count()
      result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
		switch (request.method) {
		case 'GET':
        	[titleInstancePackagePlatformInstance: new TitleInstancePackagePlatform(params)]
			break
		case 'POST':
	        def titleInstancePackagePlatformInstance = new TitleInstancePackagePlatform(params)
	        if (!titleInstancePackagePlatformInstance.save(flush: true)) {
	            render view: 'create', model: [titleInstancePackagePlatformInstance: titleInstancePackagePlatformInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform'), titleInstancePackagePlatformInstance.id])
	        redirect action: 'show', id: titleInstancePackagePlatformInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
        def titleInstancePackagePlatformInstance = TitleInstancePackagePlatform.get(params.id)
        if (!titleInstancePackagePlatformInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform'), params.id])
            redirect action: 'list'
            return
        }

        [titleInstancePackagePlatformInstance: titleInstancePackagePlatformInstance]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
		switch (request.method) {
		case 'GET':
	        def titleInstancePackagePlatformInstance = TitleInstancePackagePlatform.get(params.id)
	        if (!titleInstancePackagePlatformInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [titleInstancePackagePlatformInstance: titleInstancePackagePlatformInstance]
			break
		case 'POST':
	        def titleInstancePackagePlatformInstance = TitleInstancePackagePlatform.get(params.id)
	        if (!titleInstancePackagePlatformInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (titleInstancePackagePlatformInstance.version > version) {
	                titleInstancePackagePlatformInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform')] as Object[],
	                          "Another user has updated this TitleInstancePackagePlatform while you were editing")
	                render view: 'edit', model: [titleInstancePackagePlatformInstance: titleInstancePackagePlatformInstance]
	                return
	            }
	        }

	        titleInstancePackagePlatformInstance.properties = params

	        if (!titleInstancePackagePlatformInstance.save(flush: true)) {
	            render view: 'edit', model: [titleInstancePackagePlatformInstance: titleInstancePackagePlatformInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform'), titleInstancePackagePlatformInstance.id])
	        redirect action: 'show', id: titleInstancePackagePlatformInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def titleInstancePackagePlatformInstance = TitleInstancePackagePlatform.get(params.id)
        if (!titleInstancePackagePlatformInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform'), params.id])
            redirect action: 'list'
            return
        }

        try {
            titleInstancePackagePlatformInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

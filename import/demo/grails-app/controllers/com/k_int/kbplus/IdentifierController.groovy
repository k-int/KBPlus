package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured


class IdentifierController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [identifierInstanceList: Identifier.list(params), identifierInstanceTotal: Identifier.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[identifierInstance: new Identifier(params)]
			break
		case 'POST':
	        def identifierInstance = new Identifier(params)
	        if (!identifierInstance.save(flush: true)) {
	            render view: 'create', model: [identifierInstance: identifierInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'identifier.label', default: 'Identifier'), identifierInstance.id])
	        redirect action: 'show', id: identifierInstance.id
			break
		}
    }

    def show() {
        def identifierInstance = Identifier.get(params.id)
        if (!identifierInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'identifier.label', default: 'Identifier'), params.id])
            redirect action: 'list'
            return
        }

        [identifierInstance: identifierInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def identifierInstance = Identifier.get(params.id)
	        if (!identifierInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'identifier.label', default: 'Identifier'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [identifierInstance: identifierInstance]
			break
		case 'POST':
	        def identifierInstance = Identifier.get(params.id)
	        if (!identifierInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'identifier.label', default: 'Identifier'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (identifierInstance.version > version) {
	                identifierInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'identifier.label', default: 'Identifier')] as Object[],
	                          "Another user has updated this Identifier while you were editing")
	                render view: 'edit', model: [identifierInstance: identifierInstance]
	                return
	            }
	        }

	        identifierInstance.properties = params

	        if (!identifierInstance.save(flush: true)) {
	            render view: 'edit', model: [identifierInstance: identifierInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'identifier.label', default: 'Identifier'), identifierInstance.id])
	        redirect action: 'show', id: identifierInstance.id
			break
		}
    }

    def delete() {
        def identifierInstance = Identifier.get(params.id)
        if (!identifierInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'identifier.label', default: 'Identifier'), params.id])
            redirect action: 'list'
            return
        }

        try {
            identifierInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'identifier.label', default: 'Identifier'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'identifier.label', default: 'Identifier'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

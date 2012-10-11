package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured


class IdentifierOccurrenceController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [identifierOccurrenceInstanceList: IdentifierOccurrence.list(params), identifierOccurrenceInstanceTotal: IdentifierOccurrence.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[identifierOccurrenceInstance: new IdentifierOccurrence(params)]
			break
		case 'POST':
	        def identifierOccurrenceInstance = new IdentifierOccurrence(params)
	        if (!identifierOccurrenceInstance.save(flush: true)) {
	            render view: 'create', model: [identifierOccurrenceInstance: identifierOccurrenceInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence'), identifierOccurrenceInstance.id])
	        redirect action: 'show', id: identifierOccurrenceInstance.id
			break
		}
    }

    def show() {
        def identifierOccurrenceInstance = IdentifierOccurrence.get(params.id)
        if (!identifierOccurrenceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence'), params.id])
            redirect action: 'list'
            return
        }

        [identifierOccurrenceInstance: identifierOccurrenceInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def identifierOccurrenceInstance = IdentifierOccurrence.get(params.id)
	        if (!identifierOccurrenceInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [identifierOccurrenceInstance: identifierOccurrenceInstance]
			break
		case 'POST':
	        def identifierOccurrenceInstance = IdentifierOccurrence.get(params.id)
	        if (!identifierOccurrenceInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (identifierOccurrenceInstance.version > version) {
	                identifierOccurrenceInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence')] as Object[],
	                          "Another user has updated this IdentifierOccurrence while you were editing")
	                render view: 'edit', model: [identifierOccurrenceInstance: identifierOccurrenceInstance]
	                return
	            }
	        }

	        identifierOccurrenceInstance.properties = params

	        if (!identifierOccurrenceInstance.save(flush: true)) {
	            render view: 'edit', model: [identifierOccurrenceInstance: identifierOccurrenceInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence'), identifierOccurrenceInstance.id])
	        redirect action: 'show', id: identifierOccurrenceInstance.id
			break
		}
    }

    def delete() {
        def identifierOccurrenceInstance = IdentifierOccurrence.get(params.id)
        if (!identifierOccurrenceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence'), params.id])
            redirect action: 'list'
            return
        }

        try {
            identifierOccurrenceInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

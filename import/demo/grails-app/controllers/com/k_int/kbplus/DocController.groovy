package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException

class DocController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [docInstanceList: Doc.list(params), docInstanceTotal: Doc.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[docInstance: new Doc(params)]
			break
		case 'POST':
	        def docInstance = new Doc(params)
	        if (!docInstance.save(flush: true)) {
	            render view: 'create', model: [docInstance: docInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'doc.label', default: 'Doc'), docInstance.id])
	        redirect action: 'show', id: docInstance.id
			break
		}
    }

    def show() {
        def docInstance = Doc.get(params.id)
        if (!docInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'doc.label', default: 'Doc'), params.id])
            redirect action: 'list'
            return
        }

        [docInstance: docInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def docInstance = Doc.get(params.id)
	        if (!docInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'doc.label', default: 'Doc'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [docInstance: docInstance]
			break
		case 'POST':
	        def docInstance = Doc.get(params.id)
	        if (!docInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'doc.label', default: 'Doc'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (docInstance.version > version) {
	                docInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'doc.label', default: 'Doc')] as Object[],
	                          "Another user has updated this Doc while you were editing")
	                render view: 'edit', model: [docInstance: docInstance]
	                return
	            }
	        }

	        docInstance.properties = params

	        if (!docInstance.save(flush: true)) {
	            render view: 'edit', model: [docInstance: docInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'doc.label', default: 'Doc'), docInstance.id])
	        redirect action: 'show', id: docInstance.id
			break
		}
    }

    def delete() {
        def docInstance = Doc.get(params.id)
        if (!docInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'doc.label', default: 'Doc'), params.id])
            redirect action: 'list'
            return
        }

        try {
            docInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'doc.label', default: 'Doc'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'doc.label', default: 'Doc'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

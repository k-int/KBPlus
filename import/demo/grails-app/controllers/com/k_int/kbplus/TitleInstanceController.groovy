package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException

class TitleInstanceController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [titleInstanceInstanceList: TitleInstance.list(params), titleInstanceInstanceTotal: TitleInstance.count()]
    }

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

    def show() {
        def titleInstanceInstance = TitleInstance.get(params.id)
        if (!titleInstanceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'titleInstance.label', default: 'TitleInstance'), params.id])
            redirect action: 'list'
            return
        }

        [titleInstanceInstance: titleInstanceInstance]
    }

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

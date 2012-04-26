package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException

class PlatformController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [platformInstanceList: Platform.list(params), platformInstanceTotal: Platform.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[platformInstance: new Platform(params)]
			break
		case 'POST':
	        def platformInstance = new Platform(params)
	        if (!platformInstance.save(flush: true)) {
	            render view: 'create', model: [platformInstance: platformInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'platform.label', default: 'Platform'), platformInstance.id])
	        redirect action: 'show', id: platformInstance.id
			break
		}
    }

    def show() {
      def platformInstance = Platform.get(params.id)
      if (!platformInstance) {
    	  flash.message = message(code: 'default.not.found.message', 
                                args: [message(code: 'platform.label', default: 'Platform'), params.id])
        redirect action: 'list'
        return
      }

      [platformInstance: platformInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def platformInstance = Platform.get(params.id)
	        if (!platformInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [platformInstance: platformInstance]
			break
		case 'POST':
	        def platformInstance = Platform.get(params.id)
	        if (!platformInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (platformInstance.version > version) {
	                platformInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'platform.label', default: 'Platform')] as Object[],
	                          "Another user has updated this Platform while you were editing")
	                render view: 'edit', model: [platformInstance: platformInstance]
	                return
	            }
	        }

	        platformInstance.properties = params

	        if (!platformInstance.save(flush: true)) {
	            render view: 'edit', model: [platformInstance: platformInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'platform.label', default: 'Platform'), platformInstance.id])
	        redirect action: 'show', id: platformInstance.id
			break
		}
    }

    def delete() {
        def platformInstance = Platform.get(params.id)
        if (!platformInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
            redirect action: 'list'
            return
        }

        try {
            platformInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

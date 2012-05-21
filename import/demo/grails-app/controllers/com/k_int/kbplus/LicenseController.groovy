package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException

class LicenseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [licenseInstanceList: License.list(params), licenseInstanceTotal: License.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[licenseInstance: new License(params)]
			break
		case 'POST':
	        def licenseInstance = new License(params)
	        if (!licenseInstance.save(flush: true)) {
	            render view: 'create', model: [licenseInstance: licenseInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'license.label', default: 'License'), licenseInstance.id])
	        redirect action: 'show', id: licenseInstance.id
			break
		}
    }

    def show() {
        def licenseInstance = License.get(params.id)
        if (!licenseInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'license.label', default: 'License'), params.id])
            redirect action: 'list'
            return
        }

        [licenseInstance: licenseInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def licenseInstance = License.get(params.id)
	        if (!licenseInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'license.label', default: 'License'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [licenseInstance: licenseInstance]
			break
		case 'POST':
	        def licenseInstance = License.get(params.id)
	        if (!licenseInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'license.label', default: 'License'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (licenseInstance.version > version) {
	                licenseInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'license.label', default: 'License')] as Object[],
	                          "Another user has updated this License while you were editing")
	                render view: 'edit', model: [licenseInstance: licenseInstance]
	                return
	            }
	        }

	        licenseInstance.properties = params

	        if (!licenseInstance.save(flush: true)) {
	            render view: 'edit', model: [licenseInstance: licenseInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'license.label', default: 'License'), licenseInstance.id])
	        redirect action: 'show', id: licenseInstance.id
			break
		}
    }

    def delete() {
        def licenseInstance = License.get(params.id)
        if (!licenseInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'license.label', default: 'License'), params.id])
            redirect action: 'list'
            return
        }

        try {
            licenseInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'license.label', default: 'License'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'license.label', default: 'License'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

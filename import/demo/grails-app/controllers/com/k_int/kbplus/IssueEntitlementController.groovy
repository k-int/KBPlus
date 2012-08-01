package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException

class IssueEntitlementController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [issueEntitlementInstanceList: IssueEntitlement.list(params), issueEntitlementInstanceTotal: IssueEntitlement.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[issueEntitlementInstance: new IssueEntitlement(params)]
			break
		case 'POST':
	        def issueEntitlementInstance = new IssueEntitlement(params)
	        if (!issueEntitlementInstance.save(flush: true)) {
	            render view: 'create', model: [issueEntitlementInstance: issueEntitlementInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), issueEntitlementInstance.id])
	        redirect action: 'show', id: issueEntitlementInstance.id
			break
		}
    }

    def show() {
        def issueEntitlementInstance = IssueEntitlement.get(params.id)
        if (!issueEntitlementInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
            redirect action: 'list'
            return
        }

        [issueEntitlementInstance: issueEntitlementInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def issueEntitlementInstance = IssueEntitlement.get(params.id)
	        if (!issueEntitlementInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [issueEntitlementInstance: issueEntitlementInstance]
			break
		case 'POST':
	        def issueEntitlementInstance = IssueEntitlement.get(params.id)
	        if (!issueEntitlementInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (issueEntitlementInstance.version > version) {
	                issueEntitlementInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'issueEntitlement.label', default: 'IssueEntitlement')] as Object[],
	                          "Another user has updated this IssueEntitlement while you were editing")
	                render view: 'edit', model: [issueEntitlementInstance: issueEntitlementInstance]
	                return
	            }
	        }

	        issueEntitlementInstance.properties = params

	        if (!issueEntitlementInstance.save(flush: true)) {
	            render view: 'edit', model: [issueEntitlementInstance: issueEntitlementInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), issueEntitlementInstance.id])
	        redirect action: 'show', id: issueEntitlementInstance.id
			break
		}
    }

    def delete() {
        def issueEntitlementInstance = IssueEntitlement.get(params.id)
        if (!issueEntitlementInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
            redirect action: 'list'
            return
        }

        try {
            issueEntitlementInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

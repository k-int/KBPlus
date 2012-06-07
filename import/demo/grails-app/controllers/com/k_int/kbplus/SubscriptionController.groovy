package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured


class SubscriptionController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [subscriptionInstanceList: Subscription.list(params), subscriptionInstanceTotal: Subscription.count()]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
		switch (request.method) {
		case 'GET':
        	[subscriptionInstance: new Subscription(params)]
			break
		case 'POST':
	        def subscriptionInstance = new Subscription(params)
	        if (!subscriptionInstance.save(flush: true)) {
	            render view: 'create', model: [subscriptionInstance: subscriptionInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'subscription.label', default: 'Subscription'), subscriptionInstance.id])
	        redirect action: 'show', id: subscriptionInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
        def subscriptionInstance = Subscription.get(params.id)
        if (!subscriptionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect action: 'list'
            return
        }

        [subscriptionInstance: subscriptionInstance]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
		switch (request.method) {
		case 'GET':
	        def subscriptionInstance = Subscription.get(params.id)
	        if (!subscriptionInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [subscriptionInstance: subscriptionInstance]
			break
		case 'POST':
	        def subscriptionInstance = Subscription.get(params.id)
	        if (!subscriptionInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (subscriptionInstance.version > version) {
	                subscriptionInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'subscription.label', default: 'Subscription')] as Object[],
	                          "Another user has updated this Subscription while you were editing")
	                render view: 'edit', model: [subscriptionInstance: subscriptionInstance]
	                return
	            }
	        }

	        subscriptionInstance.properties = params

	        if (!subscriptionInstance.save(flush: true)) {
	            render view: 'edit', model: [subscriptionInstance: subscriptionInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'subscription.label', default: 'Subscription'), subscriptionInstance.id])
	        redirect action: 'show', id: subscriptionInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def subscriptionInstance = Subscription.get(params.id)
        if (!subscriptionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect action: 'list'
            return
        }

        try {
            subscriptionInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

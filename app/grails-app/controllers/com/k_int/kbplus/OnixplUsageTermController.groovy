package com.k_int.kbplus

import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class OnixplUsageTermController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def springSecurityService
    def ESWrapperService

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [onixplUsageTermInstanceList: OnixplUsageTerm.list(params), onixplUsageTermInstanceTotal: OnixplUsageTerm.count()]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
        switch (request.method) {
            case 'GET':
            default:
                [onixplUsageTermInstance: new OnixplUsageTerm(params)]
                break
            case 'POST':
                def onixplUsageTermInstance = new OnixplUsageTerm(params)
                if (!onixplUsageTermInstance.save(flush: true)) {
                    render view: 'create', model: [onixplUsageTermInstance: onixplUsageTermInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm'), onixplUsageTermInstance.id])
                redirect action: 'show', id: onixplUsageTermInstance.id
                break
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
        def onixplUsageTermInstance = OnixplUsageTerm.get(params.id)
        if (!onixplUsageTermInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm'), params.id])
            redirect action: 'list'
            return
        }

        [onixplUsageTermInstance: onixplUsageTermInstance]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
        switch (request.method) {
            case 'GET':
            default:
                def onixplUsageTermInstance = OnixplUsageTerm.get(params.id)
                if (!onixplUsageTermInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm'), params.id])
                    redirect action: 'list'
                    return
                }

                [onixplUsageTermInstance: onixplUsageTermInstance]
                break
            case 'POST':
                def onixplUsageTermInstance = OnixplUsageTerm.get(params.id)
                if (!onixplUsageTermInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (onixplUsageTermInstance.version > version) {
                        onixplUsageTermInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm')] as Object[],
                                "Another user has updated this OnixplUsageTerm while you were editing")
                        render view: 'edit', model: [onixplUsageTermInstance: onixplUsageTermInstance]
                        return
                    }
                }

                onixplUsageTermInstance.properties = params

                if (!onixplUsageTermInstance.save(flush: true)) {
                    render view: 'edit', model: [onixplUsageTermInstance: onixplUsageTermInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm'), onixplUsageTermInstance.id])
                redirect action: 'show', id: onixplUsageTermInstance.id
                break
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def onixplUsageTermInstance = OnixplUsageTerm.get(params.id)
        if (!onixplUsageTermInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm'), params.id])
            redirect action: 'list'
            return
        }

        try {
            onixplUsageTermInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

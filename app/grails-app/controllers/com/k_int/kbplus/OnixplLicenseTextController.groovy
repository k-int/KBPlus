package com.k_int.kbplus

import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class OnixplLicenseTextController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [onixplLicenseTextInstanceList: OnixplLicenseText.list(params), onixplLicenseTextInstanceTotal: OnixplLicenseText.count()]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
        switch (request.method) {
            case 'GET':
                [onixplLicenseTextInstance: new OnixplLicenseText(params)]
                break
            case 'POST':
                def onixplLicenseTextInstance = new OnixplLicenseText(params)
                if (!onixplLicenseTextInstance.save(flush: true)) {
                    render view: 'create', model: [onixplLicenseTextInstance: onixplLicenseTextInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText'), onixplLicenseTextInstance.id])
                redirect action: 'show', id: onixplLicenseTextInstance.id
                break
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
        def onixplLicenseTextInstance = OnixplLicenseText.get(params.id)
        if (!onixplLicenseTextInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText'), params.id])
            redirect action: 'list'
            return
        }

        [onixplLicenseTextInstance: onixplLicenseTextInstance]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
        switch (request.method) {
            case 'GET':
                def onixplLicenseTextInstance = OnixplLicenseText.get(params.id)
                if (!onixplLicenseTextInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText'), params.id])
                    redirect action: 'list'
                    return
                }

                [onixplLicenseTextInstance: onixplLicenseTextInstance]
                break
            case 'POST':
                def onixplLicenseTextInstance = OnixplLicenseText.get(params.id)
                if (!onixplLicenseTextInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (onixplLicenseTextInstance.version > version) {
                        onixplLicenseTextInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText')] as Object[],
                                "Another user has updated this OnixplLicenseText while you were editing")
                        render view: 'edit', model: [onixplLicenseTextInstance: onixplLicenseTextInstance]
                        return
                    }
                }

                onixplLicenseTextInstance.properties = params

                if (!onixplLicenseTextInstance.save(flush: true)) {
                    render view: 'edit', model: [onixplLicenseTextInstance: onixplLicenseTextInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText'), onixplLicenseTextInstance.id])
                redirect action: 'show', id: onixplLicenseTextInstance.id
                break
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def onixplLicenseTextInstance = OnixplLicenseText.get(params.id)
        if (!onixplLicenseTextInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText'), params.id])
            redirect action: 'list'
            return
        }

        try {
            onixplLicenseTextInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

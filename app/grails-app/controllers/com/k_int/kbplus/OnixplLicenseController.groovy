package com.k_int.kbplus

import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class OnixplLicenseController {

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
        [onixplLicenseInstanceList: OnixplLicense.list(params), onixplLicenseInstanceTotal: OnixplLicense.count()]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
        switch (request.method) {
            case 'GET':
                [onixplLicenseInstance: new OnixplLicense(params)]
                break
            case 'POST':
                def onixplLicenseInstance = new OnixplLicense(params)
                if (!onixplLicenseInstance.save(flush: true)) {
                    render view: 'create', model: [onixplLicenseInstance: onixplLicenseInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'onixplLicense.label', default: 'OnixplLicense'), onixplLicenseInstance.id])
                redirect action: 'show', id: onixplLicenseInstance.id
                break
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
        def onixplLicenseInstance = OnixplLicense.get(params.id)
        if (!onixplLicenseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplLicense.label', default: 'OnixplLicense'), params.id])
            redirect action: 'list'
            return
        }

        [onixplLicenseInstance: onixplLicenseInstance]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
        switch (request.method) {
            case 'GET':
                def onixplLicenseInstance = OnixplLicense.get(params.id)
                if (!onixplLicenseInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplLicense.label', default: 'OnixplLicense'), params.id])
                    redirect action: 'list'
                    return
                }

                [onixplLicenseInstance: onixplLicenseInstance]
                break
            case 'POST':
                def onixplLicenseInstance = OnixplLicense.get(params.id)
                if (!onixplLicenseInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplLicense.label', default: 'OnixplLicense'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (onixplLicenseInstance.version > version) {
                        onixplLicenseInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'onixplLicense.label', default: 'OnixplLicense')] as Object[],
                                "Another user has updated this OnixplLicense while you were editing")
                        render view: 'edit', model: [onixplLicenseInstance: onixplLicenseInstance]
                        return
                    }
                }

                onixplLicenseInstance.properties = params

                if (!onixplLicenseInstance.save(flush: true)) {
                    render view: 'edit', model: [onixplLicenseInstance: onixplLicenseInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'onixplLicense.label', default: 'OnixplLicense'), onixplLicenseInstance.id])
                redirect action: 'show', id: onixplLicenseInstance.id
                break
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def onixplLicenseInstance = OnixplLicense.get(params.id)
        if (!onixplLicenseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplLicense.label', default: 'OnixplLicense'), params.id])
            redirect action: 'list'
            return
        }

        try {
            onixplLicenseInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'onixplLicense.label', default: 'OnixplLicense'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'onixplLicense.label', default: 'OnixplLicense'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

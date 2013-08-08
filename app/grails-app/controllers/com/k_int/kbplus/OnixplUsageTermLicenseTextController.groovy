package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException

class OnixplUsageTermLicenseTextController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [onixplUsageTermLicenseTextInstanceList: OnixplUsageTermLicenseText.list(params), onixplUsageTermLicenseTextInstanceTotal: OnixplUsageTermLicenseText.count()]
    }

    def create() {
        switch (request.method) {
            case 'GET':
            default:
                [onixplUsageTermLicenseTextInstance: new OnixplUsageTermLicenseText(params)]
                break
            case 'POST':
                def onixplUsageTermLicenseTextInstance = new OnixplUsageTermLicenseText(params)
                if (!onixplUsageTermLicenseTextInstance.save(flush: true)) {
                    render view: 'create', model: [onixplUsageTermLicenseTextInstance: onixplUsageTermLicenseTextInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText'), onixplUsageTermLicenseTextInstance.id])
                redirect action: 'show', id: onixplUsageTermLicenseTextInstance.id
                break
        }
    }

    def show() {
        def onixplUsageTermLicenseTextInstance = OnixplUsageTermLicenseText.get(params.id)
        if (!onixplUsageTermLicenseTextInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText'), params.id])
            redirect action: 'list'
            return
        }

        [onixplUsageTermLicenseTextInstance: onixplUsageTermLicenseTextInstance]
    }

    def edit() {
        switch (request.method) {
            case 'GET':
            default:
                def onixplUsageTermLicenseTextInstance = OnixplUsageTermLicenseText.get(params.id)
                if (!onixplUsageTermLicenseTextInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText'), params.id])
                    redirect action: 'list'
                    return
                }

                [onixplUsageTermLicenseTextInstance: onixplUsageTermLicenseTextInstance]
                break
            case 'POST':
                def onixplUsageTermLicenseTextInstance = OnixplUsageTermLicenseText.get(params.id)
                if (!onixplUsageTermLicenseTextInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (onixplUsageTermLicenseTextInstance.version > version) {
                        onixplUsageTermLicenseTextInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText')] as Object[],
                                "Another user has updated this OnixplUsageTermLicenseText while you were editing")
                        render view: 'edit', model: [onixplUsageTermLicenseTextInstance: onixplUsageTermLicenseTextInstance]
                        return
                    }
                }

                onixplUsageTermLicenseTextInstance.properties = params

                if (!onixplUsageTermLicenseTextInstance.save(flush: true)) {
                    render view: 'edit', model: [onixplUsageTermLicenseTextInstance: onixplUsageTermLicenseTextInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText'), onixplUsageTermLicenseTextInstance.id])
                redirect action: 'show', id: onixplUsageTermLicenseTextInstance.id
                break
        }
    }

    def delete() {
        def onixplUsageTermLicenseTextInstance = OnixplUsageTermLicenseText.get(params.id)
        if (!onixplUsageTermLicenseTextInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText'), params.id])
            redirect action: 'list'
            return
        }

        try {
            onixplUsageTermLicenseTextInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

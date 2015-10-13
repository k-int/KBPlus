package com.k_int.kbplus

import com.k_int.custprops.PropertyDefinition
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class PropertyDefinitionController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def springSecurityService

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 50, 1000)
        [propDefInstanceList: PropertyDefinition.list(params), propertyDefinitionTotal: PropertyDefinition.count()]
    }
    
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
        log.debug("edit:: ${params} - ${request.method}")

        switch (request.method) {
            case 'GET':
                def propDefInstance = PropertyDefinition.get(params.id)
                if (!propDefInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'propertyDefinition.label', default: 'PropertyDefinition'), params.id])
                    redirect action: 'list'
                    return
                }
                [propDefInstance: propDefInstance]
                break ;
            case 'POST':
                def propDefInstance = PropertyDefinition.get(params.id)
                if (!propDefInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'propertyDefinition.label', default: 'PropertyDefinition'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (propDefInstance.version > version) {
                        propDefInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'propertyDefinition.label', default: 'PropertyDefinition')] as Object[],
                                "Another user has updated this PropertyDefinition while you were editing")
                        render view: 'edit', model: [propDefInstance: propDefInstance]
                        return
                    }
                }

                propDefInstance.properties = params

                if (!propDefInstance.save(flush: true)) {
                    render view: 'edit', model: [propDefInstance: propDefInstance]
                    return
                }

                flash.message = message(code: 'default.updated.message', args: [message(code: 'propertyDefinition.label', default: 'PropertyDefinition'), propDefInstance.id])
                redirect action: 'edit', id: propDefInstance.id
                break
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
      switch (request.method) {
            case 'GET':
            default:
                [propertyDefinitionInstance: new PropertyDefinition(params)]
                break
            case 'POST':
                def propertyDefinitionInstance = new PropertyDefinition(params)
                if (!propertyDefinitionInstance.save(flush: true)) {
                    render view: 'create', model: [propertyDefinitionInstance: propertyDefinitionInstance]
                    return
                }

                flash.message = message(code: 'default.created.message', args: [message(code: 'propertyDefinition.label', default: 'PropertyDefinition'), propertyDefinitionInstance.id])
                redirect action: 'edit', id: propertyDefinitionInstance.id
                break
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def propDefInstance = PropertyDefinition.get(params.id)
        if (!propDefInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'propertyDefinition.label', default: 'PropertyDefinition'), params.id])
            redirect action: 'list'
            return
        }

        try {
            propDefInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'propertyDefinition.label', default: 'PropertyDefinition'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'propertyDefinition.label', default: 'PropertyDefinition'), params.id])
            redirect action: 'edit', id: params.id
        }
    }
}
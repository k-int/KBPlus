package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;

class OrgController {

    def springSecurityService

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def results = null;
        def count = null;
        if ( ( params.orgNameContains != null ) && ( params.orgNameContains.length() > 0 ) &&
             ( params.orgRole != null ) && ( params.orgRole.length() > 0 ) ) {
          def qry = "from Org o where o.name like ? and exists ( from o.links r where r.roleType.id = ? )"
          results = Org.findAll(qry, ["%${params.orgNameContains}%", Long.parseLong(params.orgRole)],params);
          count = Org.executeQuery("select count(o) ${qry}",["%${params.orgNameContains}%", Long.parseLong(params.orgRole)])[0]
        }
        else if ( ( params.orgNameContains != null ) && ( params.orgNameContains.length() > 0 ) ) {
          def qry = "from Org o where o.name like ?"
          results = Org.findAll(qry, ["%${params.orgNameContains}%"], params);
          count = Org.executeQuery("select count (o) ${qry}",["%${params.orgNameContains}%"])[0]
        }
        else if ( ( params.orgRole != null ) && ( params.orgRole.length() > 0 ) ) {
          def qry = "from Org o where exists ( select r from o.links r where r.roleType.id = ? )"
          results = Org.findAll(qry, [Long.parseLong(params.orgRole)],params);
          count = Org.executeQuery("select count(o) ${qry}", [Long.parseLong(params.orgRole)])[0]
        }
        else { 
          results = Org.list(params)
          count = Org.count()
        }

        [orgInstanceList: results, orgInstanceTotal: count]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
		switch (request.method) {
		case 'GET':
        	[orgInstance: new Org(params)]
			break
		case 'POST':
	        def orgInstance = new Org(params)
	        if (!orgInstance.save(flush: true)) {
	            render view: 'create', model: [orgInstance: orgInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'org.label', default: 'Org'), orgInstance.id])
	        redirect action: 'show', id: orgInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
        def orgInstance = Org.get(params.id)
        if (!orgInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
            redirect action: 'list'
            return
        }

        [orgInstance: orgInstance]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
		switch (request.method) {
		case 'GET':
	        def orgInstance = Org.get(params.id)
	        if (!orgInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [orgInstance: orgInstance]
			break
		case 'POST':
	        def orgInstance = Org.get(params.id)
	        if (!orgInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (orgInstance.version > version) {
	                orgInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'org.label', default: 'Org')] as Object[],
	                          "Another user has updated this Org while you were editing")
	                render view: 'edit', model: [orgInstance: orgInstance]
	                return
	            }
	        }

	        orgInstance.properties = params

	        if (!orgInstance.save(flush: true)) {
	            render view: 'edit', model: [orgInstance: orgInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'org.label', default: 'Org'), orgInstance.id])
	        redirect action: 'show', id: orgInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def orgInstance = Org.get(params.id)
        if (!orgInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
            redirect action: 'list'
            return
        }

        try {
            orgInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'org.label', default: 'Org'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'org.label', default: 'Org'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

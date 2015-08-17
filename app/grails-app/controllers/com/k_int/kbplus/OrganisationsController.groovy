package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import com.k_int.custprops.PropertyDefinition

class OrganisationsController {

    def springSecurityService

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def config() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      def orgInstance = Org.get(params.id)

      if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') ) {
        result.editable = true
      }
      else {
        result.editable = orgInstance.hasUserWithRole(result.user,'INST_ADM');
      }
      if(! orgInstance.customProperties){
        grails.util.Holders.config.customProperties.org.each{ 
          def entry = it.getValue()
          def type = PropertyDefinition.lookupOrCreateType(entry.name,entry.class,null)
          def prop = PropertyDefinition.createPropertyValue(orgInstance,type)
          prop.note = entry.note
          prop.save()
        }
      }

      if (!orgInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
        redirect action: 'list'
        return
      }

      result.orgInstance=orgInstance
      result
    }
    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def list() {

      def result = [:]
      result.user = User.get(springSecurityService.principal.id)

      params.max = Math.min(params.max ? params.int('max') : 10, 100)
      def results = null;
      def count = null;
      if ( ( params.orgNameContains != null ) && ( params.orgNameContains.length() > 0 ) &&
           ( params.orgRole != null ) && ( params.orgRole.length() > 0 ) ) {
        def qry = "from Org o where lower(o.name) like ? and exists ( from o.links r where r.roleType.id = ? )"
        results = Org.findAll(qry, ["%${params.orgNameContains.toLowerCase()}%", Long.parseLong(params.orgRole)],params);
        count = Org.executeQuery("select count(o) ${qry}",["%${params.orgNameContains.toLowerCase()}%", Long.parseLong(params.orgRole)])[0]
      }
      else if ( ( params.orgNameContains != null ) && ( params.orgNameContains.length() > 0 ) ) {
        def qry = "from Org o where lower(o.name) like ?"
        results = Org.findAll(qry, ["%${params.orgNameContains.toLowerCase()}%"], params);
        count = Org.executeQuery("select count (o) ${qry}",["%${params.orgNameContains.toLowerCase()}%"])[0]
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

      result.orgInstanceList = results;
      result.orgInstanceTotal=count

      result
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def create() {
		switch (request.method) {
		case 'GET':
		    if (!params.name && !params.sector) {
				params.sector = 'Higher Education'
		    }
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
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      def orgInstance = Org.get(params.id)

      if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') ) {
        result.editable = true
      }
      else {
        result.editable = orgInstance.hasUserWithRole(result.user,'INST_ADM');
      }


      if (!orgInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
        redirect action: 'list'
        return
      }

      result.orgInstance=orgInstance
      result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def users() {
      def result = [:]
      if ( SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') ) {
        result.editable = true
      }
      def tracked_roles = ["KBPLUS_EDITOR":"KB+ Editor","ROLE_ADMIN":"KB+ Administrator"]
      result.user = User.get(springSecurityService.principal.id)
      def orgInstance = Org.get(params.id)
      if (!orgInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
        redirect action: 'list'
        return
      }
      result.users = orgInstance.affiliations.collect{ userOrg ->
        def admin_roles = []
        userOrg.user.roles.each{ 
          if (tracked_roles.keySet().contains(it.role.authority)){
            def role_match = tracked_roles.get(it.role.authority)+" (${it.role.authority})"
            admin_roles += role_match
          }
        }
        // log.debug("Found roles: ${admin_roles} for user ${userOrg.user.displayName}")

        return [userOrg,admin_roles?:null]

      }
      // log.debug(result.users)
      result.orgInstance=orgInstance
      result
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def info() {
      def result = [:]

      result.user = User.get(springSecurityService.principal.id)

      def orgInstance = Org.get(params.id)
      if (!orgInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
        redirect action: 'info'
        return
      }

      result.orgInstance=orgInstance
      result
    }


    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
		switch (request.method) {
		case 'GET':
	        def orgInstance = Org.get(params.id)
	        if (!orgInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'org.label', default: 'Org'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [orgInstance: orgInstance, editable:true]
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
	            render view: 'edit', model: [orgInstance: orgInstance, editable:true]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'org.label', default: 'Org'), orgInstance.id])
	        redirect action: 'show', id: orgInstance.id
			break
		}
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
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

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def revokeRole() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      UserOrg uo = UserOrg.get(params.grant)
      if ( uo.org.hasUserWithRole(result.user,'INST_ADM') ) {
        uo.status = 2;
        uo.save();
      }
      redirect action: 'users', id: params.id
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def enableRole() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      UserOrg uo = UserOrg.get(params.grant)
      if ( uo.org.hasUserWithRole(result.user,'INST_ADM') ) {
        uo.status = 1;
        uo.save();
      }
      redirect action: 'users', id: params.id
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def deleteRole() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      UserOrg uo = UserOrg.get(params.grant)
      if ( uo.org.hasUserWithRole(result.user,'INST_ADM') ) {
        uo.delete();
      }
      redirect action: 'users', id: params.id
    }

}

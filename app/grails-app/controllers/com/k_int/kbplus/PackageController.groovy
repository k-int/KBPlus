package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;



class PackageController {

    def springSecurityService


    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() {
      def result = [:]
      result.user = User.get(springSecurityService.principal.id)
      params.max = Math.min(params.max ? params.int('max') : 10, 100)
      result.packageInstanceList=Package.list(params)
      result.packageInstanceTotal=Package.count()
      result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
		switch (request.method) {
		case 'GET':
        	[packageInstance: new Package(params)]
			break
		case 'POST':
	        def packageInstance = new Package(params)
	        if (!packageInstance.save(flush: true)) {
	            render view: 'create', model: [packageInstance: packageInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'package.label', default: 'Package'), packageInstance.id])
	        redirect action: 'show', id: packageInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
        def packageInstance = Package.get(params.id)
        if (!packageInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
            redirect action: 'list'
            return
        }

      // Build up a crosstab array of title-platforms under this package
      def platforms = [:]
      def platform_list = []
      def titles = [:]
      def title_list = []
      int plat_count = 0;
      int title_count = 0;

      log.debug("Adding platforms");
      // Find all platforms
      packageInstance.tipps.each{ tipp ->
        // log.debug("Consider ${tipp.title.title}")
        if ( !platforms.keySet().contains(tipp.platform.id) ) {
          platform_list.add(tipp.platform)
          platforms[tipp.platform.id] = [position:plat_count++, plat:tipp.platform]
        }
      }

      // Find all titles
      packageInstance.tipps.each{ tipp ->
        if ( !titles.keySet().contains(tipp.title.id) ) {
          title_list.add([title:tipp.title])
          titles[tipp.title.id] = [:]
        }
      }

      title_list.sort{it.title.title}
      title_list.each { t ->
        t.position = title_count
        titles[t.title.id].position = title_count++
      }

      def crosstab = new Object[title_list.size()][platform_list.size()]

      // Now iterate through all tipps, puttint them in the right cell
      packageInstance.tipps.each{ tipp ->
        int plat_col = platforms[tipp.platform.id].position
        int title_row = titles[tipp.title.id].position
        crosstab[title_row][plat_col] = tipp;
      }

        [packageInstance: packageInstance, platforms:platform_list, crosstab:crosstab, titles:title_list]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
		switch (request.method) {
		case 'GET':
	        def packageInstance = Package.get(params.id)
	        if (!packageInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [packageInstance: packageInstance]
			break
		case 'POST':
	        def packageInstance = Package.get(params.id)
	        if (!packageInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (packageInstance.version > version) {
	                packageInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'package.label', default: 'Package')] as Object[],
	                          "Another user has updated this Package while you were editing")
	                render view: 'edit', model: [packageInstance: packageInstance]
	                return
	            }
	        }

	        packageInstance.properties = params

	        if (!packageInstance.save(flush: true)) {
	            render view: 'edit', model: [packageInstance: packageInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'package.label', default: 'Package'), packageInstance.id])
	        redirect action: 'show', id: packageInstance.id
			break
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def packageInstance = Package.get(params.id)
        if (!packageInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
            redirect action: 'list'
            return
        }

        try {
            packageInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'package.label', default: 'Package'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'package.label', default: 'Package'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

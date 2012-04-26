package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException

class PackageController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [packageInstanceList: Package.list(params), packageInstanceTotal: Package.count()]
    }

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

    def show() {
        def packageInstance = Package.get(params.id)
        if (!packageInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'package.label', default: 'Package'), params.id])
            redirect action: 'list'
            return
        }

      // Build up a crosstab array of title-platforms under this package
      def platforms = [:]
      def titles = [:]
      int plat_count = 0;
      int title_count = 0;

      log.debug("Adding platforms");
      // Find all platforms
      packageInstance.tipps.each{ tipp ->
        if ( !platforms.keySet().contains(tipp.platform.id) ) {
          platforms[tipp.platform.id] = [position:plat_count++, plat:tipp.platform]
        }
      }

      // Find all titles
      packageInstance.tipps.each{ tipp ->
        if ( !titles.keySet().contains(tipp.title.id) ) {
          titles[tipp.title.id] = [position:title_count++, title:tipp.title]
        }
      }

      def crosstab = new Object[titles.size()][platforms.size()]

      // Now iterate through all tipps, puttint them in the right cell
      packageInstance.tipps.each{ tipp ->
        int plat_col = platforms[tipp.platform.id].position
        int title_row = titles[tipp.title.id].position
        crosstab[title_row][plat_col] = tipp;
      }

        [packageInstance: packageInstance, platforms:platforms, crosstab:crosstab, titles:titles]
    }

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

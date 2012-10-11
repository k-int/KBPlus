package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;

class PlatformController {

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
      result.platformInstanceList = Platform.list(params)
      result.platformInstanceTotal = Platform.count()
      result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
    switch (request.method) {
    case 'GET':
          [platformInstance: new Platform(params)]
      break
    case 'POST':
          def platformInstance = new Platform(params)
          if (!platformInstance.save(flush: true)) {
              render view: 'create', model: [platformInstance: platformInstance]
              return
          }

      flash.message = message(code: 'default.created.message', args: [message(code: 'platform.label', default: 'Platform'), platformInstance.id])
          redirect action: 'show', id: platformInstance.id
      break
    }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
      def platformInstance = Platform.get(params.id)
      if (!platformInstance) {
        flash.message = message(code: 'default.not.found.message', 
                                args: [message(code: 'platform.label', default: 'Platform'), params.id])
        redirect action: 'list'
        return
      }


     // Build up a crosstab array of title-platforms under this package
      def packages = [:]
      def package_list = []
      def titles = [:]
      def title_list = []
      int pkg_count = 0;
      int title_count = 0;

      log.debug("Adding packages");
      // Find all platforms
      platformInstance.tipps.each{ tipp ->
        // log.debug("Consider ${tipp.title.title}")
        if ( !packages.keySet().contains(tipp.pkg.id) ) {
          package_list.add(tipp.pkg)
          packages[tipp.pkg.id] = [position:pkg_count++, pkg:tipp.pkg]
        }
      }

      // Find all titles
      platformInstance.tipps.each{ tipp ->
        if ( !titles.keySet().contains(tipp.title.id) ) {
          title_list.add([title:tipp.title])
          titles[tipp.title.id] = [:]
        }
      }

      title_list.sort{it.title.title}
      title_list.each { t ->
        // log.debug("Add title ${t.title.title}")
        t.position = title_count
        titles[t.title.id].position = title_count++
      }

      def crosstab = new Object[title_list.size()][package_list.size()]

      // Now iterate through all tipps, puttint them in the right cell
      platformInstance.tipps.each{ tipp ->
        int pkg_col = packages[tipp.pkg.id].position
        int title_row = titles[tipp.title.id].position
        if ( crosstab[title_row][pkg_col] != null ) {
          log.error("Warning - already a value in this cell.. it needs to be a list!!!!!");
        }
        else {
          crosstab[title_row][pkg_col] = tipp;
        }
      }

        [platformInstance: platformInstance, packages:package_list, crosstab:crosstab, titles:title_list]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
    switch (request.method) {
    case 'GET':
          def platformInstance = Platform.get(params.id)
          if (!platformInstance) {
              flash.message = message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
              redirect action: 'list'
              return
          }

          [platformInstance: platformInstance]
      break
    case 'POST':
          def platformInstance = Platform.get(params.id)
          if (!platformInstance) {
              flash.message = message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
              redirect action: 'list'
              return
          }

          if (params.version) {
              def version = params.version.toLong()
              if (platformInstance.version > version) {
                  platformInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                            [message(code: 'platform.label', default: 'Platform')] as Object[],
                            "Another user has updated this Platform while you were editing")
                  render view: 'edit', model: [platformInstance: platformInstance]
                  return
              }
          }

          platformInstance.properties = params

          if (!platformInstance.save(flush: true)) {
              render view: 'edit', model: [platformInstance: platformInstance]
              return
          }

      flash.message = message(code: 'default.updated.message', args: [message(code: 'platform.label', default: 'Platform'), platformInstance.id])
          redirect action: 'show', id: platformInstance.id
      break
    }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        def platformInstance = Platform.get(params.id)
        if (!platformInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
            redirect action: 'list'
            return
        }

        try {
            platformInstance.delete(flush: true)
      flash.message = message(code: 'default.deleted.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
      flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'platform.label', default: 'Platform'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}

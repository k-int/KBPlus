package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;


class IssueEntitlementController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
   def springSecurityService

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [issueEntitlementInstanceList: IssueEntitlement.list(params), issueEntitlementInstanceTotal: IssueEntitlement.count()]
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def create() {
    switch (request.method) {
    case 'GET':
          [issueEntitlementInstance: new IssueEntitlement(params)]
      break
    case 'POST':
          def issueEntitlementInstance = new IssueEntitlement(params)
          if (!issueEntitlementInstance.save(flush: true)) {
              render view: 'create', model: [issueEntitlementInstance: issueEntitlementInstance]
              return
          }

      flash.message = message(code: 'default.created.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), issueEntitlementInstance.id])
          redirect action: 'show', id: issueEntitlementInstance.id
      break
    }
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def show() {
      def result = [:]

      result.user = User.get(springSecurityService.principal.id)
      result.issueEntitlementInstance = IssueEntitlement.get(params.id)

      if ( result.issueEntitlementInstance.subscription.isEditableBy(result.user) ) {
        result.editable = true
      }
      else {
        result.editable = false
      }

      // Get usage statistics
      def title_id = result.issueEntitlementInstance.tipp.title?.id
      def org_id = result.issueEntitlementInstance.subscription.subscriber?.id
      def supplier_id = result.issueEntitlementInstance.tipp.pkg.contentProvider?.id

      result.usage = []

      if ( title_id != null && 
           org_id != null &&
           supplier_id != null ) {

        def q = "select sum(f.factValue),f.reportingYear,f.reportingMonth,f.factType from Fact as f where f.relatedTitle.id=? and f.supplier.id=? and f.inst.id=? group by f.factType, f.reportingYear, f.reportingMonth order by f.reportingYear,f.reportingMonth,f.factType.value"
        def l1 = Fact.executeQuery(q,[title_id, supplier_id, org_id])

        def y_axis_labels = []
        def x_axis_labels = []

        l1.each { f ->
          def y_label = "${f[1]}-${String.format('%02d',f[2])}"
          def x_label = f[3].value
          if ( ! y_axis_labels.contains(y_label) )
            y_axis_labels.add(y_label)
          if ( ! x_axis_labels.contains(x_label) )
            x_axis_labels.add(x_label)
        }
        
        x_axis_labels.sort();
        y_axis_labels.sort();

        log.debug("X Labels: ${x_axis_labels}");
        log.debug("Y Labels: ${y_axis_labels}");

        result.usage=new long[y_axis_labels.size()][x_axis_labels.size()]

        l1.each { f ->
          def y_label = "${f[1]}-${String.format('%02d',f[2])}"
          def x_label = f[3].value
          result.usage[y_axis_labels.indexOf(y_label)][x_axis_labels.indexOf(x_label)] += Long.parseLong(f[0])
        }

        result.x_axis_labels = x_axis_labels;
        result.y_axis_labels = y_axis_labels;
      }

      if (!result.issueEntitlementInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
        redirect action: 'list'
        return
      }

      result

    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def edit() {
    switch (request.method) {
    case 'GET':
          def issueEntitlementInstance = IssueEntitlement.get(params.id)
          if (!issueEntitlementInstance) {
              flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
              redirect action: 'list'
              return
          }

          [issueEntitlementInstance: issueEntitlementInstance]
      break
    case 'POST':
          def issueEntitlementInstance = IssueEntitlement.get(params.id)
          if (!issueEntitlementInstance) {
              flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
              redirect action: 'list'
              return
          }

          if (params.version) {
              def version = params.version.toLong()
              if (issueEntitlementInstance.version > version) {
                  issueEntitlementInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                            [message(code: 'issueEntitlement.label', default: 'IssueEntitlement')] as Object[],
                            "Another user has updated this IssueEntitlement while you were editing")
                  render view: 'edit', model: [issueEntitlementInstance: issueEntitlementInstance]
                  return
              }
          }

          issueEntitlementInstance.properties = params

          if (!issueEntitlementInstance.save(flush: true)) {
              render view: 'edit', model: [issueEntitlementInstance: issueEntitlementInstance]
              return
          }

      flash.message = message(code: 'default.updated.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), issueEntitlementInstance.id])
          redirect action: 'show', id: issueEntitlementInstance.id
      break
    }
    }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def delete() {
    def issueEntitlementInstance = IssueEntitlement.get(params.id)
    if (!issueEntitlementInstance) {
    flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
        redirect action: 'list'
        return
    }

    try {
      issueEntitlementInstance.delete(flush: true)
      flash.message = message(code: 'default.deleted.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
      redirect action: 'list'
    }
    catch (DataIntegrityViolationException e) {
      flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
      redirect action: 'show', id: params.id
    }
  }
}

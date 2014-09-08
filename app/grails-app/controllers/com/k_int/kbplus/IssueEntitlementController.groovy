package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;


class IssueEntitlementController {

  def factService

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

      params.max = Math.min(params.max ? params.int('max') : 10, 100)
      def paginate_after = params.paginate_after ?: 19;
      result.max = params.max
      result.offset = params.offset ? Integer.parseInt(params.offset) : 0;


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

      if ( title_id != null && 
           org_id != null &&
           supplier_id != null ) {

        def fsresult = factService.generateExpandableMonthlyUsageGrid(title_id,org_id,supplier_id)

        def jusp_login = result.issueEntitlementInstance.subscription.subscriber?.getIdentifierByType('jusplogin')?.value
        def jusp_sid = result.issueEntitlementInstance.tipp.pkg.contentProvider?.getIdentifierByType('juspsid')?.value
        def jusp_title_id = result.issueEntitlementInstance.tipp.title.getIdentifierValue('jusp')

        if ( ( jusp_login != null ) && ( jusp_sid != null ) && ( jusp_title_id != null ) ) {
          result.jusplink = "https://www.jusp.mimas.ac.uk/api/v1/Journals/Statistics/?jid=${jusp_title_id}&sid=${jusp_sid}&loginid=${jusp_login}&startrange=1800-01&endrange=2100-01&granularity=monthly"
        }

        result.usage = fsresult?.usage
        result.x_axis_labels = fsresult?.x_axis_labels;
        result.y_axis_labels = fsresult?.y_axis_labels;
      }

      if (!result.issueEntitlementInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'issueEntitlement.label', default: 'IssueEntitlement'), params.id])
        redirect action: 'list'
        return
      }


      def base_qry = "from TitleInstancePackagePlatform as tipp where tipp.title = ? and tipp.status.value != 'Deleted' "
      def qry_params = [result.issueEntitlementInstance.tipp.title]

      if ( params.filter ) {
        base_qry += " and lower(tipp.pkg.name) like ? "
        qry_params.add("%${params.filter.trim().toLowerCase()}%")
      }

      if ( params.endsAfter && params.endsAfter.length() > 0 ) {
        def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
        def d = sdf.parse(params.endsAfter)
        base_qry += " and tipp.endDate >= ?"
        qry_params.add(d)
      }

      if ( params.startsBefore && params.startsBefore.length() > 0 ) {
        def sdf = new java.text.SimpleDateFormat('yyyy-MM-dd');
        def d = sdf.parse(params.startsBefore)
        base_qry += " and tipp.startDate <= ?"
        qry_params.add(d)
      }

      if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
        base_qry += " order by lower(${params.sort}) ${params.order}"
      }
      else {
        base_qry += " order by lower(tipp.title.title) asc"
      }

      // log.debug("Base qry: ${base_qry}, params: ${qry_params}, result:${result}");
      result.tippList = TitleInstancePackagePlatform.executeQuery("select tipp "+base_qry, qry_params, [max:result.max, offset:result.offset]);
      result.num_tipp_rows = TitleInstancePackagePlatform.executeQuery("select count(tipp) "+base_qry, qry_params )[0]




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

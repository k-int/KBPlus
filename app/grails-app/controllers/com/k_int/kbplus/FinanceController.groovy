package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import grails.plugins.springsecurity.Secured


class FinanceController {

  def springSecurityService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 

    log.debug("FinanceController::index() ${params}");
    
    def result = [:]

    result.user = User.get(springSecurityService.principal.id)
    result.institution = Org.findByShortcode(params.shortcode)

    def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
    def qry_params = [result.institution]

    result.institutionSubscriptions = Subscription.executeQuery(base_qry,qry_params);

    if ( params.Add=='add' ) {

      log.debug("Process add cost item");

      // 2015-02-19 11:43:06,893 [http-bio-8080-exec-6] DEBUG kbplus.FinanceController  - FinanceController::index() [Add:add, newIe:, newValue:1, subscriptionFilter:2724, newReference:XX3344, shortcode:University_of_Oxford, newSubscription:2724, newDescription:Some description, packageFilter:xx, newInvoiceNumber:1234456, invoiceNumberFilter:1234456, newOrderNumber:2233445, newDate:2015-01-01, orderNumberFilter:2233445, newPackage:, controller:finance, action:index]

      def order = null
      if ( params.newOrderNumber ) {
        order = Order.findByOrderNumberAndOwner(params.newOrderNumber, result.institution) ?: new Order(orderNumber:params.newOrderNumber, owner:result.institution).save(flush:true);
      }

      def invoice = null
      // Lookup or create invoice if one is present
      if ( params.newInvoiceNumber ) {
        invoice = Invoice.findByInvoiceNumberAndOwner(params.newInvoiceNumber, result.institution) ?: new Invoice(invoiceNumber:params.newInvoiceNumber, owner:result.institution).save(flush:true);
      }

      def sub = null;
      if ( params.newSubscription ) {
        sub = Subscription.get(params.long('newSubscription'));
      }

      def pkg = null;
      if ( params.newPackage ) {
        pkg = Package.get(params.long('newPackage'));
      }

      def newCostItem = new CostItem(
                              sub:sub,
                              subPkg:pkg,
                              issueEntitlement:null,
                              group:null,
                              order:order,
                              invoice:invoice,
                              costItemType:null,
                              costItemCategory:null,
                              billingCurrency:null,
                              costDescription:params.newDescription,
                              costInBillingCurrency:null,
                              datePaid:null,
                              localFundCode:null,
                              costInLocalCurrency:null,
                              taxCode:null,
                              includeInSubscription:null,
                              reference: params.newReference
                            )

      newCostItem.save(flush:true);

      if ( newCostItem.errors ) {
        log.debug(newCostItem.errors);
      }


    }

    result
  }
}

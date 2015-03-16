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

    if ( params.Add=='add' ) {

      log.debug("Process add cost item");

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

      def cost_item_status = null; // lookup from params.newCostItemStatus
      def billing_currency = params.newCostCurrency ? (RefdataValue.get(params.long('newCostCurrency'))) : null;
      def cost_item_element = null; // lookup from params.costItemElement
      def cost_tax_type = null; // lookup from params.newCostTaxType

      def newCostItem = new CostItem(
                              owner:result.institution,
                              sub:sub,
                              subPkg:pkg,
                              issueEntitlement:null,
                              group:null,
                              order:order,
                              invoice:invoice,
                              costItemType:null,
                              costItemCategory:null,
                              billingCurrency:billing_currency,
                              costDescription:params.newDescription,
                              costInBillingCurrency:params.newCostInBillingCurrency,
                              datePaid:null,
                              localFundCode:null,
                              costInLocalCurrency:params.newCostInLocalCurrency,
                              taxCode:cost_tax_type,
                              includeInSubscription:null,
                              reference: params.newReference,
                              costItemStatus: cost_item_status,
                              costItemElement: cost_item_element
                            )

      newCostItem.save(flush:true);

      if ( newCostItem.errors ) {
        log.debug(newCostItem.errors);
      }
    }


    result.institutionSubscriptions = Subscription.executeQuery(base_qry,qry_params);

    def cost_item_qry_params = [ result.institution ]
    def cost_item_qry = " from CostItem as ci where ci.owner = ?"

    result.cost_item_count = CostItem.executeQuery('select count(ci.id) '+cost_item_qry, cost_item_qry_params)[0];
    result.cost_items = CostItem.executeQuery('select ci '+cost_item_qry, cost_item_qry_params, params);



    result
  }
}

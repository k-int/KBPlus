package com.k_int.kbplus

class CostItem {

  Subscription sub
  SubscriptionPackage subPkg
  IssueEntitlement issueEntitlement
  CostItemGroup group
  Order order
  Invoice invoice
  RefdataValue costItemType
  RefdataValue costItemCategory

  RefdataValue billingCurrency
  String costDescription
  float costInBillingCurrency
  Date datePaid
  String localFundCode
  float costInLocalCurrency
  RefdataValue taxCode
  Boolean includeInSubscription

  static mapping = {
                              id column:'ci_id'
                         version column:'ci_version'
                             sub column:'ci_sub_fk'
                          subPkg column:'ci_subPkg_fk'
                issueEntitlement column:'ci_e_fk'
                           group column:'ci_cig_fk'
                           order column:'ci_ord_fk'
                         invoice column:'ci_inv_fk'
                 billingCurrency column:'ci_billing_currency_rv_fk'
                 costDescription column:'ci_cost_description'
           costInBillingCurrency column:'ci_cost_in_billing_currency'
                        datePaid column:'ci_date_paid'
                   localFundCode column:'ci_local_fund_code'
             costInLocalCurrency column:'ci_cost_in_local_currency'
                         taxCode column:'ci_tax_code'
           includeInSubscription column:'ci_include_in_subscr'
                    costItemType column:'ci_type_rv_fk'
                costItemCategory column:'ci_cat_rv_fk'
  }

  static constraints = {
                       sub(nullable:true, blank:false)
                    subPkg(nullable:true, blank:false)
          issueEntitlement(nullable:true, blank:false)
                     group(nullable:true, blank:false)
                     order(nullable:true, blank:false)
                   invoice(nullable:true, blank:false)
           billingCurrency(nullable:true, blank:false)
           costDescription(nullable:true, blank:false)
     costInBillingCurrency(nullable:true, blank:false)
                  datePaid(nullable:true, blank:false)
             localFundCode(nullable:true, blank:false)
       costInLocalCurrency(nullable:true, blank:false)
                   taxCode(nullable:true, blank:false)
     includeInSubscription(nullable:true, blank:false)
              costItemType(nullable:true, blank:false)
          costItemCategory(nullable:true, blank:false)
  }

}

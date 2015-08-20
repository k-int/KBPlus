package com.k_int.kbplus

import javax.persistence.Transient

class CostItem {

    Org owner
    Subscription sub
    SubscriptionPackage subPkg
    IssueEntitlement issueEntitlement
    Order order
    Invoice invoice
    RefdataValue costItemStatus
    RefdataValue costItemType
    RefdataValue costItemCategory
    RefdataValue costItemElement

    RefdataValue billingCurrency
    String costDescription
    Double costInBillingCurrency
    Date datePaid
    String localFundCode
    Double costInLocalCurrency
    RefdataValue taxCode
    Boolean includeInSubscription
    String reference
    Date startDate
    Date endDate
    Date lastUpdated


    @Transient
    def budgetcodes //Binds getBudgetcodes

    static mapping = {
        id column: 'ci_id'
        version column: 'ci_version'
        sub column: 'ci_sub_fk'
        owner column: 'ci_owner'
        subPkg column: 'ci_subPkg_fk'
        issueEntitlement column: 'ci_e_fk'
        order column: 'ci_ord_fk'
        invoice column: 'ci_inv_fk'
        costItemStatus column: 'ci_status_rv_fk'
        billingCurrency column: 'ci_billing_currency_rv_fk'
        costDescription column: 'ci_cost_description', type:'text'
        costInBillingCurrency column: 'ci_cost_in_billing_currency'
        datePaid column: 'ci_date_paid'
        localFundCode column: 'ci_local_fund_code'
        costInLocalCurrency column: 'ci_cost_in_local_currency'
        taxCode column: 'ci_tax_code'
        includeInSubscription column: 'ci_include_in_subscr'
        costItemCategory column: 'ci_cat_rv_fk'
        costItemType column: 'ci_type_rv_fk'
        costItemElement column: 'ci_element_rv_fk'
        endDate column: 'ci_end_date'
        startDate column: 'ci_start_date'
        reference column: 'ci_reference'
    }

    static constraints = {
        owner(nullable: false, blank: false)
        sub(nullable: true, blank: false)
        subPkg(nullable: true, blank: false)
        issueEntitlement(nullable: true, blank: false)
        order(nullable: true, blank: false)
        invoice(nullable: true, blank: false)
        billingCurrency(nullable: true, blank: false)
        costDescription(nullable: true, blank: false)
        costInBillingCurrency(nullable: true, blank: false)
        datePaid(nullable: true, blank: false)
        localFundCode(nullable: true, blank: false)
        costInLocalCurrency(nullable: true, blank: false)
        taxCode(nullable: true, blank: false)
        includeInSubscription(nullable: true, blank: false)
        costItemType(nullable: true, blank: false)
        costItemCategory(nullable: true, blank: false)
        costItemStatus(nullable: true, blank: false)
        costItemElement(nullable: true, blank: false)
        reference(nullable: true, blank: false)
        startDate(nullable: true, blank: false)
        endDate(nullable: true, blank: false)
    }


    def getBudgetcodes() {
        return CostItemGroup.findAllByCostItem(this).collect {
            [id:it.id, value:it.budgetcode.value]
        }
    }

    /**
     * /todo Add other fields that will require sorting and test!
     * So we don't have to expose the data model in the GSP for relational data needed to be sorted
     * @param field - The order by link selected
     *
     * This will be used relational set to true, see index Finance Controller action.
     * @return multiple values - malicious attempt of sorting will result (null, null)
     */
    @Transient
    def static orderingByCheck(String field) {
        def (order,join,gspOrder) = ["","",""]

        switch (field)
        {
            case ["Cost Item#","id"]:
                join     = null
                order    = "id"
                gspOrder = "Cost Item#"
                break
            case "order#":
                join     = "ci.order"
                order    = "orderNumber"
                gspOrder = "order#"
                break
            case "invoice#":
                join     = "ci.invoice"
                order    = "invoiceNumber"
                gspOrder = "invoice#"
                break
            case "Subscription":
                join     = "ci.sub"
                order    = "name"
                gspOrder = "Subscription"
                break
            case "Package":
                join     = "ci.subPkg.pkg"
                order    = "name"
                gspOrder = "Package"
                break
            case "IE":
                join     = "ci.issueEntitlement"
                order    = "issueEntitlement.tipp.title.title"
                gspOrder = "IE"
                break
            case "datePaid":
                join     = null
                order    = "datePaid"
                gspOrder = "datePaid"
                break
            case "startDate":
                join     = null
                order    = "startDate"
                gspOrder = "startDate"
                break
            case "endDate":
                join     = null
                order    = "endDate"
                gspOrder = "endDate"
                break
            case "Reference":
                join     = null
                order    = "reference"
                gspOrder = "Reference"
                break
            default:
                order    = "id"
                gspOrder = "Cost Item#"
                break
        }
        return [order,join,gspOrder]
    }

}

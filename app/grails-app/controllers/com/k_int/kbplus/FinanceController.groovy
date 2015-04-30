package com.k_int.kbplus

import com.k_int.kbplus.auth.*
import grails.converters.JSON;
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import java.text.SimpleDateFormat


class FinanceController {

    def springSecurityService
    private static def dateFormat = new SimpleDateFormat("YYYY-MM-dd")



    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        log.debug("FinanceController::index() ${params}");

        if (params.Add == 'add')
            chain(action: "newCostItem", params: params) //just in case :)

        def result         = [:]
        result.user        = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)
        result.editable    = SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')
        result.filterMode  = params.filterSelectionMode?: "OFF"
        result.info        = [] as List
        params.max         = params.max ? Integer.parseInt(params.max) : 10;

        def base_qry   = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
        def qry_params = [result.institution]
        result.institutionSubscriptions = Subscription.executeQuery(base_qry, qry_params);

        def cost_item_qry_params = [result.institution]
        def cost_item_qry        = " from CostItem as ci where ci.owner = ?"

        if (result.filterMode=="ON")
        {
            println("FinanceController::index()  -- Performing filtering processing...")
            def qryOutput          = filterQuery(result,params)
            println(qryOutput.qry_string)
            if (!qryOutput.qry_string) //Nothing found from filtering!
            {
                result.info.add([status:"Filter Mode",msg:"No results found, reset filter"])
                result.info.addAll(qryOutput.failed)
                result.filterMode = "OFF" //SWITCHING BACK!
            }
            else
            {
                result.cost_items = CostItem.executeQuery('select ci ' + cost_item_qry + qryOutput.qry_string, cost_item_qry_params, params);
                result.cost_item_count = CostItem.executeQuery('select count(ci.id) ' + cost_item_qry + qryOutput.qry_string, cost_item_qry_params)[0];
            }
        }

        if (result.filterMode=="OFF" || params.resetMode == "reset")
        {
            result.cost_item_count = CostItem.executeQuery('select count(ci.id) ' + cost_item_qry, cost_item_qry_params)[0];
            result.cost_items      = CostItem.executeQuery('select ci ' + cost_item_qry, cost_item_qry_params, params);
        }


        if (request.isXhr())
            render (template: "filter", model: result)
        else
            result
    }

    def private filterQuery(LinkedHashMap result, GrailsParameterMap params) {
        result.failed     = [] as List
        result.valid      = [] as List
        result.qry_string = ""

        if (params.orderNumberFilter) {
            def order = Order.findByOrderNumberAndOwner(params.orderNumberFilter,result.institution)
            if (order) {
                result.valid.add([status: "Order", msg: "Order: "+params.orderNumberFilter])
                result.qry_string = " AND ci_ord_fk = "+order.id+" "
            } else
                result.failed.add([status: "Order", msg: "Invalid order number " + params.orderNumberFilter])
        }

        if (params.invoiceNumberFilter) {
            def invoice = Invoice.findByInvoiceNumberAndOwner(params.invoiceNumberFilter, result.institution)
            if (invoice) {
                result.valid.add([status: "Invoice", msg: "Invoice: "+params.invoiceNumberFilter])
                result.qry_string+=" AND ci.invoice = "+invoice.id+" "
            } else
                result.failed.add([status: "Invoice", msg: "Invalid invoice number " + params.invoiceNumberFilter])
        }

        if (params.long('subscriptionFilter')) {
            def sub = Subscription.get(params.long('subscriptionFilter'));
            if (sub) {
                result.valid.add([status: "Subscription", msg: "Subscription: "+sub.name])
                result.qry_string+=" AND ci_sub_fk = "+sub.id+" "
            } else
                result.failed.add([status: "Subscription", msg: "Invalid subscription " + params.subscriptionFilter])
        }

        if (params.long('packageFilter')) {
            def pkg = Package.get(params.long('packageFilter'));
            if (pkg) {
                result.valid.add([status: "Sub Package", msg: "Sub Package: "+pkg.name])
                result.qry_string+=" AND ci_subPkg_fk = "+pkg.id
            } else
                result.failed.add([status: "Invoice", msg: "Invalid package " + params.packageFilter])
        }
        return result
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def newCostItem() {
        log.debug("FinanceController::newCostItem() ${params}");

        def result = [:]
        result.institution = Org.findByShortcode(params.shortcode)
        result.error = [] as List

        def order = null
        if (params.newOrderNumber) {
            order = Order.findByOrderNumberAndOwner(params.newOrderNumber, result.institution) ?: new Order(orderNumber: params.newOrderNumber, owner: result.institution).save(flush: true);
        }

        def invoice = null
        if (params.newInvoiceNumber) {
            invoice = Invoice.findByInvoiceNumberAndOwner(params.newInvoiceNumber, result.institution) ?: new Invoice(invoiceNumber: params.newInvoiceNumber, owner: result.institution).save(flush: true);
        }

        def sub = null;
        if (params.newSubscription) {
            sub = Subscription.get(params.long('newSubscription'));
        }

        def pkg = null;
        if (params.newPackage) {
            pkg = Package.get(params.long('newPackage'));
        }

        def datePaid = null
        if (params.newDate) {
            try {
                datePaid = dateFormat.parse(params.newDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : "+params.newDate+" in format "+dateFormat.toPattern())
            }
        }

        def ie = null
        if(params.newIe)
        {
            try {
                ie = IssueEntitlement.load(params.newIe.split(":")[1])
            } catch (Exception e) {
                log.error("Non-valid IssueEntitlement sent "+params.newIe,e)
            }
        }

        def cost_item_status      = params.newCostItemStatus ? (RefdataValue.get(params.long('newCostItemStatus'))) : null;
        def billing_currency      = params.newCostCurrency ? (RefdataValue.get(params.long('newCostCurrency'))) : null;
        def cost_item_element     = params.newCostItemElement ? (RefdataValue.get(params.long('newCostItemElement'))): null
        def cost_tax_type         = params.newCostTaxType ? (RefdataValue.get(params.long('newCostTaxType'))) : null
        def cost_item_category    = params.newCostItemCategory ? (RefdataValue.get(params.long('newCostItemCategory'))): null
//            def cost_billing_currency = params.newCostInBillingCurrency? (RefdataValue.get(params.long('newCostInBillingCurrency'))) : null;
//            def cost_local_currency   = params.newCostInLocalCurrency? (RefdataValue.get(params.long('newCostInLocalCurrency'))) : null;

        def newCostItem = new CostItem(
                owner: result.institution,
                sub: sub,
                subPkg: pkg,
                issueEntitlement: ie,
                order: order,
                invoice: invoice,
                costItemType: cost_item_element,
                costItemCategory: cost_item_category,
                billingCurrency: billing_currency,
                costDescription: params.newDescription,
                costInBillingCurrency: params.newCostInBillingCurrency,
                datePaid: datePaid,
                localFundCode: null,
                costInLocalCurrency: params.newCostInLocalCurrency,
                taxCode: cost_tax_type,
                includeInSubscription: null,
                reference: params.newReference,
                costItemStatus: cost_item_status,
                costItemElement: cost_item_element
        )


        if (!newCostItem.validate()) {
            result.error = newCostItem.errors.allErrors.collect {
                log.debug("Field: " + it.properties.field + ", user input: " + it.properties.rejectedValue + ", Reason! " + it.properties.code)
                message(error: it)
            }
        } else {
            newCostItem.save(flush: true)
            if (params.newBudgetCode)
                createBudgetCodes(newCostItem, params.newBudgetCode, params.shortcode)
        }

        params.remove("Add")
        if (request.isXhr())
            render ([newCostItem:newCostItem.id, error:result.error]) as JSON
        else
        {
            def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
            def qry_params = [result.institution]
            result.institutionSubscriptions = Subscription.executeQuery(base_qry, qry_params);
            render (view: "newCostItem", model: result, params:params)
        }
    }

    private def createBudgetCodes(CostItem costItem, String budgetcodes, String owner) {
        if(budgetcodes && owner && costItem) {
            def budgetOwner = RefdataCategory.findByDesc("budgetcode_"+owner)?:new RefdataCategory(desc: "budgetcode_"+owner).save(flush: true)
            budgetcodes.split(",").each { c ->
                def rdv = null
                if (c.startsWith("-1")) //New codes from UI
                    rdv = new RefdataValue(owner: budgetOwner, value: c.substring(2).toLowerCase()).save(flush: true)
                else
                    rdv = RefdataValue.get(c)

                if (rdv != null)
                    new CostItemGroup(costItem: costItem, budgetcode: rdv).save(flush: true)
            }
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def search() {

        log.debug("FinanceController::search() ${params}");

        def result = [:]

        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        def qry_params = [result.institution]
        def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
        result.institutionSubscriptions = Subscription.executeQuery(base_qry, qry_params);
        def cost_item_qry = " from CostItem as ci where ci.owner = ?"
        result.cost_item_count = CostItem.executeQuery('select count(ci.id) ' + cost_item_qry, qry_params)[0];
        result.cost_items = CostItem.executeQuery('select ci ' + cost_item_qry, qry_params, params);

        def from = new Date();
        from.setTime(from.getTime() - 7 * 1000 * 60 * 60 * 24)
        result.from = from
        result.to = new Date()

        result

    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def getRecentCostItems() {
        def result = [:]
        def institution = Org.findByShortcode(params.shortcode)

        Date from = getFromToDate(params.from,"from")
        Date to   = new Date()
        result.costItems = CostItem.findAllByOwnerAndLastUpdatedBetween(institution,from,to)
        println(result)

        render(template: "/finance/recentlyAdded", model: result)

    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def newCostItemsPresent() {
        def institution = Org.findByShortcode(params.shortcode)
        Date to   = getFromToDate(params.to,"to")
        int count = CostItem.countByOwnerAndLastUpdatedGreaterThan(institution,to)
        render  count
    }

    private Date getFromToDate(def date, def type)
    {
        Date d  = null;
        if (type=="from")
        {
            if (date == null)
            {
                d = new Date();
                d.setTime(d.getTime() - 7 * 1000 * 60 * 60 * 24)
            }
            else
                d = new Date(date)
        }
        else if (type=="to")
        {
            if (date==null)
                d = new Date()
            else
                d = new Date(date)
        }

        return d
    }


    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        log.debug("FinanceController::delete() ${params}");

        def results        = [:]
        results.user        = User.get(springSecurityService.principal.id)
        results.successful = []
        results.failures   = []
        results.message    = null
        results.sentIDs    = JSON.parse(params.del)
        def institution    = Org.findByShortcode(params.shortcode)

        if (results.sentIDs && institution) {
            def _costItem = []
            def _props
            results.sentIDs.each { id ->
                _costItem = CostItem.findAllByIdAndOwner(id,institution)
                if (_costItem.size() > 0)
                {
                    try {
                        _props = _costItem.first().properties
                        _costItem.first().delete(flush: true)
                        results.successful.add(id)
                        log.debug("User: "+results.user+" deleted cost item with properties"+_props)
                    } catch (Exception e)
                    {
                        log.error("FinanceController::delete() : Delete Exception",e)
                        results.failures.add(id)
                    }
                }
                else
                    results.failures.add(id)
            }

            if (results.successful.size() > 0 && results.failures.isEmpty())
                results.message = "All "+results.successful.size()+" completed successfully : "+results.successful
            else if (results.successful.isEmpty() && results.failures.size() > 0)
                results.message = "All "+results.failures.size()+" failed, unable to delete, have they been deleted already? : "+results.failures
            else
                results.message = "Success completed "+results.successful.size()+" out of "+results.sentIDs.size() +" Failures as follows : "+results.failures


        } else
            results.message = "Incorrect parameters sent, not able to process the following : "+results.sentIDs

        render results as JSON
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def importCosts() {

    }
}

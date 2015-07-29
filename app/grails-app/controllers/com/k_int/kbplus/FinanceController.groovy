package com.k_int.kbplus

import com.k_int.kbplus.auth.*
import grails.converters.JSON;
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.text.SimpleDateFormat


class FinanceController {

    def springSecurityService
    def messageSource
    private static def dateFormat = new SimpleDateFormat("YYYY-MM-dd")



    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        log.debug("FinanceController::index() ${params}");

        def result         = [:]
        result.user        = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)
        result.editable    = SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')
        result.max         = params.max ? Integer.parseInt(params.max) : 10;
        result.offset      = params.offset ? Integer.parseInt(params.offset) : 0;
        result.filterMode  = params.filterMode?: "OFF"

        def base_qry   = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
        def qry_params = [result.institution]

        if (params.Add == 'add') {
            redirect(action: "newCostItem", params: params)
        }


        result.institutionSubscriptions = Subscription.executeQuery(base_qry, qry_params);

        def cost_item_qry_params = [result.institution]
        def cost_item_qry        = " from CostItem as ci where ci.owner = ?"

        if (result.filterMode=="ON")
        {
            println("WOOOOOOOOOO")
            //todo append filter requirements validate firstly

        }

        result.cost_item_count = CostItem.executeQuery('select count(ci.id) ' + cost_item_qry, cost_item_qry_params)[0];

        if (result.cost_item_count==0)
        {
            if (result.filterMode=="ON")
            {
                params.remove("filterMode")
                result.msg="Unable to filter, no results found... The filter has been reset"
            }
        }
        else
            result.cost_items  = CostItem.executeQuery('select ci ' + cost_item_qry, cost_item_qry_params, params);

        result
    }

    def private filterQuery(GrailsParameterMap params) {
        println("FilterQuery Paramerters recieved: "+params)
        def result        = [:]
        result.failed     = ""
        result.valid      = ""
        result.qry_string = ""
        result.institution = Org.findByShortcode(params.shortcode)
        if (params.orderNumberFilter) {
            def order = Order.findByOrderNumberAndOwner(params.orderNumberFilter,result.institution)
            if (order) {
                result.valid="Order: "+params.orderNumberFilter+"\n"
                result.qry_string = " AND WHERE ci_ord_fk = "+order.id+" "
            } else {
                result.failed="Invalid order number " + params.orderNumberFilter+" "
            }
        }

        if (params.invoiceNumberFilter) {
            def invoice = Invoice.findByInvoiceNumberAndOwner(params.invoiceNumberFilter, result.institution)
            if (invoice) {
                result.valid+="Invoice: "+params.invoiceNumberFilter+"\n"
                result.qry_string+=" AND where ci.invoice = "+invoice.id+" "
            } else {
                result.failed+="Invalid invoice number " + params.invoiceNumberFilter+" "
            }
        }

        if (params.long('subscriptionFilter')) {
            def sub = Subscription.get(params.long('subscriptionFilter'));
            if (sub) {
                result.valid+="Subscription: "+sub.name
                result.qry_string+=" AND where ci_sub_fk = "+sub.id+" "
            } else {
                result.failed+="Invalid subscription " + params.subscriptionFilter+" "
            }
        }

        if (params.long('packageFilter')) {
            def pkg = Package.get(params.long('packageFilter'));
            if (pkg) {
                result.valid+="Sub Package: "+pkg.name
                result.qry_string+=" AND where ci_subPkg_fk = "+pkg.id
            } else {
                result.failed+="Invalid package " + params.packageFilter+" "
            }
        }
        return result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def filterResultCheck() {
        log.debug("FinanceController::filterResultCheck() ${params}")

        if (!request.isXhr())
            redirect(action: "index", params: params)

        def result         = [:]
        result.institution = Org.findByShortcode(params.shortcode)

        def cost_item_qry_params = [result.institution]
        def cost_item_qry        = " from CostItem as ci where ci.owner = ?"

        def output = filterQuery(params)
        println("Filter Query output: "+output)
        if (!output.qry_string)
            render (count:0, msg:output.failed) as JSON
        else
        {
            println('select count(ci.id) ' + cost_item_qry + output.qry_string)
            def count = CostItem.executeQuery('select count(ci.id) ' + cost_item_qry + output.qry_string, cost_item_qry_params)[0]
            if (count > 0)
                render (count:count, msg:"Cost items results: "+output.valid.toString()) as JSON
            else
                render (count:count, msg: "No cost items results: " + output.valid.toString()) as JSON
        }

        //todo chain onto index if there are results maybe

    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def newCostItem() {
        log.debug("FinanceController::newCostItem() ${params}");

        def result = [:]
        result.institution = Org.findByShortcode(params.shortcode)

        log.debug("Process add cost item, editable active : "+result.editable);

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
                group: null,
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

        newCostItem.save(flush: true);

        if (newCostItem.errors.errorCount > 0) {
            result.error = newCostItem.errors.allErrors.collect {
                message(error:it,encodeAs:'HTML')
            }
            log.debug(newCostItem.errors);
            response.sendError(500)
        }
        params.remove("Add")

        if (request.isXhr())
            render result as JSON
        else
        {
            def base_qry = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
            def qry_params = [result.institution]
            result.institutionSubscriptions = Subscription.executeQuery(base_qry, qry_params);
            render (view: "newCostItem", model: result, params:params)
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
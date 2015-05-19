package com.k_int.kbplus

import com.k_int.kbplus.auth.*
import grails.converters.JSON;
import grails.plugins.springsecurity.Secured
import groovy.json.JsonBuilder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.elasticsearch.common.joda.time.DateTime
import java.text.SimpleDateFormat


class FinanceController {

    def springSecurityService
    private static def dateFormat     = new SimpleDateFormat("YYYY-MM-dd")
    private static def dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {{setLenient(false)}}



    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        log.debug("FinanceController::index() ${params}");

        def result         =  [:]
        result.user        =  User.get(springSecurityService.principal.id)
        result.institution =  Org.findByShortcode(params.shortcode)
        result.editable    =  SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')
        result.filterMode  =  params.filterMode?: "OFF"
        result.info        =  [] as List
        params.max         =  params.int('max') ? Math.min(Integer.parseInt(params.max),200) : 10
        result.max         =  params.max
        result.offset      =  params.offset?:0
        result.sort        =  ["desc","asc"].contains(params.sort)?params.sort : "asc"
        result.sort        =  params.boolean('opSort')==true?((result.sort=="asc")?'desc' : 'asc'): result.sort //opposite
        result.isRelation  =  params.orderRelation? params.boolean('orderRelation'):false
        params.remove('opSort')

        def (order, join, gspOrder) = CostItem.orderingByCheck(params.order) //order = field, join = left join required if not null
        result.order = gspOrder

        def base_qry                    =  " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
        def qry_params                  =  [result.institution]
        result.institutionSubscriptions =  Subscription.executeQuery(base_qry, qry_params);

        def cost_item_qry_params        =  (join)? [result.institution] : [result.institution]
        def cost_item_qry               =  (join)? "LEFT OUTER JOIN ${join} AS j WHERE ci.owner = ? " :"  where ci.owner = ? "
        def orderAndSortBy              =  (join)? "ORDER BY COALESCE(j.${order}, ${Integer.MAX_VALUE}) ${result.sort}, ci.id ASC" : " ORDER BY ci.${order} ${result.sort}"

        if (result.filterMode == "ON")
        {
            log.debug("FinanceController::index()  -- Performing filtering processing...")
            def qryOutput = filterQuery(result,params)
            if (qryOutput.filterCount == 0 || !qryOutput.qry_string) //Nothing found from filtering!
            {
                result.info.add([status:"INFO: Filter Mode",msg:"<b>No results found, <i>reset filter search</i>!!!</b>"])
                result.filterMode =  "OFF" //SWITCHING BACK...since nothing has been found!
            }
            else {
                result.cost_items      =  CostItem.executeQuery('select ci from CostItem as ci ' + cost_item_qry + qryOutput.qry_string + orderAndSortBy, cost_item_qry_params, params);
                result.cost_item_count =  CostItem.executeQuery('select count(ci.id) from CostItem as ci ' + cost_item_qry + qryOutput.qry_string, cost_item_qry_params).first();
            }
            result.info.addAll(qryOutput.failed)
            result.info.addAll(qryOutput.valid)
        }

        if (result.filterMode == "OFF" || params.resetMode == "reset")
        {
            //'SELECT ci FROM CostItem AS ci LEFT OUTER JOIN ci.order AS o WHERE ci.owner = ? ORDER BY COALESCE(o.orderNumber,?) ASC, ci.id ASC'
            result.cost_items      =  CostItem.executeQuery('select ci from CostItem as ci ' + cost_item_qry + orderAndSortBy, cost_item_qry_params, params);
            result.cost_item_count =  CostItem.executeQuery('select count(ci.id) from CostItem as ci ' + cost_item_qry, cost_item_qry_params).first();
        }

        if (request.isXhr())
            render (template: "filter", model: result)
        else
        {
            result.from = dateTimeFormat.format(new DateTime().minusDays(3).toDate())
            result
        }
    }

    def private filterQuery(LinkedHashMap result, GrailsParameterMap params) {
        result.failed     = [] as List
        result.valid      = [] as List
        result.qry_string = ""
        def countCheck    = "select count(ci.id) from CostItem as ci where ci.owner = "+result.institution.id

        if (params.orderNumberFilter) {
            def order      = Order.findByOrderNumberAndOwner(params.orderNumberFilter, result.institution)
            def costOrder  = order ? CostItem.findByOrderAndOwner(order,result.institution) : null
            if (costOrder)
            {
                result.valid.add([status: "SUCESS: Order", msg: "Found Order: " + params.orderNumberFilter])
                result.qry_string = " AND ci_ord_fk = " + order.id + " "
                countCheck       += " AND ci_ord_fk = " + order.id
            } else
            {
                result.failed.add([status: "FAILED: Order", msg: "Invalid order number " + params.orderNumberFilter + (order!=null?"...  No cost items exist with order, however, invoice exits with ID: "+order.id:"...  no existence of invoice instance")])
                params.remove('orderNumberFilter')
            }
        }

        if (params.invoiceNumberFilter) {
            def invoice     = Invoice.findByInvoiceNumberAndOwner(params.invoiceNumberFilter,result.institution)
            def costInvoice = invoice ? CostItem.findByInvoiceAndOwner(invoice,result.institution) : null
            if (costInvoice)
            {
                result.invoiceNumberFilter=params.invoiceNumberFilter
                result.valid.add([status: "SUCESS: Invoice", msg: "Found Invoice: "+params.invoiceNumberFilter])
                result.qry_string += " AND ci.invoice = "+invoice.id+" "
                countCheck        += " AND ci_inv_fk = "+invoice.id
            } else
            {
                result.failed.add([status: "FAILED: Invoice", msg: "Invalid invoice number " + params.invoiceNumberFilter + (invoice!=null?"...  No cost items exist with invoice, however, invoice exits with ID: "+invoice.id:"...  no existence of invoice instance")])
                params.remove('invoiceNumberFilter')
            }
        }

        if (params.long('subscriptionFilter')) {
            def sub     = Subscription.get(params.long('subscriptionFilter'))
            def subCost = sub ? CostItem.findBySubAndOwner(sub,result.institution) : null
            if (subCost)
            {
                result.valid.add([status: "SUCESS: Subscription", msg: "Found Subscription: "+sub.name])
                result.qry_string += " AND ci_sub_fk = "+sub.id+" "
                countCheck        += " AND ci_sub_fk = "+sub.id
            } else
            {
                result.failed.add([status: "FAILED: Subscription", msg: "Invalid subscription, no Cost items with " + (sub!=null? sub.name:'no subscription name!')])
                params.remove('subscriptionFilter')
            }
        }

        if (params.long('packageFilter')) {
            def pkg        = SubscriptionPackage.get(params.long('packageFilter'));
            def subPkgCost = pkg ? CostItem.findBySubPkgAndOwner(pkg,result.institution) : null
            if (subPkgCost)
            {
                result.valid.add([status: "SUCESS: Sub Package", msg: "Found Sub Package: " + pkg.name])
                result.qry_string += " AND ci_subPkg_fk = " + pkg.id
                countCheck        += " AND ci_subPkg_fk = " + pkg.id
            } else
            {
                result.failed.add([status: "FAILED: Sub-Package", msg: "Invalid package, no Cost items with " + params.packageFilter])
                params.remove('packageFilter')
            }
        }

        result.filterCount = CostItem.executeQuery(countCheck).first()
        if (result.failed.size() > 0 || result.filterCount == 0)
        {
            result.failed.add([status: "INFO: No matches together", msg: "Try filtering without invalid criteria...reset invalid input"])
            result.qry_string = ""
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
            pkg = SubscriptionPackage.get(params.long('newPackage'));
        }

        def datePaid = null
        if (params.newDate) {
            try {
                datePaid = dateFormat.parse(params.newDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : "+params.newDate+" in format "+dateFormat.toPattern())
            }
        }

        def startDate = null
        if (params.newStartDate) {
            try {
                startDate = dateFormat.parse(params.newStartDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : "+params.newStartDate+" in format "+dateFormat.toPattern())
            }
        }

        def endDate = null
        if (params.newEndDate) {
            try {
                endDate = dateFormat.parse(params.newEndDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : "+params.newEndDate+" in format "+dateFormat.toPattern())
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
                startDate: startDate,
                endDate: endDate,
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
            if (newCostItem.save(flush: true))
            {
                if (params.newBudgetCode)
                    createBudgetCodes(newCostItem, params.newBudgetCode, params.shortcode)
            } else {
                result.error = "Unable to save!"
            }
        }

        params.remove("Add")
        if (request.isXhr())
            render ([newCostItem:newCostItem.id, error:result.error]) as JSON
        else
        {
            def base_qry   = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
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

        def result         = [:]
        result.user        = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)

        def qry_params                  = [result.institution]
        def base_qry                    = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
        result.institutionSubscriptions = Subscription.executeQuery(base_qry, qry_params);

        def cost_item_qry      = " from CostItem as ci where ci.owner = ?"
        result.cost_item_count = CostItem.executeQuery('select count(ci.id) ' + cost_item_qry, qry_params)[0];
        result.cost_items      = CostItem.executeQuery('select ci ' + cost_item_qry, qry_params, params);

        def from    = new Date();
        from.setTime(from.getTime() - 7 * 1000 * 60 * 60 * 24)
        result.from = from
        result.to   = new Date()

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def getRecentCostItems() {
        def  institution       = Org.findByShortcode(params.shortcode)
        def  result            = [:]
        result.to              = new Date()
        result.from            = params.from? dateTimeFormat.parse(params.from): new Date() //getFromToDate(params.from,"from")
        result.recentlyUpdated = CostItem.findAllByOwnerAndLastUpdatedBetween(institution,result.from,result.to,[max:10, order:"desc", sort:"lastUpdated"])
        result.from            = dateTimeFormat.format(result.from)
        result.to              = dateTimeFormat.format(result.to)
        log.debug("Finane - getRecentCostItems, rendering template with model: "+result)
        render(template: "/finance/recentlyAdded", model: result)
    }


    @Secured(['ROLE_USER'])
    def newCostItemsPresent() {
        def institution = Org.findByShortcode(params.shortcode)
        Date dateTo     = params.to? dateTimeFormat.parse(params.to):new Date()//getFromToDate(params.to,"to")
        int counter     = CostItem.countByOwnerAndLastUpdatedGreaterThan(institution,dateTo)

        def builder = new JsonBuilder()
        def root    = builder {
            count counter
            to dateTimeFormat.format(dateTo)
        }
        log.debug("Finance - newCostItemsPresent ? params: "+params+"\tJSON output: "+builder.toString())
        render(text: builder.toString(), contentType: "text/json", encoding: "UTF-8")
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        log.debug("FinanceController::delete() ${params}");

        def results        =  [:]
        results.successful =  []
        results.failures   =  []
        results.message    =  null
        results.sentIDs    =  JSON.parse(params.del)
        def user           =  User.get(springSecurityService.principal.id)
        def institution    =  Org.findByShortcode(params.shortcode)

        if (results.sentIDs && institution) {
            def _costItem = null
            def _props
            results.sentIDs.each { id ->
                _costItem = CostItem.findByIdAndOwner(id,institution)
                if (_costItem)
                {
                    try {
                        _props = _costItem.properties
                        CostItemGroup.deleteAll(CostItemGroup.findAllByCostItem(_costItem))
                        _costItem.delete(flush: true)
                        results.successful.add(id)
                        log.debug("User: "+user.username+" deleted cost item with properties"+_props)
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
                results.message = "All "+results.successful.size()+" Cost Items completed successfully : "+results.successful
            else if (results.successful.isEmpty() && results.failures.size() > 0)
                results.message = "All "+results.failures.size()+" failed, unable to delete, have they been deleted already? : "+results.failures
            else
                results.message = "Success completed "+results.successful.size()+" out of "+results.sentIDs.size() +" Failures as follows : "+results.failures

        } else
            results.message = "Incorrect parameters sent, not able to process the following : "+(results.sentIDs.size()==0? 'Empty, no IDs present' : results.sentIDs)

        render results as JSON
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def importCosts() {
        response.sendError(404)
    }
}

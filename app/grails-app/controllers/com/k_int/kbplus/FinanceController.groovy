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
    private static def dateFormat      = new SimpleDateFormat("YYYY-MM-dd")
    private static def dateTimeFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {{setLenient(false)}}
    private static final def ci_count  = 'select count(ci.id) from CostItem as ci '
    private static final def ci_select = 'select ci from CostItem as ci '
    private static final def base_qry  = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "

    private boolean userCertified(User user, Org institution)
    {
        if (!user.getAuthorizedOrgs().id.contains(institution.id))
        {
            log.error("User ${user.id} trying to access financial Org information not privy to ${institution.name}")
            return false
        } else
            return true
    }

    //todo track state, maybe use the #! stateful style syntax along with the history API or more appropriately history.js (cross-compatible, polyfill for HTML4)
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        log.debug("FinanceController::index() ${params}");

        def result         =  [:]
        result.institution =  Org.findByShortcode(params.shortcode)

        //Check nothing strange going on with financial data
        def user = User.get(springSecurityService.principal.id)
        if (!userCertified(user,result.institution))
            response.sendError(401)

        //Setup params
        result.editable    =  SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')
        result.filterMode  =  params.filterMode?: "OFF"
        result.info        =  [] as List
        params.max         =  params.int('max') ? Math.min(Integer.parseInt(params.max),200) : 10
        result.max         =  params.max
        result.offset      =  params.offset?:0
        result.sort        =  ["desc","asc"].contains(params.sort)?params.sort : "asc"
        result.sort        =  params.boolean('opSort')==true?((result.sort=="asc")?'desc' : 'asc'): result.sort //opposite
        result.isRelation  =  params.orderRelation? params.boolean('orderRelation'):false
        result.wildcard    =  params.wildcard == "on" && params.filterMode == "ON"
        params.shortcode   =  result.institution.shortcode
        params.remove('opSort')

        def (order, join, gspOrder) = CostItem.orderingByCheck(params.order) //order = field, join = left join required or null, gsporder = to see which field is ordering by
        result.order = gspOrder

        def qry_params                  =  [result.institution]
        result.institutionSubscriptions =  Subscription.executeQuery(base_qry, qry_params);

        def cost_item_qry_params        =   [result.institution]
        def cost_item_qry               =  (join)? "LEFT OUTER JOIN ${join} AS j WHERE ci.owner = ? " :"  where ci.owner = ? "
        def orderAndSortBy              =  (join)? "ORDER BY COALESCE(j.${order}, ${Integer.MAX_VALUE}) ${result.sort}, ci.id ASC" : " ORDER BY ci.${order} ${result.sort}"

        if (result.filterMode == "ON")
        {
            log.debug("FinanceController::index()  -- Performing filtering processing...")
            def qryOutput = filterQuery(result,params,result.wildcard)
            if (qryOutput.filterCount == 0 || !qryOutput.qry_string) //Nothing found from filtering!
            {
                result.info.add([status:"INFO: Filter Mode", msg:"No results found, reset filter search!!!"])
                result.filterMode =  "OFF" //SWITCHING BACK...since nothing has been found!
                result.wildcard   = false
                log.debug("FinanceController::index()  -- Performed filtering process... no results found, turned off filter mode")
            }
            else {
                cost_item_qry_params.addAll(qryOutput.fqParams)
                result.cost_items      =  CostItem.executeQuery(ci_select + cost_item_qry + qryOutput.qry_string + orderAndSortBy, cost_item_qry_params, params);
                result.cost_item_count =  CostItem.executeQuery(ci_count + cost_item_qry + qryOutput.qry_string, cost_item_qry_params).first();
                log.debug("FinanceController::index()  -- Performed filtering process... ${result.cost_item_count} result(s) found")
            }
            result.info.addAll(qryOutput.failed)
            result.info.addAll(qryOutput.valid)
        }

        if (result.filterMode == "OFF" || params.resetMode == "reset")
        {
            //'SELECT ci FROM CostItem AS ci LEFT OUTER JOIN ci.order AS o WHERE ci.owner = ? ORDER BY COALESCE(o.orderNumber,?) ASC, ci.id ASC'
            result.cost_items      =  CostItem.executeQuery(ci_select + cost_item_qry + orderAndSortBy, cost_item_qry_params, params);
            result.cost_item_count =  CostItem.executeQuery(ci_count + cost_item_qry, cost_item_qry_params).first();
            log.debug("FinanceController::index()  -- Performing standard non-filtered process ... ${result.cost_item_count} result(s) found")
        }

        if (request.isXhr())
            render (template: "filter", model: result)
        else
        {
            result.from = dateTimeFormat.format(new DateTime().minusDays(3).toDate())
            result
        }
    }

    def private filterQuery(LinkedHashMap result, GrailsParameterMap params, boolean wildcard) {
        def fqResult      = [:]
        fqResult.failed     = [] as List
        fqResult.valid      = [] as List
        fqResult.qry_string = ""
        def countCheck      = ci_count + " where ci.owner = ? "
        def final count     = ci_count + " where ci.owner = ? "
        fqResult.fqParams   = [result.institution] as List //HQL list parameters for user data, can't be trusted!

        if (params.orderNumberFilter) {
            def _count = (wildcard) ? count + "AND ci.order.orderNumber like ? " : count + "AND ci.order.orderNumber = ? "
            def order =  CostItem.executeQuery(_count, [result.institution, (wildcard) ? "%${params.orderNumberFilter}%" : params.orderNumberFilter])
            log.debug("Filter Query - No of Orders found:${order} Qry Performed:${_count}")

            if (order && order.first() > 0) {
                fqResult.valid.add([status: "SUCCESS: Order", msg: "Found ${order.first()} Order(s): " + params.orderNumberFilter])
                if (wildcard) {
                    fqResult.qry_string = "AND ci.order.orderNumber like ? "
                    countCheck += " AND ci.order.orderNumber like ?"
                    fqResult.fqParams.add("%${params.orderNumberFilter}%")
                } else {
                    fqResult.qry_string = " AND ci.order.orderNumber = ? "
                    countCheck += " AND ci.order.orderNumber = ? "
                    fqResult.fqParams.add(params.orderNumberFilter)
                }
            } else {
                def detachedCount = Order.executeQuery("select count(o.id) from Order o where o.owner = ? AND o.orderNumber = ? ",[result.institution,params.orderNumberFilter]) //Items that exist on their own for the org
                fqResult.failed.add([status: "FAILED: Order", msg: "Invalid order number ${wildcard ? '%' + params.orderNumberFilter + '%' : params.orderNumberFilter} ...No cost items exist with specified order number and ${detachedCount.first()} detached orders"])
                params.remove('orderNumberFilter')
            }
        }

        if (params.invoiceNumberFilter) {
            def _count  = (wildcard) ? count + "AND ci.invoice.invoiceNumber like ? " : count + "AND ci.invoice.invoiceNumber = ? "
            def invoice = CostItem.executeQuery(_count, [result.institution, (wildcard) ? "%${params.invoiceNumberFilter}%" : params.invoiceNumberFilter])

            if (invoice && invoice.first() > 0) {
                fqResult.valid.add([status: "SUCCESS: Invoice", msg: "Found ${invoice.first()} Invoice(s): " + params.invoiceNumberFilter])
                if (wildcard) {
                    fqResult.qry_string = "AND ci.invoice.invoiceNumber like ? "
                    countCheck += " AND ci.invoice.invoiceNumber like ?"
                    fqResult.fqParams.add("%${params.invoiceNumberFilter}%")
                } else {
                    fqResult.qry_string = " AND ci.invoice.invoiceNumber = ? "
                    countCheck += " AND ci.invoice.invoiceNumber  = ? "
                    fqResult.fqParams.add(params.invoiceNumberFilter)
                }
            } else
            {
                def detachedCount = Order.executeQuery("select count(i.id) from Invoice i where i.owner = ? AND i.invoiceNumber = ? ",[result.institution,params.invoiceNumberFilter])
                fqResult.failed.add([status: "FAILED: Invoice", msg: "Invalid order number ${wildcard ? '%' + params.invoiceNumberFilter + '%' : params.invoiceNumberFilter} ...No cost items exist with specified invoice number and ${detachedCount.first()} detached invoices"])
                params.remove('invoiceNumberFilter')
            }
        }

        if (params.long('subscriptionFilter')) {
            def sub     = Subscription.get(params.long('subscriptionFilter'))
            def subCost = sub ? CostItem.findBySubAndOwner(sub,result.institution) : null
            if (subCost)
            {
                fqResult.valid.add([status: "SUCESS: Subscription", msg: "Found Subscription: "+sub.name])
                fqResult.qry_string += " AND ci_sub_fk = "+sub.id+" "
                countCheck        += " AND ci_sub_fk = "+sub.id
            } else
            {
                fqResult.failed.add([status: "FAILED: Subscription", msg: "Invalid subscription, no Cost items with " + (sub!=null? sub.name:'no subscription name!')])
                params.remove('subscriptionFilter')
            }
        }

        if (params.packageFilter && params.packageFilter.startsWith("com.k_int.kbplus.SubscriptionPackage:")) {
            def pkg        = SubscriptionPackage.get(params.packageFilter.split(":")[1]);
            def subPkgCost = pkg ? CostItem.findBySubPkgAndOwner(pkg,result.institution) : null
            if (subPkgCost)
            {
                fqResult.valid.add([status: "SUCESS: Sub Package", msg: "Found Sub Package: " + pkg.pkg.name])
                fqResult.qry_string += " AND ci_subPkg_fk = " + pkg.id
                countCheck        += " AND ci_subPkg_fk = " + pkg.id
            } else
            {
                fqResult.failed.add([status: "FAILED: Sub-Package", msg: "Invalid package, no Cost items with " + params.packageFilter])
                params.remove('packageFilter')
            }
        }

        fqResult.filterCount = CostItem.executeQuery(countCheck,fqResult.fqParams).first()
        if (fqResult.failed.size() > 0 || fqResult.filterCount == 0)
        {
            fqResult.failed.add([status: "INFO: No matches together", msg: "Try filtering without invalid criteria...reset invalid input"])
            fqResult.qry_string = ""
            fqResult.fqParams.clear()
        }
        else
            fqResult.fqParams.remove(0) //already have this where necessary in the index method!
        log.debug("Financials : filterQuery - Wildcard Searching active : ${wildcard} Query output : ${fqResult.qry_string? fqResult.qry_string:'qry failed!'}")

        return fqResult
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
            pkg = SubscriptionPackage.load(params.newPackage.split(":")[1])
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
                ie = IssueEntitlement.load(params.newIe.split(":")[1]) //OIDService uses get(), return proxy obj for ref instead
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
                    createBudgetCodes(newCostItem, params.newBudgetCode, result.institution.shortcode)
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
        def result = []
        if(budgetcodes && owner && costItem) {
            def budgetOwner = RefdataCategory.findByDesc("budgetcode_"+owner)?:new RefdataCategory(desc: "budgetcode_"+owner).save(flush: true)
            budgetcodes.split(",").each { c ->
                def rdv = null
                if (c.startsWith("-1")) //New codes from UI
                    rdv = new RefdataValue(owner: budgetOwner, value: c.substring(2).toLowerCase()).save(flush: true)
                else
                    rdv = RefdataValue.get(c)

                if (rdv != null)
                    result.add(new CostItemGroup(costItem: costItem, budgetcode: rdv).save(flush: true))
            }
        }
        result
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
        if (userCertified(user,institution))
            response.sendError(401)

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
    def financialRef() {
        log.debug("Financials :: financialRef - Params: ${params}")
        def result   = [:]
        result.error = [] as List
        def institution = Org.findByShortcode(params.shortcode)
        def owner       = refData(params.owner)
        log.debug("Financials :: financialRef - Owner instance returned: ${owner.obj}")
        if (owner) {
            def relation = refData(params.relation)
            log.debug("Financials :: financialRef - relation obj or stub returned "+relation)
            if (relation)
            {
                log.debug("Financials :: financialRef - Relation needs creating: "+relation.create)
                if (relation.create)
                {
                    if(relation.obj.hasProperty(params.relationField))
                    {
                        relation.obj."${params.relationField}" = params.val
                        relation.obj.owner = institution
                        log.debug("Financials :: financialRef -Creating Relation val:${params.val} field:${params.relationField} org:${institution.name}")
                        if ( relation.obj.save() )
                            log.debug("Financials :: financialRef - Saved the new relational inst ${relation.obj}")
                        else
                            result.error.add([status: "FAILED: Creating ${params.ownerField}", msg: "Invalid data received to retrieve from DB"])
                    }
                    else
                        result.error.add([status: "FAILED: Setting value", msg: "The data you are trying to set does not exist"])
                }

                if (owner.obj.hasProperty(params.ownerField)) {
                    log.debug("Using owner instance field of ${params.ownerField} to set new instance of ${relation.obj.class} with ID ${relation.obj.id}")
                    owner.obj."${params.ownerField}" = relation.obj
                    result.relation = relation.obj
                }
            }
            else
                result.error.add([status: "FAILED: Related Cost Item Data", msg: "Invalid data received to retrieve from DB"])
        }
         else
            result.error.add([status: "FAILED: Cost Item", msg: "Invalid data received to retrieve from DB"])

        render result as JSON
    }

    def private refData(String oid) {
        def result = [:]
        result.create = false
        def oid_components = oid.split(':');
        def dynamic_class  = grailsApplication.getArtefact('Domain',oid_components[0]).getClazz()
        if ( dynamic_class)
        {
            if (oid_components[1].equals("create"))
            {
                result.obj = dynamic_class.newInstance()
                result.create = true
            }
            else
                result.obj = dynamic_class.get(oid_components[1])
        }
        result
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def removeBC() {
        log.debug("Financials :: remove budget code - Params: ${params}")
        def result      = [:]
        result.success  = [status:  "Success: Deleted code", msg: "Deleted instance of the budget code for the specified cost item"]
        def user        = User.get(springSecurityService.principal.id)
        def institution = Org.findByShortcode(params.shortcode)

        if (!user.getAuthorizedOrgs().id.contains(institution.id))
        {
            log.error("User ${user.id} has tried to delete budget code information for Org not privy to ${institution.name}")
            response.sendError(401)
        }
        def ids = params.bcci ? params.bcci.split("_")[1..2] : null
        if (ids && ids.size()==2)
        {
            def cig = CostItemGroup.load(ids[0])
            def ci  = CostItem.load(ids[1])
            if (cig && ci)
            {
                if (cig.costItem == ci)
                    cig.delete(flush: true)
                else
                    result.error = [status: "FAILED: Deleting budget code", msg: "Budget code is not linked with the cost item"]
            }
        } else
            result.error = [status: "FAILED: Deleting budget code", msg: "Incorrect parameter information sent"]

        render result as JSON
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def addNewBC() {
        def result = []
        def institution = Org.findByShortcode(params.shortcode)
        def user        = User.get(springSecurityService.principal.id)
        if (!userCertified(user,institution))
            response.sendError(401)

        def ci = CostItem.findByIdAndOwner(params.cost,institution)
        if (ci) {
            def codes = createBudgetCodes(ci,params.newBudgetCode,institution.shortcode)
            if (codes)
                result = codes.collect { [id:"bcci_${it.id}_${it.costItem.id}", text: it.budgetcode.value] }
        }

        render result as JSON
    }
}
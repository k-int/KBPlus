package com.k_int.kbplus

import com.k_int.kbplus.auth.*
import grails.converters.JSON;
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

//todo Refactor aspects into service
//todo track state, maybe use the #! stateful style syntax along with the history API or more appropriately history.js (cross-compatible, polyfill for HTML4)
//todo Discuss versioning for edits?
class FinanceController {

    def springSecurityService
    private final def dateFormat      = new java.text.SimpleDateFormat("YYYY-MM-dd")
    private final def dateTimeFormat  = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {{setLenient(false)}}
    private final def ci_count        = 'select count(ci.id) from CostItem as ci '
    private final def ci_select       = 'select ci from CostItem as ci '
    private final def base_qry        = " from Subscription as s where  ( ( exists ( select o from s.orgRelations as o where o.roleType.value = 'Subscriber' and o.org = ? ) ) ) AND ( s.status.value != 'Deleted' ) "
    private final def admin_role      = Role.findByAuthority('ROLE_ADMIN')
    private final def defaultCurrency = RefdataCategory.lookupOrCreate('Currency','GBP - United Kingdom Pound')
    private final def defaultInclSub  = RefdataCategory.lookupOrCreate('YN','Yes')

    private boolean userCertified(User user, Org institution)
    {
        if (!user.getAuthorizedOrgs().id.contains(institution.id))
        {
            log.error("User ${user.id} trying to access financial Org information not privy to ${institution.name}")
            return false
        } else
            return true
    }

    boolean isFinanceAuthorised(Org org, User user) {
        def retval = false
        if (org && org.hasUserWithRole(user,admin_role))
            retval = true
        return retval
    }



    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        log.debug("FinanceController::index() ${params}");

        def result = [:]

        //Check nothing strange going on with financial data
        result.institution =  Org.findByShortcode(params.shortcode)
        def user           =  User.get(springSecurityService.principal.id)
        if (isFinanceAuthorised(result.institution, user)) {
            flash.error=message(code: 'financials.permission.unauthorised', args: [result.institution? result.institution.name : 'N/A'])
            response.sendError(401)
        }

        //Accessed from Subscription page, 'hardcoded' set subscription 'hardcode' values
        result.inSubMode   = params.boolean('inSubMode')?: false
        if (result.inSubMode)
        {
            result.fixedSubscription = params.int('sub')? Subscription.get(params.sub) : null
            if (!result.fixedSubscription) {
                log.error("Financials in FIXED subscription mode, sent incorrect subscription ID: ${params?.sub}")
                response.sendError(400, "No relevant subscription, please report this error to an administrator")
            }
        }

        //Grab the financial data
        financialData(result,params,user)


        //Other than first run, request will always be AJAX...
        if (request.isXhr())
        {
            render (template: "filter", model: result)
        }
        else
        {
            use(groovy.time.TimeCategory) {
                result.from = dateTimeFormat.format(new Date() - 3.days)
            }
            result
        }
    }

    /**
     * Sets up the financial data parameters and performs the relevant DB search
     * @param result - LinkedHashMap for model data
     * @param params - Qry data sent from view
     * @param user   - Currently logged in user object
     * @return Cost Item count & data / view information for pagination, sorting, etc
     */
    private def financialData(result,params,user) {
        //Setup params
        result.editable    =  SpringSecurityUtils.ifAllGranted(admin_role.authority)
        
        // Ensure we also add to the request object as an attribute here so that the Taglib picks
        // it up
        request.setAttribute("editable", result.editable)
        
        
        result.filterMode  =  params.filterMode?: "OFF"
        result.info        =  [] as List
        params.max         =  params.int('max') ? Math.min(Integer.parseInt(params.max),200) : (user?.defaultPageSize?: 10)
        result.max         =  params.max
        result.offset      =  params.offset?: 0
        result.sort        =  ["desc","asc"].contains(params.sort)?params.sort : "asc"
        result.sort        =  params.boolean('opSort')==true?((result.sort=="asc")?'desc' : 'asc') : result.sort //opposite
        result.isRelation  =  params.orderRelation? params.boolean('orderRelation') : false
        result.wildcard    =  params._wildcard == "off" ? false: true //defaulted to on
        params.shortcode   =  result.institution.shortcode
        params.remove('opSort')

        //Query setup options, ordering, joins, param query data....
        def (order, join, gspOrder) = CostItem.orderingByCheck(params.order) //order = field, join = left join required or null, gsporder = to see which field is ordering by
        result.order = gspOrder

        def qry_params                  =  [result.institution]
        //todo remove after UI sub select is changed to select2
        result.institutionSubscriptions =  Subscription.executeQuery(base_qry, qry_params);

        def cost_item_qry_params        =  [result.institution]
        def cost_item_qry               =  (join)? "LEFT OUTER JOIN ${join} AS j WHERE ci.owner = ? " :"  where ci.owner = ? "
        def orderAndSortBy              =  (join)? "ORDER BY COALESCE(j.${order}, ${Integer.MAX_VALUE}) ${result.sort}, ci.id ASC" : " ORDER BY ci.${order} ${result.sort}"

        //Filter processing...
        if (result.filterMode == "ON")
        {
            log.debug("FinanceController::index()  -- Performing filtering processing...")
            def qryOutput = filterQuery(result,params,result.wildcard)
            if (qryOutput.filterCount == 0 || !qryOutput.qry_string) //Nothing found from filtering!
            {
                result.info.add([status:message(code: 'financials.result.filtered.info', args: [message(code: 'financials.result.filtered.mode')]),
                                 msg:message(code: 'finance.result.filtered.empty')])
                result.filterMode =  "OFF" //SWITCHING BACK!!! ...Since nothing has been found, informed user!
                result.wildcard   =  true //default behaviour is ON
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

        //Normal browse mode, default behaviour
        if (result.filterMode == "OFF" || params.resetMode == "reset")
        {
            //'SELECT ci FROM CostItem AS ci LEFT OUTER JOIN ci.order AS o WHERE ci.owner = ? ORDER BY COALESCE(o.orderNumber,?) ASC, ci.id ASC'
            result.cost_items      =  CostItem.executeQuery(ci_select + cost_item_qry + orderAndSortBy, cost_item_qry_params, params);
            result.cost_item_count =  CostItem.executeQuery(ci_count + cost_item_qry, cost_item_qry_params).first();
            log.debug("FinanceController::index()  -- Performing standard non-filtered process ... ${result.cost_item_count} result(s) found")
        }
    }


    def financialsExport()  {
        if (request.isPost() && params.format == "csv") {
            def result = [:]
            result.institution =  Org.findByShortcode(params.shortcode)
            def user           =  User.get(springSecurityService.principal.id)
            if (isFinanceAuthorised(result.institution, user)) {
                flash.error=message(code: 'financials.permission.unauthorised', args: [result.institution? result.institution.name : 'N/A'])
                response.sendError(401)
            }

            financialData(result,params,user)

            if (result.result.cost_item_count > 0)
            {
                response.setHeader("Content-disposition", "attachment; filename=${result?.institution}.csv")
                response.contentType = "text/csv"
                def out = response.outputStream
                def useHeader = params.header? true : false
                processFinancialCSV(out,chainModel,useHeader)
                out.close()
            }
            else
            {
                response.sendError(501)
            }
        }
        else
        {
            response.sendError(400)
        }
    }

    /**
     * Make a CSV export of cost item results
     * @param out    - Output stream
     * @param result - passed from index
     * @param header - true or false
     * @return
     */
    def private processFinancialCSV(out, result, header) {
        def generation_start = new Date()

        out.withWriter { writer ->

            if ( header ) {
                writer.write("Institution,Generated Date,Cost Item Count\n")
                writer.write("${result.institution.name?:''},${dateFormat.format(generation_start)},${result.cost_item_count}\n")
            }

            // Output the body text
            writer.write("cost_item_id,owner,invoice_no,order_no,subscription_name,subscription_package,issueEntitlement,date_paid,date_valid_from,date_valid_to,cost_Item_Type,cost_Item_Category,cost_Item_Status,billing_Currency,cost_In_Billing_Currency,cost_In_Local_Currency,tax_Code,cost_Item_Element,cost_Description,reference,codes,created_by,date_created,edited_by,date_last_edited\n");

            result.cost_items.each { ci ->

                def codes = CostItemGroup.findAllByCostItem(ci).collect { it.budgetcode.value+'\t' }

                def start_date   = ci.startDate ? dateFormat.format(ci.startDate) : ''
                def end_date     = ci.endDate ? dateFormat.format(ci.endDate) : ''
                def paid_date    = ci.datePaid ? dateFormat.format(ci.datePaid) : ''
                def created_date = ci.dateCreated ? dateFormat.format(ci.dateCreated) : ''
                def edited_date  = ci.lastUpdated ? dateFormat.format(ci.lastUpdated) : ''

                writer.write("\"${ci.id}\",\"${ci.owner}\",\"${ci.invoice?ci.invoice.invoiceNumber:''}\",${ci.order? ci.order.orderNumber:''},${ci.sub? ci.sub.name:''},${ci.subPkg?ci.subPkg.pkg.name:''},${ci.issueEntitlement?ci.issueEntitlement.tipp.title.title:''},${paid_date},${start_date},\"${end_date}\",\"${ci.costItemType?ci.costItemType.value:''}\",\"${ci.costItemCategory?ci.costItemCategory.value:''}\",\"${ci.costItemStatus?ci.costItemStatus.value:''}\",\"${ci.billingCurrency.value?:''}\",\"${ci.costInBillingCurrency?:''}\",\"${ci.costInLocalCurrency?:''}\",\"${ci.taxCode?ci.taxCode.value:''}\",\"${ci.costItemElement?ci.costItemElement.value:''}\",\"${ci.costDescription?:''}\",\"${ci.reference?:''}\",\"${codes?codes.toString():''}\",\"${ci.createdBy}\",\"${created_date}\",\"${ci.lastUpdatedBy}\",\"${edited_date}\"\n");
            }
            writer.flush()
            writer.close()
        }
    }

    def private filterQuery(LinkedHashMap result, GrailsParameterMap params, boolean wildcard) {
        def fqResult        = [:]
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
                fqResult.valid.add([status: message(code: 'financials.result.filtered.success', args: [message(code: 'financials.field.order')]),
                                    msg: message(code: 'financials.result.filtered.success.msg1', args: [order.first(), message(code: 'financials.field.order'), params.orderNumberFilter])])
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
                fqResult.failed.add([status: message(code: 'financials.result.filtered.failed', args: [message(code: 'financials.field.order')]),
                                     msg: message(code: 'financials.result.filtered.failed.msg1', args: [message(code: 'financials.field.order'), (wildcard ? '%' + params.orderNumberFilter + '%' : params.orderNumberFilter), detachedCount.first()])])
                params.remove('orderNumberFilter')
            }
        }

        if (params.invoiceNumberFilter) {
            def _count  = (wildcard) ? count + "AND ci.invoice.invoiceNumber like ? " : count + "AND ci.invoice.invoiceNumber = ? "
            def invoice = CostItem.executeQuery(_count, [result.institution, (wildcard) ? "%${params.invoiceNumberFilter}%" : params.invoiceNumberFilter])

            if (invoice && invoice.first() > 0) {
                fqResult.valid.add([status: message(code: 'financials.result.filtered.success', args: [message(code: 'financials.field.invoice')]),
                                    msg: message(code: 'financials.result.filtered.success.msg1', args: [invoice.first(), message(code: 'financials.field.invoice'), params.invoiceNumberFilter])])
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
                fqResult.failed.add([status:message(code: 'financials.result.filtered.failed', args: [message(code: 'financials.field.invoice')]),
                                     msg: "Invalid order number ${wildcard ? '%' + params.invoiceNumberFilter + '%' : params.invoiceNumberFilter} ...No cost items exist with specified invoice number and ${detachedCount.first()} detached invoices"])
                params.remove('invoiceNumberFilter')
            }
        }

        if (params?.subscriptionFilter?.startsWith("com.k_int.kbplus.Subscription:")) {

            def sub     = Subscription.get(params.subscriptionFilter.split(":")[1])
            def subCost = sub ? CostItem.findBySubAndOwner(sub,result.institution) : null
            if (subCost)
            {
                fqResult.valid.add([status: message(code: 'financials.result.filtered.success', args: [message(code: 'financials.field.sub')]),
                                    msg: "Found Subscription: "+sub.name])
                fqResult.qry_string += " AND ci_sub_fk = "+sub.id+" "
                countCheck          += " AND ci_sub_fk = "+sub.id
            } else
            {
                fqResult.failed.add([status: message(code: 'financials.result.filtered.failed', args: [message(code: 'financials.field.sub')]),
                                     msg: message(code: 'financials.result.filtered.failed.msg2', args: [ message(code: 'financials.field.sub'), sub!=null? sub.name:'no subscription name!'])])
                params.remove('subscriptionFilter')
            }
        }

        if (params?.packageFilter?.startsWith("com.k_int.kbplus.SubscriptionPackage:")) {
            def pkg        = SubscriptionPackage.get(params.packageFilter.split(":")[1]);
            def subPkgCost = pkg ? CostItem.findBySubPkgAndOwner(pkg,result.institution) : null
            if (subPkgCost)
            {
                fqResult.valid.add([status: message(code: 'financials.result.filtered.success', args: [message(code: 'financials.field.subpkg')]),
                                    msg: message(code: 'financials.result.filtered.success.msg2', args: [ message(code: 'financials.field.subpkg'), params.packageFilter])])
                fqResult.qry_string += " AND ci_subPkg_fk = " + pkg.id
                countCheck          += " AND ci_subPkg_fk = " + pkg.id
            } else
            {
                fqResult.failed.add([status: message(code: 'financials.result.filtered.failed', args: [message(code: 'financials.field.subpkg')]),
                                     msg: message(code: 'financials.result.filtered.failed.msg2', args: [ message(code: 'financials.field.subpkg'), params.packageFilter])])
                params.remove('packageFilter')
            }
        }

        fqResult.filterCount = CostItem.executeQuery(countCheck,fqResult.fqParams).first()
        if (fqResult.failed.size() > 0 || fqResult.filterCount == 0)
        {
            fqResult.failed.add([status: message(code: 'financials.result.filtered.info', args: [message(code: 'financials.result.filtered.nomatch')]),
                                 msg: message(code: 'financials.result.filtered.invalid')])
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

        def result          =  [:]
        result.institution  =  Org.findByShortcode(params.shortcode)
        def user            =  User.get(springSecurityService.principal.id)
        result.error        =  [] as List

        if (isFinanceAuthorised(result.institution, user)) {
            result.error=message(code: 'financials.permission.unauthorised', args: [result.institution? result.institution.name : 'N/A'])
            response.sendError(403)
        }

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
            try {
                sub = Subscription.get(params.newSubscription.contains(":")? params.newSubscription.split(":")[1] : params.newSubscription);
            } catch (Exception e) {
                log.error("Non-valid subscription sent ${params.newSubscription}",e)
            }

        }

        def pkg = null;
        if (params.newPackage?.size() > 2) { //default xx
            try {
                pkg = SubscriptionPackage.load(params.newPackage.split(":")[1])
            } catch (Exception e) {
                log.error("Non-valid sub-package sent ${params.newPackage}",e)
            }
        }

        def datePaid = null
        if (params.newDate) {
            try {
                datePaid = dateFormat.parse(params.newDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : ${params.newDate} in format ${dateFormat.toPattern()}")
            }
        }

        def startDate = null
        if (params.newStartDate) {
            try {
                startDate = dateFormat.parse(params.newStartDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : ${params.newStartDate} in format ${dateFormat.toPattern()}")
            }
        }

        def endDate = null
        if (params.newEndDate) {
            try {
                endDate = dateFormat.parse(params.newEndDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : ${params.newEndDate} in format ${dateFormat.toPattern()}")
            }
        }

        def ie = null
        if(params.newIe)
        {
            try {
                ie = IssueEntitlement.load(params.newIe.split(":")[1])
            } catch (Exception e) {
                log.error("Non-valid IssueEntitlement sent ${params.newIe}",e)
            }
        }

        def billing_currency = null
        if (params.long('newCostCurrency')) //GBP,etc
        {
            billing_currency = RefdataValue.get(params.newCostCurrency)
            if (!billing_currency)
                billing_currency = defaultCurrency
        }

        def cost_item_status    = params.newCostItemStatus ? (RefdataValue.get(params.long('newCostItemStatus'))) : null;    //estimate, commitment, etc
        def cost_item_element   = params.newCostItemElement ? (RefdataValue.get(params.long('newCostItemElement'))): null    //admin fee, platform, etc
        def cost_tax_type       = params.newCostTaxType ? (RefdataValue.get(params.long('newCostTaxType'))) : null           //on invoice, self declared, etc
        def cost_item_category  = params.newCostItemCategory ? (RefdataValue.get(params.long('newCostItemCategory'))): null  //price, bank charge, etc
//            def cost_billing_currency = params.newCostInBillingCurrency? (RefdataValue.get(params.long('newCostInBillingCurrency'))) : null;
//            def cost_local_currency   = params.newCostInLocalCurrency? (RefdataValue.get(params.long('newCostInLocalCurrency'))) : null;

        def inclSub = params.includeInSubscription? (RefdataValue.get(params.long('includeInSubscription'))): defaultInclSub

        //todo check fields which need calculating and giving specific default values
        def newCostItem = new CostItem(
                owner: result.institution,
                sub: sub,
                subPkg: pkg,
                issueEntitlement: ie,
                order: order,
                invoice: invoice,
                costItemType: null, //todo Ask Owen/Ian unknown field
                costItemCategory: cost_item_category,
                costItemElement: cost_item_element,
                costItemStatus: cost_item_status,
                billingCurrency: billing_currency, //Not specified default to GDP
                taxCode: cost_tax_type,
                costDescription: params.newDescription? params.newDescription.trim()?.toLower():null,
                costInBillingCurrency: params.newCostInBillingCurrency? params.double('newCostInBillingCurrency'):null,
                datePaid: datePaid,
                startDate: startDate,
                endDate: endDate,
                localFundCode: null,
                costInLocalCurrency: params.double('newCostInLocalCurrency')?: null,
                includeInSubscription: inclSub?: defaultInclSub,
                reference: params.newReference? params.newReference.trim()?.toLower() : null
        )


        if (!newCostItem.validate()) {
            result.error = newCostItem.errors.allErrors.collect {
                log.error("Field: ${it.properties.field}, user input: ${it.properties.rejectedValue}, Reason! ${it.properties.code}")
                message(code:'finance.addNew.error',args:[it.properties.field])
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
        render ([newCostItem:newCostItem.id, error:result.error]) as JSON
    }

    private def createBudgetCodes(CostItem costItem, String budgetcodes, String owner) {
        def result = []
        if(budgetcodes && owner && costItem) {
            def budgetOwner = RefdataCategory.findByDesc("budgetcode_"+owner)?:new RefdataCategory(desc: "budgetcode_"+owner).save(flush: true)
            budgetcodes.split(",").each { c ->
                def rdv = null
                if (c.startsWith("-1")) //New code option from select2 UI
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
    def getRecentCostItems() {
        def  institution       = Org.findByShortcode(params.shortcode)
        def  result            = [:]
        result.to              = new Date()
        result.from            = params.from? dateTimeFormat.parse(params.from): new Date()
        result.recentlyUpdated = CostItem.findAllByOwnerAndLastUpdatedBetween(institution,result.from,result.to,[max:10, order:"desc", sort:"lastUpdated"])
        result.from            = dateTimeFormat.format(result.from)
        result.to              = dateTimeFormat.format(result.to)
        log.debug("FinanceController - getRecentCostItems, rendering template with model: "+result)
        render(template: "/finance/recentlyAdded", model: result)
    }


    @Secured(['ROLE_USER'])
    def newCostItemsPresent() {
        def institution = Org.findByShortcode(params.shortcode)
        Date dateTo     = params.to? dateTimeFormat.parse(params.to):new Date()//getFromToDate(params.to,"to")
        int counter     = CostItem.countByOwnerAndLastUpdatedGreaterThan(institution,dateTo)

        def builder = new groovy.json.JsonBuilder()
        def root    = builder {
            count counter
            to dateTimeFormat.format(dateTo)
        }
        log.debug("Finance - newCostItemsPresent ? params: ${params} JSON output: ${builder.toString()}")
        render(text: builder.toString(), contentType: "text/json", encoding: "UTF-8")
    }

    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def delete() {
        log.debug("FinanceController::delete() ${params}");

        def results        =  [:]
        results.successful =  []
        results.failures   =  []
        results.message    =  null
        results.sentIDs    =  JSON.parse(params.del) //comma seperated list
        def user           =  User.get(springSecurityService.principal.id)
        def institution    =  Org.findByShortcode(params.shortcode)
        if (isFinanceAuthorised(institution, user))
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
                        log.debug("User: ${user.username} deleted cost item with properties ${_props}")
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
                results.message = "All ${results.successful.size()} Cost Items completed successfully : ${results.successful}"
            else if (results.successful.isEmpty() && results.failures.size() > 0)
                results.message = "All ${results.failures.size()} failed, unable to delete, have they been deleted already? : ${results.failures}"
            else
                results.message = "Success completed ${results.successful.size()} out of ${results.sentIDs.size()}  Failures as follows : ${results.failures}"

        } else
            results.message = "Incorrect parameters sent, not able to process the following : ${results.sentIDs.size()==0? 'Empty, no IDs present' : results.sentIDs}"

        render results as JSON
    }


    @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
    def financialRef() {
        log.debug("Financials :: financialRef - Params: ${params}")

        def result      = [:]
        result.error    = [] as List
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
        def result         = [:]
        result.create      = false
        def oid_components = oid.split(':');
        def dynamic_class  = grailsApplication.getArtefact('Domain',oid_components[0]).getClazz()
        if ( dynamic_class)
        {
            if (oid_components[1].equals("create"))
            {
                result.obj    = dynamic_class.newInstance()
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
            def cig = CostItemGroup.get(ids[0])
            def ci  = CostItem.get(ids[1])
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
    def createCode() {
        def result      = [:]
        def user        = springSecurityService.currentUser
        def institution = Org.findByShortcode(params.shortcode)

        if (!userCertified(user,institution))
            response.sendError(401)

        def code        = params.code?.trim()
        def ci          = CostItem.findByIdAndOwner(params.id, institution)

        if (code && ci)
        {
            def cig_codes = createBudgetCodes(ci,code,institution.shortcode)
            if (result.codes.isEmpty())
                result.error = "Unable to create budget code(s): ${code}"
            else
            {
                result.success = "${cig_codes.size()} new code(s) added to cost item"
                result.codes   = cig_codes.collect {
                    "<span class='budgetCode'>${it.budgetcode.value}</span><a id='bcci_${it.id}_${it.costItem.id}' class='badge budgetCode'>x</a>"
                }
            }
        } else
            result.error = "Invalid data received for code creation"

        render result as JSON
    }
}
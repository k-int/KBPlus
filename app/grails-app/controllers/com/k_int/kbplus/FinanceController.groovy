package com.k_int.kbplus

import com.k_int.kbplus.auth.*
import grails.converters.JSON;
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

//todo Refactor aspects into service
//todo track state, opt 1: potential consideration of using get, opt 2: requests maybe use the #! stateful style syntax along with the history API or more appropriately history.js (cross-compatible, polyfill for HTML4)
//todo Change notifications integration maybe use : changeNotificationService with the onChange domain event action
//todo Refactor index separation of filter page (used for AJAX), too much content, slows DOM on render/binding of JS functionality
//todo Enable advanced searching, use configurable map, see filterQuery() 
class FinanceController {

    def springSecurityService
    private final def dateFormat      = new java.text.SimpleDateFormat("yyyy-MM-dd")
    private final def dateTimeFormat  = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {{setLenient(false)}}
    private final def ci_count        = 'select count(ci.id) from CostItem as ci '
    private final def ci_select       = 'select ci from CostItem as ci '
    private final def admin_role      = Role.findByAuthority('INST_ADM')
    private final def defaultCurrency = RefdataCategory.lookupOrCreate('Currency','GBP - United Kingdom Pound')
    private final def maxAllowedVals  = [10,20,50,100,200] //in case user has strange default list size, plays hell with UI
    //private final def defaultInclSub  = RefdataCategory.lookupOrCreate('YN','Yes') //Owen is to confirm this functionality

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
        if (org && org.hasUserWithRole(user,admin_role)) //ROLE_ADMIN
            retval = true
        return retval
    }



    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
      log.debug("FinanceController::index() ${params}");

      def result = [:]

      try {

        //Check nothing strange going on with financial data
        result.institution =  Org.findByShortcode(params.shortcode)
        def user           =  User.get(springSecurityService.principal.id)

        //Accessed from Subscription page, 'hardcoded' set subscription 'hardcode' values
        //todo Once we know we are in sub only mode, make nessesary adjustments in setupQueryData()
        result.inSubMode   = params.sub ? true : false
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

        result.isXHR = request.isXhr()
        //Other than first run, request will always be AJAX...
        if (result.isXHR)
        {
            render (template: "filter", model: result)
        }
        else
        {
            //First run, make a date for recently updated costs AJAX operation
            use(groovy.time.TimeCategory) {
                result.from = dateTimeFormat.format(new Date() - 3.days)
            }
        }
      }
      catch ( Exception e ) {
        log.error("Error processing index",e);
      }

      result
    }

    /**
     * Setup the data for the financials processing (see financialData())
     * @param result
     * @param params
     * @param user
     * @return Query data to performing selection, ordering/sorting, and query parameters (e.g. institution)
     */
    private def setupQueryData(result, params, user) {
        //Setup params
        result.editable    =  result.institution.hasUserWithRole(user,admin_role)
        request.setAttribute("editable", result.editable) //editable Taglib doesn't pick up AJAX request, REQUIRED!
        result.filterMode  =  params.filterMode?: "OFF"
        result.info        =  [] as List
        params.max         =  params.max && params.int('max') ? Math.min(params.int('max'),200) : (user?.defaultPageSize? maxAllowedVals.min{(it-user.defaultPageSize).abs()} : 10)
        result.max         =  params.max
        result.offset      =  params.int('offset',0)?: 0
        result.sort        =  ["desc","asc"].contains(params.sort)? params.sort : "desc" //defaults to sort & order of desc id 
        result.sort        =  params.boolean('opSort')==true? ((result.sort=="asc")? 'desc' : 'asc') : result.sort //opposite
        result.isRelation  =  params.orderRelation? params.boolean('orderRelation',false) : false
        result.wildcard    =  params._wildcard == "off"? false: true //defaulted to on
        params.shortcode   =  result.institution.shortcode
        result.advSearch   =  params.boolean('advSearch',false)
        params.remove('opSort')

        if (params.csvMode && request.getHeader('referer')?.endsWith("${params?.shortcode}/finance")) {
            params.max = -1 //Adjust so all results are returned, in regards to present user screen query
            log.debug("Making changes to query setup data for an export...")
        }
        //Query setup options, ordering, joins, param query data....
        def (order, join, gspOrder) = CostItem.orderingByCheck(params.order) //order = field, join = left join required or null, gsporder = to see which field is ordering by
        result.order = gspOrder
        
        //todo Add to query params and HQL query if we are in sub mode e.g. result.inSubMode, result.fixedSubscription
        def cost_item_qry_params  =  [result.institution]
        def cost_item_qry         =  (join)? "LEFT OUTER JOIN ${join} AS j WHERE ci.owner = ? " :"  where ci.owner = ? "
        def orderAndSortBy        =  (join)? "ORDER BY COALESCE(j.${order}, ${Integer.MAX_VALUE}) ${result.sort}, ci.id ASC" : " ORDER BY ci.${order} ${result.sort}"

        return [cost_item_qry_params, cost_item_qry, orderAndSortBy]
    }

    /**
     * Sets up the financial data parameters and performs the relevant DB search
     * @param result - LinkedHashMap for model data
     * @param params - Qry data sent from view
     * @param user   - Currently logged in user object
     * @return Cost Item count & data / view information for pagination, sorting, etc
     *
     * Note - Requests DB requests are cached ONLY if non-hardcoded values are used
     */
    private def financialData(result,params,user) {
        //Setup using param data, returning back DB query info
        def (cost_item_qry_params, cost_item_qry, orderAndSortBy) = setupQueryData(result,params,user)

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

        //Normal browse mode, default behaviour, nothing found from trying to filter, resorts to below
        if (result.filterMode == "OFF" || params.resetMode == "reset")
        {
            //'SELECT ci FROM CostItem AS ci LEFT OUTER JOIN ci.order AS o WHERE ci.owner = ? ORDER BY COALESCE(o.orderNumber,?) ASC, ci.id ASC'
            result.cost_items      =  CostItem.executeQuery(ci_select + cost_item_qry + orderAndSortBy, cost_item_qry_params, params);
            result.cost_item_count =  CostItem.executeQuery(ci_count + cost_item_qry, cost_item_qry_params).first();
            log.debug("FinanceController::index()  -- Performing standard non-filtered process ... ${result.cost_item_count} result(s) found")
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def financialsExport()  {
        log.debug("Financial Export :: ${params}")

        if (request.isPost() && params.format == "csv") {
            def result = [:]
            result.institution =  Org.findByShortcode(params.shortcode)
            def user           =  User.get(springSecurityService.principal.id)

            if (!isFinanceAuthorised(result.institution, user)) {
                flash.error=message(code: 'financials.permission.unauthorised', args: [result.institution? result.institution.name : 'N/A'])
                response.sendError(403)
                return
            }

            financialData(result,params,user) //Grab the financials!
            def filename = result.institution.name
            response.setHeader("Content-disposition", "attachment; filename=\"${filename}_financialExport.csv\"")
            response.contentType = "text/csv"
            def out = response.outputStream
            def useHeader = params.header? true : false //For batch processing...
            processFinancialCSV(out,result,useHeader)
            out.close()
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
    //todo change for batch processing... don't want to kill the server, defaulting to all results presently!
    def private processFinancialCSV(out, result, header) {
        def generation_start = new Date()
        def processedCounter = 0

        switch (params.csvMode)
        {
            case "code":
                log.debug("Processing code mode... Estimated total ${params.estTotal?: 'Unknown'}")

                def categories = RefdataValue.findAllByOwner(RefdataCategory.findByDesc('CostItemStatus')).collect {it.value.toString()} << "Unknown"

                def codeResult = [:].withDefault {
                    categories.collectEntries {
                        [(it): 0 as Double]
                    }
                }

                result.cost_items.each { c ->
                    if (!c.budgetcodes.isEmpty())
                    {
                        log.debug("${c.budgetcodes.size()} codes for Cost Item: ${c.id}")

                        def status = c?.costItemStatus?.value? c.costItemStatus.value.toString() : "Unknown"

                        c.budgetcodes.each {code ->
                            if (!codeResult.containsKey(code.value))
                                codeResult[code.value] //sets up with default values

                            if (!codeResult.get(code.value).containsKey(status))
                            {
                                log.warn("Status should exist in list already, unless additions have been made? Code:${code} Status:${status}")
                                codeResult.get(code.value).put(status, c?.costInLocalCurrency? c.costInLocalCurrency : 0.0)
                            }
                            else
                            {
                                codeResult[code.value][status] += c?.costInLocalCurrency?: 0.0
                            }
                        }
                    }
                    else
                    {
                        log.debug("skipped cost item ${c.id} NO codes are present")
                    }
                }

                def catSize = categories.size()-1
                out.withWriter { writer ->
                    writer.write("\t" + categories.join("\t") + "\n") //Header

                    StringBuilder sb = new StringBuilder() //join map vals e.g. estimate : 123

                    codeResult.each {code, cat_statuses ->
                        sb.append(code).append("\t")
                        cat_statuses.eachWithIndex { status, amount, idx->
                            sb.append(amount)
                            if (idx < catSize)
                                sb.append("\t")
                        }
                        sb.append("\n")
                    }
                    writer.write(sb.toString())
                    writer.flush()
                    writer.close()
                }

                processedCounter = codeResult.size()
                break

            case "sub":
                log.debug("Processing subscription data mode... calculation of costs Estimated total ${params.estTotal?: 'Unknown'}")

                def categories = RefdataValue.findAllByOwner(RefdataCategory.findByDesc('CostItemStatus')).collect {it.value} << "Unknown"

                def subResult = [:].withDefault {
                    categories.collectEntries {
                        [(it): 0 as Double]
                    }
                }

                def skipped = []

                result.cost_items.each { c ->
                    if (c?.sub)
                    {
                        def status = c?.costItemStatus?.value? c.costItemStatus.value.toString() : "Unknown"
                        def subID  = c.sub.name

                        if (!subResult.containsKey(subID))
                            subResult[subID] //1st time around for subscription, could 1..* cost items linked...

                        if (!subResult.get(subID).containsKey(status)) //This is here as a safety precaution, you're welcome :P
                        {
                            log.warn("Status should exist in list already, unless additions have been made? Sub:${subID} Status:${status}")
                            subResult.get(subID).put(status, c?.costInLocalCurrency? c.costInLocalCurrency : 0.0)
                        }
                        else
                        {
                            subResult[subID][status] += c?.costInLocalCurrency?: 0.0
                        }
                    }
                    else
                    {
                        skipped.add("${c.id}")
                    }
                }

                log.debug("Skipped ${skipped.size()} out of ${result.cost_items.size()} Cost Item's (NO subscription present) IDs : ${skipped} ")

                def catSize = categories.size()-1
                out.withWriter { writer ->
                    writer.write("\t" + categories.join("\t") + "\n") //Header

                    StringBuilder sb = new StringBuilder() //join map vals e.g. estimate : 123

                    subResult.each {sub, cat_statuses ->
                        sb.append(sub).append("\t")
                        cat_statuses.eachWithIndex { status, amount, idx->
                            sb.append(amount)
                            if (idx < catSize)
                                sb.append("\t")
                        }
                        sb.append("\n")
                    }
                    writer.write(sb.toString())
                    writer.flush()
                    writer.close()
                }

                processedCounter = subResult.size()
                break

            case "all":
            default:
                log.debug("Processing all mode... Estimated total ${params.estTotal?: 'Unknown'}")

                out.withWriter { writer ->

                    if ( header ) {
                        writer.write("Institution\tGenerated Date\tCost Item Count\n")
                        writer.write("${result.institution.name?:''}\t${dateFormat.format(generation_start)}\t${result.cost_item_count}\n")
                    }

                    // Output the body text
                    writer.write("cost_item_id\towner\tinvoice_no\torder_no\tsubscription_name\tsubscription_package\tissueEntitlement\tdate_paid\tdate_valid_from\t" +
                            "date_valid_to\tcost_Item_Category\tcost_Item_Status\tbilling_Currency\tcost_In_Billing_Currency\tcost_In_Local_Currency\ttax_Code\t" +
                            "cost_Item_Element\tcost_Description\treference\tcodes\tcreated_by\tdate_created\tedited_by\tdate_last_edited\n");

                    result.cost_items.each { ci ->

                        def codes = CostItemGroup.findAllByCostItem(ci).collect { it?.budgetcode?.value+'\t' }

                        def start_date   = ci.startDate ? dateFormat.format(ci?.startDate) : ''
                        def end_date     = ci.endDate ? dateFormat.format(ci?.endDate) : ''
                        def paid_date    = ci.datePaid ? dateFormat.format(ci?.datePaid) : ''
                        def created_date = ci.dateCreated ? dateFormat.format(ci?.dateCreated) : ''
                        def edited_date  = ci.lastUpdated ? dateFormat.format(ci?.lastUpdated) : ''

                        writer.write("\"${ci.id}\"\t\"${ci?.owner?.name}\"\t\"${ci?.invoice?ci.invoice.invoiceNumber:''}\"\t${ci?.order? ci.order.orderNumber:''}\t" +
                                "${ci?.sub? ci.sub.name:''}\t${ci?.subPkg?ci.subPkg.pkg.name:''}\t${ci?.issueEntitlement?ci.issueEntitlement?.tipp?.title?.title:''}\t" +
                                "${paid_date}\t${start_date}\t\"${end_date}\"\t\"${ci?.costItemCategory?ci.costItemCategory.value:''}\"\t\"${ci?.costItemStatus?ci.costItemStatus.value:''}\"\t" +
                                "\"${ci?.billingCurrency.value?:''}\"\t\"${ci?.costInBillingCurrency?:''}\"\t\"${ci?.costInLocalCurrency?:''}\"\t\"${ci?.taxCode?ci.taxCode.value:''}\"\t" +
                                "\"${ci?.costItemElement?ci.costItemElement.value:''}\"\t\"${ci?.costDescription?:''}\"\t\"${ci?.reference?:''}\"\t\"${codes?codes.toString():''}\"\t" +
                                "\"${ci.createdBy.username}\"\t\"${created_date}\"\t\"${ci.lastUpdatedBy.username}\"\t\"${edited_date}\"\n")
                    }
                    writer.flush()
                    writer.close()
                }

                processedCounter = result.cost_items.size()
                break
        }
        groovy.time.TimeDuration duration = groovy.time.TimeCategory.minus(new Date(), generation_start)
        log.debug("CSV export operation for ${params.csvMode} mode -- Duration took to complete (${processedCounter} Rows of data) was: ${duration} --")
    }

    /**
     * Method used by index to configure the HQL, check existence, and setup helpful messages
     * @param result
     * @param params
     * @param wildcard
     * @return
     */
    //todo convert to use a property map, too big now with advanced searching options
    //todo adjust filterQuery to check for being in subscription only mode using result.inSubMode and force HQL filtered query string to use  result.fixedSubscription when searching for other results.
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

        if (result.advSearch)
        {
            log.debug("Advanced Filter search setup...")
            //todo complete advance searching
            params.findAll {key, val->
                println("Key ${key} Val ${val}")
            }
        }

        if (result.offset > 0) //Remove count successes, etc when a user is not on the first page
            fqResult.valid.clear()

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

    //todo complete a configurable search which can be extended via external properties if necessary
    //Not sure if this is the best approach
    private static qry_conf = [
            'ci'  : [
                    selectQry:'select ci from CostItem as ci ',
                    countQry: 'select count(ci.id) from CostItem as ci '
             ],
            'invoice' : [
                    stdQry: 'AND ci.invoice.invoiceNumber like ? ',
                    wildQry: 'AND ci.invoice.invoiceNumber like ? ',
                    countQry:"",
            ],
            'adv' : [
                 'adv_ref' : [

                 ],
                 'adv_codes':[

                 ],
                 'adv_start':[

                 ],
                 'adv_end':[

                 ],
                 'adv_costItemStatus':[

                 ],
                 'adv_costItemCategory':[

                 ],
                 'adv_amount':[

                 ],
                 'adv_datePaid':[

                 ],
                 'adv_ie':[

                 ],
            ]
    ]


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def newCostItem() {

      def result =  [:]
      def newCostItem = null;

      try {
        log.debug("FinanceController::newCostItem() ${params}");

        result.institution  =  Org.findByShortcode(params.shortcode)
        def user            =  User.get(springSecurityService.principal.id)
        result.error        =  [] as List

        if (!isFinanceAuthorised(result.institution, user))
        {
            result.error=message(code: 'financials.permission.unauthorised', args: [result.institution? result.institution.name : 'N/A'])
            response.sendError(403)
        }

        def order = null
        if (params.newOrderNumber)
            order = Order.findByOrderNumberAndOwner(params.newOrderNumber, result.institution) ?: new Order(orderNumber: params.newOrderNumber, owner: result.institution).save(flush: true);

        def invoice = null
        if (params.newInvoiceNumber)
            invoice = Invoice.findByInvoiceNumberAndOwner(params.newInvoiceNumber, result.institution) ?: new Invoice(invoiceNumber: params.newInvoiceNumber, owner: result.institution).save(flush: true);

        def sub = null;
        if (params.newSubscription?.contains("com.k_int.kbplus.Subscription:"))
        {
            try {
                sub = Subscription.get(params.newSubscription.split(":")[1]);
            } catch (Exception e) {
                log.error("Non-valid subscription sent ${params.newSubscription}",e)
            }

        }

        def pkg = null;
        if (params.newPackage?.contains("com.k_int.kbplus.SubscriptionPackage:"))
        {
            try {
                pkg = SubscriptionPackage.load(params.newPackage.split(":")[1])
            } catch (Exception e) {
                log.error("Non-valid sub-package sent ${params.newPackage}",e)
            }
        }

        def datePaid = null
        if (params.newDate)
        {
            try {
                datePaid = dateFormat.parse(params.newDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : ${params.newDate} in format ${dateFormat.toPattern()}")
            }
        }

        def startDate = null
        if (params.newStartDate)
        {
            try {
                startDate = dateFormat.parse(params.newStartDate)
            } catch (Exception e) {
                log.debug("Unable to parse date : ${params.newStartDate} in format ${dateFormat.toPattern()}")
            }
        }

        def endDate = null
        if (params.newEndDate)
        {
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

        def tempCurrencyVal       = params.newCostExchangeRate? params.double('newCostExchangeRate',1.00) : 1.00
        def cost_item_status      = params.newCostItemStatus ? (RefdataValue.get(params.long('newCostItemStatus'))) : null;    //estimate, commitment, etc
        def cost_item_element     = params.newCostItemElement ? (RefdataValue.get(params.long('newCostItemElement'))): null    //admin fee, platform, etc
        def cost_tax_type         = params.newCostTaxType ? (RefdataValue.get(params.long('newCostTaxType'))) : null           //on invoice, self declared, etc
        def cost_item_category    = params.newCostItemCategory ? (RefdataValue.get(params.long('newCostItemCategory'))): null  //price, bank charge, etc
        def cost_billing_currency = params.newCostInBillingCurrency? params.double('newCostInBillingCurrency',0.00) : 0.00
        def cost_local_currency   = params.newCostInLocalCurrency?   params.double('newCostInLocalCurrency',cost_billing_currency * tempCurrencyVal) : 0.00

        //def inclSub = params.includeInSubscription? (RefdataValue.get(params.long('includeInSubscription'))): defaultInclSub //todo Speak with Owen, unknown behaviour

        newCostItem = new CostItem(
                owner: result.institution,
                sub: sub,
                subPkg: pkg,
                issueEntitlement: ie,
                order: order,
                invoice: invoice,
                costItemCategory: cost_item_category,
                costItemElement: cost_item_element,
                costItemStatus: cost_item_status,
                billingCurrency: billing_currency, //Not specified default to GDP
                taxCode: cost_tax_type,
                costDescription: params.newDescription? params.newDescription.trim()?.toLowerCase():null,
                costInBillingCurrency: cost_billing_currency as Double,
                costInLocalCurrency: cost_local_currency as Double,
                datePaid: datePaid,
                startDate: startDate,
                endDate: endDate,
                includeInSubscription: null, //todo Discussion needed, nobody is quite sure of the functionality behind this...
                reference: params.newReference? params.newReference.trim()?.toLowerCase() : null
        )


        if (!newCostItem.validate())
        {
            result.error = newCostItem.errors.allErrors.collect {
                log.error("Field: ${it.properties.field}, user input: ${it.properties.rejectedValue}, Reason! ${it.properties.code}")
                message(code:'finance.addNew.error',args:[it.properties.field])
            }
        }
        else
        {
            if (newCostItem.save(flush: true))
            {
                if (params.newBudgetCode)
                    createBudgetCodes(newCostItem, params.newBudgetCode?.trim()?.toLowerCase(), result.institution.shortcode)
            } else {
                result.error = "Unable to save!"
            }
        }
      }
      catch ( Exception e ) {
        log.error("Problem in add cost item",e);
      }

      params.remove("Add")
      render ([newCostItem:newCostItem.id, error:result.error]) as JSON
    }

    private def createBudgetCodes(CostItem costItem, String budgetcodes, String owner)
    {
        def result = []
        if(budgetcodes && owner && costItem)
        {
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
        def  recentParams      = [max:10, order:'desc', sort:'lastUpdated']
        result.to              = new Date()
        result.from            = params.from? dateTimeFormat.parse(params.from): new Date()
        result.recentlyUpdated = CostItem.findAllByOwnerAndLastUpdatedBetween(institution,result.from,result.to,recentParams)
        result.from            = dateTimeFormat.format(result.from)
        result.to              = dateTimeFormat.format(result.to)
        log.debug("FinanceController - getRecentCostItems, rendering template with model: ${result}")
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
        if (!isFinanceAuthorised(institution, user))
        {
            response.sendError(403)
            return
        }

        if (results.sentIDs && institution)
        {
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

        //check in reset mode e.g. subscription changed, meaning IE & SubPkg will have to be reset
        boolean resetMode = params.boolean('resetMode',false) && params.fields
        boolean wasReset  = false


        if (owner) {
            if (resetMode)
                wasReset = refReset(owner,params.fields,result)


            if (resetMode && !wasReset)
                log.debug("Field(s): ${params.fields} should have been reset as relation: ${params.relation} has been changed")
            else {
                //continue happy path...
                def relation = refData(params.relation)
                log.debug("Financials :: financialRef - relation obj or stub returned " + relation)

                if (relation) {
                    log.debug("Financials :: financialRef - Relation needs creating: " + relation.create)
                    if (relation.create) {
                        if (relation.obj.hasProperty(params.relationField)) {
                            relation.obj."${params.relationField}" = params.val
                            relation.obj.owner = institution
                            log.debug("Financials :: financialRef -Creating Relation val:${params.val} field:${params.relationField} org:${institution.name}")
                            if (relation.obj.save())
                                log.debug("Financials :: financialRef - Saved the new relational inst ${relation.obj}")
                            else
                                result.error.add([status: "FAILED: Creating ${params.ownerField}", msg: "Invalid data received to retrieve from DB"])
                        } else
                            result.error.add([status: "FAILED: Setting value", msg: "The data you are trying to set does not exist"])
                    }

                    if (owner.obj.hasProperty(params.ownerField)) {
                        log.debug("Using owner instance field of ${params.ownerField} to set new instance of ${relation.obj.class} with ID ${relation.obj.id}")
                        owner.obj."${params.ownerField}" = relation.obj
                        result.relation = ['class':relation.obj.id, 'id':relation.obj.id] //avoid excess data leakage
                    }
                } else
                    result.error.add([status: "FAILED: Related Cost Item Data", msg: "Invalid data received to retrieve from DB"])
            }
        } else
            result.error.add([status: "FAILED: Cost Item", msg: "Invalid data received to retrieve from DB"])



        render result as JSON
    }

    /**
     *
     * @param costItem - The owner instance passed from financialRef
     * @param fields - comma seperated list of fields to reset, has to be in allowed list (see below)
     * @param result - LinkedHashMap from financialRef
     * @return
     */
    def private refReset(costItem, String fields, result) {
        log.debug("Attempting to reset a reference for cost item data ${costItem} for field(s) ${fields}")
        def wasResetCounter = 0
        def f               = fields?.split(',')
        def allowed         = ["sub", "issueEntitlement", "subPkg", "invoice", "order"]
        boolean validFields = false

        if (f)
        {
            validFields = f.every { allowed.contains(it) && costItem.obj.hasProperty(it) }

            if (validFields)
                f.each { field ->
                    costItem.obj."${field}" = null
                    if (costItem.obj."${field}" == null)
                        wasResetCounter++
                    else
                        result.error.add([status: "FAILED: Cost Item", msg: "Problem resetting data for field ${field}"])
                }
            else
                result.error.add([status: "FAILED: Cost Item", msg: "Problem resetting data, invalid fields received"])
        }
        else
            result.error.add([status: "FAILED: Cost Item", msg: "Invalid data received"])

        return validFields && wasResetCounter == f.size()
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
            response.sendError(403)
            return
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
        {
            response.sendError(403)
            return
        }
        def code  = params.code?.trim()
        def ci    = CostItem.findByIdAndOwner(params.id, institution)

        if (code && ci)
        {
            def cig_codes = createBudgetCodes(ci,code,institution.shortcode)
            if (cig_codes.isEmpty())
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

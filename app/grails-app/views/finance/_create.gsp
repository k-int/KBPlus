<form id="createCost">
    <table id="newCosts" class="table table-striped table-bordered table-condensed table-tworow">
        <thead>
        <tr>
            <th rowspan="2" style="vertical-align: top;">Cost Item#</th>
            <th>Invoice#</th>
            <th>Order#</th>
            <th>Subscription</th>
            <th>Package</th>
            <th colspan="2" style="vertical-align: top;">Issue Entitlement</th>
        </tr>
        <tr>
            <th>Date</th>
            <th>Amount [billing]/<br/>[local]</th>
            <th>Reference</th>
            <th colspan="3">Description</th>
        </tr>
        </thead>

        <tbody>
        <tr><td colspan="9">&nbsp;</td></tr>
        <tr>
            <td rowspan="2">Add a new cost item</td>
            <td><input type="text" name="newInvoiceNumber" class="input-medium"
                       placeholder="New item invoice #" id="newInvoiceNumber" value="${params.newInvoiceNumber}"/></td>
            <td><input type="text" name="newOrderNumber" class="input-medium"
                       placeholder="New Order #" id="newOrderNumber" value="${params.newOrderNumber}"/></td>
            <td>
                <select  name="newSubscription" class="input-medium" id="newSubscription" value="${params.newSubscription}">
                    <option value="xx">Not specified</option>
                    <g:each in="${institutionSubscriptions}" var="s">
                        <option value="${s.id}" ${s.id==params.long('subscriptionFilter')?'selected="selected"':''}>${s.name}</option>
                    </g:each>
                </select>
            </td>
            <td>
                <select name="newPackage" class="input-medium" id="newPackage" value="${params.newPackage}">
                    <option value="xx">Not specified</option>
                </select>
            </td>
            <td>
                <input name="newIe" class="input-medium" id="newIE" value="${params.newIe}">
            </td>
            <td rowspan="2">
                <g:submitToRemote data-action="create" onSuccess="Finance.updateResults('create')"
                                  onFailure="errorHandling(textStatus,'create',errorThrown)"
                                  url="[controller:'finance', action: 'newCostItem']" type="submit"
                                  name="Add" value="add">
                </g:submitToRemote> </br></br>
                <button id="resetCreate"  type="button">reset</button>
            </td>
        </tr>
        <tr>
            <td>
                <h3>Cost date</h3>
                <input class="datepicker-class" type="date" placeholder="Date Paid" name="newDate" value="${params.newDate}"/><br/>
                <h3>Statuses</h3>
                <g:select name="newCostItemStatus"
                          from="${costItemStatus}"
                          optionKey="id"
                          title="${g.message(code: 'financials.addNew.costState')}"
                          noSelection="${['':'No Status']}"/> <br/>

                <g:select name="newCostItemCategory"
                          from="${costItemCategory}"
                          optionKey="id"
                          title="${g.message(code: 'financials.addNew.costCategory')}"
                          noSelection="${['':'No Category']}"/> <br/>

                <g:select name="newCostItemElement"
                          from="${costItemElement}"
                          optionKey="id"
                          noSelection="${['':'No Element']}"/> <br/>

                <g:select name="newCostTaxType"
                          from="${taxType}"
                          optionKey="id"
                          title="${g.message(code: 'financials.addNew.taxCateogry')}"
                          noSelection="${['':'No Tax Type']}"/> <br/>
            </td>
            <td>
                <h3>Cost values and tax</h3>
                <input type="number" name="newCostInBillingCurrency" placeholder="New Cost Ex-Tax - Billing Currency" id="newCostInBillingCurrency" step="0.01"/> <br/>
                <input title="${g.message(code: 'financials.addNew.exchangeRate')}" type="number" class="percentage" step="0.01" name="newCostExchangeRate" placeholder="Exchange Rate" id="newCostExchangeRate" value="1" /> <br/>
                <input type="number" name="newCostInLocalCurrency" placeholder="New Cost Ex-Tax - Local Currency" id="newCostInLocalCurrency" step="0.01"/> <br/>

                <g:select name="newCostCurrency"
                          from="${currency}"
                          optionKey="id"
                          title="${g.message(code: 'financials.addNew.currencyType')}"
                          optionValue="text"/>
            </td>
            <td>
                <h3>Reference/Codes</h3>
                <input type="text" name="newReference" placeholder="New Item Reference" id="newCostItemReference" value="${params.newReference}"/><br/>
                <input type="text" style="width: 220px; border-radius: 4px;" placeholder="New code or lookup code" name="newBudgetCode" id="newBudgetCode" ><br/><br/><br/>
                <h3>Validity Period (Dates)</h3>
                From: <input class="datepicker-class" placeholder="Start Date" type="date" id="newStartDate" name="newStartDate"/> </br>
                To: &nbsp;&nbsp;&nbsp;&nbsp;<input class="datepicker-class" placeholder="End Date" type="date" id="newEndDate" name="newEndDate"/>
            </td>
            <td colspan="2">
                %{--todo Get some clarity on what includeInSubscription is actually for--}%
                %{--<h3>Include in Subscription</h3>--}%
                %{--<g:select style="width: 60%" name="includeInSubscription"--}%
                          %{--from="${yn}"--}%
                          %{--optionKey="id"--}%
                          %{--title="${g.message(code: 'financials.addNew.inclSub')}"--}%
                          %{--optionValue="value"--}%
                %{--/>--}%
                <h3>Description</h3>
                <textarea name="newDescription"
                          placeholder="New Item Description" id="newCostItemDescription"/></textarea>
        </tr>
        <g:hiddenField name="shortcode" value="${params.shortcode}"></g:hiddenField>
        </tbody>
    </table>
</form>
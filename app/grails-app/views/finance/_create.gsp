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
            <th>Amount [billing] * [Exchange] = [local]</th>
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
                <input ${inSubMode ? "disabled='disabled' data-filterMode='${fixedSubscription?.class.getName()}:${fixedSubscription?.id}'" : '' }
                        name="newSubscription" class="input-xlarge select2" placeholder="New Subscription" id="newSubscription"
                        value="${inSubMode ? fixedSubscription?.name : params.newSubscription}" data-subfilter=""/>
                <g:if test="${inSubMode}">
                    <g:hiddenField data-subfilter="" name="newSubscription" value="${fixedSubscription?.class.getName()}:${fixedSubscription?.id}"></g:hiddenField>
                </g:if>
            </td>
            <td>
                <g:if test="${inSubMode}">
                  <input class="select2 input-xlarge"  data-subFilter="${fixedSubscription?.id}" data-disableReset="true" name="newPackage" id="newPackage" />
                </g:if>
                <g:else>
                  <input class="select2 input-xlarge" disabled='disabled' data-subFilter="" data-disableReset="true" name="newPackage" id="newPackage" />
                </g:else>
            </td>
            <td>
                <g:if test="${inSubMode}">
                  <input name="newIe"  data-subFilter="${fixedSubscription?.id}" data-disableReset="true" class="input-large select2" id="newIE" value="${params.newIe}">
                </g:if>
                <g:else>
                  <input name="newIe" disabled='disabled' data-subFilter="" data-disableReset="true" class="input-large select2" id="newIE" value="${params.newIe}">
                </g:else>
            </td>
            <td rowspan="2">
                <g:submitToRemote data-action="create" onSuccess="Finance.updateResults('create');Finance.clearCreate()"
                                  onFailure="errorHandling(textStatus,'create',errorThrown)"
                                  url="[controller:'finance', action: 'newCostItem']" type="submit"
                                  name="Add" value="add">
                </g:submitToRemote> </br></br>
            </td>
        </tr>
        <tr>
            <td>
                <h3>Cost date</h3>
                <input class="datepicker-class" type="date" placeholder="Date Paid" name="newDate" id="newDatePaid" value="${params.newDate}"/><br/>
                <h3>Statuses</h3>
                <g:select name="newCostItemStatus"
                          id="newCostItemStatus"
                          from="${costItemStatus}"
                          optionKey="id"
                          title="${g.message(code: 'financials.addNew.costState')}"
                          noSelection="${['':'No Status']}"/> <br/>

                <g:select name="newCostItemCategory"
                          id="newCostItemCategory"
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
                <h3>Cost values and Currency</h3>
                <input type="number" name="newCostInBillingCurrency" class="calc" placeholder="New Cost Ex-Tax - Billing Currency" id="newCostInBillingCurrency" step="0.01"/> <br/>
                <input title="${g.message(code: 'financials.addNew.exchangeRate')}" type="number" class="calc" step="0.01" name="newCostExchangeRate" placeholder="Exchange Rate" id="newCostExchangeRate" value="1" /> <br/>
                <input type="number" class="calc" name="newCostInLocalCurrency" placeholder="New Cost Ex-Tax - Local Currency" id="newCostInLocalCurrency" step="0.01"/> <br/>

                <g:select name="newCostCurrency"
                          from="${currency}"
                          optionKey="id"
                          title="${g.message(code: 'financials.addNew.currencyType')}"
                          optionValue="text"/>
            </td>
            <td>
                <h3>Reference/Codes</h3>
                <input type="text" name="newReference" placeholder="New Item Reference" id="newCostItemReference" value="${params.newReference}"/><br/>
                <input type="text" class="select2" style="width: 220px; border-radius: 4px;" placeholder="New code or lookup code" name="newBudgetCode" id="newBudgetCode" ><br/><br/><br/>
                <h3>Validity Period (Dates)</h3>
                From: <input class="datepicker-class" placeholder="Start Date" type="date" id="newStartDate" name="newStartDate"/> </br>
                To: &nbsp;&nbsp;&nbsp;&nbsp;<input class="datepicker-class" placeholder="End Date" type="date" id="newEndDate" name="newEndDate"/>
            </td>
            <td colspan="2">
                <h3>Description</h3>
                <textarea name="newDescription"
                          placeholder="New Item Description" id="newCostItemDescription"/></textarea>
        </tr>
        <g:hiddenField name="shortcode" value="${params.shortcode}"></g:hiddenField>
        </tbody>
    </table>
</form>

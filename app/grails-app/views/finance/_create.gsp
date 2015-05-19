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
            <td rowspan="2">Add new cost item</td>
            <td><input type="text" name="newInvoiceNumber" class="input-medium"
                       placeholder="New item invoice #" id="newInvoiceNumber" value="${params.newInvoiceNumber}"/></td>
            <td><input type="text" name="newOrderNumber" class="input-medium"
                       placeholder="New Order #" id="newOrderNumber" value="${params.newOrderNumber}"/></td>
            <td>
                <select name="newSubscription" class="input-medium" id="newSubscription" value="${params.newSubscription}">
                    <option value="all">Not Set</option>
                    <g:each in="${institutionSubscriptions}" var="s">
                        <option value="${s.id}" ${s.id==params.long('newSubscription')?'selected="selected"':''}>${s.name}</option>
                    </g:each>
                </select>
            </td>
            <td>
                <select name="newPackage" class="input-medium" id="newPackage" value="${params.newPackage}">
                    <option value="">Not Set</option>
                </select>
            </td>
            <td>
                <input name="newIe" class="input-medium" id="newIE" value="${params.newIe}">
            </td>
            <td rowspan="2">
                <g:submitToRemote onSuccess="clearCreateForm();updateResults('create')" onFailure="errorHandling(textStatus,'create',errorThrown)" url="[controller:'finance', action: 'newCostItem']" type="submit" name="Add" value="add"></g:submitToRemote> </br></br>
                <button onclick="reset()" type="reset">reset</reset>
            </td>
        </tr>
        <tr>
            <td>
                <h3>Cost date and status</h3>
                <input class="datepicker-class" type="date" placeholder="Date Paid" name="newDate" value="${params.newDate}"/><br/>

                <g:select name="newCostItemStatus"
                          from="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','CostItemStatus')}"
                          optionKey="id"
                          noSelection="${['':'No Status']}"/>

                <g:select name="newCostItemCategory"
                          from="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','CostItemCategory')}"
                          optionKey="id"
                          noSelection="${['':'No Category']}"/>

                <g:select name="newCostItemElement"
                          from="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','CostItemElement')}"
                          optionKey="id"
                          noSelection="${['':'No Element']}"/>

                <g:select name="newCostCurrency"
                          from="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','Currency')}"
                          optionKey="id"
                          noSelection="${['':'No Currency']}"/>

                <g:select name="newCostTaxType"
                          from="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','TaxType')}"
                          optionKey="id"
                          noSelection="${['':'No Tax Type']}"/>
            </td>
            <td>
                <h3>Cost values and tax</h3>
                <input type="number" name="newCostInBillingCurrency" placeholder="New Cost Ex-Tax - Billing Currency" id="newCostInBillingCurrency" step="0.01"/> <br/>
                <input type="number" name="newCostExchangeRate" placeholder="Exchange Rate" id="newCostExchangeRate" step="0.01"/> <br/>
                <input type="number" name="newCostInLocalCurrency" placeholder="New Cost Ex-Tax - Local Currency" id="newCostInLocalCurrency" step="0.01"/>
                <input type="number" name="newCostTaxRate" placeholder="New Cost Tax Rate" id="newCostTaxRate" step="0.01"/>
                <input type="number" name="newCostTaxAmount" placeholder="New Cost Tax Amount" id="newCostTaxAmount" step="0.01"/>
            </td>
            <td>
                <h3>Reference</h3>
                <input type="text" name="newReference" placeholder="New Item Reference" id="newCostItemReference" value="${params.newReference}"/><br/>
                <input type="text" style="width: 100%; border-radius: 4px;" placeholder="New code or lookup code" name="newBudgetCode" id="newBudgetCode" ><br/><br/><br/>
                <input class="datepicker-class" placeholder="Start Date" type="date" id="newStartDate" name="newStartDate"/>
                <input class="datepicker-class" placeholder="End Date"   type="date" id="newEndDate" name="newEndDate"/>
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
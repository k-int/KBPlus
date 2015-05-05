<g:if test="${info}">
<div id="info">
    <table id="financeErrors" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>Problem/Update</th>
            <th>Info</th>
        </tr>
        </thead>

        <tbody>
        <g:each in="${info}" var="i">
            <tr>
                <td>${i.status}</td>
                <td>${i.msg}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
    </div>
</g:if>

<button class="btn btn-primary pull-right" type="submit" onclick="confirmSubmit()" id="BatchSelectedBtn" value="remove">Remove Selected</button>
<button style="margin-right: 10px" class="btn btn-primary pull-right" type="submit" onclick="scrollToTop(3000,'createCost')" id="addNew">Add New Cost</button>
<h1>${institution.name} Cost Items</h1>
<g:form id="filterView" action="index" method="post" params="${[shortcode:params.shortcode]}">
    <input type="hidden" name="shortcode" value="${params.shortcode}"/>
    <table id="costTable" class="table table-striped table-bordered table-condensed table-tworow">
        <thead>
        <tr>
            <th rowspan="2" style="vertical-align: top;">Cost Item#</th>
            <th class="sortable" data-order="invoice.name">Invoice#<br/>
                <input autofocus="true" type="text" name="invoiceNumberFilter"
                       class="input-medium required-indicator" onKeyUp="filtersUpdated();"
                       id="filterInvoiceNumber" value="${params.invoiceNumberFilter}"/>
            </th>
            <th class="sortable" data-order="order.name">Order#<br/>
                <input type="text" name="orderNumberFilter"
                       class="input-medium required-indicator" onKeyUp="filtersUpdated();"
                       id="filterOrderNumber"  value="${params.orderNumberFilter}" data-type="select"/>
            </th>
            <th class="sortable" data-order="sub.name">Subscription<br/>
                <select name="subscriptionFilter" class="input-medium required-indicator" onChange="filterSubUpdated();"
                        id="filterSubscription" value="${params.subscriptionFilter}" data-type="select">
                    <option value="all">All</option>
                    <g:each in="${institutionSubscriptions}" var="s">
                        <option value="${s.id}" ${s.id==params.long('subscriptionFilter')?'selected="selected"':''}>${s.name}</option>
                    </g:each>
                </select>
            </th>
            <th class="sortable" data-order="subPkg.name">Package<br/>
                <select name="packageFilter" class="input-medium required-indicator sortable" onChange="filtersUpdated();" id="filterPackage" value="${params.packageFilter}">
                    <option value="all">All</option>
                </select>
            </th>
            <th style="vertical-align: top;">IE</th>
            <th rowspan="2" style="vertical-align: top; text-align: center;">Filter:
                <div id="filtering" class="btn-group" data-toggle="buttons-radio">
                    <g:if test="${filterMode=='OFF'}">
                        <g:select  onchange="filterSelection()" name="filterSelectionMode" from="['OFF','ON']" value="${params.filterMode}" type="button" class="btn btn-primary btn-mini"></g:select><br/><br/>
                    </g:if>
                <g:hiddenField type="hidden" name="resetMode" value="${params.resetMode}"></g:hiddenField>
                <g:submitToRemote onComplete="fadeAway('info',10000);filterSelection();" update="filterTemplate" value="${filterMode=='ON'?'reset':'search'}" class="btn-block" url="[controller:'finance', action:'index']" before="if(!filterValidation()) return false" id="submitFilterMode"></g:submitToRemote>
                </div>
            </th>
            <g:if test="${editable}">
                <th rowspan="2" colspan="1" style="vertical-align: top;">Delete</th>
            </g:if>
        </tr>
        <tr>
            <th>Date</th>
            <th>Amount [billing]/<br/>[local]</th>
            <th>Reference</th>
            <th colspan="2">Description</th>
        </tr>
        </thead>
        <tbody>
        <tr><td colspan="10">&nbsp;</td></tr>
        <g:if test="${cost_item_count==0}">
            <tr><td colspan="8" style="text-align:center">&nbsp;<br/><g:if test="${msg}">${msg}</g:if><g:else>No Cost Items Found</g:else><br/>&nbsp;</td></tr>
        </g:if>
        <g:else>
            <g:each in="${cost_items}" var="ci">
                <tr id="bulkdelete-a${ci.id}">
                    <td rowspan="2">${ci.id}</td>
                    <td>
                        <g:if test="${ci.invoice}">
                            <g:xEditable owner="${ci.invoice}" field="invoiceNumber"/>
                        </g:if>
                    </td>
                <td>
                    <g:if test="${ci.order}">
                        <g:xEditable owner="${ci?.order}" field="orderNumber"/></td>
                    </g:if>
                    <td>${ci.sub?.name}</td>
                    <td>${ci.subPkg?.name}</td>
                    <td colspan="2">${ci?.issueEntitlement?.tipp?.title?.title}</td>
                    <g:if test="${editable}">
                        <td rowspan="2"><input type="checkbox" value="${ci.id}" class="bulkcheck"/></td>
                    </g:if>
                </tr>
                <tr id="bulkdelete-b${ci.id}">
                    <td><g:xEditable owner="${ci}" type="date" field="datePaid" /></td>
                    <td><g:xEditable owner="${ci}" field="costInBillingCurrency" /> <g:xEditable owner="${ci}" field="billingCurrency" /> / <g:xEditable owner="${ci}" field="costInLocalCurrency" /></td>
                    <td><g:xEditable owner="${ci}" field="reference" /></td>
                    <td colspan="3"><g:xEditable owner="${ci}" field="costDescription" /></td>
                </tr>
            </g:each>
        </g:else>
        </tbody>
    </table>
</g:form>

<div class="pagination">
    <div id="paginateInfo" hidden="true" data-offset="${offset}" data-max="${max}" data-sort="${sort}" data-order="${order}"></div>
    <util:remotePaginate onFailure="alert('Sorry, problem occured')" on302="alert('Redirect')" on401="alert('User authentication required!')" offset='0' onError="alert('error occurred please refresh')" onComplete="scrollToTop(2000,'costTable');tester()" onSuccess="filterSelection()" params="${params+["filterMode": "${filterMode}"]}"  controller="finance" action="index" total="${cost_item_count}"  update="filterTemplate" max="20" pageSizes="[10, 20, 50, 100, 200]" alwaysShowPageSizes="true"/>
</div>
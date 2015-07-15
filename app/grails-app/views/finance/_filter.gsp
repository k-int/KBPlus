<g:if test="${info}">
    <div id="info" >
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

<button style="margin-left: 10px" class="btn btn-primary pull-right" type="submit" data-toggle="modal" title="Show recently updated costs in a modal window"  href="#recentDialog" id="showHideRecent">Show/Hide Recent Costs</button>
<button class="btn btn-primary pull-right" type="submit" onclick="performBulkDelete(this)" id="BatchSelectedBtn" title="Removed all checked delete values rows permanently" value="remove">Remove Selected</button>
<button style="margin-right: 10px" class="btn btn-primary pull-right" type="submit" title="Scrolls to create cost section" onclick="scrollToTop(3000,'createCost')" id="addNew">Add New Cost</button>
<h1>${institution.name} Cost Items</h1>
<g:form id="filterView" action="index" method="post" params="${[shortcode:params.shortcode]}">
    <input type="hidden" name="shortcode" value="${params.shortcode}"/>
    %{--<span id="hiddenPrompt" hidden=""><g:checkBox name="showEmpty" value="${params.showEmpty}"/></span>--}%
    <table id="costTable" class="table table-striped table-bordered table-condensed table-tworow">
        <thead>
        <tr>
            <th rowspan="2" style="vertical-align: top;"><a data-order="id"  class="sortable ${order=="Cost Item#"? "sorted ${sort}":''}">Cost Item#</a></th>
            <th><a class="sortable ${order=="invoice#"? "sorted ${sort}":''}"  data-order="invoice#">Invoice#</a> <br/>
                <input autofocus="true" type="text" name="invoiceNumberFilter"
                       class="input-medium required-indicator" onKeyUp="filtersUpdated();"
                       id="filterInvoiceNumber" value="${params.invoiceNumberFilter}"/>
            </th>
            <th><a class="sortable ${order=="order#"? "sorted":''}"  data-order="order#">Order#</a><br/>
                <input type="text" name="orderNumberFilter"
                       class="input-medium required-indicator" onKeyUp="filtersUpdated();"
                       id="filterOrderNumber"  value="${params.orderNumberFilter}" data-type="select"/>
            </th>
            <th><a data-order="Subscription"  class="sortable ${order=="Subscription"? "sorted ${sort}":''}">Subscription</a><br/>
                <select name="subscriptionFilter" class="input-medium required-indicator" onChange="filterSubUpdated();"
                        id="filterSubscription" value="${params.subscriptionFilter}" data-type="select">
                    <option value="all">All</option>
                    <g:each in="${institutionSubscriptions}" var="s">
                        <option value="${s.id}" ${s.id==params.long('subscriptionFilter')?'selected="selected"':''}>${s.name}</option>
                    </g:each>
                </select>
            </th>
            <th><a data-order="Package"  class="sortable ${order=="Package"? "sorted ${sort}":''}">Package</a> <br/>
                <select name="packageFilter" class="input-medium required-indicator" onChange="filtersUpdated();" id="filterPackage" value="${params.packageFilter}">
                    <option value="all">All</option>
                </select>
            </th>
            <th style="vertical-align: top;">IE</th>
            <th rowspan="2" style="vertical-align: top; text-align: center; width: 5%">Filter:
                <div id="filtering" class="btn-group" data-toggle="buttons-radio">
                    <g:if test="${filterMode=='OFF'}">
                        <g:select  onchange="filterSelection()" name="filterMode" from="['OFF','ON']" type="button" class="btn btn-primary btn-mini"></g:select><br/><br/>
                    </g:if>
                <g:hiddenField type="hidden" name="resetMode" value="${params.resetMode}"></g:hiddenField>
                %{--${response.status} ${request.properties}--}%
                <g:submitToRemote onFailure="errorHandling(textStatus,'Filtering',errorThrown)" onComplete="filterSelection();deleteSelectAll();sortAndOrder();fadeAway('info',15000);" update="filterTemplate" title="Selecting search will filter results based on input i.e. invoice number" value="${filterMode=='ON'?'reset':'search'}" class="btn-block" url="[controller:'finance', action:'index']" before="if(!filterValidation()) return false" id="submitFilterMode"></g:submitToRemote>
                </div>
            </th>
            <g:if test="${editable}">
                <th rowspan="2" colspan="1" style="vertical-align: top;">Delete <br/><br/><input id="selectAll" type="checkbox" value=""/></th>
            </g:if>
        </tr>
        <tr>
            <th><a style="color: #990100;" data-order="datePaid" class="sortable ${order=="date"? "sorted ${sort}":''}">Date</a></th>
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
                        <g:xEditable owner="${ci?.order}" field="orderNumber"/>
                    </g:if>
                 </td>
                 <td>
                      <g:if test="${ci.sub}">
                        <g:link controller="subscriptionDetails" action="index" id="${ci.sub.id}">
                          <g:if test="${ci.sub.name}">${ci.sub.name}</g:if><g:else>-- Name Not Set  --</g:else>
                          <g:if test="${ci.sub.consortia}">( ${ci.sub.consortia?.name} )</g:if>
                        </g:link>
                      </g:if>
                 </td>

                    <td>${ci.subPkg?.name}</td>
                    <td colspan="2">${ci?.issueEntitlement?.tipp?.title?.title}</td>
                    <g:if test="${editable}">
                        <td rowspan="2"><input type="checkbox" value="${ci.id}" class="bulkcheck"/></td>
                    </g:if>
                </tr>
                <tr id="bulkdelete-b${ci.id}">
                    <td>
                      paid:<g:xEditable owner="${ci}" type="date" field="datePaid" />
                    </td>
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
    <div id="paginateInfo" hidden="true" data-offset="${offset!=null?offset:params.offset}" data-max="${max!=null?max:params.max}" data-sort="${sort!=null?sort:params.sort}" data-order="${order!=null?order:params.order}" data-relation="${isRelation!=null?isRelation:params.orderRelation}" data-filterMode="${filterMode}" data-total="${cost_item_count}" data-resetMode="${params.resetMode}" data-subscriptionFilter="${params.subscriptionFilter}" data-invoiceNumberFilter="${params.invoiceNumberFilter}" data-orderNumberFilter="${params.orderNumberFilter}" data-packageFilter="${params.packageFilter}"></div>
    <util:remotePaginate title="Select to change page of results or select the drop-down to change number of results shown" onFailure="errorHandling(textStatus,'Pagination',errorThrown)" on401="alert('User authentication required!')" offset='0' onComplete="scrollToTop(2000,'costTable');tester();deleteSelectAll();sortAndOrder();" onSuccess="filterSelection()" params="${params+["filterMode": "${filterMode}", "sort":"${sort}", "order":"${order}"]}"  controller="finance" action="index" total="${cost_item_count}"  update="filterTemplate" max="20" pageSizes="[10, 20, 50, 100, 200]" alwaysShowPageSizes="true"/>
</div>

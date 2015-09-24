%{--AJAX rendered messages--}%
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
                    <td>${i.status.encodeAsHTML()}</td>
                    <td>${i.msg.encodeAsHTML()}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</g:if>

%{--Basic static help text--}%
<g:render template="help" />

<button style="margin-left: 10px" class="btn btn-primary pull-right" type="submit" data-toggle="modal" title="${g.message(code: 'financials.recent.title')}"  href="#recentDialog" id="showHideRecent">Recent Costs</button>
<button class="btn btn-primary pull-right" type="submit" id="BatchSelectedBtn" title="${g.message(code: 'financials.filtersearch.deleteAll')}" value="remove">Remove Selected</button>
<button style="margin-right: 10px" class="btn btn-primary pull-right" type="submit" title="${g.message(code: 'financials.addNew.title')}" data-offset="#createCost" id="addNew">Add New Cost</button>
<h1>${institution.name} Cost Items</h1>
<g:form id="filterView" action="index" method="post" params="${[shortcode:params.shortcode]}">
    <input type="hidden" name="shortcode" value="${params.shortcode}"/>
        <table id="costTable" class="table table-striped table-bordered table-condensed table-tworow">
            <thead>
                <tr>
                    <th rowspan="2" style="vertical-align: top;"><a data-order="id"  class="sortable ${order=="Cost Item#"? "sorted ${sort}":''}">Cost Item#</a></th>
                    <th><a class="sortable ${order=="invoice#"? "sorted ${sort}":''}"  data-order="invoice#">Invoice#</a> <br/>
                        <input autofocus="true" type="text" name="invoiceNumberFilter"
                               class="input-medium required-indicator filterUpdated"
                               id="filterInvoiceNumber" value="${params.invoiceNumberFilter}"/>
                    </th>
                    <th><a class="sortable ${order=="order#"? "sorted":''}"  data-order="order#">Order#</a><br/>
                        <input type="text" name="orderNumberFilter"
                               class="input-medium required-indicator filterUpdated"
                               id="filterOrderNumber"  value="${params.orderNumberFilter}" data-type="select"/>
                    </th>
                    <th><a data-order="Subscription"  class="sortable ${order=="Subscription"? "sorted ${sort}":''}">Subscription</a><br/>

                        <input class="input-medium required-indicator" style="width:250px;" name="subscriptionFilter" id="subscriptionFilter" value="${params.subscriptionFilter}"/> <br/>
                    </th>
                    <th> <a data-order="Package"  class="sortable ${order=="Package"? "sorted ${sort}":''}">Package</a> <br/>
                        %{--<select name="packageFilter" class="input-medium required-indicator filterUpdated"  id="filterPackage" value="${params.packageFilter}">--}%
                            %{--<option value="xx">Not specified</option>--}%
                        %{--</select>--}%
                        <input class="input-medium required-indicator filterUpdated" style="width:250px;" name="packageFilter" id="packageFilter" value="${params.packageFilter}"/> <br/>
                    </th>
                    <th style="vertical-align: top;">IE</th>
                    <th rowspan="2" style="vertical-align: top; text-align: center; width: 5%">Filter
                        <span ${wildcard && filterMode=='ON'? hidden="hidden" : ''}>
                            (${g.message(code: 'financials.help.wildcard')} : <g:checkBox name="wildcard" title="${g.message(code: 'financials.wildcard.title')}" type="checkbox" value="${wildcard}"></g:checkBox> )
                        </span><br/>
                        <div id="filtering" class="btn-group" data-toggle="buttons-radio">
                            <g:if test="${filterMode=='OFF'}">
                                <g:select name="filterMode" from="['OFF','ON']" type="button" class="btn btn-primary btn-mini"></g:select><br/><br/>
                            </g:if>
                            <g:hiddenField type="hidden" name="resetMode" value="${params.resetMode}"></g:hiddenField>
                            <g:submitButton name="submitFilterMode" id="submitFilterMode" class="btn-block"  value="${filterMode=='ON'?'reset':'search'}" title="${g.message(code: 'financials.pagination.title')}"></g:submitButton>

                        </div>
                        <a ${filterMode=="ON"?"hidden='hidden'":''} id="advancedFilter" data-toggle="#">More filter options</a>
                    </th>

                %{--If has editable rights, allow delete column to be shown--}%
                    <g:if test="${editable}">
                        <th rowspan="2" colspan="1" style="vertical-align: top;">Delete
                            <br/><br/> <input title="${g.message(code: 'financials.deleteall.title')}" id="selectAll" type="checkbox" value=""/>
                        </th>
                    </g:if>
                </tr>
                %{--End of table row one of headers--}%

                <tr>
                    <th>
                        <a style="color: #990100;" data-order="datePaid" class="sortable ${order=="datePaid"? "sorted ${sort}":''}">Date Paid</a><br/>
                    </th>
                    <th>Amount [billing]/<br/>[local]</th>
                    <th>
                        Reference &nbsp;/&nbsp; Codes </br></br>
                        <a style="color: #990100;" data-order="startDate" class="sortable ${order=="startDate"? "sorted ${sort}":''}">Start Period</a> &nbsp;<i>to</i>&nbsp;
                        <a style="color: #990100;" data-order="endDate" class="sortable ${order=="endDate"? "sorted ${sort}":''}">End Period</a>
                    </th>
                    <th colspan="2">Description</th>
                </tr>
            %{--End of table row two of headers--}%
            </thead>
            <tbody>

            %{--Advanced filtering row--}%
            %{--<tr id="advancedFilterOpt" hidden="hidden">--}%
            %{--<table width="100%">--}%
            %{--<thead>--}%
            %{--<tr>--}%
            %{--<th>Code(s)</th>--}%
            %{--<th>Status</th>--}%
            %{--<th>Category</th>--}%
            %{--<th>Reference</th>--}%
            %{--<th>Charged Period (dates)</th>--}%
            %{--</tr>--}%
            %{--</thead>--}%
            %{--<tbody>--}%
            %{--<tr>--}%
            %{--<td>Code(s)</td>--}%
            %{--<td>Status</td>--}%
            %{--<td>Category</td>--}%
            %{--<td>Reference</td>--}%
            %{--<td>Charged Period (dates)</td>--}%
            %{--</tr>--}%
            %{--</tbody>--}%
            %{--</table>--}%
            %{--</tr>--}%

            %{--blank row--}%
            <tr><td colspan="10">&nbsp;</td></tr>

            %{--Empty result set--}%
            <g:if test="${cost_item_count==0}">
                <tr><td colspan="8" style="text-align:center">&nbsp;<br/><g:if test="${msg}">${msg}</g:if><g:else>No Cost Items Found</g:else><br/>&nbsp;</td></tr>
            </g:if>
            <g:else>
            %{--Two rows of data per CostItem, separated for readability--}%
                <g:render template="filter_data" model="[editable: editable, cost_items: cost_items]"></g:render>
            </g:else>
            </tbody>
        </table>
</g:form>

%{--todo refactor non-plugin usage, separate the JS concerns or convert to taglib--}%
<div id="paginationWrapper" class="pagination">
    <div id="paginateInfo" hidden="true" data-offset="${offset!=null?offset:params.offset}" data-max="${max!=null?max:params.max}" data-wildcard="${wildcard!=null?wildcard:params.wildcard}"
         data-sort="${sort!=null?sort:params.sort}" data-order="${order!=null?order:params.order}" data-relation="${isRelation!=null?isRelation:params.orderRelation}"
         data-filterMode="${filterMode}" data-total="${cost_item_count}" data-resetMode="${params.resetMode}" data-subscriptionFilter="${params.subscriptionFilter}"
         data-invoiceNumberFilter="${params.invoiceNumberFilter}" data-orderNumberFilter="${params.orderNumberFilter}" data-packageFilter="${params.packageFilter}">
    </div>
    <util:remotePaginate title="${g.message(code: 'financials.pagination.title')}" onFailure="Finance.errorHandling(textStatus,'Pagination',errorThrown)" offset='0' onComplete="Finance.scrollTo(null,'#costTable');tester();" onSuccess="filterSelection()" params="${params+["filterMode": "${filterMode}", "sort":"${sort}", "order":"${order}"]}"  controller="finance" action="index" total="${cost_item_count}"  update="filterTemplate" max="20" pageSizes="[10, 20, 50, 100, 200]" alwaysShowPageSizes="true"/>
</div>
%{--<%@page defaultCodec="HTML" %>--}%
<table  class="table table-striped table-bordered " id="recentUpdatesTable" data-resultsTo="${to}" data-resultsFrom="${from}">
    <thead>
    <tr>
        <td style="text-align: center" colspan="4">
                <p>Quickly see costing items that have been recently added/updated</p>
                <p>From: ${from}  -> To: ${to}</p>
        </td>
    </tr>
    <tr>
        <th>Cost#</th>
        <th>Invoice#</th>
        <th>Order#</th>
        <th>Subscription</th>
    </tr>
    <tr>
        <th>Package</th>
        <th>IE</th>
        <th>Amount [billing]/<br/>[local]</th>
        <th>Updated</th>
    </tr>
    </thead>
    <tbody>
        <g:if test="${recentlyUpdated}">
            <g:each in="${recentlyUpdated}" var="item">
                <tr>
                    <td>${item?.id}</td>
                    <td>${item?.invoice?.invoiceNumber.encodeAsHTML()}</td>
                    <td>${item?.order?.orderNumber.encodeAsHTML()}</td>
                    <td>${item?.sub?.name.encodeAsHTML()}</td>
                </tr>
                <tr>
                    <td>${item?.subPkg?.pkg?.name.encodeAsHTML()}</td>
                    <td>${item?.issueEntitlement?.tipp?.title?.title.encodeAsHTML()}</td>
                    <td>${item?.costInBillingCurrency.encodeAsHTML()}</td>
                    <td><g:formatDate format="dd-MM-yy" date="${item?.lastUpdated}"/></td>
                </tr>
            </g:each>
        </g:if>
        <g:else>
            <tr>
                <td colspan="2">No recent cost items...</td>
                <td colspan="2">Table automatically updates</td>
            </tr>
        </g:else>
    </tbody>
</table>
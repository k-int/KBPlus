<g:if test="${recentlyUpdated}" >
<table style="width: 10%; margin-left: 10px;" id="recentUpdatesTable" data-resultsTo="${to}" data-resultsFrom="${from}">
    <caption>
        <p>Quickly see costing items that have been recently added/updated</p>
    </caption>
    <thead>
    <tr><td colspan="4">&nbsp;</td></tr>
    <tr>
        <th>Invoice</th>
        <th>Order</th>
        <th>Amount</th>
        <th>Reference</th>
    </tr>
    </thead>

    <tbody>

    <g:if test="${recentlyUpdated}">
        <g:each in="${recentlyUpdated}" var="item">
            <tr>
                <td>${item.invoice?.invoiceNumber}</td>
                <td>${item.order?.orderNumber}</td>
                <td>${item.costInBillingCurrency}</td>
                <td>${item.reference}</td>
            </tr>
        </g:each>
    </g:if>
    <g:else>
        <tr>
            <td colspan="4">No recent cost items...</td>
        </tr>
    </g:else>
    </tbody>
</table>
</g:if>
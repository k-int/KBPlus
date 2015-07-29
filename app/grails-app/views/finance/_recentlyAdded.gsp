<table>
    <caption>
        <p>Quickly access and see costing items that have been recently added</p>
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

    <g:if test="${costItems}">
        <g:each in="${costItems}" var="item">
            <tr>
                <td>${item.invoice}</td>
                <td>${item.order}</td>
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
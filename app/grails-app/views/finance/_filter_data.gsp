%{--Two rows of data per CostItem--}%
<g:each in="${cost_items}" var="ci">
    <tr id="bulkdelete-a${ci.id}">
        <td rowspan="2">${ci.id}</td>
        <td>
            <g:if test="${editable}">
                <g:simpleReferenceTypedown modified="${true}" style="width: 100%" class="finance-select2" data-shortcode="${params.shortcode}"
                                           data-owner="${ci.class.name}" baseClass="com.k_int.kbplus.Invoice"
                                           data-relationID="${ci?.invoice!=null? ci.invoice.id:'create'}"
                                           data-placeholder="${ci?.invoice==null? 'Enter invoice number':''}"
                                           data-defaultValue="${ci?.invoice?.invoiceNumber?.encodeAsHTML()}" data-ownerid="${ci.id}"
                                           data-ownerfield="invoice" name="invoiceField" data-relationField="invoiceNumber"/>
            </g:if>
            <g:else>
                ${ci?.invoice?.invoiceNumber?.encodeAsHTML()}
            </g:else>
        </td>
        <td>
            <g:if test="${editable}">
                <g:simpleReferenceTypedown modified="${true}" style="width: 100%" class="finance-select2" data-shortcode="${params.shortcode}"
                                           data-owner="${ci.class.name}" baseClass="com.k_int.kbplus.Order"
                                           data-relationID="${ci?.order!=null? ci.order.id:'create'}"
                                           data-placeholder="${ci?.order==null? 'Enter order number':''}"
                                           data-defaultValue="${ci?.order?.orderNumber}" data-ownerid="${ci.id}"
                                           data-ownerfield="order" name="orderField" data-relationField="orderNumber"/>
            </g:if>
            <g:else>
                ${ci?.order?.orderNumber?.encodeAsHTML()}
            </g:else>
        </td>
        <td data-pk="${ci.sub?.id}">${ci.sub?.name?.encodeAsHTML()}</td>
        <td data-pk="${ci.subPkg?.pkg?.id}">${ci.subPkg?.pkg?.name?.encodeAsHTML()}</td>
        <td data-pk="${ci?.issueEntitlement?.tipp?.title?.id}" colspan="2">${ci?.issueEntitlement?.tipp?.title?.title?.encodeAsHTML()}</td>
        <g:if test="${editable}">
            <td rowspan="2"> <input type="checkbox" value="${ci.id}" class="bulkcheck"/> </td>
        </g:if>
    </tr>
    <tr id="bulkdelete-b${ci.id}">
        <td>
            <g:if test="${editable}">
                <g:xEditable emptytext="Edit Paid-Date" owner="${ci}" type="date" field="datePaid" /> </br>
            </g:if>
            <g:else>
                <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ci?.datePaid}"/>
            </g:else>
        </td>
        <td>
            <g:if test="${editable}">
                <g:xEditable emptytext="Edit Cost" owner="${ci}" field="costInBillingCurrency" />
                <g:xEditableRefData config="Currency" emptytext="Edit billed" owner="${ci}" field="billingCurrency" /> /
                <g:xEditable emptytext="Edit local" owner="${ci}" field="costInLocalCurrency" />
            </g:if>
            <g:else>
                ${ci?.costInBillingCurrency}
                ${ci?.billingCurrency}
                ${ci?.costInLocalCurrency}
            </g:else>
        </td>
        <td>
            <g:if test="${editable}">
                <g:xEditable emptytext="Edit Ref" owner="${ci}" field="reference" /> &nbsp;/&nbsp;
                <div style="display: inline-block" class="budgetCodeWrapper">
                    <div id="budgetcodes_${ci.id}">
                        <g:each in="${ci.budgetcodes}" var="bc">
                            <span class="budgetCode">${bc.value.encodeAsHTML()} </span>
                            <a style="display: inline-block" id="bcci_${bc.id}_${ci.id}" class="badge budgetCode">x</a>
                        </g:each>
                    </div>
                    <a class="editable-empty budgetCode" data-owner="${ci.id}">Add Codes...</a>
                </div> </br></br>
                <g:xEditable emptytext="Edit Start-Date" owner="${ci}" type="date" field="startDate" /> &nbsp;<i>to</i>&nbsp;
                <g:xEditable emptytext="Edit End-Date" owner="${ci}" type="date" field="endDate" />
            </g:if>
            <g:else>
                ${ci?.reference} &nbsp;/&nbsp;
                <g:each in="${ci.budgetcodes}" var="bc" status="${i}">${(i+1)}. ${bc.value.encodeAsHTML()}&nbsp;</g:each> </br>
                <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ci?.startDate}"/> </br>
                <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ci?.endDate}"/>
            </g:else>
        </td>
        <td colspan="3">
            <g:if test="${editable}">
                <g:xEditable emptytext="Edit Description" owner="${ci}" field="costDescription" />
            </g:if>
            <g:else>
                ${ci?.costDescription}
            </g:else>
        </td>
    </tr>
</g:each>
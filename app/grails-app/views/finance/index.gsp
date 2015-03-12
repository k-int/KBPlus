<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: Financial Information</title>
  </head>

  <body>

    <div class="container-fluid">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="finance" params="${[shortcode:params.shortcode]}">${institution.name} Finance</g:link> </li>
      </ul>
    </div>

    <div class="container-fluid">
      <h1>${institution.name} Cost Items</h1>
      <g:form action="index" method="post" params="${[shortcode:params.shortcode]}">
        <input type="hidden" name="shortcode" value="${params.shortcode}"/>
        <table class="table table-striped table-bordered table-condensed table-tworow">
          <thead>
            <tr>
              <th rowspan="2" style="vertical-align: top;">Cost Item#</th>
              <th>Invoice#<br/>
                <input type="text" name="invoiceNumberFilter" 
                       class="input-medium" onKeyUp="filtersUpdated();" 
                       id="filterInvoiceNumber" value="${params.invoiceNumberFilter}"/>
              </th>
              <th>Order#<br/>
                <input type="text" name="orderNumberFilter" 
                       class="input-medium" onKeyUp="filtersUpdated();" 
                       id="filterOrderNumber"  value="${params.invoiceNumberFilter}"/>
              </th>
              <th>Subscription<br/>
                <select name="subscriptionFilter" class="input-medium" onChange="filterSubUpdated();" 
                        id="filterSubscription" value="${params.subscriptionFilter}">
                  <option value="all">All</option>
                  <g:each in="${institutionSubscriptions}" var="s">
                    <option value="${s.id}" ${s.id==params.long('subscriptionFilter')?'selected="selected"':''}>${s.name}</option>
                  </g:each>
                </select>
              </th>
              <th>Package<br/>
                <select name="packageFilter" class="input-medium" onChange="filtersUpdated();" id="filterPackage" value="${params.packageFilter}">
                  <option value="all">All</option>
                </select>
              </th>
              <th style="vertical-align: top;">IE</th>
              <th rowspan="2" style="vertical-align: top;"><button type="submit" name="Filter" value="filter">Filter</button></th>
            </tr>
            <tr>
              <th>Date</th>
              <th>Amount [billing]/<br/>[local]</th>
              <th>Reference</th>
              <th colspan="2">Description</th>
            </tr>
          </thead>
          <tbody>
            <tr><td colspan="9">&nbsp;</td></tr>
            <g:if test="${cost_item_count==0}">
              <tr><td colspan="7" style="text-align:center">&nbsp;<br/>No Cost Items Found<br/>&nbsp;</td></tr>
            </g:if>
            <g:else>
              <g:each in="${cost_items}" var="ci">
                <tr>
                  <td rowspan="2">${ci.id}</td>
                  <td>${ci.invoice?.invoiceNumber}</td>
                  <td>${ci.order?.orderNumber}</td>
                  <td>${ci.sub?.name}</td>
                  <td>${ci.subPkg?.name}</td>
                  <td colspan="2">${ci.issueEntitlement?.id}</td>
                </tr>
                <tr>
                  <td>${ci.datePaid}</td>
                  <td>${ci.costInBillingCurrency} ${ci.billingCurrency?.value} / ${ci.costInLocalCurrency}</td>
                  <td>${ci.reference}</td>
                  <td colspan="3">${ci.costDescription}</td>
                </tr>
              </g:each>
            </g:else>
          </tbody>
          <tfoot>
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
              <td rowspan="2"><button type="submit" name="Add" value="add">Add</button></td>
            </tr>
            <tr>
              <td>
                <h3>Cost date and status</h3>
                <input type="date" name="newDate" value="${params.newDate}"/><br/>

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
                <input type="number" name="newCostTaxRate" placeholder="New Cost Tax Rate" id="newCostInLocalCurrency" step="0.01"/>
                <input type="number" name="newCostTaxAmount" placeholder="New Cost Tax Amount" id="newCostInLocalCurrency" step="0.01"/>
              </td>
              <td>
                <h3>Reference</h3>
                <input type="text" name="newReference" placeholder="New Item Reference" id="newCostItemReference" value="${params.newReference}"/><br/>
                <input type="text" name="newBudgetCode" placeholder="New Item Budget Code" id="newBudgetCode" ></td>
              <td colspan="2">
                <h3>Description</h3>
                <textarea name="newDescription" 
                                     placeholder="New Item Description" id="newCostItemDescription"/></textarea>
            </tr>
  
          </tfoot>
        </table>
      </g:form>

    </div>
  </body>

  <r:script type="text/javascript">

    function filtersUpdated() {
      $('#newInvoiceNumber').val($('#filterInvoiceNumber').val());
      $('#newOrderNumber').val($('#filterOrderNumber').val());
      $('#newSubscription').val($('#filterSubscription').val());
      $('#newPackage').val($('#filterPackage').val());
    }

    function filterSubUpdated() {
      // Fetch packages for the selected subscription
      var selectedSub = $('#filterSubscription').val();

      $.ajax({
        url: "<g:createLink controller='ajax' action='lookup'/>",
        data: {
          format:'json',
          subFilter:selectedSub,
          baseClass:'com.k_int.kbplus.SubscriptionPackage'
        },
        dataType:'json'
      }).done(function(data) {
        console.log("%o",data);
        $('#filterPackage').children().remove()
        $('#filterPackage').append('<option value="xx">Not specified</option>');
        var numValues = data.values.length;
        for (var i = 0; i != numValues; i++) {
          $('#filterPackage').append('<option value="'+data.values[i].id+'">'+data.values[i].text+'</option>');
        }
      });


      filtersUpdated();
    }

  $(document).ready(function() {

    $("#newIE").select2({
      placeholder: "Identifier..",
      minimumInputLength: 1,
      ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
        url: "<g:createLink controller='ajax' action='lookup'/>",
        dataType: 'json',
        data: function (term, page) {
            return {
                format:'json',
                q: term,
                subFilter: $('#newSubscription').val(),
                baseClass:'com.k_int.kbplus.IssueEntitlement'
            };
        },
        results: function (data, page) {
          return {results: data.values};
        }
      }
    });

  });



  </r:script>
</html>

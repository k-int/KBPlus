<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: Financial Information</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="finance" params="${[shortcode:params.shortcode]}">${institution.name} Finance</g:link> </li>
      </ul>
    </div>

    <div class="container">
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
              <th>IE<br/>
                <select name="ieFilter" class="input-medium" onChange="filtersUpdated();" id="filterIE" value="${params.packageFilter}">
                  <option value="all">All</option>
                </select>
              </th>
              <th rowspan="2" style="vertical-align: top;"><button type="submit" name="Filter">Filter</button></th>
            </tr>
            <tr>
              <th>Date</th>
              <th>Amount</th>
              <th>Reference</th>
              <th colspan="2">Description</th>
            </tr>
          </thead>
          <tbody>
            <g:if test="${1==1}">
              <tr><td colspan="7" style="text-align:center">&nbsp;<br/>No Cost Items Found<br/>&nbsp;</td></tr>
            </g:if>
            <g:else>
              <tr>
                <td rowspan="2"></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
              </tr>
              <tr>
                <td></td>
                <td></td>
                <td></td>
                <td colspan="2"></td>
              </tr>
            </g:else>
          </tbody>
          <tfoot>
            <tr>
              <td rowspan="2" style="vertical-align: top;">Add new <br/>cost item</td>
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
                <select name="newIe" class="input-medium" id="newIE" value="${params.newIe}">
                  <option value="">Not Set</option>
                </select>
              </td>
              <td rowspan="2"><button type="submit" name="Add">Add</button></td>
            </tr>
            <tr>
              <td><input type="date" name="newDate" value="${params.newDate}"/></td>
              <td><input type="number" name="newValue" placeholder="New Cost" id="newCostItemValue" /></td>
              <td><input type="text" name="newReference" placeholder="New Item Reference" id="newCostItemReference" value="${params.newReference}"/></td>
              <td colspan="2"><input type="text" name="newDescription" 
                                     placeholder="New Item Description" id="newCostItemDescription"/></td>
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
      $('#newIE').val($('#filterIE').val());
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

  </r:script>
</html>

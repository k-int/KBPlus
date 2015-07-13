<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: Financial Information</title>
  </head>
  <body>
  <g:set var="to1" value="${new Date()}" scope="page" />

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
                        <td>
                          <g:if test="${ci.sub}">
                            <g:link controller="subscriptionDetails" action="index" id="${ci.sub.id}">
                              <g:if test="${ci.sub.name}">${ci.sub.name}</g:if><g:else>-- Name Not Set  --</g:else>
                              <g:if test="${ci.sub.consortia}">( ${ci.sub.consortia?.name} )</g:if>
                            </g:link>
                          </g:if>
                        </td>
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

          </tfoot>
        </table>
      </g:form>

    </div>


    <div id="test">
        ...
    </div>

  </body>

  <r:script type="text/javascript">


    function recentCostItems() {
        $.ajax({
        url: "<g:createLink controller='finance' params='["to":to1, "shortcode": params.shortcode]' action='newCostItemsPresent'/>",
        data: {
          format:'json'
        },
        dataType:'json'
      }).done(function(data) {
        console.log("%o",data);
            if(data > 0)
            {
                console.log("Performing update for cost items...",data," new items");
                <g:set var="to1" value="${new Date()}" />
                performCostItemUpdate();
            }
        });
    }

    function performCostItemUpdate() {
        $.ajax({
        url: "<g:createLink controller='finance' params='["from":from, "to":to1, "shortcode": params.shortcode]' action='getRecentCostItems'/>",
        data: {
          format:'json'
        },
        dataType:'json'
      }).done(function(data) {
        alert(data)
        console.log("%o",data);
        $('#test').html(data);
        $('#test').show();
      });
    }

    setInterval(recentCostItems, 10000);

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


    //first run
    performCostItemUpdate();
  });

  </r:script>
</html>

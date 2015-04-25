<!doctype html>
<html xmlns="http://www.w3.org/1999/html">
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: Financial Information</title>
  </head>
  <body>

    <div class="container-fluid">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="finance" params="${[shortcode:params.shortcode]}">${institution.name} Finance</g:link> </li>
          <g:if test="${editable}">
              <li class="pull-right"><span class="badge badge-warning">Editable</span>&nbsp;</li>
          </g:if>
      </ul>
    </div>

  <div class="container">
      <button class="btn btn-primary pull-right" type="submit" onclick="confirmSubmit()" id="BatchSelectedBtn" value="remove">Remove Selected</button>
      <button style="margin-right: 10px" class="btn btn-primary pull-right" type="submit" id="addNew" value="remove">Add New</button>
      <h1>${institution.name} Cost Items</h1>
      <g:form id="filterView" action="index" method="post" params="${[shortcode:params.shortcode]}">
        <input type="hidden" name="shortcode" value="${params.shortcode}"/>
        <table id="costTable" class="table table-striped table-bordered table-condensed table-tworow">
          <thead>
            <tr>
              <th rowspan="2" style="vertical-align: top;">Cost Item#</th>
              <th>Invoice#<br/>
                <input type="text" name="invoiceNumberFilter"
                       class="input-medium required-indicator" onKeyUp="filtersUpdated();"
                       id="filterInvoiceNumber" value="${params.invoiceNumberFilter}"/>
              </th>
              <th>Order#<br/>
                <input type="text" name="orderNumberFilter"
                       class="input-medium required-indicator" onKeyUp="filtersUpdated();"
                       id="filterOrderNumber"  value="${params.invoiceNumberFilter}" data-type="select"/>
              </th>
              <th>Subscription<br/>
                <select name="subscriptionFilter" class="input-medium required-indicator" onChange="filterSubUpdated();"
                        id="filterSubscription" value="${params.subscriptionFilter}" data-type="select">
                  <option value="all">All</option>
                  <g:each in="${institutionSubscriptions}" var="s">
                    <option value="${s.id}" ${s.id==params.long('subscriptionFilter')?'selected="selected"':''}>${s.name}</option>
                  </g:each>
                </select>
              </th>
              <th>Package<br/>
                <select name="packageFilter" class="input-medium required-indicator" onChange="filtersUpdated();" id="filterPackage" value="${params.packageFilter}">
                  <option value="all">All</option>
                </select>
              </th>
              <th style="vertical-align: top;">IE</th>
              <th rowspan="2" style="vertical-align: top; text-align: center;">Filter:
                  <input type="hidden" id="filterMode" value="${filterMode}">
                <div id="filtering" class="btn-group" data-toggle="buttons-radio">
                    <button id="off"  data-filter="OFF" type="button" class="btn btn-primary btn-mini ${((filterMode=='OFF')||(filterMode==null))?'active':''}">OFF</button>
                    <button id="on"   data-filter="ON"  type="button" class="btn btn-primary btn-mini ${filterMode=='ON'?'active':''}">ON</button>
                </div>
                  </br></br><a disabled="disabled" id="filterSearch" class="btn btn-primary" onclick="return filterSearch(this)" >Filter ?</a>
                  %{--</br></br><button type="submit"  id="filterSearch" class="btn btn-primary" onclick="return filterSearch()" >Filter ?</button>--}%
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

      <div class="pagination" style="text-align:center">
          <g:if test="${cost_items}" >
            <span onclick="console.log($(this).children())"><g:paginate controller="finance" action="index" params="${params}" next="Next" prev="Prev" total="${cost_item_count}" /></span>
          </g:if>
      </div>


      <br/><br/><br/>

      <form id="createCost">
          <table id="newCosts" class="table table-striped table-bordered table-condensed table-tworow">
              <thead>
              <tr>
                  <th rowspan="2" style="vertical-align: top;">Cost Item#</th>
                  <th>Invoice#</th>
                  <th>Order#</th>
                  <th>Subscription</th>
                  <th>Package</th>
                  <th colspan="2" style="vertical-align: top;">Issue Entitlement</th>
              </tr>
              <tr>
                  <th>Date</th>
                  <th>Amount [billing]/<br/>[local]</th>
                  <th>Reference</th>
                  <th colspan="3">Description</th>
              </tr>
              </thead>

              <tbody>
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
                  <td rowspan="2">
                      <button type="submit" name="Add" value="add">Add</button></br></br>
                      <button type="reset">Reset</reset>
                  </td>
              </tr>
              <tr>
                  <td>
                      <h3>Cost date and status</h3>
                      <input class="datepicker-class" type="date" placeholder="Date Paid" name="newDate" value="${params.newDate}"/><br/>

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
               <g:hiddenField name="shortcode" value="${params.shortcode}"></g:hiddenField>
              </tbody>
          </table>
      </form>
      <button class="btn btn-primary pull-right" id="top">Back to top</button>
  </div>

    </body>

  <r:script type="text/javascript">

    function confirmSubmit() {
          var agree=confirm("Are you sure you wish to continue?\n\nActions are permanent for selected Cost Item(s)");
          if (agree)
            performBulkDelete();
          else
            return false ;
      }

    function performBulkDelete() {
        var allVals = [];
        $('.bulkcheck:checked').each(function(){
            allVals.push($(this).val());
         });

        $.ajax({
        method: "POST",
        url: "<g:createLink  controller='finance' action='delete'/>",
        data: {
          format:'json',
          del:JSON.stringify(allVals),
          shortcode:'${params.shortcode}'
        },
        dataType:'json'
      }).done(function(data) {
        console.log("%o",data);
        alert(data.message); //list of succesfully deleted ids
        $.each(data.successful, function( i, val ) {
            $("#bulkdelete-a" + val).remove();
            $("#bulkdelete-b" + val).remove();
        });
      });

    };

    $('form#createCost').submit(function(e) {
     e.preventDefault();
      console.log(e);
      var paramsData = $('#createCost').serialize()+"&format=json";

      $.ajax({
        method: "POST",
        url: "<g:createLink  controller='finance' action='newCostItem'/>",
        data: paramsData,
        dataType:'json'
      })
      .done(function(data) {
        console.log("%o",data);
        $('form#createCost').trigger("reset");
      })
    });


    $('#filtering > button').click(function(e) {
        e.preventDefault();
        var mode = $(this).data('filter');
        $('#filterMode').val(mode);
        var state = (mode == "ON")? false:true;
        $('#filterSearch').attr("disabled",state);


       %{--if(mode=="ON")--}%
       %{--{--}%
            %{--var counter       = 0;--}%
            %{--var selectCounter = 0;--}%
            %{--$(".required-indicator").each(function() {--}%
              %{--if($(this).is("select")) {--}%
                %{--if($.trim($(this).val())=="all")--}%
                   %{--counter++;--}%
              %{--} else {--}%
                %{--if($.trim($(this).val()).length==0)--}%
                  %{--selectCounter++;--}%
              %{--}--}%
            %{--});--}%

            %{--//if nothings changed--}%
            %{--if(counter==2 && selectCounter==2) {--}%
              %{--$(this).next().addClass("active");--}%
              %{--e.stopPropagation();--}%
              %{--$(this).removeClass("active");--}%
              %{--$(this).blur();--}%
              %{--$('#filterSearch').attr("disabled",true);--}%
              %{--return alert("Please enter one or more values from the 4 different fields to filter by firstly");--}%
    %{--} else {--}%
          %{--var params   = "?" + $('[required]').serialize() + "&" + $.param({"filterMode":mode,"shortcode":'${params.shortcode}'});--}%
                  %{--var link     = "<g:createLink controller='finance' action='index'/>" + params--}%
                  %{--$('#filterSearch').attr('href', link);--}%
                  %{--$('#filterSearch').attr("disabled",false);--}%
                  %{--console.log(link);--}%
            %{--}--}%
       %{--}--}%
    });

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

    function filterSearch(e){
        if($('#filterMode')=="OFF")
            return false;
        var reqFields = $(".required-indicator");
         var counter = 0;
         reqFields.each(function() {
              if($(this).is("select")) {
                if($.trim($(this).val())=="all")
                   counter++;
              } else {
                if($.trim($(this).val()).length==0)
                  counter++;
              }
            });

            if(counter==4)
               return false

        var paramsData = reqFields.serialize()+"&"+$('#filterMode').serialize()+"&shortcode=${params.shortcode}&format=json";
      $.ajax({
        url: "<g:createLink controller='finance' action='filterResultCheck'/>",
        data: paramsData,
        dataType:'json'
      }).done(function(data) {
        console.log("%o",data);
        if(data.count==0)
        {
          alert("No results");
          return false
        }
        else
            $('#filterView').submit();
      });
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

    $(".datepicker-class").datepicker({
        format: "yyyy-mm-dd"
    });

    $("#addNew").click(function() {
        $('html, body').animate({
            scrollTop: $("#newCosts").offset().top
        }, 2000);
    });

    $("#top").click(function() {
        $('html, body').animate({
            scrollTop: $("#costTable").offset().top
        }, 2000);
    });
  });



  </r:script>
</html>

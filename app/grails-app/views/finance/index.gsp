<!doctype html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="mmbootstrap"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'style.css')}" type="text/css"/>
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
            <li class="pull-left"><a class="badge badge-info" onclick="quickHelpInfo()">?</a>&nbsp;</li>
        </ul>
    </div>


    <div class="modal hide" id="recentDialog">
        <div class="modal-header">
            <button class="close" data-dismiss="modal">×</button>
            <h3>Recently Updated Cost Items</h3>
        </div>
        <div class="modal-body">
            <div id="recent">
                <g:render template="recentlyAdded"></g:render>
            </div>
        </div>
    </div>


    <div class="container-fluid">

        <div id="userError" hidden="">
            <table class="table table-striped table-bordered ">
                <thead>
                <tr><th>Problem/Update</th>
                    <th>Info</th></tr>
                </thead>
                <tbody><tr></tr></tbody>
            </table>
        </div>

        <div id="filterTemplate">
            <g:render template="filter"></g:render>
        </div>

        <br/><br/><br/><br/><br/>

        <g:render template="create"></g:render>

        <button class="btn btn-primary pull-right" onclick="scrollToTop(2000,'costTable')" title="Select this button to go back to the top of the page" id="top">Back to top</button>
    </div>

</body>

    <r:script type="text/javascript">

        function tester() {
            $('.xEditable').editable();
            $('.xEditableValue').editable();
        }

        $( document ).ajaxComplete(function( event,request, settings ) {
            console.log(event);
            console.log(settings);
        });


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

            $("#newBudgetCode").select2({
              placeholder: "New code or lookup  code",
              allowClear: true,
               tags: true,
              tokenSeparators: [',', ' '],
              minimumInputLength: 1,
              ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                url: "<g:createLink controller='ajax' action='lookup'/>",
                dataType: 'json',
                data: function (term, page) {
                    return {
                        format:'json',
                        q: term,
                        shortcode: '${params.shortcode}',
                        baseClass:'com.k_int.kbplus.CostItemGroup'
                    };
                },
                results: function (data, page) {
                    return {results: data.values};
                }
              },
              createSearchChoice:function(term, data) {
                 var existsAlready     = false;
                 for (var i = 0; i < data.length; i++)
                 {
                    if(term.toLowerCase() == data[i].text.toLowerCase())
                    {
                        existsAlready = true;
                        break;
                    }
                 }
                 if(!existsAlready)
                    return {id:-1+term, text:"new code: "+term};
              }

            });

            //If we want to do something upon selection
            $("#newBudgetCode").on("select2-selecting", function(e) {
                var presentSelections = $("#newBudgetCode").select2("data");
                var current = e.choice.text.trim().toLowerCase();
                if(current.indexOf("new code: ",0) == 0)
                    current = current.substring(10,current.length);

                if(presentSelections.length > 0)
                {
                    for (var i = 0; i < presentSelections.length; i++)
                    {
                        var p = (presentSelections[i].text.indexOf("new code: ",0) == 0)? presentSelections[i].text.trim().toLowerCase().substring(10,presentSelections[i].text.length):presentSelections[i].text.trim().toLowerCase();
                        if(p == current)
                        {
                            e.preventDefault();
                            break;
                        }
                    }
                }
            });

            $(".datepicker-class").datepicker({
                format: "yyyy-mm-dd"
            });

            $('#filterMode').val("${filterMode}");
            $('#submitFilterMode').prop('disabled',${filterMode=='OFF'});

            //first run
            sortAndOrder();
            performCostItemUpdate(null);
            deleteSelectAll();
        });

        //Page updating/actions - i.e. before/after: delete, create
        function updateResults(action) {
            console.log("Performing update of results via action : "+action);
            var paginateData   = $('#paginateInfo').data();
            var adjustedOffset = paginateData.offset;
            if(action.startsWith('delete'))
            {
                var newTotal = parseInt(paginateData.total) - parseInt(action.split(':')[1]);
                if(newTotal % paginateData.max == 0 && adjustedOffset !=0)
                    adjustedOffset = paginateData.offset - paginateData.max;
            }

            $.ajax({
                method: "POST",
                url: "<g:createLink controller='finance' action='index'/>",
                data: {
                    shortcode:"${params.shortcode}",
                    offset:adjustedOffset,
                    max:paginateData.max,
                    sort:paginateData.sort,
                    order:paginateData.order,
                    filterMode: paginateData.filtermode,
                    mode:"updateResults"
                }
            })
            .fail(function( jqXHR, textStatus, errorThrown ) {
                 errorHandling(textStatus,'Updating results after action: '+action,errorThrown);
             })
            .done(function(data) {
                 $('#filterTemplate').html(data);
                 console.log("%o",data);
                 deleteSelectAll();
                 sortAndOrder();
            });
        }

        function deleteSelectAll() {
            $('#selectAll').click(function(event) {
                if(this.checked) {
                    $('.bulkcheck').each(function() {
                        this.checked = true;
                    });
                } else{
                    $('.bulkcheck').each(function() {
                        this.checked = false;
                    });
                }
            });
        }

        function performBulkDelete() {
            var agreed = confirmSubmit("Actions are permanent for selected Cost Item(s)");
            if(agreed == false)
            {
                $('.bulkcheck:checked').each(function() {
                   this.checked = false;
                });
                return false;
            } else
            {
                var allVals = [];
                $('.bulkcheck:checked').each(function(){
                    allVals.push($(this).val());
                 });

                $.ajax({
                method: "POST",
                url: "<g:createLink controller='finance' action='delete'/>",
                data: {
                  format:'json',
                  del:JSON.stringify(allVals),
                  shortcode:'${params.shortcode}'
                },
                dataType:'json'
              }).done(function(data) {
                    console.log("%o",data);
                    userInfo("deletion(s)",data.message); //list of succesfully deleted ids
                    $.each(data.successful, function( i, val ) {
                        $("#bulkdelete-a" + val).remove();
                        $("#bulkdelete-b" + val).remove();
                    });
                    updateResults('delete:'+data.successful.length);
                });
           }
        }

        function clearCreateForm() {
            $('form#createCost').trigger("reset");
            $('#newBudgetCode').select2('data', '');
            $('#newIE').select2('data', '');
        }

        //on succesful
        function bindAjaxResults(fadeID,fadeTime,scrollTime,scrollID,updateAction) {
            if(fadeID != null)
                fadeAway(fadeID,fadeTime);
            if(scrollID != null)
                scrollToTop(scrollTime,scrollID);
            if(updateAction != null)
                updateResults(updateAction);
            clearCreateForm();
            filterSelection();
            sortAndOrder();
            deleteSelectAll();
        }


        //Pagination/filtering Handling code block
        function sortAndOrder()
        {
            $('.sortable').on('click', function(event){
                console.log(event)
                event.preventDefault();
                var selected       = $(this)[0].firstChild.textContent;
                var order          = $(this).data('order');
                var paginateData   = $('#paginateInfo').data();

                var data = {
                    shortcode: "${params.shortcode}",
                    filterMode: paginateData.filtermode,
                    opSort:true,
                    sort:paginateData.sort,
                    order: order,
                    offset:0,
                    max:paginateData.max
                }

                var formData = null
                if(paginateData.filtermode == "ON")
                {
                    var formData = {
                        subscriptionFilter:paginateData.subscriptionfilter,
                        packageFilter:paginateData.packagefilter,
                        invoiceNumberFilter:paginateData.invoicenumberfilter,
                        orderNumberFilter:paginateData.ordernumberfilter,
                        resetMode:paginateData.resetMode?paginateData.resetMode:'search'
                    }
                    console.log(formData)
                }
                data = (formData!=null)?$.param(formData) + '&' + $.param(data):$.param(data);

                $.ajax({
                    method: "POST",
                    url: "<g:createLink url="[controller: 'finance', action: 'index']"/>",
                    data: data
                })
                .fail(function( jqXHR, textStatus, errorThrown ) {
                     errorHandling(textStatus,'Sorting (via '+ selected +')',errorThrown);
                  })
                .done(function(data) {
                     console.log("%o",data);
                     $('#filterTemplate').html(data);
                     sortAndOrder();
                });
                return false;
            });

            $('.sortable').hover(function() {
                var sortLink = $(this); //.hasClass('sorted')
                var title    = $('#paginateInfo').data('filtermode')=="OFF"?' filtering is not active':' you can sort by your active filter';
                sortLink.prop('title', (sortLink.hasClass('sorted')?'Presently sorting':'Choose to sort') + ' via '+sortLink[0].firstChild.textContent+'\n'+title);
                //$("#hiddenPrompt").show(); //todo look into this
            }, function() {
                 //$("#hiddenPrompt").hide();
            });
        }

        function filterSelection() {
            if($('#submitFilterMode').attr("value")!="reset")
            {
                var newMode       = $('#filterMode');
                var disabledState = (newMode.val() == "ON")? false:true; //Need to turn off, i.e. refresh result
                $('#submitFilterMode').prop('disabled',disabledState);
            }
        }

        function filterValidation() {
            if($('#submitFilterMode').val() != 'reset')
            {
                if("${filterMode}"=="OFF" && $('#filterMode').val()=="OFF")
                    return false;
                var reqFields = $(".required-indicator");
                var counter   = 0;

                reqFields.each(function() {
                  if($(this).is("select")) {
                    if($.trim($(this).val())=="all")
                       counter++;
                  } else {
                    if($.trim($(this).val()).length==0)
                      counter++;
                  }});

                if(counter==4)
                {
                    userInfo("Filtering","You need to enter/select from the 1 or more of the 4 options");
                    return false;
                } else
                {
                    $('#resetMode').val($('#submitFilterMode').val());
                    return true;
                }
            } else
                return true;
        }

        $('#submitFilterMode').on('click',function(e) {
            var newMode       = $('#filterMode').val();
            var disabledState = (newMode == "ON")? false:true; //Need to turn off, i.e. refresh results
            $('#filterSearch').attr("disabled",disabledState);
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

        //error/info code block
        function errorHandling(status,actionFailed,reason) {
            switch (reason)
            {
                case 'Unauthorized':
                {
                    if(confirmSubmit(actionFailed + ' failed due to: ' + reason + ' action, will require re-authentication, page will now reload?'))
                        location.reload(true);
                     else
                        alert('Data can not be updated until refresh occurs!');
                    break;
                }
                case 'Internal Server Error':
                {
                    alert(actionFailed + ' failed due to: ' + reason?reason:status + '... Report this error!');
                    break;
                }
                default :
                {
                    alert('Problem occurred with action: '+ actionFailed +', needs investigation! Possibly due to: '+reason);
                    break;
                }
            }
        }

        function userInfo(status,message,timeout) {
            var html = ""
            $.each(message.split(",,,"), function( i, val ) {
               html += ('<tr><td>'+status+'</td><td>'+val+'</td></tr>');
            });
            var errorDisplay = $('#userError');
            errorDisplay.stop(true,true);
            errorDisplay.find('tbody:last').html(html);
            scrollToTop(2000,"userError");
            errorDisplay.fadeToggle(1000).delay(timeout!=null?timeout:3000).slideUp(4000, function(){
                $("#userError > table > tbody").children().remove();
            });
        }

        function fadeAway(id,time) {
             $('#'+id).fadeIn(2000).delay(time).slideUp(4000, function(){
                $('#'+id).remove();
             });
        }

         function scrollToTop(time,id) {
            $('html, body').animate({
                scrollTop: $("#"+id).offset().top
            }, time);
         }

        function confirmSubmit(msg) {
          return confirm("Are you sure you wish to continue?\n\n"+msg);
        }

        // Recently updated code block
        setInterval(recentCostItems, 60000);

        function recentCostItems() {
            var renderedDateTo = $('#recentUpdatesTable').data('resultsto');
            if(renderedDateTo!=null)
            {
              $.ajax({
                method: "POST",
                url: "<g:createLink controller='finance' action='newCostItemsPresent'/>",
                data: {
                    shortcode: "${params.shortcode}",
                    to:renderedDateTo,
                    from: "${from}",
                    format:'json'
                }
              })
              .fail(function( jqXHR, textStatus, errorThrown ) {
                 errorHandling(textStatus,'Recent Cost Updates',errorThrown);
              })
              .done(function(data) {
                 console.log("%o",data);
                 if(data.count > 0)
                    performCostItemUpdate(renderedDateTo);
              });
            }
        }

        function performCostItemUpdate(to) {
            $.ajax({
                method: 'POST',
                url: "<g:createLink controller='finance' action='getRecentCostItems'/>",
                    data: {
                    from: "${from}",
                    to: to,
                    shortcode: "${params.shortcode}"
                }
            }).done(function(data) {
                $('#recent').html(data);
            });
        }

        function quickHelpInfo() {
            userInfo("Help","<b>Sorting</b> via clickable title links of the following : Cost Item#, Invoice#, Order#, Subscription, Package, date, IE ,,, " +
             "<b>Filter Search</b> via the 4 input fields : Invoice#, Order#, Subscription, and Package, selecting filter mode as ON and submitting the search. On finishing with your results reset via the 'reset' button,,," +
              "<b>Pagination</b> (<i>Results Navigation</i>) via the clickable links, below the results table,,," +
               "<b>Deleting Costs</b> via checking the boxes attached to each cost item row individually or all and submitting by clicking 'remove selected,,," +
                "<b>Add New Costs</b> via creation screen, after results, shortcut button select 'Add New Cost'",20000)
        }
    </r:script>
</html>

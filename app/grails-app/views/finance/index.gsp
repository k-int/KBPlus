<!doctype html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: Financial Information</title>
</head>
<body>

<div class="container">
    <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="finance" params="${[shortcode:params.shortcode]}">${institution.name} Finance</g:link> </li>
        <g:if test="${editable}">
            <li class="pull-right"><span class="badge badge-warning">Editable</span>&nbsp;</li>
        </g:if>
    </ul>
</div>

<div class="container">

    <div id="userError" hidden="">
        <table>
            <thead>
            <tr>
                <th>Issue/Status</th>
                <th>Info</th>
            </tr>
            </thead>

            <tbody>
            <tr>

            </tr>
            </tbody>
        </table>
    </div>

    <div id="filterTemplate">
        <g:render template="filter"></g:render>
    </div>

    <br/><br/><br/><br/><br/>

    <g:render template="create"></g:render>

    <button class="btn btn-primary pull-right" onclick="scrollToTop(2000,'costTable')" id="top">Back to top</button>
</div>

<g:hiddenField id="pageReload" name="pageReload" value=""></g:hiddenField>
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

    function clearCreateForm()
    {
        $('form#createCost').trigger("reset");
        $('#newBudgetCode').select2('data', '');
        $('#newIE').select2('data', '');
    }

    $('#submitFilterMode').on('click',function(e) {
        //e.preventDefault();
        var newMode       = $('#filterSelectionMode').val();
        var prevMode      = "${filterMode}";
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

    function filterSelection() {
        var newMode       = $('#filterSelectionMode');
        var disabledState = (newMode.val() == "ON")? false:true; //Need to turn off, i.e. refresh result
        $('#submitFilterMode').prop('disabled',disabledState);
    }

    function filterValidation() {
        var reqFields = $(".required-indicator");
        var counter   = 0;

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
        {
            userInfo("Filtering","You need to enter/select from the 1 or more of the 4 options");
            return false;
        } else
        {
            $('#resetMode').val($('#submitFilterMode').val());
            return true;
        }
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
         return {id:-1+term, text:"New Code: "+term};
      }

    });

    //If we want to do something upon selection
    $("#newBudgetCode").on("select2-selecting", function(e) {
        var presentSelections = $("#newBudgetCode").select2("data");
        if(presentSelections.length > 0) {
            for (var i = 0; i < presentSelections.length; i++) {
                if(presentSelections[i].text.toString == e.choice.text.toString)
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


    $('#filterSelectionMode').val("${filterMode}")
    $('#submitFilterMode').prop('disabled',${filterMode=='OFF'});

    window.onbeforeunload = function() {
      if("${filterMode}"=="ON")
       return "Are you sure you want to navigate away?";
      }
    });

  function scrollToTop(time,id) {
    $('html, body').animate({
        scrollTop: $("#"+id).offset().top
    }, time);
  };

    function userInfo(status,message) {
        if(status!=null && message!=null)
        {
            $('#userError').find('tbody:last').append('<tr><td>'+status+'</td><td>'+message+'</td></tr>');
            scrollToTop(2000,"userError");
            $('#userError').fadeToggle('fast').fadeToggle(5000).promise().done(function(){
                $('#userError').find('tr:last').remove();
            });
        }
    }

    function fadeAway(id,time) {
         $('#info').fadeToggle('slow'); //todo won't work bind event i.e. on ?
     }

</r:script>
</html>

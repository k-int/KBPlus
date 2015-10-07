<!doctype html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: Financial Information</title>
</head>
<body>
%{--<r:require modules="finance" />--}%

%{--Run once data... can be reused for edit based functionality too. Pointless sending back this static data every request --}%
<g:set var="costItemStatus"   scope="page" value="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','CostItemStatus')}"/>
<g:set var="costItemCategory" scope="page" value="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','CostItemCategory')}"/>
<g:set var="costItemElement"  scope="page" value="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','CostItemElement')}"/>
<g:set var="taxType"          scope="page" value="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','TaxType')}"/>
<g:set var="yn"               scope="page" value="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','YN')}"/>
<g:set var="currency"         scope="page" value="${com.k_int.kbplus.CostItem.orderedCurrency()}"/>


<div class="container-fluid">
    <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li>
            <g:if test="${inSubMode}">
                <li> <g:link controller="subscriptionDetails" action="index" params="${[shortcode: params.shortcode, sub:fixedSubscription?.id]}" id="${fixedSubscription?.id}">${fixedSubscription?.name}</g:link> <span class="divider">/</span> </li>
                <g:link mapping="subfinance" controller="myInstitutions" action="finance" params="${[shortcode:params.shortcode, sub:fixedSubscription?.id]}">${institution.name} Finance <i>(Subscription Mode)</i></g:link>
            </g:if>
            <g:else>
                <g:link controller="myInstitutions" action="finance" params="${[shortcode:params.shortcode]}">${institution.name} Finance</g:link>
            </g:else>
        </li>
        <g:if test="${editable}">
            <li class="pull-right"><span class="badge badge-warning">Editable</span>&nbsp;</li>
            <li class="pull-right"><span style="font-weight: 400;" class="badge"><a href="${createLink(controller: 'myInstitutions', action: 'financeImport', params: [shortcode:params.shortcode])}">Finance Import</a></span> </li>
        </g:if>
        <li class="pull-left"><a class="badge badge-info" onclick="quickHelpInfo()">?</a>&nbsp;</li>

        <li class="dropdown pull-right">
            <a class="dropdown-toggle badge" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">Exports<b class="caret"></b></a>

            <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
                <li><a data-mode="all" class="export" style="cursor: pointer">CSV Export Current List</a></li>
                 <li><a data-mode="sub" class="disabled export" style="cursor: pointer">CSV Sub Costs</a></li>
                 <li><a data-mode="code" class="disabled export" style="cursor: pointer">CSV Code Costs</a></li>
            </ul>
        </li>
    </ul>
</div>

<div style="padding-left: 2%" hidden="hidden" class="loadingData">
    <span>Loading...<img src="${resource(dir: 'images', file: 'loading.gif')}" /></span>
</div>

<div id="recentModalWrapper" class="wrapper">
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

    <div id="filterTemplateWrapper" class="wrapper">
        <div id="filterTemplate">
            <g:render template="filter"></g:render>
        </div>
    </div>

    <br/><br/><br/><br/><br/>

    <div style="padding-left: 2%" hidden="hidden" class="loadingData">
        <span>Loading...<img src="${resource(dir: 'images', file: 'loading.gif')}" /></span>
    </div>

    <div id="CreateTemplateWrapper" class="wrapper">
        <g:render template="create"></g:render>
    </div>

    <button class="btn btn-primary pull-right"  data-offset="#costTable" title="Select this button to go back to the top of the page" id="top">Back to top</button>
</div>

</body>

<r:script type="text/javascript">

     //todo use AJAX promises, refactor into a resuable AJAX function using a callback object
     //Module pattern
     ;var Finance = (function(){
        var s = { //Setup
            mybody: $('body'),
            ft: { //filter template settings
                searchBtn:'#submitFilterMode',
                filterOpt:'#filterMode',
                filterTemplate:'#filterTemplate',
                delSelectAll:'#selectAll',
                delCheckboxes:'.bulkcheck',
                delBatch:'#BatchSelectedBtn',
                paginateData:'#paginateInfo',
                advFilterBtn:'#advancedFilter',
                advFilterOpts:'#advancedFilterOpt',
                filterModSelect2a:'.modifiedReferenceTypedown.refData',
                filterModSelect2b:'.modifiedReferenceTypedown.refObj',
                filterSubscription:'#subscriptionFilter',
                filterSubPkg:'#packageFilter',
                codeDelete: 'a.badge.budgetCode',
                codeEdit:'a.budgetCode.editable-empty',
                tableWrapper:'#filterTableWrapper',
                editableBind:'.xEditableValue, .xEditableManyToOne'
            },
            ct: { //create template
                resetBtn:'#resetCreate',
                datePickers:'.datepicker-class',
                newBudgetCode:"#newBudgetCode",
                newIE:'#newIE',
                newSubPkg:'#newPackage',
                newSubscription:'#newSubscription'
            },
            url: {
                ajaxLookupURL:"<g:createLink controller='ajax' action='lookup'/>",
                ajaxFinanceIndex:"<g:createLink controller='finance' action='index'/>",
                ajaxFinanceDelete:"<g:createLink controller='finance' action='delete'/>",
                ajaxFinanceRecent:"<g:createLink controller='finance' action='getRecentCostItems'/>",
                ajaxFinancePresent:"<g:createLink controller='finance' action='newCostItemsPresent'/>",
                ajaxFinanceRefData:"<g:createLink controller='finance' action='financialRef'/>",
                ajaxFinanceCodeDel:"<g:createLink controller='finance' action='removeBC'/>",
                ajaxFinanceExport:"<g:createLink controller='finance' action='financialsExport'></g:createLink>"
            },
            misc: {
                recentlyUpdated: '#recent',
                noOption:'<option value="xx">Not specified</option>',
                exportLink:'a.export'
            },
            options: {
                timeoutLoading: null,
                timeoutRecent: null,
                showInfo:"${session?.userPereferences?.showInfoIcon}",
                dateFormat:"${session.sessionPreferences?.globalDatepickerFormat?: 'yyyy-mm-dd'}"
            }
        };


        var _performCostItemUpdate = function(to) {
            $.ajax({
                method: 'POST',
                url: s.url.ajaxFinanceRecent,
                    data: {
                    from: "${from}",
                    to: to,
                    shortcode: "${params.shortcode}"
                },
                global: false
            }).done(function(data) {
                $(s.misc.recentlyUpdated).html(data);
            })
            .fail(function(jqXHR, textStatus, errorThrown ) {
                console.log('Unable to perform recent cost update ... ',errorThrown);
              clearTimeout(s.options.timeoutRecent);
            });
        };

        //Setup to build
        //todo make this configurable, factory builder style, pass in a list of config objects
        function setupModSelect2s() {
             //unable to use placeholder with initSelection, manually set via GSP with data-placeholder
            $(s.ft.filterModSelect2a).select2({
              initSelection : function (element, callback) {
                    //If default value has been set in the markup!
                if(element.data('defaultvalue'))
                    var data = {id: element.data('domain')+':'+element.data('relationid'), text: element.data('defaultvalue')};
                callback(data);
              },
              minimumInputLength: 1,
              ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
              url: s.url.ajaxLookupURL,
              dataType: 'json',
              global: false,
              data: function (term, page){
                  return {
                      format:'json',
                      q: term,
                      baseClass:$(this).data('domain'),
                      shortcode: $(this).data('shortcode')
                  };
              },
              results: function (data, page) {
                return {results: data.values};
              }
            },
              createSearchChoice:function(term, data) {
                 var existsAlready = false;
                 for (var i = 0; i < data.length; i++)
                 {
                    if(term.toLowerCase() == data[i].text.toLowerCase()) {
                        existsAlready = true;
                        break;
                    }
                 }
                 if(!existsAlready)
                    return {id:term+':create', text:"new code:"+term};
              }
          });

            //select2 setup for subs, pkgs, ie
          var sel2_b_objs = $(s.ft.filterModSelect2b);
          var sel2_b_opts = {
              initSelection : function (element, callback) {
                if(element.data('defaultvalue')) //If default value has been set in the markup!
                    var data = {id: element.data('domain')+':'+element.data('relationid'), text: element.data('defaultvalue')};
                callback(data);
              },
              minimumInputLength: 1,
              ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
              url: s.url.ajaxLookupURL,
              dataType: 'json',
              enable: false,
              global: false,
              data: function (term, page){
                  return {
                      format:'json',
                      q: '%'+term,
                      baseClass:$(this).data('domain'),
                      inst_shortcode: $(this).data('shortcode'),
                      subFilter: $(this).data('subfilter'),
                      hideDeleted: 'true',
                      hideIdent: 'false',
                      inclSubStartDate: 'false',
                      page_limit: 10
                  };
              },
              results: function (data, page) {
                return {results: data.values};
              }
            }
          };

          //Individually bind each element, bug on present version, unable to dynamically disable with options setup
          var currentSub  = null;
          sel2_b_objs.each(function(index) {
              $(this).select2(sel2_b_opts);
              if(index % 3) //pkg & ie inputs
              {
                  if(currentSub.data().defaultvalue == null)
                      $(this).select2('disable');
              }
              else  //sub inp
                  currentSub = $(this);
          });
          currentSub = null;


            if(!s.misc.inSubMode) {
                //subscription
                $(s.ft.filterSubscription).select2({
                    placeholder: "Type subscription name...",
                    minimumInputLength: 1,
                    global: false,
                    ajax: {
                        url: s.url.ajaxLookupURL,
                        dataType: 'json',
                        data: function (term, page) {
                            return {
                                hideDeleted: 'true',
                                hideIdent: 'false',
                                inclSubStartDate: 'false',
                                inst_shortcode: '${params.shortcode}',
                                q: '%'+term , // contains search term
                                page_limit: 20,
                                baseClass:'com.k_int.kbplus.Subscription'
                            };
                        },
                        results: function (data, page) {
                            return {results: data.values};
                        }
                    },
                    allowClear: true,
                    formatSelection: function(data) {
                       return data.text;
                    }
                });
            } //end of if check


            //sub pkg
            $(s.ft.filterSubPkg).select2({
                placeholder: "Type sub pkg name...",
                minimumInputLength: 1,
                global: false,
                ajax: {
                    url: s.url.ajaxLookupURL,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            hideDeleted: 'true',
                            hideIdent: 'false',
                            inclSubStartDate: 'false',
                            inst_shortcode: '${params.shortcode}',
                            q: '%'+term , // contains search term
                            page_limit: 20,
                            subFilter:$(s.ft.filterSubscription).data().subFilter.split(":")[1],
                            baseClass:'com.k_int.kbplus.SubscriptionPackage'
                        };
                    },
                    results: function (data, page) {
                        return {results: data.values};
                    }
                },
                allowClear: true,
                formatSelection: function(data) {
                   return data.text;
                }
            });

            if(s.misc.inSubMode == false)
            {
                $(s.ft.filterSubPkg).select2('disable');
            }



            } //end of select2setup

        //todo See why this is causing delete of everything on create
        //Separate function instead of
        //var _clearCreateForm = function() {
        //    $('form#createCost').trigger("reset");
        //    $('#newBudgetCode').select2('data','');
        //    $('#newIE').select2('data','');
        //};

        //This is for AJAX functionality, inclusion of first run too!
        var _bindBehavior = function() {
            //s.mybody.on('click',s.resetBtn, _clearCreateForm); //Reset btn (create form)
            $(s.ct.datePickers).datepicker({format: s.options.dateFormat}); //datepicker
            setupModSelect2s();
            $(s.ft.editableBind).editable(); //technically being performed twice on first run
        };

        var _recentCostItems = function() {
            var renderedDateTo = $('#recentUpdatesTable').data('resultsto');
            if(renderedDateTo!=null)
            {
              $.ajax({
                method: "POST",
                url: s.url.ajaxFinancePresent,
                data: {
                    shortcode: "${params.shortcode}",
                    to:renderedDateTo,
                    from: "${from}",
                    format:'json'
                },
                global: false
              })
              .fail(function( jqXHR, textStatus, errorThrown ) {
                 errorHandling(textStatus,'Recent Cost Updates',errorThrown);
                 clearTimeout(s.options.timeoutRecent);
                 $('#recentModalWrapper','#showHideRecent').remove();
              })
              .done(function(data) {
                 if(data.count > 0)
                    _performCostItemUpdate(renderedDateTo);
              });
            }
        };


        var _performBulkDelete = function() {
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
                url: s.url.ajaxFinanceDelete,
                data: {
                  format:'json',
                  del:JSON.stringify(allVals),
                  shortcode:'${params.shortcode}'
                },
                dataType:'json'
              }).done(function(data) {
                    userInfo("deletion(s)",data.message); //list of succesfully deleted ids
                    $.each(data.successful, function( i, val ) {
                        $("#bulkdelete-a" + val).remove();
                        $("#bulkdelete-b" + val).remove();
                    });
                    _updateResults('delete:'+data.successful.length);
                });
           }
        };


        var _filtersUpdated = function() {
          $('#newInvoiceNumber').val($('#filterInvoiceNumber').val());
          $('#newOrderNumber').val($('#filterOrderNumber').val());
          if($(s.ct.newSubscription).val() != $(s.ft.filterSubscription).val())
          {
              $(s.ct.newSubscription).val($(s.ft.filterSubscription).val());
              $(s.ct.newSubPkg).val($(s.ft.filterSubPkg).val());
          }
        };

        //Fetch packages for the selected subscription
        var filterSubUpdated  = function() {
          var filterSubscription = $(this);
            console.log("filter sub updated called ",filterSubscription.val());

           var filterVal = filterSubscription.val().split(':');
          $.ajax({
            url: s.url.ajaxLookupURL,
            data: {
              format:'json',
              subFilter:filterVal[1],
              baseClass:'com.k_int.kbplus.SubscriptionPackage'
            },
            dataType:'json'
          })
          .done(function(data) {
            var newSubPackge = $(s.ct.newSubPkg);
            var filterSubPkg = $(s.ft.filterSubPkg);
            newSubPackge.children('option').remove();
            filterSubPkg.children('option:gt(0)').remove();
            var numValues = data.values.length;
            for (var i = 0; i != numValues; i++) {
              $(s.ft.filterSubPkg).append('<option value="'+data.values[i].id+'">'+data.values[i].text+'</option>');
            }
            filterSubPkg.children('option').clone().appendTo(newSubPackge);
          });
        };


        var _filterSelection = function() {
            var filterMode = $(s.ft.filterOpt);
            var submitBtn  = $(s.ft.searchBtn);
            if(submitBtn.val() != 'reset')
            {
                var disabledState = filterMode.val() == "ON"? false:true; //Need to turn off, i.e. refresh result
                submitBtn.prop('disabled',disabledState);
            }
        };

        var _removeBudgetCode = function(e) {
            var element = $(this);
            $.ajax({
                method: "POST",
                url: s.url.ajaxFinanceCodeDel,
                data: {
                    bcci:element.attr('id'),
                    shortcode:"${params.shortcode}"
                }
             })
             .done(function(data) {
                if(data.error)
                    userInfo(data.error.status,data.error.msg,null);
                else
                {
                    element.prev('span').remove();
                    element.remove();
                    userInfo(data.success.status,data.success.msg,null);
                }
            });
        };


        //var _filterValidation = function() {
        function filterValidation()   {
            var submitBtn = $(s.ft.searchBtn);
            if(submitBtn.val() != 'reset')
            {
                if("${filterMode}"=="OFF" && $(s.ft.filterOpt).val()=="OFF")
                    return false;
                var reqFields = $(".required-indicator");
                var counter   = 0;

                reqFields.each(function() {
                  if($(this).is("select")) {
                    if($.trim($(this).val())=="Not specified")
                       counter++;
                  } else {
                    if($.trim($(this).val()).length==0)
                      counter++;
                  }});

                if(counter==4)
                {
                    userInfo("${g.message(code: 'financials.help.filterSearch')}","${g.message(code: 'financials.filtersearch.error')}");
                    return false;
                } else
                {
                    $('#resetMode').val(submitBtn.val());
                    return true;
                }
            } else
                return true;
        };

        var _submitFilterSearch = function(e) {
            if(!filterValidation())
                return false;

            //todo send fixed subscription data ... causing redirect on AJAX with below
            //var paginateData = $('#paginateInfo').data();
            //var paramAppend  = "&sub="+fixedsub+"&inSubMode="+paginateData.insubmode;

            $.ajax({
                type:'POST',
                data:$(this).parents('form:first').serialize(),
                url:s.url.ajaxFinanceIndex,

                success:function(data,textStatus){
                    $('#filterTemplate').html(data);
                },
                error:function(XMLHttpRequest,textStatus,errorThrown){
                    errorHandling(textStatus,'Filtering',errorThrown);
                },
                complete:function(XMLHttpRequest,textStatus){
                    _filterSelection();fadeAway('info',15000);
                    _bindBehavior();
                }
            });
            return false
        };

        //todo add additional arg for error usage add id
        var _scrollTo = function(e, scrollTo) {
            var id = ($.isEmptyObject(scrollTo))? $(this).data('offset') : scrollTo;
            $('html, body').animate({
                scrollTop: $(id).offset().top
            }, 1000);
         };

        var startLoadAnimation = function() {
            s.options.timeout = setTimeout(function() {
                $(s.ft.tableWrapper).addClass('overlay');
                $('div.loadingData').show();
            }, 50); //50ms delay
        };

        //If ajax call finishes before the timeout occurs, we wouldn't have shown any animation.
        var stopLoadAnimation = function() {
            $('div.loadingData').hide();
            $(s.ft.tableWrapper).removeClass('overlay');
            clearTimeout(s.options.timeout);
        };

        var _updateResults = function(e) {
            var action = $.isEmptyObject($(this).data('action'))? e : $(this).data('action');
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
                url: s.url.ajaxFinanceIndex,
                data: {
                    shortcode:"${params.shortcode}",
                    offset:adjustedOffset,
                    max:paginateData.max,
                    sort:paginateData.sort,
                    order:paginateData.order,
                    filterMode: paginateData.filtermode,
                    wildcard: paginateData.wildcard,
                    mode:"updateResults"
                }
            })
            .fail(function( jqXHR, textStatus, errorThrown ) {
                 errorHandling(textStatus,'Updating results after action: '+action,errorThrown);
             })
            .done(function(data) {
                 console.log('updateResults...');
                 $(s.ft.filterTemplate).html(data);
                 _bindBehavior();
            });
        };

        //List of comma seperated fields to reset for a cost item, as these fields
        //have a reliance on other data e.g. subscription that might have changed
        function _performReset(fields) {
            ajax({
               //.. config different to default...
               url: s.url.ajaxFinanceRefData,
               data: {
                    fields: fields
               }
            }).done(function(data) {
                // .. run callbacks in the normal way.
                console.log(data);
            });
        };

        //re-usable AJAX method, pass in default config
        //Use as follows (see _performReset) ...
        function ajax (config) {
          var paginateData = $('#paginateInfo').data();
          config = $.extend({
            //Default config....
            url: s.url.ajaxFinanceIndex,
            data: {
                    shortcode:"${params.shortcode}",
                    offset:paginateData.offset,
                    max:paginateData.max,
                    sort:paginateData.sort,
                    order:paginateData.order,
                    filterMode: paginateData.filtermode,
                    wildcard: paginateData.wildcard
            }
          }, config | {});
          return $.ajax(config);
        }




        //Binds everything which needs to be run the once, including the majority of dynamically rendered HTML content
        //For everything else which can't be binded once will be in _bindBehavior
        var _firstRun = function() {
            $.fn.editable.defaults.mode = 'inline'; //default is popup mode

            $(document).ajaxStart(startLoadAnimation);
            $(document).ajaxStop(stopLoadAnimation);

            s.mybody.on("click","#advancedFilter", function() {
              $( "#advSearchRow" ).slideToggle("fast");
            });

            s.misc.inSubMode = "${inSubMode}" == "true";
            console.log('In subscription only mode',s.misc.inSubMode);

            if(!s.misc.inSubMode) {
                $(s.ct.newSubscription).select2({
                    placeholder: "Type subscription name...",
                    minimumInputLength: 1,
                    global: false,
                    ajax: {
                        url: s.url.ajaxLookupURL,
                        dataType: 'json',
                        data: function (term, page) {
                            return {
                                hideDeleted: 'true',
                                hideIdent: 'false',
                                inclSubStartDate: 'false',
                                inst_shortcode: '${params.shortcode}',
                                q: '%'+term , // contains search term
                                page_limit: 20,
                                baseClass:'com.k_int.kbplus.Subscription'
                            };
                        },
                        results: function (data, page) {
                            return {results: data.values};
                        }
                    },
                    allowClear: true,
                    formatSelection: function(data) {
                       return data.text;
                    }
                });
            }


            $(s.ct.newIE).select2({
                placeholder: "Identifier..",
                minimumInputLength: 1,
                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: s.url.ajaxLookupURL,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            format:'json',
                            q: term,
                            subFilter: $(s.ct.newSubscription).val(),
                            baseClass:'com.k_int.kbplus.IssueEntitlement'
                        };
                    },
                    results: function (data, page) {
                      return {results: data.values};
                    }
                }
            });

            $(s.ct.newBudgetCode).select2({
              placeholder: "New code or lookup  code",
              allowClear: true,
              tags: true,
              tokenSeparators: [',', ' '],
              minimumInputLength: 1,
              ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                url: s.url.ajaxLookupURL,
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
            $(s.ct.newBudgetCode).on("select2-selecting", function(e) {
                var presentSelections = $(s.ct.newBudgetCode).select2('data');
                var current           = e.choice.text.trim().toLowerCase();
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

            //On user selection, saves previous and new selection, if there's an error it will reset to previous
            //'create' is appended to a new input (e.g see createSearchChoice)
            s.mybody.on("select2-selecting",s.ft.filterModSelect2a, function(e) {
                 var element = $(this);
                 var currentText = "";
                 var rel = "";
                 var prevSelection = element.select2("data");

                 if(e.choice.id.split(':')[1] == 'create')
                 {
                    rel         = element.data('domain') + ':create';
                    currentText = e.choice.text.trim().toLowerCase().substring(9);
                 }
                 else {
                    rel         = e.choice.id;
                    currentText = e.choice.text.trim().toLowerCase();
                 }

                 $.ajax({
                        method: "POST",
                        url: s.url.ajaxFinanceRefData,
                        data: {
                            owner:element.data('owner')+':'+element.data('ownerid'), //org.kbplus.CostItem:1
                            ownerField: element.data("ownerfield"), //order
                            relation: rel,  //org.kbplus.Order:100
                            relationField: element.data('relationfield'), //orderNumber
                            val:currentText,         //123456
                            shortcode:element.data('shortcode')
                        },
                        global: false
                 })
                .fail(function( jqXHR, textStatus, errorThrown ) {
                     alert('Reset back to the original value, there was an error trying to save the data');
                     element.select2('data', prevSelection ? prevSelection : '');
                 })
                .done(function(data) {
                    if(data.error.length > 0)
                        element.select2('data', prevSelection);
                    else
                    {
                        element.data('previous',prevSelection ? prevSelection.id+'_'+prevSelection.text : '');
                        element.data('defaultvalue',e.choice.text);
                        element.data('relationid',data.relation.id);
                    }
                });
            });

            s.mybody.on("select2-selecting",s.ft.filterModSelect2b, function(e) {
                 var element       = $(this);
                 var prevSelection = element.select2("data");
                 var rel           = e.choice.id;
                 var currentText   = e.choice.text;
                 var isSubPkg      = element.data().issubpkg; //bool
                 var mode          = element.data().mode;
                 var id            = '#'+element.data().ownerid;
                 var resetMode     = false;

                 if(mode == 'sub')
                 {
                    var ie     = $(id+'_ie');
                    var subPkg = $(id+'_subPkg');
                    var fields = null;
                    console.log(ie, ie.data().defaultValue,ie.data(), ie.select2('data'),ie.data(),subPkg, subPkg.data().defaultValue, subPkg.data(), subPkg.select2('data'));
                    if(ie.val() != null)
                        fields = "issueEntitlement";
                    if(subPkg.val() != null )
                        fields += ",subPkg";

                    if(fields != null)
                    {
                        console.log('Sub selection means data needs to be reset for: '+fields);
                        resetMode = true;
                    }
                 }


                 $.ajax({
                        method: "POST",
                        url: s.url.ajaxFinanceRefData,
                        data: {
                            resetMode: resetMode,
                            fields: fields,
                            owner:element.data('owner')+':'+element.data('ownerid'), //org.kbplus.CostItem:1
                            ownerField: element.data("ownerfield"), //order
                            relation: rel,  //org.kbplus.Order:100
                            relationField: element.data('relationfield'), //orderNumber
                            shortcode:element.data('shortcode'),
                            subFilter:element.data('subfilter')
                        },
                        global: false
                 })
                .fail(function( jqXHR, textStatus, errorThrown ) {
                     alert('Reset back to the original value, there was an error trying to save the data');
                     element.select2('data', prevSelection ? prevSelection : '');
                 })
                .done(function(data) {
                    if(data.error.length > 0)
                    {
                        var errorMsg = "Cost Item Change Error\n";
                        for(var i = 0; i < data.error.length; i++) {
                          errorMsg += (data.error[i].msg + '\n');
                        }
                        alert(errorMsg);
                        element.select2('data', prevSelection);
                    }
                    else {
                        element.data('previous',prevSelection ? prevSelection.id+'_'+prevSelection.text : '');
                        element.data('defaultvalue',e.choice.text);
                        element.data('relationid',data.relation.id);

                        console.log("Relation ID: "+data.relation.id,"Choice Text: "+e.choice.text, "Prev Selection: ",prevSelection);

                        //set the subFilter i.e. the Sub ID
                        switch (mode)
                        {
                            case "ie":
                                console.log('IE was changed...');
                            break;

                            case "pkg":
                                console.log('Sub Pkg...');
                            break;

                            case "sub":
                                console.log('Sub was changed...');
                                $(id+'_subPkg').data('subfilter',data.relation.id);
                                $(id+'_ie').data('subfilter',currentText);
                            break;

                            default:
                                console.log('unknown behaviour!');
                            break;
                        }
                    }
                });
            });


            //filterSubscription select2 selection event
            s.mybody.on("select2-selecting", s.ft.filterSubscription, function(e) {
                 var element = $(this);
                 var text    = e.choice.text;
                 var id      = e.choice.id;
                 console.log('Chosen ID is as follows',id);
                 element.data('subFilter',id);
                 var prevSelection = element.select2("data");
                 $(s.ft.filterSubPkg).select2('enable');
                 console.log('filter subscription selection : ',text, id, '\nPrevious selection : ',prevSelection);
            });

            //filterSubscription select2 clear down event
            s.mybody.on("select2-removed", s.ft.filterSubscription, function(e) {
                 var pkgFilter = $(s.ft.filterSubPkg);
                 pkgFilter.select2('disable');
                 pkgFilter.select2('data',null);
                 console.log('filter sub search has been cleared... removed ability to enter sub pkg without subscription');
            });


            $('#filterMode').val("${filterMode}"); //default the filtering mode
            $(s.ft.searchBtn).prop('disabled',${filterMode=='OFF'}); //greys out search button if inactive

            s.mybody.on('click',s.ft.delSelectAll, function(event) {
                var isChecked = this.checked? true : false;
                $(s.ft.delCheckboxes).each(function() {
                    this.checked = isChecked;
                });
            }); //delete button select all functionality

            _performCostItemUpdate(null); //pulls down latest cost item updates for modal

            s.options.timeoutRecent = setInterval(_recentCostItems,60 * 1000 * 5); // Recently updated code block

            s.mybody.on('click',s.ft.delBatch,_performBulkDelete); //Bulk delete action

            s.mybody.on('keyup change','.filterUpdated',_filtersUpdated); //Change & keyup on input event

            //s.mybody.on('change',s.ft.filterSubscription + ', ' + s.ct.newSubscription, filterSubUpdated); //on change of subscription select filter

            s.mybody.on('change',s.ft.filterOpt, _filterSelection); //on change of subscription select filter

            s.mybody.on('click', s.ft.codeDelete, _removeBudgetCode); //attach delete functionality for budget codes

            s.mybody.on('click', '#addNew, #top', _scrollTo); //attach auto scrolling functionality

            $('input[name=_wildcard]').val("${wildcard?'on':'off'}"); //silly checkbox issues... default behaviour 1st run
            s.mybody.on('change', '#wildcard', function(event) {
                var checkbox = $(this);
                checkbox.is(":checked")? checkbox.prev().val('on') : checkbox.prev().val('off'); //Grails bug with checkbox
            });

            //Attach a dynamic message to the sortable criteria
            s.mybody.on('mouseenter','a.sortable', function() {
                var sortLink = $(this); //.hasClass('sorted')
                var title    = $('#paginateInfo').data('filtermode')=="OFF"?' filtering is inactive':' you can also sort by your active filter';
                sortLink.prop('title', (sortLink.hasClass('sorted')?'Presently sorting':'Choose to sort') + ' via '+sortLink[0].firstChild.textContent+'\n'+title);
            });

            //Dynamically attach popover to code edit
            //todo check efficiency
            //todo bind select2
            s.mybody.on('click', s.ft.codeEdit, function(event) {
                event.preventDefault();
                $(this).popover({
                     trigger: 'manual',
                     html:true,
                     placement:'top',
                     title:'Add to codes...',
                     template: "<div class='popover' style='width: 600px;'><div></div><div class='popover-inner'><h3 class='popover-title'></h3><div class='popover-content'></div>"+ $(this).text() +"</div></div>",
                     'max-width':600,
                     content:function() {
                        //return getContent();
                        return 'Addition of more codes!'
                     }
                });

                if($(this).hasClass('pop')) {
                    $(this).popover('hide').removeClass('pop');
                } else {
                    $(this).popover('show').addClass('pop');
                }
            });

            s.mybody.on('click','.sortable', function(event){
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
                    max:paginateData.max,
                    wildcard:paginateData.wildcard
                };
                console.log("Sorting/Ordering Info", "SELECTED",selected, "ORDER",order, "PAGINATE DATA",paginateData, "PARAM DATA SENDING", data);

                var formData = null;
                if(paginateData.filtermode == "ON")
                {
                    var formData = {
                        subscriptionFilter:paginateData.subscriptionfilter,
                        packageFilter:paginateData.packagefilter,
                        invoiceNumberFilter:paginateData.invoicenumberfilter,
                        orderNumberFilter:paginateData.ordernumberfilter,
                        resetMode:paginateData.resetMode?paginateData.resetMode:'search'
                    }
                }
                data = (formData!=null)?$.param(formData) + '&' + $.param(data):$.param(data);

                $.ajax({
                    method: "POST",
                    url: s.url.ajaxFinanceIndex,
                    data: data
                })
                .fail(function( jqXHR, textStatus, errorThrown ) {
                     errorHandling(textStatus,'Sorting (via '+ selected +')',errorThrown);
                  })
                .done(function(data) {
                     $(s.ft.filterTemplate).html(data);
                     _bindBehavior();
                });
                return false;
            });

            //Filter Search using form in header
            s.mybody.on('click',s.ft.searchBtn, _submitFilterSearch);

            //calculation for billed amount
            s.mybody.on('change','input.calc', function() {
                var billed   = $('#newCostInBillingCurrency');
                var exchange = $('#newCostExchangeRate');
                var local    = $('#newCostInLocalCurrency');
                
                var billedAmount = billed.val().length > 0? (parseFloat(billed.val()).toFixed(2)) : 0.00;
                var exchangeAmount = exchange.val().length > 0? parseFloat(exchange.val()) : 1;
                var localAmount = local.val().length > 0? parseFloat(local.val()) : 0.00;

                billed.val(billedAmount);
                local.val((billedAmount * exchangeAmount).toFixed(2));
            });

            //export
            s.mybody.on('click',s.misc.exportLink, function(e) {
                var exportMode   = $(this).data('mode');
                var paginateData = $('#paginateInfo').data();
                var totalResults = parseInt(paginateData.total);
            
                var data  = {
                    shortcode: "${params.shortcode}",
                    filterMode: paginateData.filtermode,
                    wildcard:paginateData.wildcard,
                    opSort:false,
                    sort:paginateData.sort,
                    order: paginateData.order,
                    offset:0,
                    estTotal:totalResults,
                    format:'csv',
                    csvMode:exportMode
                };
                
                if(paginateData.filtermode == "ON")
                {
                    $.extend(data, {
                        subscriptionFilter:paginateData.subscriptionfilter,
                        packageFilter:paginateData.packagefilter,
                        invoiceNumberFilter:paginateData.invoicenumberfilter,
                        orderNumberFilter:paginateData.ordernumberfilter,
                        resetMode:paginateData.resetMode?paginateData.resetMode:'search'
                    });
                }

                //Non-AJAX method
                //window.location = "${createLink(controller:'finance', action:'financialsExport')}?" + $.param(data);

                //Hacky AJAX method, could be more informative for user, but encodeURIComponent, not sure will stand large data :/
                $.ajax({
                    method: "POST",
                    url: s.url.ajaxFinanceExport,
                    data: data
                })
                .fail(function( jqXHR, textStatus, errorThrown ) {
                     errorHandling(textStatus,'Export CSV',errorThrown);
                  })
                .done(function(data) {
                     csvData = 'data:text/csv;charset=utf-8,' + encodeURIComponent(data);
                     window.open(csvData, 'Finance Data');
                });
                return false; //stop standard link behaviour
            });


            //End of firstRun...
        };











        /**
        * Publicly returned interface methods (revealing module pattern)
        * (Full scope access, including 'this' entire annon func (private var & method access)
        *
        * @method init - Binds first run behaviour and AJAX driven plugins/etc
        *
        * @method rebind - rebind is for AJAX driven plugins/etc
        */
        return {
            init: function() {
                console.log('Running initialising method for Finance module');
                _firstRun();
                _bindBehavior();
                console.log('Finished initialising method for Finance module');
            },

            rebind: function() {
                console.log('Running rebind method designed for AJAX');
                _bindBehavior();
                console.log('Finished rebind method designed for AJAX');
            },

            //TEMP ACCESSIBLE METHODS
            filterValidation: filterValidation,
            scrollTo: _scrollTo,
            updateResults: _updateResults
        };
        //End of public returned methods...

    })();

    Finance.init();


        //////////////////////////////////////////////TO REFACTOR//////////////////////////////////////////////

        function filterSelection() {
            if($('#submitFilterMode').attr("value")!="reset")
            {
                var newMode       = $('#filterMode');
                var disabledState = (newMode.val() == "ON")? false:true; //Need to turn off, i.e. refresh result
                $('#submitFilterMode').prop('disabled',disabledState);
            }
        }


        function userInfo(status,message,timeout) {
            var html = "";
            $.each(message.split(",,,"), function( i, val ) {
               html += ('<tr><td>'+status+'</td><td>'+val+'</td></tr>');
            });
            var errorDisplay = $('#userError');
            errorDisplay.stop(true,true);
            errorDisplay.find('tbody:last').html(html);
            Finance.scrollTo(null,"#userError");
            errorDisplay.fadeToggle(1000).delay(timeout!=null?timeout:3000).slideUp(4000, function(){
                $("#userError > table > tbody").children().remove();
            });
        }

        function fadeAway(id,time) {
             $('#'+id).fadeIn(2000).delay(time).slideUp(4000, function(){
                $('#'+id).remove();
             });
        }

        function confirmSubmit(msg) {
          return confirm("Are you sure you wish to continue?\n\n"+msg);
        }

        //////////////////////////////////////////////TO REFACTOR//////////////////////////////////////////////


        //////////////////////////////////////////////LEAVE FOR NOW//////////////////////////////////////////////
        function quickHelpInfo() {
            userInfo("Help","<b>Sorting</b> via clickable title links of the following : Cost Item#, Invoice#, Order#, Subscription, Package, date, IE ,,, " +
             "<b>Filter Search</b> via the 4 input fields : Invoice#, Order#, Subscription, and Package, selecting filter mode as ON and submitting the search. On finishing with your results reset via the 'reset' button,,," +
              "<b>Pagination</b> (<i>Results Navigation</i>) via the clickable links, below the results table,,," +
               "<b>Deleting Costs</b> via checking the boxes attached to each cost item row individually or all and submitting by clicking 'remove selected,,," +
                "<b>Add New Costs</b> via creation screen, after results, shortcut button select 'Add New Cost'",20000)
        }


    //error/info code block
        var errorHandling = function(status,actionFailed,reason) {
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
                    console.log(actionFailed + ' failed due to: ' + reason?reason:status + '... Report this error!');
                    break;
                }
                default :
                {
                    console.log('Problem occurred with action: '+ actionFailed +', needs investigation! Possibly due to: '+reason!='undefined'?reason:'Unknown');
                    break;
                }
            }
        };

    //////////////////////////////////////////////LEAVE FOR NOW//////////////////////////////////////////////

</r:script>
</html>

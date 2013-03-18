<%@ page import="com.k_int.kbplus.Subscription" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>

    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Details</g:link> </li>
        <li class="pull-right">
          <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}" params="${[format:'csv',sort:params.sort,order:params.order,filter:params.filter]}">CSV Export</g:link> 
          <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}" params="${[format:'csv',sort:params.sort,order:params.order,filter:params.filter,omitHeader:'Y']}">(No header)</g:link></li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

    <g:if test="${flash.message}">
      <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
    </g:if>

    <g:if test="${flash.error}">
      <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
    </g:if>

    <div class="container">

      ${institution?.name} ${subscriptionInstance?.type?.value}

       <h1><g:inPlaceEdit domain="Subscription" 
                          pk="${subscriptionInstance.id}" 
                          field="name" 
                          id="name" 
                          class="${editable?'newipe':''}">${subscriptionInstance?.name}</g:inPlaceEdit></h1>

      <g:render template="nav" contextPath="." />
    </div>


    <g:if test="${subscriptionInstance.pendingChanges?.size() > 0}">
      <div class="container alert-warn">
        <h6>This Subscription has pending change notifications</h6>
        <table class="table table-bordered">
          <thead>
            <tr>
              <td>Field</td>
              <td>Has changed to</td>
              <td>Reason</td>
              <td>Actions</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${subscriptionInstance.pendingChanges}" var="pc">
              <tr>
                <td style="white-space:nowrap;">${pc.updateProperty}</td>
                <td>${pc.updateValue}</td>
                <td>${pc.updateReason}</td>
                <td>
                  <g:link controller="subscriptionDetails" action="acceptChange" id="${params.id}" params="${[changeid:pc.id]}" class="btn btn-primary">Accept</g:link>
                  <g:link controller="subscriptionDetails" action="rejectChange" id="${params.id}" params="${[changeid:pc.id]}" class="btn btn-primary">Reject</g:link>
                </td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
    </g:if>


    <div class="container">
      <button class="hidden-license-details btn" data-toggle="collapse" data-target="#collapseableSubDetails">Show/Hide Subscription Details <i class="icon-minus"></i></button>
    </div>


    <div id="collapseableSubDetails" class="container collapse in">
      <div class="row">
        <div class="span8"> 
            <br/>
            <h6>Subscription Information</h6>
            <div class="inline-lists"> 
                <dl>
                    <dt>License</dt>
                    <dd><g:if test="${subscriptionInstance.subscriber}">
                           <g:relation domain='Subscription' 
                            pk='${subscriptionInstance.id}' 
                            field='owner' 
                            class='reldataedit'
                            id='ownerLicense'>${subscriptionInstance?.owner?.reference}</g:relation></g:if><g:else>N/A (Subscription offered)</g:else>
                    </dd>
                </dl>

                <dl>
                    <dt>Package Name</dt>
                    <dd>
                        <g:each in="${subscriptionInstance.packages}" var="sp">${sp.pkg.name} (${sp.pkg?.contentProvider?.name}) <br/></g:each>
                    </dd>
                </dl>

                <dl>
                  <dt>Public?</dt>
                  <dd><g:refdataValue val="${subscriptionInstance.isPublic?.value}" domain="Subscription" pk="${subscriptionInstance.id}" field="isPublic" cat='YN' class="${editable?'ynrefdataedit':''}"/></dd>
                </dl> 

                <dl><dt>Consortia</dt><dd>${subscriptionInstance.getConsortia()?.name}<br/></dd></dl>

                <dl><dt>Start Date</dt><dd><span><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${subscriptionInstance.startDate}"/></span>
                <input id="Subscription:${subscriptionInstance.id}:startDate" type="hidden" class="${editable?'dp1':''}" />
                </dd>
                </dl>

               <dl>
                    <dt>End Date</dt>
                    <dd><span><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${subscriptionInstance.endDate}"/></span>
                        <input id="Subscription:${subscriptionInstance.id}:endDate" type="hidden" class="${editable?'dp1':''}" />
                    </dd>
               </dl>

               <dl>
                    <dt>Nominal Platform(s)</dt>
                    <dd> 
                    <g:each in="${subscriptionInstance.packages}" var="sp">
                        ${sp.pkg?.nominalPlatform?.name}<br/>
                    </g:each>
                    </dd>
                </dl>

                <dl>
                      <dt><label class="control-label" for="licenseeRef">Org Links</label></dt>
                      <dd>
                        <g:render template="orgLinks" contextPath="../templates" model="${[roleLinks:subscriptionInstance?.orgRelations,editmode:editable]}" />
                      </dd>
                </dl>

                <div class="clear-fix"></div>
            </div>
        </div>

        <div class="span4">
          <g:render template="documents" contextPath="../templates" model="${[doclist:subscriptionInstance.documents, ownobj:subscriptionInstance, owntp:'subscription']}" />
          <g:render template="notes" contextPath="../templates" model="${[doclist:subscriptionInstance.documents, ownobj:subscriptionInstance, owntp:'subscription']}" />
        </div>
      </div>
    </div>

    <div class="container">
      <dl>
        <dt>Entitlements ( ${offset+1} to ${offset+(entitlements?.size())} of ${num_sub_rows} )
          <g:form action="index" params="${params}" method="get" class="form-inline">
             <input type="hidden" name="sort" value="${params.sort}">
             <input type="hidden" name="order" value="${params.order}">
             <label>Filter:</label> <input name="filter" value="${params.filter}"/>
             <input type="submit" class="btn btn-primary" />
          </g:form>
        </dt>
        <dd>
          <g:form action="subscriptionBatchUpdate" params="${[id:subscriptionInstance?.id]}" class="form-inline">
          <g:set var="counter" value="${offset+1}" />
          <table  class="table table-striped table-bordered">

            <tr>
              <th></th>
              <th>#</th>
              <g:sortableColumn params="${params}" property="tipp.title.title" title="Title" />
              <th>ISSN</th>
              <th>eISSN</th>
              <g:sortableColumn params="${params}" property="coreStatus" title="Core Status" />
              <g:sortableColumn params="${params}" property="startDate" title="Start Date" />
              <g:sortableColumn params="${params}" property="endDate" title="End Date" />
              <g:sortableColumn params="${params}" property="coreStatusStart" title="Core Start Date" />
              <g:sortableColumn params="${params}" property="coreStatusEnd" title="Core End Date" />
              <th>Actions</th>
            </tr>  

            <tr class="no-background">  
              <th>
                <g:if test="${editable}"><input type="checkbox" name="chkall" onClick="javascript:selectAll();"/></g:if>
              </th>
              <th colspan="4">
                <g:if test="${editable}">
                  <select id="bulkOperationSelect" name="bulkOperation">
                    <option value="edit">Edit Selected</option>
                    <option value="remove">Remove Selected</option>
                  </select>
                  <input type="Submit" value="Apply Batch Changes" class="btn btn-primary" onClick="return confirmSubmit()"/></g:if></th>
              <th><span id="entitlementBatchEdit" class="${editable?'entitlementBatchEdit isedit':''}"></span><input type="hidden" name="bulk_core" id="bulk_core"/></th>
              <th><g:if test="${editable}"><span class="datevalue">edit</span> <input name="bulk_start_date" type="hidden" class="${editable?'hdp':''}" /></g:if></th>
              <th><g:if test="${editable}"><span class="datevalue">edit</span> <input name="bulk_end_date" type="hidden" class="${editable?'hdp':''}" /></g:if></th>
              <th colspan="4"></th>
            </tr>

          <g:if test="${entitlements}">
            <g:each in="${entitlements}" var="ie">
              <tr>
                <td><g:if test="${editable}"><input type="checkbox" name="_bulkflag.${ie.id}" class="bulkcheck"/></g:if></td>
                <td>${counter++}</td>
                <td>
                  <g:link controller="issueEntitlement" id="${ie.id}" action="show">${ie.tipp.title.title}</g:link>
                  <g:if test="${ie.tipp?.hostPlatformURL}">( <a href="${ie.tipp?.hostPlatformURL}" TITLE="${ie.tipp?.hostPlatformURL}">Host Link</a> )</g:if>
                </td>
                <td>${ie?.tipp?.title?.getIdentifierValue('ISSN')}</td>
                <td>${ie?.tipp?.title?.getIdentifierValue('eISSN')}</td>
                <td><g:refdataValue val="${ie.coreStatus?.value}" 
                                    domain="IssueEntitlement" 
                                    pk="${ie.id}" 
                                    field="coreStatus" 
                                    cat="CoreStatus"
                                    class="${editable?'corestatusedit':''}"/></td>
                <td>
                    <span class="datevalue"><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.startDate}"/></span>
                    <input id="IssueEntitlement:${ie.id}:startDate" type="hidden" class="${editable?'dp1':''}" />
                </td>
                <td><span class="datevalue"><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.endDate}"/></span>
                    <input id="IssueEntitlement:${ie.id}:endDate" type="hidden" class="${editable?'dp1':''}" />
                </td>
                <td><span class="datevalue"><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.coreStatusStart}"/></span>
                    <input id="IssueEntitlement:${ie.id}:coreStatusStart" type="hidden" class="${editable?'dp1':''}" />
                </td>
                <td><span class="datevalue"><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.coreStatusEnd}"/></span>
                    <input id="IssueEntitlement:${ie.id}:coreStatusEnd" type="hidden" class="${editable?'dp1':''}" />
                </td>
                <td>
                  <g:if test="${editable}"><g:link action="removeEntitlement" params="${[ieid:ie.id, sub:subscriptionInstance.id]}" onClick="return confirm('Are you sure you wish to delete this entitlement');">Delete</g:link></g:if>
                  <g:if test="${institutional_usage_identifier}">
                    <g:if test="${ie?.tipp?.title?.getIdentifierValue('ISSN')}">
                      | <a href="https://www.jusp.mimas.ac.uk/secure/v2/ijsu/?id=${institutional_usage_identifier.value}&issn=${ie?.tipp?.title?.getIdentifierValue('ISSN')}">ISSN Usage</a>
                    </g:if>
                    <g:if test="${ie?.tipp?.title?.getIdentifierValue('eISSN')}">
                      | <a href="https://www.jusp.mimas.ac.uk/secure/v2/ijsu/?id=${institutional_usage_identifier.value}&issn=${ie?.tipp?.title?.getIdentifierValue('eISSN')}">eISSN Usage</a>
                    </g:if>
                  </g:if>
                </td>
              </tr>
            </g:each>
          </g:if>
          </table>
          </g:form>
        </dd>
      </dl>

      <div class="pagination" style="text-align:center">
        <g:if test="${entitlements}" >
          <bootstrap:paginate  action="index" controller="subscriptionDetails" params="${params}" next="Next" prev="Prev" maxsteps="${max}" total="${num_sub_rows}" />
        </g:if>
      </div>
    </div>

    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[roleLinks:subscriptionInstance?.orgRelations,parent:subscriptionInstance.class.name+':'+subscriptionInstance.id,property:'orgs',recip_prop:'sub']}" />

    <script language="JavaScript">
      <g:if test="${editable}">
      $(document).ready(function() {

        var datepicker_config = {
          buttonImage: '../../images/calendar.gif',
          buttonImageOnly: true,
          changeMonth: true,
          changeYear: true,
          showOn: 'both',
          showButtonPanel: true,
          showClearButton: true,
          clearText: "Clear",
          onSelect: function(dateText, inst) { 
            var elem_id = inst.input[0].id;
            $.ajax({url: '<g:createLink controller="ajax" action="genericSetValue"/>?elementid='+
                             elem_id+'&value='+dateText+'&dt=date&idf=MM/dd/yyyy&odf=${session.sessionPreferences?.globalDateFormat}',
                   success: function(result){inst.input.parent().find('span').html(result)}
                   });
          },
          beforeShow: function( input ) {
            setTimeout(function() {
                var buttonPane = $( input )
                    .datepicker( "widget" )
                    .find( ".ui-datepicker-buttonpane" );
    
                $( "<button/>", {
                    text: "Clear",
                    click: function() {
                      // var parent=$(this).parent('.dp1')
                      console.log("%o",input)
                      $(input).parent().find('span.datevalue').html("")
                      $.ajax({url: '<g:createLink controller="ajax" action="genericSetValue"/>?elementid='+input.id+'&value=__NULL__',});
                      $(input).value="__NULL__"
                    }
                }).appendTo( buttonPane ).addClass("ui-datepicker-clear ui-state-default ui-priority-primary ui-corner-all");
            }, 1 );
          }
        };

        $("input.dp1").datepicker(datepicker_config);

        $("input.hdp").datepicker({
          buttonImage: '../../images/calendar.gif',
          buttonImageOnly: true,
          changeMonth: true,
          changeYear: true,
          showOn: 'both',
          showButtonPanel: true,
          showClearButton: true,
          onSelect: function(dateText, inst) {
            inst.input.parent().find('span').html(dateText)
          }
        });

        $('span.fieldNote').editable('<g:createLink controller="ajax" action="genericSetValue"/>', {
          type      : 'textarea',
          cancel    : 'Cancel',
          submit    : 'OK',
          id        : 'elementid',
          rows      : 3,
          tooltip   : 'Click to edit...',
          onblur    : 'ignore'
        });

        $('.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...',
           onblur        : 'ignore'
         });

        $('span.entitlementBatchEdit').editable(function(value, settings) { 
          $("#bulk_core").val(value);
          return(value);          
        },{ data:{'Yes':'Yes',
                  'No':'No',
                  'Print':'Print',
                  'Electronic':'Electronic',
                  'Print+Electronic':'Print+Electronic'}, type:'select',cancel:'Cancel',submit:'OK', rows:3, tooltop:'Click to edit...', width:'100px', onblur:'ignore'});

        $('span.embargoBatchEdit').editable(function(value, settings) { 
          $("#bulk_embargo").val(value);
          return(value);
        },{type:'textarea',cancel:'Cancel',submit:'OK', rows:3, tooltop:'Click to edit...', width:'100px', onblur:'ignore'});

        $('span.coverageBatchEdit').editable(function(value, settings) { 
          $("#bulk_coverage").val(value);
          return(value);
        },{type:'textarea',cancel:'Cancel',submit:'OK', rows:3, tooltop:'Click to edit...', width:'100px', onblur:'ignore'});

        $('td span.cuedit').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
           data   : {'true':'true', 'false':'false'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...'
         });

         $('.ynrefdataedit').editable('<g:createLink controller="ajax" action="genericSetRef" />', {
           data   : {'Yes':'Yes', 'No':'No'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           onblur        : 'ignore',
           callback : function(value) {
               var iconList = {
                   'Yes' : 'greenTick',
                   'No' : 'redCross'
               };

               var icon = $(document.createElement('span'));
               $(this).prepend(icon.addClass('select-icon').addClass(iconList[value]));
           }
         });

         $('dd span.reldataedit').editable('<g:createLink controller="ajax" params="${[resultProp:'reference']}" action="genericSetRel" />', {
           loadurl: '<g:createLink controller="MyInstitutions" params="${[shortcode:subscriptionInstance.subscriber?.shortcode]}" action="availableLicenses" />',
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
           }
         });

         $('.corestatusedit').editable('<g:createLink controller="ajax" params="${[resultProp:'value']}" action="genericSetRel" />', {
           loadurl: '<g:createLink controller="ajax" params="${[id:'CoreStatus',format:'json']}" action="refdataSearch" />',
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
           }
         });

         var checkEmptyEditable = function() {
           $('.ipe, .ynrefdataedit, .fieldNote, .isedit').each(function() {
             if($(this).text().length == 0) {
               $(this).addClass('editableEmpty');
             } else {
               $(this).removeClass('editableEmpty');
             }
           });
         }

         // On jEditable click remove the hide the icon and show it 
         // when one of the buttons are clicked or ESC is hit.
         $('.ipe, .intedit, .ynrefdataedit, .cuedit, .fieldNote, .newipe, .isedit').click(function() {
            // Ensure we're not clicking in an editing element.
            if($(this).hasClass('clicked')) {
                return;
            }
            
         	// Hide edit icon with overwriting style.
         	$(this).addClass('clicked');
            
            var e = $(this);
            
            var outsideElements;
                        
            setTimeout(function() {
                outsideElements = e.parent().find("span:not(.clicked)");
                console.log(outsideElements);
                outsideElements.hide();
            }, 1);
         	
         	var removeClicked = function() {
         		setTimeout(function() {
         			e.removeClass('clicked');
         			if(outsideElements) {
         				outsideElements.show();
         			}
         		}, 1);
         	}
         	
         	setTimeout(function() {
         		e.find('form button').click(function() {
         			removeClicked();
         		});
         		e.keydown(function(event) {
         			if(event.keyCode == 27) {
         				removeClicked();
         			}
         		});
         	}, 1);
         });

        $(".announce").click(function(){
           var id = $(this).data('id');
           $('#modalComments').load('<g:createLink controller="alert" action="commentsFragment" />/'+id);
           $('#modalComments').modal('show');
         });

         
         $('#collapseableSubDetails').on('show', function() {
            $('.hidden-license-details i').removeClass('icon-plus').addClass('icon-minus');
        });

        // Reverse it for hide:
        $('#collapseableSubDetails').on('hide', function() {
            $('.hidden-license-details i').removeClass('icon-minus').addClass('icon-plus');
        });

      });

      function selectAll() {
        $('.bulkcheck').attr('checked', true);
      }

      function confirmSubmit() {
        if ( $('#bulkOperationSelect').val() === 'remove' ) {
          var agree=confirm("Are you sure you wish to continue?");
          if (agree)
            return true ;
          else
            return false ;
        }
      }
      </g:if>
      <g:else>
        $(document).ready(function() {
          $(".announce").click(function(){
            var id = $(this).data('id');
            $('#modalComments').load('<g:createLink controller="alert" action="commentsFragment" />/'+id);
            $('#modalComments').modal('show');
          });
        }
      </g:else>
    </script>
  </body>
</html>

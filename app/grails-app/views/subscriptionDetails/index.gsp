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

      <ul class="nav nav-pills">
        <li class="active"><g:link controller="subscriptionDetails" 
                                   action="index" 
                                   params="${[id:params.id]}">Current Entitlements</g:link></li>

        <g:if test="${editable}">
          <li><g:link controller="subscriptionDetails" 
                      action="addEntitlements" 
                      params="${[id:params.id]}">Add Entitlements</g:link></li>
        </g:if>

        <li><g:link controller="subscriptionDetails" 
                    action="documents" 
                    params="${[id:params.id]}">Documents</g:link></li>

        <li><g:link controller="subscriptionDetails" 
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>

      </ul>


      <button class="hidden-license-details btn" data-toggle="collapse" data-target="#collapseableSubDetails">Show/Hide Subscription Details <i class="icon-plus"></i></button>
    </div>

    <div id="collapseableSubDetails" class="container collapse">
      <div class="row">
        <div class="span8"> 
            <br/>
            <h6>Subscription Information</h6>
            <div class="licence-info"> 
                <dl>
                    <dt>License</dt>
                    <dd><g:relation domain='Subscription' 
                            pk='${subscriptionInstance.id}' 
                            field='owner' 
                            class='refdataedit'
                            id='ownerLicense'>${subscriptionInstance?.owner?.reference}</g:relation></dd>
                </dl>
                <dl>
                    <dt>Package Name</dt>
                    <dd>
                        <g:each in="${subscriptionInstance.packages}" var="sp">
                            ${sp.pkg.name} (${sp.pkg?.contentProvider?.name}) <br/>
                        </g:each>
                    </dd>
                </dl>
                <dl>
                    <dt>Vendor</dt>
                    <dd><g:relationAutocomplete field="vendor" relatedClass="Org" typedownField="name" displayField="name"/></dd>
                </dl>
                <dl>
                    <dt>Consortia</dt>
                    <dd>${subscriptionInstance.getConsortia()?.name}</dd>
                </dl>
               <dl>
                    <dt>Start Date</dt>
                    <dd><span><g:formatDate format="dd MMMM yyyy" date="${subscriptionInstance.startDate}"/></span>
                        <input id="Subscription:${subscriptionInstance.id}:startDate" type="hidden" class="${editable?'dp1':''}" />
                    </dd>
               </dl>
               <dl>
                    <dt>End Date</dt>
                    <dd><span><g:formatDate format="dd MMMM yyyy" date="${subscriptionInstance.endDate}"/></span>
                        <input id="Subscription:${subscriptionInstance.id}:endDate" type="hidden" class="${editable?'dp1':''}" />
                    </dd>
               </dl>
               <dl>
                    <dt>Nominal Platform</dt>
                    <dd> 
                    <g:each in="${subscriptionInstance.packages}" var="sp">
                        ${sp.pkg?.nominalPlatform?.name}<br/>
                    </g:each>
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
          <g:form action="index" params="${params}" method="get">
             <input type="hidden" name="sort" value="${params.sort}">
             <input type="hidden" name="order" value="${params.order}">
             Filter: <input name="filter" value="${params.filter}"/><input type="submit">
          </g:form>
        </dt>
        <dd>
          <g:form action="subscriptionBatchUpdate" params="${[id:subscriptionInstance?.id]}">
          <g:set var="counter" value="${offset+1}" />
          <table  class="table table-striped table-bordered">
            <tr>
              <th></th>
              <th>#</th>
              <g:sortableColumn params="${params}" property="tipp.title.title" title="Title" />
              <th>ISSN</th>
              <th>eISSN</th>
              <g:sortableColumn params="${params}" property="coreTitle" title="Core" />
              <g:sortableColumn params="${params}" property="startDate" title="Start Date" />
              <g:sortableColumn params="${params}" property="endDate" title="End Date" />
              <th>Embargo</th>
              <th>Coverage Depth</th>
              <th>Coverage Note</th>
              <th>Actions</th>
            </tr>  
            <tr>  
              <th>
                <g:if test="${editable}"><input type="checkbox" name="chkall" onClick="javascript:selectAll();"/></g:if>
              </th>
              <th colspan="4">
                <g:if test="${editable}">
                  <select id="bulkOperationSelect" name="bulkOperation">
                    <option value="edit">Edit Selected</option>
                    <option value="remove">Remove Selected</option>
                  </select>
                  <input type="Submit" value="Apply Batch Changes" onClick="return confirmSubmit()"/></g:if></th>
              <th><span id="entitlementBatchEdit" class="${editable?'entitlementBatchEdit isedit':''}"></span><input type="hidden" name="bulk_core" id="bulk_core"/></th>
              <th><g:if test="${editable}"><span class="datevalue">edit</span> <input name="bulk_start_date" type="hidden" class="${editable?'hdp':''}" /></g:if></th>
              <th><g:if test="${editable}"><span class="datevalue">edit</span> <input name="bulk_end_date" type="hidden" class="${editable?'hdp':''}" /></g:if></th>
              <th><span id="embargoBatchEdit" class="${editable?'embargoBatchEdit isedit':''}"></span><input type="hidden" name="bulk_embargo" id="bulk_embargo"></th>
              <th><span id="coverageBatchEdit" class="${editable?'coverageBatchEdit isedit':''}"></span><input type="hidden" name="bulk_coverage" id="bulk_coverage"></th>
              <th colspan="3"></th>
            </tr>
          <g:if test="${entitlements}">
            <g:each in="${entitlements}" var="ie">
              <tr>
                <td><g:if test="${editable}"><input type="checkbox" name="_bulkflag.${ie.id}" class="bulkcheck"/></g:if></td>
                <td>${counter++}</td>
                <td>
                  <g:if test="${ie.tipp?.hostPlatformURL}"><a href="${ie.tipp?.hostPlatformURL}" TITLE="${ie.tipp?.hostPlatformURL}">${ie.tipp.title.title}</a></g:if>
                  <g:else>${ie.tipp.title.title}</g:else>
                </td>
                <td>${ie?.tipp?.title?.getIdentifierValue('ISSN')}</td>
                <td>${ie?.tipp?.title?.getIdentifierValue('eISSN')}</td>
                <td><g:refdataValue val="${ie.coreTitle}" 
                                    domain="IssueEntitlement" 
                                    pk="${ie.id}" 
                                    field="coreTitle" 
                                    cat="isCoreTitle"
                                    class="${editable?'cuedit':''}"/></td>
                <td>
                    <span class="datevalue"><g:formatDate format="dd MMMM yyyy" date="${ie.startDate}"/></span>
                    <input id="IssueEntitlement:${ie.id}:startDate" type="hidden" class="${editable?'dp1':''}" />
                </td>
                <td><span class="datevalue"><g:formatDate format="dd MMMM yyyy" date="${ie.endDate}"/></span>
                    <input id="IssueEntitlement:${ie.id}:endDate" type="hidden" class="${editable?'dp1':''}" />
                </td>
                <td><g:inPlaceEdit domain="IssueEntitlement" pk="${ie.id}" field="embargo" id="embargo" class="${editable?'fieldNote':''}">${ie.embargo}</g:inPlaceEdit></td>
                <td><g:inPlaceEdit domain="IssueEntitlement" pk="${ie.id}" field="coverageDepth" id="coverageDepth" class="${editable?'fieldNote':''}">${ie.coverageDepth}</g:inPlaceEdit></td>
                <td>${ie.coverageNote}</td>  
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
        <dt>Org Links</td>
        <dd>
          <ul>
            <g:each in="${subscriptionInstance.orgRelations}" var="or">
              <li>${or.org.name}:${or.roleType?.value}</li>
            </g:each>
          <ul>
        </dd>
      </dl>

      <div class="pagination" style="text-align:center">
        <g:if test="${entitlements}" >
          <bootstrap:paginate  action="index" controller="subscriptionDetails" params="${params}" next="Next" prev="Prev" maxsteps="${max}" total="${num_sub_rows}" />
        </g:if>
      </div>
    </div>

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
                             elem_id+'&value='+dateText+'&dt=date&idf=MM/dd/yyyy&odf=dd MMMM yyyy',
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
        },{ data:{'true':'true',
                  'false':'false'}, type:'select',cancel:'Cancel',submit:'OK', rows:3, tooltop:'Click to edit...', width:'100px', onblur:'ignore'});

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

         $('dd span.refdataedit').editable('<g:createLink controller="ajax" params="${[resultProp:'reference']}" action="genericSetRel" />', {
           loadurl: '<g:createLink controller="MyInstitutions" params="${[shortcode:institution?.shortcode]}" action="availableLicenses" />',
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
           }
         });

         var checkEmptyEditable = function() {
           $('.ipe, .refdataedit, .fieldNote, .refdataedit, .isedit').each(function() {
             if($(this).text().length == 0) {
               $(this).addClass('editableEmpty');
             } else {
               $(this).removeClass('editableEmpty');
             }
           });
         }

         // On jEditable click remove the hide the icon and show it 
         // when one of the buttons are clicked or ESC is hit.
         $('.ipe, .intedit, .refdataedit, .cuedit, .fieldNote, .newipe, .isedit').click(function() {
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

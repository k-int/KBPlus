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
        <li> <g:link controller="home">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Details</g:link> </li>
        <li class="pull-right"><g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}" params="${[format:'csv',sort:params.sort,order:params.order,filter:params.filter]}">CSV Export</g:link></li>
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

      ${institution?.name} ${subscriptionInstance?.type?.value} Subscription Taken

       <h1><g:inPlaceEdit domain="Subscription" pk="${subscriptionInstance.id}" field="name" id="name" class="${editable?'newipe':''}">${subscriptionInstance?.name}</g:inPlaceEdit></h1>

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
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>

        <li><g:link controller="subscriptionDetails" 
                    action="documents" 
                    params="${[id:params.id]}">Documents</g:link></li>
      </ul>


      <button class="btn" data-toggle="collapse" data-target="#collapseableSubDetails">Show/Hide Details</button>
    </div>

    <div id="collapseableSubDetails" class="container collapse">
      <div class="row">
        <div class="span8">
          <dl>
            <dt>License</dt>
            <dd><g:relation domain='Subscription' 
                            pk='${subscriptionInstance.id}' 
                            field='owner' 
                            class='reldataEdit'
                            id='ownerLicense'>${subscriptionInstance?.owner?.reference}</g:relation></dd>
    
            <dt>Package Name</dt>
            <dd>
              <g:each in="${subscriptionInstance.packages}" var="sp">
                ${sp.pkg.name} (${sp.pkg?.contentProvider?.name}) <br/>
              </g:each>
            </dd>

            <dt>Vendor</dt>
            <dd><g:relationAutocomplete field="vendor" relatedClass="Org" typedownField="name" displayField="name"/></dd>
    
            <dt>Consortia</dt>
            <dd>${subscriptionInstance.getConsortia()?.name}</dd>
    
            <dt>Start Date</dt>
            <dd><g:formatDate format="dd MMMM yyyy" date="${subscriptionInstance.startDate}"/></dd>
    
            <dt>End Date</dt>
            <dd><g:formatDate format="dd MMMM yyyy" date="${subscriptionInstance.endDate}"/></dd>
    
            <dt>Nominal Platform</dt>
            <dd> 
              <g:each in="${subscriptionInstance.packages}" var="sp">
                ${sp.pkg?.nominalPlatform?.name}<br/>
              </g:each>
            </dd>
          </dl>
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
          <table  class="table table-striped table-bordered table-condensed">
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
              <th><span id="entitlementBatchEdit" class="${editable?'entitlementBatchEdit':''}"></span><input type="hidden" name="bulk_core" id="bulk_core"/></th>
              <th><g:if test="${editable}"><span>edit</span> <input name="bulk_start_date" type="hidden" class="${editable?'hdp':''}" /></g:if></th>
              <th><g:if test="${editable}"><span>edit</span> <input name="bulk_end_date" type="hidden" class="${editable?'hdp':''}" /></g:if></th>
              <th><span id="embargoBatchEdit" class="${editable?'embargoBatchEdit':''}"></span><input type="hidden" name="bulk_embargo" id="bulk_embargo"></th>
              <th><span id="coverageBatchEdit" class="${editable?'coverageBatchEdit':''}"></span><input type="hidden" name="bulk_coverage" id="bulk_coverage"></th>
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
                                    class="${editable?'coreedit':''}"/></td>
                <td>
                    <span><g:formatDate format="dd MMMM yyyy" date="${ie.startDate}"/></span>
                    <input id="IssueEntitlement:${ie.id}:startDate" type="hidden" class="${editable?'dp1':''}" />
                </td>
                <td><span><g:formatDate format="dd MMMM yyyy" date="${ie.endDate}"/></span>
                    <input id="IssueEntitlement:${ie.id}:endDate" type="hidden" class="${editable?'dp2':''}" />
                </td>
                <td><g:inPlaceEdit domain="IssueEntitlement" pk="${ie.id}" field="embargo" id="embargo" class="${editable?'newipe':''}">${ie.embargo}</g:inPlaceEdit></td>
                <td><g:inPlaceEdit domain="IssueEntitlement" pk="${ie.id}" field="coverageDepth" id="coverageDepth" class="${editable?'newipe':''}">${ie.coverageDepth}</g:inPlaceEdit></td>
                <td>${ie.coverageNote}</td>  
                <td><g:if test="${editable}"><g:link action="removeEntitlement" params="${[ieid:ie.id, sub:subscriptionInstance.id]}" onClick="return confirm('Are you sure you wish to delete this entitlement');">Delete</g:link></g:if></td>
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
    </div>

    <div class="paginateButtons" style="text-align:center">
      <g:if test="${entitlements}" >
        <span><g:paginate controller="subscriptionDetails" 
                          action="index" 
                          params="${params}" next="Next" prev="Prev" 
                          max="${max}" 
                          total="${num_sub_rows}" /></span>
      </g:if>
    </div>

    </div>
    <script language="JavaScript">
      $(document).ready(function() {

        var datepicker_config = {
          buttonImage: '../../images/calendar.gif',
          buttonImageOnly: true,
          changeMonth: true,
          changeYear: true,
          showOn: 'both',
          onSelect: function(dateText, inst) { 
            var elem_id = inst.input[0].id;
            $.ajax({url: '<g:createLink controller="ajax" action="genericSetValue" absolute="true"/>?elementid='+
                             elem_id+'&value='+dateText+'&dt=date&idf=MM/dd/yyyy&odf=dd MMMM yyyy',
                   success: function(result){inst.input.parent().find('span').html(result)}
                   });
          }
        };

        $("div dl dd table tr td input.dp1").datepicker(datepicker_config);
        $("div dl dd table tr td input.dp2").datepicker(datepicker_config);

        $("input.hdp").datepicker({
          buttonImage: '../../images/calendar.gif',
          buttonImageOnly: true,
          changeMonth: true,
          changeYear: true,
          showOn: 'both',
          onSelect: function(dateText, inst) {
            inst.input.parent().find('span').html(dateText)
          }
        });

        $('span.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" absolute="true"/>', {
          type      : 'textarea',
          cancel    : 'Cancel',
          submit    : 'OK',
          id        : 'elementid',
          rows      : 3,
          tooltip   : 'Click to edit...'
        });

        $('span.entitlementBatchEdit').editable(function(value, settings) { 
          $("#bulk_core").val(value);
          return(value);          
        },{ data:{'true':'true',
                  'false':'false'}, type:'select',cancel:'Cancel',submit:'OK', rows:3, tooltop:'Click to edit...', width:'100px'});

        $('span.embargoBatchEdit').editable(function(value, settings) { 
          $("#bulk_embargo").val(value);
          return(value);
        },{type:'textarea',cancel:'Cancel',submit:'OK', rows:3, tooltop:'Click to edit...', width:'100px'});

        $('span.coverageBatchEdit').editable(function(value, settings) { 
          $("#bulk_coverage").val(value);
          return(value);
        },{type:'textarea',cancel:'Cancel',submit:'OK', rows:3, tooltop:'Click to edit...', width:'100px'});

        $('td span.coreedit').editable('<g:createLink controller="ajax" action="genericSetValue" absolute="true"/>', {
           data   : {'true':'true', 'false':'false'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...'
         });

         $('dd span.reldataEdit').editable('<g:createLink controller="ajax" params="${[resultProp:'reference']}" action="genericSetRel" absolute="true"/>', {
           loadurl: '<g:createLink controller="MyInstitutions" params="${[shortcode:institution?.shortcode]}" action="availableLicenses" absolute="true"/>',
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
           }
         });

         var checkEmptyEditable = function() {
           $('.ipe, .refdataedit, .fieldNote, .reldataEdit').each(function() {
             if($(this).text().length == 0) {
               $(this).addClass('editableEmpty');
             } else {
               $(this).removeClass('editableEmpty');
             }
           });
         }
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

    </script>
  </body>
</html>

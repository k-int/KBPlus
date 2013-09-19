<%@ page import="com.k_int.kbplus.Subscription" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Details</g:link> </li>
        
        <!-- OLD VERSION
        <li class="pull-right">
          <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}" params="${[format:'csv',sort:params.sort,order:params.order,filter:params.filter]}">CSV Export</g:link> 
          <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}" params="${[format:'csv',sort:params.sort,order:params.order,filter:params.filter,omitHeader:'Y']}">(No header)</g:link></li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
        -->
        
        <li class="dropdown pull-right">
	        <a class="dropdown-toggle" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">
		  		Exports<b class="caret"></b>
			</a>
			<ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
				<li>
          			<g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}" params="${[format:'csv',sort:params.sort,order:params.order,filter:params.filter]}">CSV Export</g:link> 
          			<g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}" params="${[format:'csv',sort:params.sort,order:params.order,filter:params.filter,omitHeader:'Y']}">CSV Export (No header)</g:link>
        		</li>
				<li>
		  			<% def ps_json = [:]; ps_json.putAll(params); ps_json.format = 'json'; %>
					<g:link action="index" params="${ps_json}">Json Export</g:link>
	      		</li>
				<li>
		  			<% def ps_xml = [:]; ps_xml.putAll(params); ps_xml.format = 'xml'; %>
					<g:link action="index" params="${ps_xml}">XML Export</g:link>
	      		</li>
	      		<g:each in="${com.k_int.kbplus.UserTransforms.findAllByUser(user)}" var="ut">
	      			<g:if test="${ut.transforms.accepts_type.value == "subscription"}">
	      				<% 
						  	def ps_trans = [:];
						  	if(ut.transforms.accepts_format.value == "xml")
				  				ps_trans.putAll(ps_xml);
						  	else if(ut.transforms.accepts_format.value == "json")
								ps_trans.putAll(ps_json);
							ps_trans.transforms=ut.transforms.id;
					  	%>
	      				<li>
							<g:link action="index" params="${ps_trans}">${ut.transforms.name}</g:link>
			      		</li>
	      			</g:if>
	      		</g:each>
		    </ul>
		</li>
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
       <h1><g:xEditable owner="${subscriptionInstance}" field="name" /></h1>
       <g:render template="nav" contextPath="." />
    </div>


    <g:if test="${subscriptionInstance.pendingChanges?.size() > 0}">
      <div class="container alert-warn">
        <h6>This Subscription has pending change notifications</h6>
        <g:link controller="pendingChange" action="acceptAll" id="com.k_int.kbplus.Subscription:${subscriptionInstance.id}" class="btn btn-success"><i class="icon-white icon-ok"></i>Accept All</g:link>
        <g:link controller="pendingChange" action="rejectAll" id="com.k_int.kbplus.Subscription:${subscriptionInstance.id}" class="btn btn-danger"><i class="icon-white icon-remove"></i>Reject All</g:link>
        <br/>&nbsp;<br/>
        <table class="table table-bordered">
          <thead>
            <tr>
              <td>Info</td>
              <td>Action</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${subscriptionInstance.pendingChanges}" var="pc">
              <tr>
                <td>${pc.desc}</td>
                <td>
                  <g:link controller="pendingChange" action="accept" id="${pc.id}" class="btn btn-success"><i class="icon-white icon-ok"></i>Accept</g:link>
                  <g:link controller="pendingChange" action="reject" id="${pc.id}" class="btn btn-danger"><i class="icon-white icon-remove"></i>Reject</g:link>
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
               <dl><dt>License</dt><dd><g:if test="${subscriptionInstance.subscriber}">
                         <g:xEditableRefData owner="${subscriptionInstance}" field="owner" dataController="subscriptionDetails" dataAction="possibleLicensesForSubscription"/>
                       </g:if><g:else>N/A (Subscription offered)</g:else>
                   </dd>
               </dl>

               <dl><dt>Package Name</dt><dd><g:each in="${subscriptionInstance.packages}" var="sp">
                           <g:link controller="packageDetails" action="show" id="${sp.pkg.id}">${sp?.pkg?.name}</g:link> (${sp.pkg?.contentProvider?.name}) <br/>
                       </g:each></dd></dl>

               <dl><dt>Subscription Identifier</dt><dd>${subscriptionInstance.identifier}</dd></dl>

               <dl><dt>Public?</dt><dd><g:xEditableRefData owner="${subscriptionInstance}" field="isPublic" config='YN'/></dd></dl> 

               <dl><dt>Start Date</dt><dd><g:xEditable owner="${subscriptionInstance}" field="startDate" type="date"/></dd></dl>

               <dl><dt>End Date</dt><dd><g:xEditable owner="${subscriptionInstance}" field="endDate" type="date"/></dd></dl>

               <dl><dt>Nominal Platform(s)</dt><dd><g:each in="${subscriptionInstance.packages}" var="sp">
                        ${sp.pkg?.nominalPlatform?.name}<br/>
                    </g:each></dd></dl>

               <dl><dt><label class="control-label" for="licenseeRef">Org Links</label></dt><dd>
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
             <label>From Package:</label> <select name="pkgfilter">
                                <option value="">All</option>
                               <g:each in="${subscriptionInstance.packages}" var="sp">
                                 <option value="${sp.pkg.id}" ${sp.pkg.id.toString()==params.pkgfilter?'selected=true':''}>${sp.pkg.name}</option>
                               </g:each>
                            </select>
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

                  <input type="Submit" value="Apply Batch Changes" onClick="return confirmSubmit()" class="btn btn-primary"/></g:if></th>

              	<th>
                	<g:if test="${editable}"><g:simpleHiddenRefdata id="bulk_core" name="bulk_core" refdataCategory="CoreStatus"/></g:if>
              	</th>

              <th><g:if test="${editable}"> <g:simpleHiddenValue id="bulk_start_date" name="bulk_start_date" type="date"/> </g:if> </th>
              <th><g:if test="${editable}"> <g:simpleHiddenValue id="bulk_end_date" name="bulk_end_date" type="date"/> </g:if></th>
              <th><g:if test="${editable}"> <g:simpleHiddenValue id="bulk_core_start" name="bulk_core_start" type="date"/> </g:if></th>
              <th><g:if test="${editable}"> <g:simpleHiddenValue id="bulk_core_end" name="bulk_core_end" type="date"/> </g:if></th>
              <th colspan="2"></th>
            </tr>

          <g:if test="${entitlements}">
            <g:each in="${entitlements}" var="ie">
              <tr>
                <td><g:if test="${editable}"><input type="checkbox" name="_bulkflag.${ie.id}" class="bulkcheck"/></g:if></td>
                <td>${counter++}</td>
                <td>
                  <g:link controller="issueEntitlement" id="${ie.id}" action="show">${ie.tipp.title.title}</g:link>
                  <g:if test="${ie.tipp?.hostPlatformURL}">( <a href="${ie.tipp?.hostPlatformURL}" TITLE="${ie.tipp?.hostPlatformURL}">Host Link</a> 
                            <a href="${ie.tipp?.hostPlatformURL}" TITLE="${ie.tipp?.hostPlatformURL} (In new window)" target="_blank"><i class="icon-share-alt"></i></a>)</g:if>
                </td>
                <td>${ie?.tipp?.title?.getIdentifierValue('ISSN')}</td>
                <td>${ie?.tipp?.title?.getIdentifierValue('eISSN')}</td>
                <td>
                  <g:xEditableRefData owner="${ie}" field="coreStatus" config='CoreStatus'/></td>
                <td>
                    <g:xEditable owner="${ie}" type="date" field="startDate" />
                </td>
                <td>
                    <g:xEditable owner="${ie}" type="date" field="endDate" />
                </td>
                <td>
                    <g:xEditable owner="${ie}" type="date" field="coreStatusStart" />
                </td>
                <td>
                    <g:xEditable owner="${ie}" type="date" field="coreStatusEnd" />
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
      
        $.fn.editable.defaults.mode = 'inline';

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

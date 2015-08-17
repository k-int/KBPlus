<%@ page import="com.k_int.kbplus.Subscription" %>
<%@ page import="java.text.SimpleDateFormat"%>
<%
  def dateFormater = new SimpleDateFormat(session.sessionPreferences?.globalDateFormat)
%>
<r:require module="annotations" />

<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Subscription Details</title>

  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Details</g:link> </li>
        
      

    </li>
        <g:if test="${editable}">
          <li class="pull-right"><span class="badge badge-warning">Editable</span>&nbsp;</li>
        </g:if>
        <li class="pull-right"><g:annotatedLabel owner="${subscriptionInstance}" property="detailsPageInfo"></g:annotatedLabel>&nbsp;</li>
      </ul>
    </div>

    <g:if test="${flash.message}">
      <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
    </g:if>

    <g:if test="${flash.error}">
      <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
    </g:if>

    <div class="container">
      <g:if test="${params.asAt}"><h1>Snapshot on ${params.asAt} from </h1></g:if>
       <h1><g:xEditable owner="${subscriptionInstance}" field="name" /></h1>
       <g:render template="nav"  />
    </div>


    <g:render template="/templates/pendingChanges" model="${['pendingChanges': pendingChanges,'flash':flash,'model':subscriptionInstance]}"/>


    <div id="collapseableSubDetails" class="container collapse in">
      <div class="row">
        <div class="span8"> 
            <br/>
            <h6>Subscription Information</h6>
            <div class="inline-lists"> 
               <dl><dt>${message(code:'licence')}</dt><dd><g:if test="${subscriptionInstance.subscriber}">
                         <g:xEditableRefData owner="${subscriptionInstance}" field="owner" dataController="subscriptionDetails" dataAction="possibleLicensesForSubscription" />
                         <g:if test="${subscriptionInstance.owner != null}">(<g:link controller="licenseDetails" action="index" id="${subscriptionInstance.owner.id}">Link</g:link> <g:link controller="licenseDetails" action="index" target="new" id="${subscriptionInstance.owner.id}"><i class="icon-share-alt"></i></g:link>)</g:if>
                       </g:if><g:else>N/A (Subscription offered)</g:else>
                   </dd>
               </dl>
               <dl><dt>Package Name</dt><dd><g:each in="${subscriptionInstance.packages}" var="sp">
                           <g:link controller="packageDetails" action="show" id="${sp.pkg.id}">${sp?.pkg?.name}</g:link> (${sp.pkg?.contentProvider?.name}) 
                           <g:if test="${editable}">
                           <a onclick="unlinkPackage(${sp.pkg.id})">Unlink <i class="fa fa-times"></i></a>
                           <br/>
                           </g:if>
                       </g:each></dd></dl>

               <dl><dt><g:annotatedLabel owner="${subscriptionInstance}" property="ids">Subscription Identifiers</g:annotatedLabel></dt>
                   <dd>
                     <table class="table table-bordered">
                       <thead>
                         <tr>
                           <th>Authority</th>
                           <th>Identifier</th>
                         </tr>
                       </thead>
                       <tbody>
                         <g:each in="${subscriptionInstance.ids}" var="io">
                           <tr>
                             <td>${io.identifier.ns.ns}</td>
                             <td>${io.identifier.value}</td>
                           </tr>
                         </g:each>
                       </tbody>
                     </table>
           <g:if test="${editable}">
              <g:form controller="ajax" action="addToCollection" class="form-inline" name="add_ident_submit">
                Select an existing identifer using the typedown, or create a new one by entering namespace:value (EG JC:66454) then clicking that value in the dropdown to confirm.<br/>
                <input type="hidden" name="__context" value="${subscriptionInstance.class.name}:${subscriptionInstance.id}"/>
                <input type="hidden" name="__newObjectClass" value="com.k_int.kbplus.IdentifierOccurrence"/>
                <input type="hidden" name="__recip" value="sub"/>
                <input type="hidden" name="identifier" id="addIdentifierSelect"/>
                <input type="submit" value="Add Identifier..." class="btn btn-primary btn-small"/><br/>
              </g:form>
            </g:if>
                   </dd>
               </dl>

               <dl><dt>Start Date</dt><dd><g:xEditable owner="${subscriptionInstance}" field="startDate" type="date"/></dd></dl>

               <dl><dt>End Date</dt><dd><g:xEditable owner="${subscriptionInstance}" field="endDate" type="date"/></dd></dl>

               <dl><dt>Financial</dt>
                   <dd>
                     <table class="table table-striped table-bordered">
                       <thead>
                         <tr>
                           <th>CI #</th>
                           <th>Order #</th>
                           <th>Date Paid</th>
                           <th>Start Date</th>
                           <th>End Date</th>
                           <th>Amount</th>
                         </tr>
                       </thead>
                       <tbody>
                         <g:each in="${subscriptionInstance.costItems}" var="ci">
                           <tr>
                             <td>${ci.id}</td>
                             <td>${ci.order?.orderNumber}</td>
                             <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ci.datePaid}"/></td>
                             <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ci.startDate}"/></td>
                             <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ci.endDate}"/></td>
                             <td>${ci.costInLocalCurrency} / ${ci.costInBillingCurrency}</td>
                         </tr>
                         </g:each>
                       </tbody>
                     </table>
                   </dd>
               </dl>


               <dl><dt>Manual Renewal Date</dt><dd><g:xEditable owner="${subscriptionInstance}" field="manualRenewalDate" type="date"/></dd></dl>
               <dL><dt>Child </dt><dd>
                        <g:xEditableRefData owner="${subscriptionInstance}" field="isSlaved" config='YN'/>
               </dd></dL>
               <dl>
                 <dt>
                   <g:annotatedLabel owner="${subscriptionInstance}" property="nominalPlatform">Nominal Platform(s)</g:annotatedLabel>
                 </dt><dd>
                    <g:each in="${subscriptionInstance.packages}" var="sp">
                        ${sp.pkg?.nominalPlatform?.name}<br/>
                    </g:each></dd></dl>

             <dl>
                <dt>Cancellation Allowances</dt>
                <dd>
                  <g:xEditable owner="${subscriptionInstance}" field="cancellationAllowances" />
                </dd>
              </dl>


               <dl><dt><label class="control-label" for="licenseeRef">Org Links</label></dt><dd>
                       <g:render template="orgLinks" contextPath="../templates" model="${[roleLinks:subscriptionInstance?.orgRelations,editmode:editable]}" />
                     </dd>
               </dl>

               <g:if test="${params.mode=='advanced'}">
                 <dl><dt><label class="control-label" for="licenseeRef">Status</label></dt><dd>
                      <g:xEditableRefData owner="${subscriptionInstance}" field="status" config='Subscription Status'/>
                     </dd>
               </dl>
               </g:if>

               <div class="clear-fix"></div>
            </div>
        </div>

        <div class="span4">
          <g:render template="documents" contextPath="../templates" model="${[ ownobj:subscriptionInstance, owntp:'subscription']}" />
          <g:render template="notes" contextPath="../templates" model="${[ ownobj:subscriptionInstance, owntp:'subscription']}" />
        </div>
      </div>
    </div>
    <div id="magicArea"></div>
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[linkType:subscriptionInstance?.class?.name,roleLinks:subscriptionInstance?.orgRelations,parent:subscriptionInstance.class.name+':'+subscriptionInstance.id,property:'orgs',recip_prop:'sub']}" />
    <r:script language="JavaScript">

      function unlinkPackage(pkg_id){
        var req_url = "${createLink(controller:'subscriptionDetails', action:'unlinkPackage',params:[subscription:subscriptionInstance.id])}&package="+pkg_id

        $.ajax({url: req_url, 
          success: function(result){
             $('#magicArea').html(result);
          },
          complete: function(){
            $("#unlinkPackageModal").modal("show");
          }
        });
      }
      
      function hideModal(){
        $("[name='coreAssertionEdit']").modal('hide');
      }

      function showCoreAssertionModal(){

        $("[name='coreAssertionEdit']").modal('show');
       
      }
      
      <g:if test="${editable}">


      $(document).ready(function() {
           
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


        <g:if test="${editable}">
          $("[name='add_ident_submit']").submit(function( event ) {
            event.preventDefault();
            $.ajax({
              url: "<g:createLink controller='ajax' action='validateIdentifierUniqueness'/>?identifier="+$("input[name='identifier']").val()+"&owner="+"${subscriptionInstance.class.name}:${subscriptionInstance.id}",
              success: function(data) {
                if(data.unique){
                  $("[name='add_ident_submit']").unbind( "submit" )
                  $("[name='add_ident_submit']").submit();
                }else if(data.duplicates){
                  var warning = "The following Subscriptions are also associated with this identifier:\n";
                  for(var ti of data.duplicates){
                      warning+= ti.id +":"+ ti.title+"\n";
                  }
                  var accept = confirm(warning);
                  if(accept){
                    $("[name='add_ident_submit']").unbind( "submit" )
                    $("[name='add_ident_submit']").submit();
                  }
                }
              },
            });
          });

          $("#addIdentifierSelect").select2({
            placeholder: "Search for an identifier...",
            minimumInputLength: 1,
            ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
              url: "<g:createLink controller='ajax' action='lookup'/>",
              dataType: 'json',
              data: function (term, page) {
                  return {
                      q: term, // search term
                      page_limit: 10,
                      baseClass:'com.k_int.kbplus.Identifier'
                  };
              },
              results: function (data, page) {
                return {results: data.values};
              }
            },
            createSearchChoice:function(term, data) {
              return {id:'com.k_int.kbplus.Identifier:__new__:'+term,text:"New - "+term};
            }
          });
        </g:if>

      });

      </g:if>
      <g:else>
        $(document).ready(function() {
          $(".announce").click(function(){
            var id = $(this).data('id');
            $('#modalComments').load('<g:createLink controller="alert" action="commentsFragment" />/'+id);
            $('#modalComments').modal('show');
          });
        });
      </g:else>

      <g:if test="${params.asAt && params.asAt.length() > 0}"> $(function() {
        document.body.style.background = "#fcf8e3";
      });</g:if>
      
    </r:script>
  </body>
</html>

<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
         <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions"  action="addSubscription" params="${[shortcode:params.shortcode]}">${institution.name} Add Subscripton</g:link> </li>
      </ul>
    </div>


    <div class="container">
      <h1>${institution?.name} - Add Subscription</h1>

      <ul class="nav nav-pills">
        <li><g:link controller="myInstitutions" 
                                   action="currentSubscriptions" 
                                   params="${[shortcode:params.shortcode]}">Current Subscriptions</g:link></li>
        <li class="active"><g:link controller="myInstitutions" 
                               action="addSubscription" 
                               params="${[shortcode:params.shortcode]}">Add New Subscription</g:link></li>
      </ul>
    </div>

        
      <div class="container">
          <div class="pull-right">
              <g:form action="addSubscription" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="get" class="form-inline">
                  <label>Search text</label>: <input type="text" name="q" placeholder="enter search term..."  value="${params.q?.encodeAsHTML()}"  />
                  <label>Valid On</label>: <input name="validOn" type="text" value="${validOn}"/>
                  <input type="submit" class="btn btn-primary" value="Search" />
              </g:form>
          </div>
      </div>

    <div class="container">
        <g:if test="${packages}" >
          <g:form action="processAddSubscription" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="post">
 
            <div class="pull-left subscription-create">
            <g:if test="${is_admin}"> 
              <select name="createSubAction"> 
                <option value="copy">Copy With Entitlements</option>
                <option value="nocopy">Copy Without Entitlements</option>
                <input type="submit" class="btn disabled" value="Create Subscription" /> 
            </g:if>
            <g:else>You must have editor role to be able to add licences</g:else>
            </div>
              
              <div class="clearfix"></div>
              
            <table class="table table-striped table-bordered subscriptions-list">
                <tr>
                  <th>Select</th>
                  <g:sortableColumn params="${params}" property="p.name" title="Name" />
                  <th>Consortia</th>
                  <g:sortableColumn params="${params}" property="p.startDate" title="Start Date" />
                  <g:sortableColumn params="${params}" property="p.endDate" title="End Date" />
                  <th>Platform</th>
                  <th>License</th>
                </tr>
                <g:each in="${packages}" var="p">
                  <tr>
                    <td><input type="radio" name="packageId" value="${p.id}"/></td>
                    <td>
                      <g:link controller="packageDetails" action="show" id="${p.id}">${p.name} <g:if test="${p.consortia}">( ${p.consortia?.name} )</g:if></g:link>
                    </td>
                    <td>${p.getConsortia()?.name}</td>
                    <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${p.startDate}"/></td>
                    <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${p.endDate}"/></td>
                    <td>
                      ${p.nominalPlatform?.name}<br/>
                    </td>
                    <td>lic</td>
                  </tr>
                  
                </g:each>
             </table>
          </g:form>
        </g:if>
  
        <div class="pagination" style="text-align:center">
          <g:if test="${packages}" >
            <bootstrap:paginate  action="addSubscription" controller="myInstitutions" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${num_pkg_rows}" />
          </g:if>
        </div>
    </div>
    <script type="text/javascript">
        $(document).ready(function() {
            var activateButton = function() {
                $('.subscription-create input').removeClass('disabled');
                $('.subscription-create input').addClass('btn-primary');
            }
            
            // Disables radio selection when using back button.
            $('.subscriptions-list input[type=radio]:checked').prop('checked', false);
            
            // Activates the create subscription button when a radio button is selected.
            $('.subscriptions-list input[type=radio]').click(activateButton);
        });
    </script>
  </body>
</html>

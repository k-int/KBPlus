<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>
    <div class="row-fluid">

      <h2>${institution?.name} - Subscriptions</h2>
      <hr/>

      <ul class="nav nav-pills">
       <li class="active"><g:link controller="myInstitutions" 
                                  action="currentSubscriptions" 
                                  params="${[shortcode:params.shortcode]}">Current Subscriptions</g:link></li>
        <li><g:link controller="myInstitutions" 
                                action="addSubscription" 
                                params="${[shortcode:params.shortcode]}">Subscriptions Offered / Add New</g:link></li>
      </ul>

      <table class="table table-striped table-bordered table-condensed">
                <tr>
                  <th># (Name)</th>
                  <th>Parent</th>
                  <th>Package Name</th>
                  <th>Vendor</th>
                  <th>Consortia</th>
                  <th>Start Date</th>
                  <th>End Date</th>
                  <th>Platform</th>
                  <th>License</th>
                  <th>Docs</th>
                </tr>
                <g:each in="${subscriptions}" var="s">
                  <tr>
                    <td><g:link controller="myInstitutions" action="subscriptionDetails" params="${[shortcode:params.shortcode]}" id="${s.id}">${s.id} (${s.name})</g:link></td>
                    <td><g:if test="${s.instanceOf}"><g:link controller="Subscription" action="show" id="i${s.instanceOf.id}">${s.instanceOf.name}<g:link></g:if></td>
                    <td>
                      <g:each in="${s.instanceOf?.packages}" var="sp">
                        ${sp.pkg.name} (${sp.pkg?.contentProvider?.name}) <br/>
                      </g:each>
                    </td>
                    <td><g:${s.instanceOf?.provider?.name}</td>
                    <td>${s.instanceOf?.getConsortia()?.name}</td>
                    <td>${s.startDate}</td>
                    <td>${s.endDate}</td>
                    <td>
                      <g:each in="${s.instanceOf?.packages}" var="sp">
                        ${sp.pkg?.nominalPlatform?.name}<br/>
                      </g:each>
                    </td>
                    <td>${owner.reference}</td>
                    <td></td>
                  </tr>
                </g:each>
      </table>


    </div>
  </body>
</html>

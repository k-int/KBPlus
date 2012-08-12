<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:params.shortcode]}">${institution.name} Current Subscriptions</g:link> </li>
      </ul>
    </div>

    <div class="container">

      <h1>${institution?.name} - Current Subscriptions</h1>

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
                    <td>
                      <g:link controller="subscriptionDetails" action="index" id="${s.id}">${s.id} ${s.name} <g:if test="${s.consortia}">( ${s.consortia?.name} )</g:if></g:link>
                    </td>
                    <td>
                      <g:if test="${s.instanceOf}"><g:link controller="SubscriptionDetails" action="index" id="${s.instanceOf.id}">${s.instanceOf.name}</g:link></g:if>
                    </td>
                    <td>
                      <g:each in="${s.packages}" var="sp">
                        ${sp.pkg.name} (${sp.pkg?.contentProvider?.name}) <br/>
                      </g:each>
                    </td>
                    <td>${s.instanceOf?.provider?.name}</td>
                    <td>${s.instanceOf?.getConsortia()?.name}</td>
                    <td><g:formatDate format="dd MMMM yyyy" date="${s.startDate}"/></td>
                    <td><g:formatDate format="dd MMMM yyyy" date="${s.endDate}"/></td>
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

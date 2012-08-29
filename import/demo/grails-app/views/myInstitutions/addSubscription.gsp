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
                               params="${[shortcode:params.shortcode]}">Subscriptions Offered / Add New</g:link></li>
      </ul>
    </div>


    <div class="container" style="text-align:center">
      <g:form action="addSubscription" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="get">
        Search text: <input type="text" name="q" placeholder="enter search term..."  value="${params.q?.encodeAsHTML()}"  />
        <input type="submit" class="btn btn-primary" value="Search" />
      </g:form><br/>
    </div>

    <div class="container">
        <g:if test="${subscriptions}" >
          <g:form action="processAddSubscription" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="post">
  
            <table class="table table-striped table-bordered">
                <tr>
                  <th>Select</th>
                  <g:sortableColumn params="${params}" property="s.name" title="Name" />
                  <th>Package Name(s)</th>
                  <th>Consortia</th>
                  <g:sortableColumn params="${params}" property="s.startDate" title="Start Date" />
                  <g:sortableColumn params="${params}" property="s.endDate" title="End Date" />
                  <th>Platform(s)</th>
                  <th>License</th>
                </tr>
                <g:each in="${subscriptions}" var="s">
                  <tr>
                    <td><input type="radio" name="subOfferedId" value="${s.id}"/></td>
                    <td>
                      <g:link controller="subscriptionDetails" action="index" id="${s.id}">${s.name} <g:if test="${s.consortia}">( ${s.consortia?.name} )</g:if></g:link>
                    </td>
                    <td>
                      <g:each in="${s.packages}" var="sp">
                        ${sp.pkg.name} (${sp.pkg?.contentProvider?.name}) <br/>
                      </g:each>
                    </td>
                    <td>${s.getConsortia()?.name}</td>
                    <td><g:formatDate format="dd MMMM yyyy" date="${s.startDate}"/></td>
                    <td><g:formatDate format="dd MMMM yyyy" date="${s.endDate}"/></td>
                    <td>
                      <g:each in="${s.packages}" var="sp">
                        ${sp.pkg?.nominalPlatform?.name}<br/>
                      </g:each>
                    </td>
                    <td><g:if test="${s.owner}"><g:link controller="licenseDetails" action="index" id="${s.owner.id}">${s.owner?.reference}</g:link></g:if></td>
                  </tr>
                </g:each>
             </table>

            <div class="paginateButtons" style="text-align:center">
              <select name="createSubAction"> 
                <option value="copy">Copy With Entitlements</option>
                <option value="nocopy">Copy Without Entitlements</option>
              &nbsp;<input type="submit" class="btn btn-primary" value="Create Subscription"/> 
            </div>
          </g:form>
        </g:if>
  
        <div class="paginateButtons" style="text-align:center">
          <g:if test="${subscriptions}" >
            <span><g:paginate  action="addSubscription" controller="myInstitutions" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${num_sub_rows}" /></span>
          </g:if>
        </div>
    </div>
  </body>
</html>

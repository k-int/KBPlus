<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
    <style>
.paginateButtons {
    margin: 3px 0px 3px 0px;
}

.paginateButtons a {
    padding: 2px 4px 2px 4px;
    background-color: #A4A4A4;
    border: 1px solid #EEEEEE;
    text-decoration: none;
    font-size: 10pt;
    font-variant: small-caps;
    color: #EEEEEE;
}

.paginateButtons a:hover {
    text-decoration: underline;
    background-color: #888888;
    border: 1px solid #AA4444;
    color: #FFFFFF;
}
    </style>
  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home">Home</g:link> <span class="divider">/</span> </li>
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
                  <th>Vendor</th>
                  <th>Consortia</th>
                  <g:sortableColumn params="${params}" property="s.startDate" title="Start Date" />
                  <g:sortableColumn params="${params}" property="s.endDate" title="End Date" />
                  <th>Platform(s)</th>
                  <th>License</th>
                  <th>Docs</th>
                </tr>
                <g:each in="${subscriptions}" var="s">
                  <tr>
                    <td><input type="radio" name="subOfferedId" value="${s.id}"/></td>
                    <td>
                      <g:link controller="subscriptionDetails" action="index" id="${s.id}">${s.name}</g:link>
                    </td>
                    <td>
                      <g:each in="${s.packages}" var="sp">
                        ${sp.pkg.name} (${sp.pkg?.contentProvider?.name}) <br/>
                      </g:each>
                    </td>
                    <td>${s.provider?.name}</td>
                    <td>${s.getConsortia()?.name}</td>
                    <td><g:formatDate format="dd MMMM yyyy" date="${s.startDate}"/></td>
                    <td><g:formatDate format="dd MMMM yyyy" date="${s.endDate}"/></td>
                    <td>
                      <g:each in="${s.packages}" var="sp">
                        ${sp.pkg?.nominalPlatform?.name}<br/>
                      </g:each>
                    </td>
                    <td>${owner.reference}</td>
                    <td></td>
                  </tr>
                </g:each>
             </table>

            <div class="paginateButtons" style="text-align:center">
              <input type="submit" value="Create Subscription"/>
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

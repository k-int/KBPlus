<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
  </head>

  <body>
    <div class="container">
        <ul class="breadcrumb">
            <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
           <li> <g:link controller="reports" action="list">${institution.name}  Current Titles</g:link> </li>
        </ul>
    </div>
    
    <g:if test="${flash.message}">
      <div class="container">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </div>
    </g:if>

    <g:if test="${flash.error}">
      <div class="container">
        <bootstrap:alert class="error-info">${flash.error}</bootstrap:alert>
      </div>
    </g:if>

    <div class="container">
      <h1>${institution?.name} - Current Titles</h1>
      
      <g:set var="filterSub" value="${params.sub?:"all"}" />
      <g:set var="filterPvd" value="${params.pvd?:"all"}" />
      
      <ul class="nav nav-pills">
        <li class="active"><a>Filtering :</a></li>
        <li class="dropdown">
          <a class="dropdown-toggle" id="subscription-menu" role="button" data-toggle="dropdown" data-target="#" href="">
            Subscriptions
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu" role="menu" aria-labelledby="subscription-menu">
            <li<%= (filterSub.equals("all")) ? ' class="active"' : '' %>>
              <% params.sub = "all" %>
              <g:link action="currentTitles" params="${params}">All Subscriptions</g:link>
            </li>
            <g:each in="${subscriptions}" var="s">
              <li<%= (filterSub.equals(s.id.toString())) ? ' class="active"' : '' %>>
                <% params.sub = s.id %>
                <g:link action="currentTitles" params="${params}">${s.name} <g:if test="${s.consortia}">( ${s.consortia.name} )</g:if></g:link>
              </li>
            </g:each>
            <% params.sub = filterSub %>
          </ul>
        </li>
        <li class="dropdown">
          <a class="dropdown-toggle" id="dLabel" role="button" data-toggle="dropdown" data-target="#" href="/page.html">
            Content Provider
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
            <li<%= (filterPvd.equals("all")) ? ' class="active"' : '' %>>
              <% params.pvd = "all" %>
              <g:link action="currentTitles" params="${params}">All Content Providers</g:link>
            </li>
            <g:each in="${providers}" var="p">
              <% 
              def pvdId = p.id.toString()
              def pvdName = p.name 
              %>
              <li<%= (filterPvd.equals(pvdId)) ? ' class="active"' : '' %>>
                <% params.pvd = pvdId %>
                <g:link action="currentTitles" params="${params}">${pvdName}</g:link>
              </li>
            </g:each>
            <% params.pvd = filterPvd %>
          </ul>
        </li>
        <li class="dropdown disabled">
          <a class="dropdown-toggle" id="dLabel" role="button" data-toggle="dropdown" data-target="#" href="/page.html">
            Host platform
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
            <!--List of Host platform-->
          </ul>
        </li>
        <li class="dropdown disabled">
          <a class="dropdown-toggle" id="dLabel" role="button" data-toggle="dropdown" data-target="#" href="/page.html">
            Other platform
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
            <!--List of Other platform-->
          </ul>
        </li>
      </ul>
    </div>
      
    <div class="container" style="text-align:center">
      <g:form action="currentTitles" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="get" class="form-inline">
        <label>Subscription(s) Valid On</label> <input name="validOn" type="text" value="${validOn}" disabled="disabled"/>
        <input type="submit" class="btn btn-primary" value="Reload" disabled="disabled"/>
      </g:form><br/>
    </div>
  
    <div class="container">
      <dl>
        <dt>Titles ( ${offset+1} to ${offset+(titles.size())} of ${num_ti_rows} )
          <g:form action="index" params="${params}" method="get" class="form-inline">
             <input type="hidden" name="sort" value="${params.sort}">
             <input type="hidden" name="order" value="${params.order}">
             <label>Filter:</label> 
             <input name="filter" value="${params.filter}" disabled="disabled"/>
             <input type="submit" class="btn btn-primary" disabled="disabled"/>
          </g:form>
        </dt>
        <dd>
          <g:form action="subscriptionBatchUpdate" params="${[id:subscriptionInstance?.id]}" class="form-inline">
          <g:set var="counter" value="${offset+1}" />
          <table  class="table table-striped table-bordered">

            <tr>
              <g:sortableColumn params="${params}" property="tipp.title.title" title="Title" />
              <th>ISSN</th>
              <th>eISSN</th>
              <th>Earliest Date</th>
              <th>Latest Date</th>
              <th>Subscription</th>
            </tr>  
            
            <g:each in="${titles}" var="ti">
              <tr>
                <td>${ti[0].title}</td>
                <td>${ti[0].getIdentifierValue('ISSN')}</td>
                <td>${ti[0].getIdentifierValue('eISSN')}</td>
                <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ti[1]}"/></td>
                <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ti[2]}"/></td>
                <td>
                  <g:if test="${ti[3] > 1}">
                    <%--
                    <div class="accordion" id="accordions">
                      <div class="accordion-group"> 
                      --%>
                        <div class="accordion-heading">
                          <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordions" href="#collapse-${ti[0].id}">
                            ${ti[3]} subscriptions (show/hide)
                          </a>
                        </div>
                        <div id="collapse-${ti[0].id}" class="accordion-body collapse out">
                          <div class="accordion-inner">
                            <g:each in="${entitlements}" var="ie">
                              <g:if test="${ie.tipp.title.id == ti[0].id}">
                                <p>
                                  ${ie.subscription.name}:
                                  <g:if test="${ie.startVolume}">Vol. ${ie.startVolume}</g:if>
                                  <g:if test="${ie.startIssue}">Iss. ${ie.startIssue}</g:if>
                                  <g:formatDate format="yyyy" date="${ie.startDate}"/>
                                  -
                                  <g:if test="${ie.endVolume}">Vol. ${ie.endVolume}</g:if>
                                  <g:if test="${ie.endIssue}">Iss. ${ie.endIssue}</g:if>
                                  <g:formatDate format="yyyy" date="${ie.endDate}"/>
                                  (<g:link controller="subscriptionDetails" action="index" id="${ie.subscription.id}">Subscription Link</g:link>;
                                  <g:link controller="issueEntitlement" action="show" id="${ie.id}">Issue Entitlement Link</g:link>)
                                </p>
                              </g:if>
                            </g:each>
                          </div>
                        </div>
                    <%--
                      </div>
                    </div>
                    --%>
                  </g:if>
                  <g:else>
                    <g:each in="${entitlements}" var="ie">
                      <g:if test="${ie.tipp.title.id == ti[0].id}">
                          ${ie.subscription.name}: 
                          <g:if test="${ie.startVolume}">Vol. ${ie.startVolume}</g:if>
                          <g:if test="${ie.startIssue}">Iss. ${ie.startIssue}</g:if>
                          <g:formatDate format="yyyy" date="${ie.startDate}"/>
                          -
                          <g:if test="${ie.endVolume}">Vol. ${ie.endVolume}</g:if>
                          <g:if test="${ie.endIssue}">Iss. ${ie.endIssue} </g:if>
                          <g:formatDate format="yyyy" date="${ie.endDate}"/>
                          (
                          <g:link controller="subscriptionDetails" action="index" id="${ie.subscription.id}">Subscription Link</g:link>;
                          <g:link controller="issueEntitlement" action="show" id="${ie.id}">Issue Entitlement Link</g:link>
                          )
                      </g:if>
                    </g:each>
                  </g:else>
                </td>
              </tr>
            </g:each>
            
          </table>
          </g:form>
        </dd>
      </dl>

      <div class="pagination" style="text-align:center">
        <g:if test="${titles}" >
          <bootstrap:paginate  action="currentTitles" controller="myInstitutions" params="${params}" next="Next" prev="Prev" maxsteps="${max}" total="${num_ti_rows}" />
        </g:if>
      </div>
    </div>
  
  </body>
</html>

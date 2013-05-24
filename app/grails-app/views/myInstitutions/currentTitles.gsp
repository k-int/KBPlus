<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
    
    <style>
      .filtering-dropdown-menu {max-height: 400px; overflow: hidden; overflow-y: auto;}
    </style>
  </head>

  <body>
    <div class="container">
        <ul class="breadcrumb">
          <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
          <li> <g:link controller="reports" action="list">${institution.name}  Current Titles</g:link> </li>
          <li class="dropdown pull-right">
            <a class="dropdown-toggle" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">
              Exports<b class="caret"></b>
            </a>
            <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
              <li>
                <% def ps_csv = [:]; ps_csv.putAll(params); ps_csv.format = 'csv'; %>
                <g:link action="currentTitles" params="${ps_csv}">CSV Export</g:link>
              </li>
              <li>
                <% def ps_json = [:]; ps_json.putAll(params); ps_json.format = 'json'; %>
                <g:link action="currentTitles" params="${ps_json}">Json Export</g:link>
              </li>
            </ul>
          </li>
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
      
      <g:set var="filterSub" value="${params.filterSub?:"all"}" />
      <g:set var="filterPvd" value="${params.filterPvd?:"all"}" />
      <g:set var="filterHostPlat" value="${params.filterHostPlat?:"all"}" />
      <g:set var="filterOtherPlat" value="${params.filterOtherPlat?:"all"}" />
      
      <ul class="nav nav-pills">
        <li class="active"><a>Filtering :</a></li>
        <li class="dropdown">
          <a class="dropdown-toggle" id="subscription-menu" role="button" data-toggle="dropdown" data-target="#" href="">
            Subscriptions
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="subscription-menu">
            <li<%= (filterSub.equals("all")) ? ' class="active"' : '' %>>
              <% params.filterSub = "all" %>
              <g:link action="currentTitles" params="${params}">All Subscriptions</g:link>
            </li>
            <g:each in="${subscriptions}" var="s">
              <li<%= (filterSub.equals(s.id.toString())) ? ' class="active"' : '' %>>
                <% params.filterSub = s.id %>
                <g:link action="currentTitles" params="${params}">${s.name} <g:if test="${s.consortia}">( ${s.consortia.name} )</g:if></g:link>
              </li>
            </g:each>
            <% params.filterSub = filterSub %>
          </ul>
        </li>
        <li class="dropdown">
          <a class="dropdown-toggle" id="dLabel" role="button" data-toggle="dropdown" data-target="#" href="/page.html">
            Content Provider
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="dLabel">
            <li<%= (filterPvd.equals("all")) ? ' class="active"' : '' %>>
              <% params.filterPvd = "all" %>
              <g:link action="currentTitles" params="${params}">All Content Providers</g:link>
            </li>
            <g:each in="${providers}" var="p">
              <% 
              def pvdId = p.id.toString()
              def pvdName = p.name 
              %>
              <li<%= (filterPvd.equals(pvdId)) ? ' class="active"' : '' %>>
                <% params.filterPvd = pvdId %>
                <g:link action="currentTitles" params="${params}">${pvdName}</g:link>
              </li>
            </g:each>
            <% params.filterPvd = filterPvd %>
          </ul>
        </li>
        <li class="dropdown">
          <a class="dropdown-toggle" id="dLabel" role="button" data-toggle="dropdown" data-target="#" href="/page.html">
            Host platform
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="dLabel">
            <li<%= (filterHostPlat.equals("all")) ? ' class="active"' : '' %>>
              <% params.filterHostPlat = "all" %>
              <g:link action="currentTitles" params="${params}">All Host Platforms</g:link>
            </li>
            <g:each in="${hostplatforms}" var="hp">
              <% 
              def hostId = hp.id.toString()
              def hostName = hp.name 
              %>
              <li<%= (filterHostPlat.equals(hostId)) ? ' class="active"' : '' %>>
                <% params.filterHostPlat = hostId %>
                <g:link action="currentTitles" params="${params}">${hostName}</g:link>
              </li>
            </g:each>
            <% params.filterHostPlat = filterHostPlat %>
          </ul>
        </li>
        <li class="dropdown">
          <a class="dropdown-toggle" id="dLabel" role="button" data-toggle="dropdown" data-target="#" href="/page.html">
            Other platform
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="dLabel">
            <li<%= (filterOtherPlat.equals("all")) ? ' class="active"' : '' %>>
              <% params.filterOtherPlat = "all" %>
              <g:link action="currentTitles" params="${params}">All Additional Platforms</g:link>
            </li>
            <g:each in="${otherplatforms}" var="op">
              <% 
              def platId = op.id.toString()
              def platName = op.name 
              %>
              <li<%= (filterOtherPlat.equals(platId)) ? ' class="active"' : '' %>>
                <% params.filterOtherPlat = platId %>
                <g:link action="currentTitles" params="${params}">${platName}</g:link>
              </li>
            </g:each>
            <% params.filterOtherPlat = filterOtherPlat %>
          </ul>
        </li>
      </ul>
    </div>
      
    <div class="container" style="text-align:center">
      <g:form action="currentTitles" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="get" class="form-inline">
        <label>Subscription(s) Valid On</label> <input name="validOn" type="text" value="${validOn}"/><!-- disabled="disabled"-->
        <input type="submit" class="btn btn-primary" value="Reload"/> <!-- disabled="disabled"-->
      </g:form><br/>
    </div>
  
    <div class="container">
      <dl>
        <dt>Titles ( ${offset+1} to ${offset+(titles.size())} of ${num_ti_rows} )
          <g:form action="currentTitles" params="${params}" method="get" class="form-inline">
             <input type="hidden" name="sort" value="${params.sort}">
             <input type="hidden" name="order" value="${params.order}">
             <label>Filter:</label> 
             <input name="filter" value="${params.filter}"/> <!-- disabled="disabled"-->
             <input type="submit" class="btn btn-primary"/> <!-- disabled="disabled"-->
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
      
      
      <!-- For Test Only 
      <g:if env="development"> -->
      <div class="accordion" id="accordions">
        <div class="accordion-group"> 
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordions" href="#collapse-full-table">
              For Test Only: Full Table (show/hide)
            </a>
          </div>
          <div id="collapse-full-table" class="accordion-body collapse out">
            <div class="accordion-inner">
              <table  class="table table-striped table-bordered">
                <tr>
                  <g:sortableColumn params="${params}" property="tipp.title.title" title="Title" />
                  <th>ISSN</th>
                  <th>eISSN</th>
                  <th>Earliest Date</th>
                  <th>Latest Date</th>
                  <th>Subscription</th>
                  <th>Content Provider</th>
                  <th>Host Platform</th>
                  <th>Other Platform</th>
                </tr>
                <g:each in="${entitlements}" var="ie">
                  <tr>
                    <td>${ie.tipp.title.title}</td>
                    <td>${ie.tipp.title.getIdentifierValue('ISSN')}</td>
                    <td>${ie.tipp.title.getIdentifierValue('eISSN')}</td>
                    <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.startDate}"/></td>
                    <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.endDate}"/></td>
                    <td>${ie.subscription.name}</td>
                    <td>
                      <g:each in="${ie.tipp.pkg.orgs}" var="role">
                        <g:if test="${role.roleType?.value?.equals('Content Provider')}" >${role.org.name}</g:if>
                      </g:each>
                    </td>
                    <td><div><i class="icon-globe"></i><span>${ie.tipp.platform.name}</span></div></td>
                    <td>
                      <g:each in="${ie.tipp.additionalPlatforms}" var="p">
                        <div><i class="icon-globe"></i><span>${p.platform.name}</span></div>
                      </g:each>
                    </td>
                  </tr> 
                </g:each>
              </table>
            </div>
          </div>
        </div>
      </div>
      <!--</g:if>
       End - For Development Only -->
    </div>
  
  </body>
</html>

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
    
	<g:form id="filtering-form" action="currentTitles" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="get" class="form-inline">
	    <div class="container">
	    <h1>${institution?.name} - Current Titles</h1>
	      
		<g:set var="filterSub" value="${params.filterSub?:"all"}" />
		<g:set var="filterPvd" value="${params.filterPvd?:"all"}" />
		<g:set var="filterHostPlat" value="${params.filterHostPlat?:"all"}" />
		<g:set var="filterOtherPlat" value="${params.filterOtherPlat?:"all"}" />
      
      	<select size="5" name="filterSub" class="span3"> <!-- onchange="this.form.submit()" -->
      		<option<%= (filterSub.equals("all")) ? ' selected="selected"' : '' %> value="all">All Subscriptions</option>
      		<g:each in="${subscriptions}" var="s">
              <option<%= (filterSub.equals(s.id.toString())) ? ' selected="selected"' : '' %> value="${s.id}">
                ${s.name} <g:if test="${s.consortia}">( ${s.consortia.name} )</g:if>
              </option>
            </g:each>
      	</select>
      	<select size="5" name="filterPvd" class="span3">
      		<option<%= (filterPvd.equals("all")) ? ' selected="selected"' : '' %> value="all">All Content Providers</option>
      		<g:each in="${providers}" var="p">
              <% 
              def pvdId = p.id.toString()
              def pvdName = p.name 
              %>
              <option<%= (filterPvd.equals(pvdId)) ? ' selected="selected"' : '' %> value="${pvdId}">
                ${pvdName}
              </option>
            </g:each>
      	</select>
      	<select size="5" name="filterHostPlat" class="span3">
      		<option<%= (filterHostPlat.equals("all")) ? ' selected="selected"' : '' %> value="all">All Host Platforms</option>
      		<g:each in="${hostplatforms}" var="hp">
              <% 
              def hostId = hp.id.toString()
              def hostName = hp.name 
              %>
              <option<%= (filterHostPlat.equals(hostId)) ? ' selected="selected"' : '' %> value="${hostId}">
                ${hostName}
              </option>
            </g:each>
      	</select>
      	<select size="5" name="filterOtherPlat" class="span3">
      		<option<%= (filterOtherPlat.equals("all")) ? ' selected="selected"' : '' %> value="all">All Additional Platforms</option>
      		<g:each in="${hostplatforms}" var="op">
              <% 
              def platId = op.id.toString()
              def platName = op.name 
              %>
              <option<%= (filterOtherPlat.equals(hostId)) ? ' selected="selected"' : '' %> value="${platId}">
                ${platName}
              </option>
            </g:each>
      	</select>
    	</div>
      	<br/><br/>
	    <div class="container" style="text-align:center">
	    	<div class="pull-right">
		        <input type="hidden" name="sort" value="${params.sort}">
		        <input type="hidden" name="order" value="${params.order}">
		        <label>Search text:</label> 
		        <input name="filter" value="${params.filter}" placeholder="enter search term..."/>
		        <label>Subscription Valid On</label> <input name="validOn" type="text" value="${validOn}"/>
		        <input type="submit" class="btn btn-primary" value="Reload"/>
	        </div>
	    </div>
    </g:form>
  	<br/>
  	
    <div class="container">
      <dl>
        <dt>Titles ( ${offset+1} to ${offset+(titles.size())} of ${num_ti_rows} )</dt>
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
                        (<g:link controller="subscriptionDetails" action="index" id="${ie.subscription.id}">${ie.subscription.name}</g:link>;
                        <g:link controller="issueEntitlement" action="show" id="${ie.id}">Full details</g:link>)
                      </p>
                    </g:if>
                  </g:each>
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
      
      
      <!-- For Test Only -->
      <g:if env="development">
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
      </g:if>
      <!-- End - For Test Only -->
    </div>
  
  </body>
</html>

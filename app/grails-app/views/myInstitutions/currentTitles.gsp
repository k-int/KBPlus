<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} Titles</title>
    
    <style>
      .filtering-dropdown-menu {max-height: 400px; overflow: hidden; overflow-y: auto;}
    </style>
  </head>

  <body>
    <div class="container">
        <ul class="breadcrumb">
          <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
          <li> <g:link controller="myInstitutions" action="currentTitles" params="[shortcode:params.shortcode]">${institution.name}  Current Titles</g:link> </li>
          <li class="dropdown pull-right">
            <a class="dropdown-toggle badge" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">
              Exports<b class="caret"></b></a>&nbsp;
            <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
	            <li>
	              <g:link action="currentTitles" params="${params+[format:'csv']}">CSV Export</g:link>
	            </li>
	            <li>
	              <g:link action="currentTitles" params="${params+[format:'json']}">Json Export</g:link>
	            </li>
	            <li>
	              <g:link action="currentTitles" params="${params+[format:'xml',shortcode:params.shortcode]}">XML Export</g:link>
	            </li>

              <g:each in="${transforms}" var="transkey,transval">
                <li><g:link action="currentTitles" id="${params.id}" params="${params+[format:'xml',transformId:transkey]}"> ${transval.name}</g:link></li>
              </g:each>

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
	      
		<g:set var="filterSub" value="${params.filterSub?params.list('filterSub'):"all"}" />
		<g:set var="filterPvd" value="${params.filterPvd?params.list('filterPvd'):"all"}" />
		<g:set var="filterHostPlat" value="${params.filterHostPlat?params.list('filterHostPlat'):"all"}" />
		<g:set var="filterOtherPlat" value="${params.filterOtherPlat?params.list('filterOtherPlat'):"all"}" />
      
      	<select size="5" name="filterSub" class="span3" multiple="multiple"> <!-- onchange="this.form.submit()" -->
      		<option<%= (filterSub.contains("all")) ? ' selected="selected"' : '' %> value="all">All Subscriptions</option>
      		<g:each in="${subscriptions}" var="s">
              <option<%= (filterSub.contains(s.id.toString())) ? ' selected="selected"' : '' %> value="${s.id}" title="${s.name}${s.consortia?' ('+s.consortia.name+')':''}">
                ${s.name} <g:if test="${s.consortia}">( ${s.consortia.name} )</g:if>
              </option>
            </g:each>
      	</select>
      	<select size="5" name="filterPvd" class="span3" multiple="multiple">
      		<option<%= (filterPvd.contains("all")) ? ' selected="selected"' : '' %> value="all">All Content Providers</option>
      		<g:each in="${providers}" var="p">
              <% 
              def pvdId = p.id.toString()
              def pvdName = p.name 
              %>
              <option<%= (filterPvd.contains(pvdId)) ? ' selected="selected"' : '' %> value="${pvdId}" title="${pvdName}">
                ${pvdName}
              </option>
            </g:each>
      	</select>
      	<select size="5" name="filterHostPlat" class="span3" multiple="multiple">
      		<option<%= (filterHostPlat.contains("all")) ? ' selected="selected"' : '' %> value="all">All Host Platforms</option>
      		<g:each in="${hostplatforms}" var="hp">
              <% 
              def hostId = hp.id.toString()
              def hostName = hp.name 
              %>
              <option<%= (filterHostPlat.contains(hostId)) ? ' selected="selected"' : '' %> value="${hostId}" title="${hostName}">
                ${hostName}
              </option>
            </g:each>
      	</select>
      	<select size="5" name="filterOtherPlat" class="span3" multiple="multiple">
      		<option<%= (filterOtherPlat.contains("all")) ? ' selected="selected"' : '' %> value="all">All Additional Platforms</option>
      		<g:each in="${otherplatforms}" var="op">
              <% 
              def platId = op.id.toString()
              def platName = op.name 
              %>
              <option<%= (filterOtherPlat.contains(platId)) ? ' selected="selected"' : '' %> value="${platId}" title="${platName}">
                ${platName}
              </option>
            </g:each>
      	</select>
    	</div>
      	<br/>
	    <div class="container" style="text-align:center">
      		<div class="pull-left">
      			<label class="checkbox">
      				<input type="checkbox" name="filterMultiIE" value="${true}"<%=(params.filterMultiIE)?' checked="true"':''%>/> Titles we subscribe to through 2 or more packages
				</label>
      		</div>
	    	<div class="pull-right">
		        <input type="hidden" name="sort" value="${params.sort}">
		        <input type="hidden" name="order" value="${params.order}">
		        <label>Search text:</label> 
		        <input name="filter" value="${params.filter}" placeholder="enter search term..."/>
		        <label>Subscriptions Valid on</label> 
                        <g:simpleHiddenValue id="validOn" name="validOn" type="date" value="${validOn}"/>
		        &nbsp;<input type="submit" class="btn btn-primary" value="Search"/>
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
                <td><g:link controller="titleDetails" action="show" id="${ti[0].id}">${ti[0].title}</g:link>
                <br/> 
                <g:link controller="public" action="journalLicences" params="${['journal':'kb:'+ti[0].id,'org':institution.id]}">Check current licence terms</g:link>
                </td>
                <td style="white-space:nowrap">${ti[0].getIdentifierValue('ISSN')}</td>
                <td style="white-space:nowrap">${ti[0].getIdentifierValue('eISSN')}</td>

                <g:set var="title_coverage_info" value="${ti[0].getInstitutionalCoverageSummary(institution, session.sessionPreferences?.globalDateFormat, date_restriction)}" />

                <td  style="white-space:nowrap">${title_coverage_info.earliest}</td>
                <td  style="white-space:nowrap">${title_coverage_info.latest ?: 'To Current'}</td>
                <td>
                  <g:each in="${title_coverage_info.ies}" var="ie">
                      <p>
                        <g:link controller="subscriptionDetails" action="index" id="${ie.subscription.id}">${ie.subscription.name}</g:link>:
                        <g:if test="${ie.startVolume}">Vol. ${ie.startVolume}</g:if>
                        <g:if test="${ie.startIssue}">Iss. ${ie.startIssue}</g:if>
                        <g:formatDate format="yyyy" date="${ie.startDate}"/>
                        -
                        <g:if test="${ie.endVolume}">Vol. ${ie.endVolume}</g:if>
                        <g:if test="${ie.endIssue}">Iss. ${ie.endIssue}</g:if>
                        <g:formatDate format="yyyy" date="${ie.endDate}"/>
                        (<g:link controller="issueEntitlement" action="show" id="${ie.id}">Full Issue Entitlement Details</g:link>)
                      </p>
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
      
      <g:if env="development">
      <!-- For Test Only -->
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
      <!-- End - For Test Only -->
      </g:if>
    </div>
  
  </body>
</html>

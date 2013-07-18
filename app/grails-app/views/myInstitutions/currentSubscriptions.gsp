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
        <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:params.shortcode]}">${institution.name} Current Subscriptions</g:link> </li>
        <li class="dropdown pull-right">
	        <a class="dropdown-toggle" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">
		  		Exports<b class="caret"></b>
			</a>
			<ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
				<li>
		  			<% def ps_json = [:]; ps_json.putAll(params); ps_json.format = 'json'; %>
					<g:link action="currentSubscriptions" params="${ps_json}" target="_blank">Json Export</g:link>
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

      <h1>${institution?.name} - Current Subscriptions</h1>

      <g:render template="subsNav" contextPath="." />
    </div>

    <div class="container" style="text-align:center">
      <g:form action="currentSubscriptions" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="get" class="form-inline">
        <label>Search text</label> <input type="text" name="q" placeholder="enter search term..." value="${params.q?.encodeAsHTML()}"  />
        <label>Valid On</label> <input name="validOn" type="text" value="${validOn}"/>
        <input type="submit" class="btn btn-primary" value="Search" />
      </g:form><br/>
    </div>

    <g:form action="actionCurrentSubscriptions" controller="myInstitutions" params="${[shortcode:params.shortcode]}">

      <div class="container">
        <div class="well subscription-options">
          <input type="submit" name="delete-subscription" value="Delete Selected" class="btn btn-danger delete-subscription" />
        </div>
      </div>

      <div class="container subscription-results">
        <table class="table table-striped table-bordered table-condensed">
          <tr>
            <th>Select</th>
            <g:sortableColumn params="${params}" property="s.name" title="Name" />
            <th>Linked Packages</th>
            <th>Consortia</th>
            <g:sortableColumn params="${params}" property="s.startDate" title="Start Date" />
            <g:sortableColumn params="${params}" property="s.endDate" title="End Date" />
            <th>Platform</th>
            <th>License</th>
            <g:sortableColumn params="${params}" property="s.lastUpdated" title="Last Updated" />
          </tr>
          <g:each in="${subscriptions}" var="s">
            <tr>
              <td><input type="radio" name="basesubscription" value="${s.id}"/></td>
              <td>
                <g:link controller="subscriptionDetails" action="index" id="${s.id}">
                  <g:if test="${s.name}">${s.name}</g:if><g:else>-- Name Not Set  --</g:else>
                  <g:if test="${s.consortia}">( ${s.consortia?.name} )</g:if>
                </g:link>
              </td>
              <td>
                <g:each in="${s.packages}" var="sp">
                  ${sp.pkg.name} (${sp.pkg?.contentProvider?.name}) <br/>
                </g:each>
                <g:if test="${((s.packages==null) || (s.packages.size()==0))}">
                  <i>None currently, Add packages via <g:link controller="subscriptionDetails" action="linkPackage" id="${s.id}">Link Package</g:link></i>
                </g:if>
              </td>
              <td>${s.getConsortia()?.name}</td>
              <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${s.startDate}"/></td>
              <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${s.endDate}"/></td>
              <td>
                <g:each in="${s.instanceOf?.packages}" var="sp">
                  ${sp.pkg?.nominalPlatform?.name}<br/>
                </g:each>
              </td>
              <td><g:if test="${s.owner}"><g:link controller="licenseDetails" action="index" id="${s.owner.id}">${s.owner?.reference}</g:link></g:if></td>
              <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${s.lastUpdated}"/></td>
            </tr>
          </g:each>
        </table>
      </div>

      <div class="pagination" style="text-align:center">
        <g:if test="${subscriptions}" >
          <bootstrap:paginate  action="currentSubscriptions" controller="myInstitutions" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${num_sub_rows}" />
        </g:if>
      </div>

    </g:form>

    <script type="text/javascript">
        $('.subscription-results input[type="radio"]').click(function () {
            $('.subscription-options').slideDown('fast');
        });

        $('.subscription-options .delete-subscription').click(function () {
            $('.subscription-results input:checked').each(function () {
                $(this).parent().parent().fadeOut('slow');
                $('.subscription-options').slideUp('fast');
            })
        })                
    </script>

  </body>
</html>

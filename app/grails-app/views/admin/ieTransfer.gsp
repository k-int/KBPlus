<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::IE Transfer</title>
  </head>
  <body>
  	<div class="container">
  	<h1>IE Transfer</h1>

        <g:form action="ieTransfer" method="get">
          <p>Add the appropriate ID's below. All IssueEntitlements of source will be removed and transfered to target. Detailed information and confirmation will be presented before proceeding</p>
          <dl>
            <div class="control-group">
              <dt>Database ID of IE source TIPP</dt>
              <dd>
                <input type="text" name="sourceTIPP" value="${params.sourceTIPP}" />

              </dd>
            </div>

            <div class="control-group">
              <dt>Database ID of target TIPP</dt>
              <dd>
                <input type="text" name="targetTIPP" value="${params.targetTIPP}"/>
              </dd>
            </div>
 			<g:if test="${sourceTIPPObj && targetTIPPObj}">
 			<div class="container">

				  <table class="table table-bordered">
			      <thead>
			        <th></th>
			        <th>(${params.sourceTIPP}) ${sourceTIPPObj.title.title}</th>
			        <th>(${params.targetTIPP}) ${targetTIPPObj.title.title}</th>
			      </thead>
			      <tbody>
			      <tr>
			      	<td><strong>Package</strong></td>
			      	<td>${sourceTIPPObj.pkg.name}</td>
			      	<td>${targetTIPPObj.pkg.name}</td>
			      </tr>
			      <tr>
			      	<td><strong>Start Date</strong> <br/><strong> End Date</strong></td>
			      	<td>
<g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${sourceTIPPObj.startDate}"/>
			      	 <br/>
<g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${sourceTIPPObj.endDate}"/>
 </td>
  			      	<td>
  <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${targetTIPPObj.startDate}"/>
 <br/>
 <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${targetTIPPObj.endDate}"/>
</td>
			      </tr>
			      <tr>
			      	<td><strong> Number of IEs</strong></td>
			      	<td>${com.k_int.kbplus.IssueEntitlement.countByTipp(sourceTIPPObj)}</td>
			      	<td>${com.k_int.kbplus.IssueEntitlement.countByTipp(targetTIPPObj)}</td>
			      </tr>
			      </tbody>
			      </table>
 			</div>

              <button onclick="return confirm('All source IEs will be moved to target. Continue?')" class="btn-success" name="transfer" type="submit" value="Go">Transfer</button>
  			</g:if>

            <button class="btn-primary" type="submit" value="Go">Look Up TIPP Info...</button>
          </dl>
        </g:form>
      </div>
  	</div>

  </body>
</html>
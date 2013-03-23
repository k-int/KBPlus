
<%@ page import="com.k_int.kbplus.TitleInstance" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>${titleInstanceInstance?.title} in ${tipp.pkg.name} on ${tipp.platform.name}</title>
  </head>
  <body>
    <div class="container">
      
        <div class="page-header">
          <h1>"${titleInstanceInstance?.title}" in "${tipp.pkg.name}" on ${tipp.platform.name}</h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>
        
          <g:if test="${titleInstanceInstance?.ids}">
            <dt><g:message code="titleInstance.ids.label" default="Ids" /></dt>
            
              <g:each in="${titleInstanceInstance.ids}" var="i">
              <dd>${i.identifier.ns.ns}:${i.identifier.value}</dd>
              </g:each>
            
          </g:if>
        
          <dt>TIPP Start Date</dt>
          <dd><g:xEditable owner="${tipp}" field="startDate"/></dd>

          <dt>TIPP Start Volume</dt>
          <dd><g:xEditable owner="${tipp}" field="startVolume"/></dd>

          <dt>TIPP Start Issue</dt>
          <dd><g:xEditable owner="${tipp}" field="startIssue"/></dd>

          <dt>TIPP End Date</dt>
          <dd><g:xEditable owner="${tipp}" field="endDate"/></dd>

          <dt>TIPP End Volume</dt>
          <dd><g:xEditable owner="${tipp}" field="endVolume"/></dd>

          <dt>TIPP End Issue</dt>
          <dd><g:xEditable owner="${tipp}" field="endIssue"/></dd>

          <dt>Coverage Depth</dt>
          <dd><g:xEditable owner="${tipp}" field="coverageDepth"/></dd>

          <dt>Coverage Note</dt>
          <dd><g:xEditable owner="${tipp}" field="coverageNote"/></dd>

          <dt>Embargo</dt>
          <dd><g:xEditable owner="${tipp}" field="embargo"/></dd>

          <dt>Host URL</dt>
          <dd><g:xEditable owner="${tipp}" field="hostPlatformURL"/></dd>

          <dt>Host Platform</dt>
          <dd>${tipp.platform.name}</dd>



          <g:if test="${titleInstanceInstance?.tipps}">
            <dt><g:message code="titleInstance.tipps.label" default="Occurences of this title against Packages / Platforms" /></dt>
            <dd>
            <table border="1" cellpadding="5" cellspacing="5">
              <tr>
                <th>From Date</th><th>From Volume</th><th>From Issue</th>
                <th>To Date</th><th>To Volume</th><th>To Issue</th><th>Coverage Depth</th>
                <th>Platform</th><th>Package</th><th>Actions</th>
              </tr>
              <g:each in="${titleInstanceInstance.tipps}" var="t">
                <tr>
                  <td><g:formatDate format="dd MMM yyyy" date="${t.startDate}"/></td>
                  <td>${t.startVolume}</td>
                  <td>${t.startIssue}</td>
                  <td><g:formatDate format="dd MMM yyyy" date="${t.endDate}"/></td>
                  <td>${t.endVolume}</td>
                  <td>${t.endIssue}</td>
                  <td>${t.coverageDepth}</td>
                  <td><g:link controller="platform" action="show" id="${t.platform.id}">${t.platform.name}</g:link></td>
                  <td><g:link controller="package" action="show" id="${t.pkg.id}">${t.pkg.name} (${t.pkg.contentProvider?.name})</g:link></td>
                  <td></td>
                </tr>
              </g:each>

            </table>
            </dd>
          </g:if>
        
        </dl>

    </div>
  </body>
</html>

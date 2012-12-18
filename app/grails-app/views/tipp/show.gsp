
<%@ page import="com.k_int.kbplus.TitleInstance" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'titleInstance.label', default: 'TitleInstance')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="container">
      
        <div class="page-header">
          <h1>Title Instance: ${titleInstanceInstance?.title}</h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>
        
          <g:if test="${titleInstanceInstance?.title}">
            <dt><g:message code="titleInstance.title.label" default="Title" /></dt>
              <dd><g:fieldValue bean="${titleInstanceInstance}" field="title"/></dd>
          </g:if>

          <g:if test="${titleInstanceInstance?.ids}">
            <dt><g:message code="titleInstance.ids.label" default="Ids" /></dt>
            
              <g:each in="${titleInstanceInstance.ids}" var="i">
              <dd>${i.identifier.ns.ns}:${i.identifier.value}</dd>
              </g:each>
            
          </g:if>
        
          <g:if test="${titleInstanceInstance?.impId}">
            <dt><g:message code="titleInstance.impId.label" default="Imp Id" /></dt>
            
              <dd><g:fieldValue bean="${titleInstanceInstance}" field="impId"/></dd>
            
          </g:if>
        
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

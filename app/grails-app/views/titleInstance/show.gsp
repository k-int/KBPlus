
<%@ page import="com.k_int.kbplus.TitleInstance" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'titleInstance.label', default: 'TitleInstance')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="row-fluid">
      
      <div class="span2">
        <div class="well">
          <ul class="nav nav-list">
            <li class="nav-header">${entityName}</li>
            <li>
              <g:link class="list" action="list">
                <i class="icon-list"></i>
                <g:message code="default.list.label" args="[entityName]" />
              </g:link>
            </li>
<sec:ifAnyGranted roles="ROLE_EDITOR,ROLE_ADMIN">
            <li>
              <g:link class="create" action="create">
                <i class="icon-plus"></i>
                <g:message code="default.create.label" args="[entityName]" />
              </g:link>
            </li>
            </sec:ifAnyGranted>
          </ul>
        </div>
      </div>
      
      <div class="span10">

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
                  <td><g:link controller="titleInstancePackagePlatform" action="show" id="${t.id}">Full TIPP record</g:link></td>
                </tr>
              </g:each>

            </table>
            </dd>
          </g:if>
        
        </dl>

        <g:form>
          <sec:ifAnyGranted roles="ROLE_EDITOR,ROLE_ADMIN">
          <g:hiddenField name="id" value="${titleInstanceInstance?.id}" />

          <div class="form-actions">
            <g:link class="btn" action="edit" id="${titleInstanceInstance?.id}">
              <i class="icon-pencil"></i>
              <g:message code="default.button.edit.label" default="Edit" />
            </g:link>
            <button class="btn btn-danger" type="submit" name="_action_delete">
              <i class="icon-trash icon-white"></i>
              <g:message code="default.button.delete.label" default="Delete" />
            </button>
          </div>
          </sec:ifAnyGranted>
        </g:form>

      </div>

    </div>
  </body>
</html>

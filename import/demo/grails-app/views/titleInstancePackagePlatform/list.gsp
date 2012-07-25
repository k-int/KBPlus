
<%@ page import="com.k_int.kbplus.TitleInstancePackagePlatform" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="row-fluid">
      
      <div class="span2">
        <div class="well">
          <ul class="nav nav-list">
            <li class="nav-header">${entityName}</li>
            <li class="active">
              <g:link class="list" action="list">
                <i class="icon-list icon-white"></i>
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
          <h1><g:message code="default.list.label" args="[entityName]" /> (Showing ${titleInstancePackagePlatformInstanceTotal})...</h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>
        
        <table class="table table-striped">
          <thead>
              <g:sortableColumn property="title.title" title="${message(code: 'titleInstancePackagePlatform.title.label', default: 'Title')}" />
              <g:sortableColumn property="platform.name" title="${message(code: 'titleInstancePackagePlatform.title.label', default: 'Platform')}" />
              <g:sortableColumn property="pkg.name" title="${message(code: 'titleInstancePackagePlatform.title.label', default: 'Package')}" />
              <g:sortableColumn property="startDate" title="${message(code: 'titleInstancePackagePlatform.startDate.label', default: 'Start Date')}" />
              <g:sortableColumn property="startVolume" title="${message(code: 'titleInstancePackagePlatform.startVolume.label', default: 'Start Volume')}" />
              <g:sortableColumn property="startIssue" title="${message(code: 'titleInstancePackagePlatform.startIssue.label', default: 'Start Issue')}" />
              <g:sortableColumn property="endDate" title="${message(code: 'titleInstancePackagePlatform.endDate.label', default: 'End Date')}" />
              <g:sortableColumn property="endVolume" title="${message(code: 'titleInstancePackagePlatform.endVolume.label', default: 'End Volume')}" />
              <g:sortableColumn property="endIssue" title="${message(code: 'titleInstancePackagePlatform.endIssue.label', default: 'End Issue')}" />
              <th></th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${titleInstancePackagePlatformInstanceList}" var="tipp">
            <tr>
              <td><g:link controller="titleInstance" action="show" id="${tipp.title.id}">${fieldValue(bean: tipp, field: "title.title")}</g:link></td>
              <td><g:link controller="platform" action="show" id="${tipp.platform.id}">${fieldValue(bean: tipp, field: "platform.name")}</g:link></td>
              <td><g:link controller="package" action="show" id="${tipp.pkg.id}">${fieldValue(bean: tipp, field: "pkg.name")}</g:link></td>
              <td><g:formatDate format="dd MMMM yyyy" date="${tipp.startDate}" /></td>
              <td>${fieldValue(bean: tipp, field: "startVolume")}</td>
              <td>${fieldValue(bean: tipp, field: "startIssue")}</td>
              <td><g:formatDate format="dd MMMM yyyy" date="${tipp.endDate}" /></td>
              <td>${fieldValue(bean: tipp, field: "endVolume")}</td>
              <td>${fieldValue(bean: tipp, field: "endIssue")}</td>
              <td class="link">
                <g:link action="show" id="${tipp.id}" class="btn btn-small">Show &raquo;</g:link>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
        <div class="pagination">
          <bootstrap:paginate total="${titleInstancePackagePlatformInstanceTotal}" />
        </div>
      </div>

    </div>
  </body>
</html>

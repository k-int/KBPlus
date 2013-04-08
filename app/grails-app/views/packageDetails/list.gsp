
<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="container">
      <div class="page-header">
        <h1><g:message code="default.list.label" args="[entityName]" /></h1>
      </div>

      <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>
        
      <table class="table table-bordered table-striped">
        <thead>
          <tr>
            <g:sortableColumn property="identifier" title="${message(code: 'package.identifier.label', default: 'Identifier')}" />
            <g:sortableColumn property="name" title="${message(code: 'package.name.label', default: 'Name')}" />
            <th></th>
          </tr>
        </thead>
        <tbody>
          <g:each in="${packageInstanceList}" var="packageInstance">
            <tr>
              <td>${fieldValue(bean: packageInstance, field: "identifier")}</td>
              <td>${fieldValue(bean: packageInstance, field: "name")}</td>
              <td class="link">
                <g:link action="show" id="${packageInstance.id}" class="btn btn-small">Show &raquo;</g:link>
              </td>
            </tr>
          </g:each>
        </tbody>
      </table>
      <div class="pagination">
        <bootstrap:paginate total="${packageInstanceTotal}" />
      </div>
    </div>
  </body>
</html>

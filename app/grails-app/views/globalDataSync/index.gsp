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
        <h1>Global Data Download - Search for global packages</h1>
      </div>
      <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>
    </div>

    <div class="container" style="text-align:center">
      <g:form action="list" method="get" class="form-inline">
        <label>Search text</label> <input type="text" name="q" placeholder="enter search term..." value="${params.q?.encodeAsHTML()}"  />
        <input type="submit" class="btn btn-primary" value="Search" />
      </g:form><br/>
    </div>

    <div class="container">
        
      <table class="table table-bordered table-striped">
        <thead>
          <tr>
            <g:sortableColumn property="identifier" title="${message(code: 'package.identifier.label', default: 'Identifier')}" />
            <g:sortableColumn property="name" title="${message(code: 'package.name.label', default: 'Name')}" />
            <g:sortableColumn property="dateCreated" title="${message(code: 'package.dateCreated.label', default: 'Created')}" />
            <g:sortableColumn property="lastUpdated" title="${message(code: 'package.lastUpdated.label', default: 'Last Updated')}" />
            <th></th>
          </tr>
        </thead>
        <tbody>
          <g:each in="${packageInstanceList}" var="packageInstance">
            <tr>
              <td>${fieldValue(bean: packageInstance, field: "identifier")}</td>
              <td>${fieldValue(bean: packageInstance, field: "name")}</td>
              <td>${fieldValue(bean: packageInstance, field: "dateCreated")}</td>
              <td>${fieldValue(bean: packageInstance, field: "lastUpdated")}</td>
              <td class="link">
                <g:link action="show" id="${packageInstance.id}" class="btn btn-small">Show &raquo;</g:link>
              </td>
            </tr>
          </g:each>
        </tbody>
      </table>
      <div class="pagination">
        <bootstrap:paginate  action="index" controller="globalDataSync" params="${params}" next="Next" prev="Prev" max="${max}" total="${globalItemTotal}" />
      </div>
    </div>
  </body>
</html>

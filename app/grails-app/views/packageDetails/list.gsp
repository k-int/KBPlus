
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
        <h1>Packages</h1>
      </div>
      <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>
    </div>

    <div class="container" style="text-align:center">
      <g:form action="list" method="get" class="form-inline">
        <table>
          <tr>
            <td >
              <label>Search text : </label> <input type="text" name="q" placeholder="enter search term..." value="${params.q?.encodeAsHTML()}"  /> &nbsp;
            </td>
            <td style="width:30%;">
              <label>Updated After : </label> <input name="updateStartDate" type="date" value="${params.updateStartDate}"/> &nbsp;
              <label>Created After : </label> <input name="createStartDate" type="date" value="${params.createStartDate}"/> &nbsp;
            </td>
            <td style="width:30%;">
              <label>Updated Before : </label> <input name="updateEndDate" type="date" value="${params.updateEndDate}"/> &nbsp;
              <label>Created Before : </label> <input name="createEndDate" type="date" value="${params.createEndDate}"/> &nbsp;
            </td>
            <td >
              <input type="submit" class="btn btn-primary" value="Search" />
              <button type="submit" name="format" value="csv" class="btn btn-primary" value="Search">Export</button>
            </td>
          </tr>
        </table>
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
        <bootstrap:paginate  action="list" controller="packageDetails" params="${params}" next="Next" prev="Prev" max="${max}" total="${packageInstanceTotal}" />
      </div>
    </div>
  </body>
</html>

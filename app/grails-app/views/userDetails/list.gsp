<%@ page import="com.k_int.kbplus.*" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="container">
      
        
        <div class="page-header">
          <h1>Users</h1>
        </div>

        <div class="well">
          <g:form action="list" method="get">
            Name Contains: <input type="text" name="name" value="${params.name}"/>
            <input type="submit" value="GO ->" class="btn btn-primary"/> (${count} Matches)
          </g:form>
        </div>

        <g:if test="${flash.message}">
          <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>
        
        <table class="table table-striped">
          <thead>
            <tr>
              <g:sortableColumn property="name" title="${message(code: 'user.name.label', default: 'User Name')}" />
              <g:sortableColumn property="name" title="${message(code: 'user.display.label', default: 'Display Name')}" />
              <g:sortableColumn property="name" title="${message(code: 'user.instname.label', default: 'Institution')}" />
            </tr>
          </thead>
          <tbody>
          <g:each in="${users}" var="user">
            <tr>
              <td><g:link  action="edit" id="${user.id}">${fieldValue(bean: user, field: "username")}</g:link></td>
              <td>${fieldValue(bean: user, field: "display")}</td>
              <td>${fieldValue(bean: user, field: "instname")}</td>
            </tr>
          </g:each>
          </tbody>
        </table>

        <div class="pagination">
          <bootstrap:paginate total="${total}" params="${params}" />
        </div>

    </div>
  </body>
</html>

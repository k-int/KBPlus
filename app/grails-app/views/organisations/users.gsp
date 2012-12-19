<%@ page import="com.k_int.kbplus.Org" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'org.label', default: 'Org')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>

    <div class="container">
      <h1>${orgInstance.name}</h1>
      <g:render template="nav" contextPath="." />
    </div>

    <div class="container">
      

      <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>


      <table  class="table table-striped table-bordered">
        <tr>
          <th>User</th>
          <th>Role</th>
          <th>Actions</th>
        </tr>
 
        <g:each in="${orgInstance.affiliations}" var="a">
          <tr>
            <td>${a.user.display}</td>
            <td>${a.formalRole.authority}</td>
            <td></td>
          </tr>
        </g:each>
      </table>
    </div>
  </body>
</html>

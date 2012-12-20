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
          <th>Status</th>
          <th>Actions</th>
        </tr>
 
        <g:each in="${orgInstance.affiliations}" var="a">
          <tr>
            <td>${a.user.display}</td>
            <td>${a.formalRole.authority}</td>
            <td>
              <g:if test="${a.status==0}">Pending</g:if>
              <g:if test="${a.status==1}">Approved</g:if>
              <g:if test="${a.status==2}">Rejected</g:if>
              <g:if test="${a.status==3}">Auto Approved</g:if>
            </td>
            <td>
              <g:if test="${((a.status==1 ) || (a.status==3)) }">
                <g:link controller="organisations" action="revokeRole" params="${[grant:a.id, id:params.id]}" class="btn">Revoke</g:link>
              </g:if>
              <g:else>
                <g:link controller="organisations" action="enableRole" params="${[grant:a.id, id:params.id]}" class="btn">Allow</g:link>
              </g:else>
              <g:link controller="organisations" action="deleteRole" params="${[grant:a.id, id:params.id]}" class="btn">Delete Link</g:link>
            </td>
          </tr>
        </g:each>
      </table>
    </div>
  </body>
</html>

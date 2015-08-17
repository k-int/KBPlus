<%@ page import="com.k_int.kbplus.Org" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'org.label', default: 'Org')}" />
    <title>KB+ <g:message code="default.show.label" args="[entityName]" /></title>
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
          <th>Email</th>
          <th>System Role</th>
          <th>Institutional Role</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
 
        <g:each in="${users}" var="userOrg">
          <tr>
            <td><g:link controller="userDetails" action="edit" id="${userOrg[0].user.id}">${userOrg[0].user.displayName} ${userOrg[0].user.defaultDash?.name?"(${userOrg[0].user.defaultDash.name})":""}</g:link></td>
            <td>
            ${userOrg[0].user.email}
            </td>
            <td>
              <g:if test="${userOrg[1]}">
                <ul>
                  <g:each in="${userOrg[1]}" var="admRole">
                    <li>${admRole}</li>
                  </g:each>
                </ul>
                </g:if>
            </td>
            <td>${userOrg[0].formalRole?.authority}</td>
            <td>
              <g:if test="${userOrg[0].status==0}">Pending</g:if>
              <g:if test="${userOrg[0].status==1}">Approved</g:if>
              <g:if test="${userOrg[0].status==2}">Rejected</g:if>
              <g:if test="${userOrg[0].status==3}">Auto Approved</g:if>
            </td>
            <td>
              <g:if test="${editable}">
              <g:if test="${((userOrg[0].status==1 ) || (userOrg[0].status==3)) }">
                <g:link controller="organisations" action="revokeRole" params="${[grant:userOrg[0].id, id:params.id]}" class="btn">Revoke</g:link>
              </g:if>
              <g:else>
                <g:link controller="organisations" action="enableRole" params="${[grant:userOrg[0].id, id:params.id]}" class="btn">Allow</g:link>
              </g:else>
              <g:link controller="organisations" action="deleteRole" params="${[grant:userOrg[0].id, id:params.id]}" class="btn">Delete Link</g:link>
            </g:if>
            </td>
          </tr>
        </g:each>
      </table>
    </div>
  </body>
</html>

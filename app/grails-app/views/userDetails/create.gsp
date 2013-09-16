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
          <h1>New User</h1>
        </div>

        <g:if test="${flash.message}">
          <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>
        
        <div class="well">
          <g:form action="create" method="post">
             <div class="inline-lists">
               <dl><dt>Username</dt><dd><input type="text" name="username" value="${params.username}"/></dd></dl>
               <dl><dt>Dispay Name</dt><dd><input type="text" name="display" value="${params.display}"/></dd></dl>
               <dl><dt>Password</dt><dd><input type="password" name="password" value="${params.password}"/></dd></dl>
               <dl><dt>eMail</dt><dd><input type="text" name="email" value="${params.email}"/></dd></dl>
               <dl><dt></td><dd><input type="submit" value="GO ->" class="btn btn-primary"/></dd></dl>
            </div>
          </g:form>
        </div>

    </div>
  </body>
</html>

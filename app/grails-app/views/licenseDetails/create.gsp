<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
  </head>
  <body>
      <div class="container">
        <div class="row">
          <div class="span12">

            <div class="page-header">
              <h1>New Template Licence</h1>
            </div>

            <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <g:if test="${flash.error}">
            <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
            </g:if>

            <p>Use this form to create a new template licence. Enter the new licence reference below, click "continue" and you will be redirected to the new licence</p>

            <p>
              <g:form action="processNewTemplateLicense"> New licence Reference: <input type="text" name="reference"/><input type="submit"/></g:form>
            </p>

          </div>
        </div>
      </div>

  </body>
</html>

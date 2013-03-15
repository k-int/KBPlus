<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
    <!-- r:require modules="bootstrap-typeahead"-->
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
      <div class="container">

        <div class="page-header">
          <h1>Subscription Offered - Manual Upload</h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <g:if test="${flash.error}">
        <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
        </g:if>

        <g:hasErrors bean="${packageInstance}">
        <bootstrap:alert class="alert-error">
        <ul>
          <g:eachError bean="${packageInstance}" var="error">
          <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
          </g:eachError>
        </ul>
        </bootstrap:alert>
        </g:hasErrors>

        <g:form action="reviewSO" method="post" enctype="multipart/form-data">
            <input type="file" id="soFile" name="soFile"/>
            <button type="submit" class="btn btn-primary">Upload SO</button>
        </g:form>


        <g:if test="${validationResult}">
          <pre>
            ${validationResult}
          </pre>
        </g:if>
        
        <table class="table">
          <tbody>
            <tr><td>SO Name</td><td>${validationResult.soName?.value}</td></tr>
          </tbody>
        </table>

        <table class="table">
          <tbody>
            <g:each in="${validationResult.tipps}" var="tipp">
              <tr><td><pre>tipp:${tipp}</pre></td></tr>
            </g:each>
          </tbody>
        </table>
        
      </div>

  </body>
</html>

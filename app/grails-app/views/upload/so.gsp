<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
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

        <g:form action="so" method="post" enctype="multipart/form-data">
            Updload File: <input type="file" id="soFile" name="soFile"/><br/>

            Doc Style: <select name="docstyle">
              <option value="csv" selected>Comma Separated</option>
              <option value="tsv">Tab Separated</option>
            </select></br/>

            Override Character Set Test: <input type="checkbox" name="OverrideCharset" checked="false"/>

            <button type="submit" class="btn btn-primary">Upload SO</button>
        </g:form>

        <g:if test="${new_pkg_id && new_sub_id}">
          <g:link controller="subscriptionDetails" action="index" id="${new_sub_id}">Created subscription ${new_sub_id}</g:link><br/>
          <g:link controller="package" action="show" id="${new_pkg_id}">Created package ${new_pkg_id}</g:link>
        </g:if>
      </div>

  </body>
</html>

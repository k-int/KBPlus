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
          <h1>Package - Manual Upload</h1>
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
            Updload File: <input type="file" id="soFile" name="soFile"/><br/>

            Doc Style: <select name="docstyle">
              <option value="csv" selected>Comma Separated</option>
              <option value="tsv">Tab Separated</option>
            </select></br/>

            Override Character Set Test: <input type="checkbox" name="OverrideCharset" checked="false"/>

            <button type="submit" class="btn btn-primary">Upload SO</button>
        </g:form>
        
        <br/>

        <g:if test="${validationResult}">
          <g:if test="${validationResult.stats != null}">
            <h3>Stats</h3>
            <ul>
              <g:each in="${validationResult?.stats}" var="msg">
                <li>${msg.key} = ${msg.value}</li>
              </g:each>
            </ul>
          </g:if>

          <g:each in="${validationResult?.messages}" var="msg">
            <div class="alert alert-error">${msg}</div>
          </g:each>

          <hr/>

          <g:if test="${validationResult.processFile==true}">
            <bootstrap:alert class="alert-success">File passed validation checks, new SO details follow:<br/>
              <g:link controller="packageDetails" action="show" id="${validationResult.new_pkg_id}">New Package Details</g:link><br/>
            </bootstrap:alert>
          </g:if>
          <g:else>
            <div class="alert alert-error">File failed validation checks, details follow</div>
          </g:else>
          <table class="table">
            <tbody>
              <g:each in="${['soName', 'soIdentifier', 'soProvider', 'soPackageIdentifier', 'soPackageName', 'aggreementTermStartYear', 'aggreementTermEndYear', 'consortium', 'numPlatformsListed']}" var="fld">
                <tr>
                  <td>${fld}</td>
                  <td>${validationResult[fld]?.value} 
                    <g:if test="${validationResult[fld]?.messages != null}">
                      <hr/>
                      <g:each in="${validationResult[fld]?.messages}" var="msg">
                        <div class="alert alert-error">${msg}</div>
                      </g:each>
                    </g:if>
                  </td>
                </tr>
              </g:each>
            </tbody>
          </table>

          <table class="table">
            <thead>
              <tr>
                <g:each in="${validationResult.soHeaderLine}" var="c">
                  <th>${c}</th>
                </g:each>
              </tr>
            </thead>
            <tbody>
              <g:each in="${validationResult.tipps}" var="tipp">
              
                <tr>
                  <g:each in="${tipp.row}" var="c">
                    <td>${c}</td>
                  </g:each>
                </tr>
                
                <g:if test="${tipp.messages?.size() > 0}">
                  <tr>
                    <td colspan="${validationResult.soHeaderLine.size()}">
                      <ul>
                        <g:each in="${tipp.messages}" var="msg">
                          <g:if test="${msg instanceof java.lang.String || msg instanceof org.codehaus.groovy.runtime.GStringImpl}">
                            <div class="alert alert-error">${msg}</div>
                          </g:if>
                          <g:else>
                            <div class="alert ${msg.type}">${msg.message}</div>
                          </g:else>
                        </g:each>
                      </ul>
                    </td>
                  </tr>
                </g:if>
              </g:each>
            </tbody>
          </table>
          
        </g:if>
        
      </div>

  </body>
</html>

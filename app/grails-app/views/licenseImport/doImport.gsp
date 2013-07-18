<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <!--<g:set var="entityName" value="${message(code: 'license.label',
            default: 'License')}" />-->
    <title><g:message code="default.import.label" args="[entityName]" /></title>
  </head>
  <body>
      <div class="container">

        <div class="page-header">
            <h1>Import ONIX-PL License</h1>
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

          <g:form action="doImport" method="post" enctype="multipart/form-data">
              Upload File: <input type="file" id="importFile" name="importFile"/>
              <br/>
              Title: <input type="text" name="upload_title"/>
              <br/>
              <button type="submit" class="btn btn-primary">
                  Import license
              </button>
          </g:form>

          <br/>
${result}
      <%-- Show summary --%>
          <g:if test="${result}">
              Upload successful
              <g:if test="${result.file}">
              <div class="alert alert-success">
                  Imported ${result.filename} (${result.contentType})
                  and associated with license ${result.license.id}.
              </div>
              </g:if>
              <g:else>
                  You messed up fool!
              </g:else>

              <g:if test="${result.termStatuses!= null}">
                  <div class="alert alert-info">
                      <h2>Usage terms summary</h2>
                      <ul>
                          <g:each in="${result?.termStatuses}" var="ts">
                              <li>${ts.value} × ${ts.key.value}</li>
                          </g:each>
                      </ul>
                  </div>
              </g:if>

              <g:each in="${result?.messages}" var="msg">
                  <div class="alert alert-error">${msg}</div>
              </g:each>

              <hr/>

          <%--
      <g:if test="${result.processFile==true}">
          <bootstrap:alert class="alert-success">File passed validation checks, new SO details follow:<br/>
              <g:link controller="packageDetails" action="show" id="${result.new_pkg_id}">New Package Details</g:link><br/>
              <g:link controller="subscriptionDetails" action="index" id="${result.new_sub_id}">New Subscription Details</g:link>
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
                  <td>${result[fld]?.value}
                      <g:if test="${result[fld]?.messages != null}">
                          <hr/>
                          <g:each in="${result[fld]?.messages}" var="msg">
                              <div class="alert alert-error">${msg}</div>
                          </g:each>
                      </g:if>
                  </td>
              </tr>
          </g:each>
          </tbody>
      </table>

--%>

          </g:if>

      </div>
  </body>
</html>

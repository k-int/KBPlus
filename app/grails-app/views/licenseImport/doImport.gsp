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

          <hr/>
      <%-- Show summary --%>
          <g:if test="${validationResult}">

              <g:if test="${validationResult.messages!=null}">
                  <g:each in="${validationResult.messages}" var="msg">
                      <div class="alert alert-info">${msg}</div>
                  </g:each>
              </g:if>


              <g:if test="${validationResult.errors!=null}">
                  <g:each in="${validationResult.errors}" var="msg">
                      <div class="alert alert-error">${msg}</div>
                  </g:each>
              </g:if>

              <g:if test="${validationResult.termStatuses!=null}">
                  <div class="alert alert-info">
                      <h2>Usage terms summary</h2>
                      <ul>
                          <g:each in="${validationResult.termStatuses}" var="ts">
                              <li>${ts.value} × ${ts.key}</li>
                          </g:each>
                      </ul>
                  </div>
              </g:if>

              <g:if test="${validationResult.success==true}">
                  Upload successful
                  <div class="alert alert-success">
                      Imported ${validationResult.filename} (${validationResult.contentType})
                      and associated with license ${validationResult.license?validationResult.license.id:"none"}.
                  </div>
              </g:if>


          <%--<ul><g:each in="${validationResult}" var="e"><li>${e}</li></g:each></ul>--%>


          </g:if>

      </div>
  </body>
</html>

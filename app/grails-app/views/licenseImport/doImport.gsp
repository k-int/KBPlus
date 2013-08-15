<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'opl.license.label',
            default: 'ONIX-PL License')}" />
    <title><g:message code="default.import.label" args="[entityName]" /></title>
  </head>
  <body>
      <div class="container">

        <div class="page-header">
            <h1>Import ONIX-PL License
            <g:if test="${license}"> for license '${license.reference}'</g:if>
            <g:else> for unspecified license</g:else>
            </h1>
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
              <button type="submit" class="btn btn-primary">
                  Import license
              </button>
              <g:hiddenField name="license_id" value="${params.license_id}" />
              <g:hiddenField name="upload_title" value="${upload_title}" />
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
              <g:else>
                  <%--<div class="alert alert-info">No messages!</div>--%>
              </g:else>


              <g:if test="${validationResult.errors!=null}">
                  <g:each in="${validationResult.errors}" var="msg">
                      <div class="alert alert-error">${msg}</div>
                  </g:each>
              </g:if>

              <g:if test="${validationResult.success==true}">
                  <div class="alert alert-success">
                      <h2>Upload successful</h2>

                      Imported ${validationResult.filename} (${validationResult.contentType})
                      <g:if test="${validationResult.license}">
                          and associated with
                          <g:link action="index"
                                  controller="licenseDetails"
                                  id="${validationResult.license.id}">
                              license ${validationResult.license.id}
                          </g:link>
                          ('${validationResult.license.reference}').
                      </g:if>

                      <g:if test="${validationResult.termStatuses!=null}">
                          <h2>Usage terms summary</h2>
                          <ul>
                              <g:each in="${validationResult.termStatuses}" var="ts">
                                  <li>${ts.value} × ${ts.key}</li>
                              </g:each>
                          </ul>
                      </g:if>

                      <br/><br/>
                      <g:link action="index"
                              controller="onixplLicenseDetails"
                              class="btn btn-info"
                              id="${validationResult.onixpl_license.id}">
                          View new ONIX-PL license</g:link>

                  </div>
              </g:if>

          </g:if>

      </div>
  </body>
</html>

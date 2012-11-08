<%@ page import="com.k_int.kbplus.License" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'license.label', default: 'License')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="row-fluid">
      
      <div class="span11">

        <div class="page-header">
          <h1><g:message code="default.create.label" args="[entityName]" /></h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <g:hasErrors bean="${licenseInstance}">
        <bootstrap:alert class="alert-error">
        <ul>
          <g:eachError bean="${licenseInstance}" var="error">
          <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
          </g:eachError>
        </ul>
        </bootstrap:alert>
        </g:hasErrors>

        <fieldset>
          <g:form class="form-horizontal" 
                  action="newLicense" 
                  params="${[shortcode:params.shortcode]}">
            <fieldset>
              

              <div class="control-group ">
                <label class="control-label" for="reference">License Reference / Name</label>
                  <div class="controls">
                    <g:textField name="reference" value="${fieldValue(bean: licenseInstance, field: 'reference')}" class="large" />
                 </div>
              </div>

              <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                  <i class="icon-ok icon-white"></i>
                  <g:message code="default.button.create.label" default="Create" />
                </button>
              </div>
            </fieldset>
          </g:form>
        </fieldset>
        
      </div>

    </div>
  </body>
</html>

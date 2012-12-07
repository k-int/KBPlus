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
          <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
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

        <fieldset>
          <g:form class="form-horizontal" action="edit" id="${packageInstance?.id}" autocomplete="off" >
            <g:hiddenField name="version" value="${packageInstance?.version}" />

            <!--
              packageType
              packageStatus
              contentProvider
              nominalPlatform
              packageListStatus
              identifier
              impId
              name
              orgs
              subscriptions
              tipps
            -->
            <fieldset>

              <div class="control-group ">
       	        <label class="control-label">Org Links</label>
                <div class="controls">
                  <g:render template="orgLinks" 
                            contextPath="../templates" 
                            model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',editmode:true]}" />
                </div>
              </div>

              <div class="control-group ">
       	        <label class="control-label">Package Type</label>
                <div class="controls">
                  <g:refdataValue val="${packageInstance?.packageType?.value?:'Not set'}" domain="Package" pk="${packageInstance?.id}" field="packageType" cat="Package Type" />
                </div>
              </div>

              <div class="control-group ">
       	        <label class="control-label">Content Provider</label>
                <div class="controls">
                  <g:enhancedSelect id="contentProvider"
                                    title="select content provider"
                                    owner="${packageInstance}"
                                    ownerProperty="contentProvider"
                                    refdataProfile="ContentProvider"
                                    filterFields="name">
                    <g:if test="${packageInstance.contentProvider}">${packageInstance.contentProvider?.name}</g:if>
                    <g:else>Not Set</g:else>
                  </g:enhancedSelect>
                </div>
              </div>


              <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                  <i class="icon-ok icon-white"></i>
                  <g:message code="default.button.update.label" default="Update" />
                </button>
                <button type="submit" class="btn btn-danger" name="_action_delete" formnovalidate>
                  <i class="icon-trash icon-white"></i>
                  <g:message code="default.button.delete.label" default="Delete" />
                </button>
              </div>


              
            </fieldset>
          </g:form>
        </fieldset>
      </div>

    <g:render template="enhanced_select" contextPath="../templates" />
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',recip_prop:'pkg']}" />

    <script language="JavaScript">

      $(document).ready(function(){
      });

    </script>

  </body>
</html>

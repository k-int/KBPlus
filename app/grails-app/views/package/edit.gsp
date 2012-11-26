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
    <div class="row-fluid">

      <div class="span3">
        <div class="well">
          <ul class="nav nav-list">
            <li class="nav-header">${entityName}</li>
            <li>
              <g:link class="list" action="list">
                <i class="icon-list"></i>
                <g:message code="default.list.label" args="[entityName]" />
              </g:link>
            </li>
            <li>
              <g:link class="create" action="create">
                <i class="icon-plus"></i>
                <g:message code="default.create.label" args="[entityName]" />
              </g:link>
            </li>
          </ul>
        </div>
      </div>
      
      <div class="span9">

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
                            model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs']}" />
                </div>
              </div>

              <div class="control-group ">
       	        <label class="control-label">Package Type</label>
                <div class="controls">
                  <g:select id="packageTypeSelect" name="packageType.id" from="${com.k_int.kbplus.RefdataValue.executeQuery('select rv from RefdataValue as rv where rv.owner.desc=?',['Package Type'])}" optionKey="id" value="${packageInstance?.packageType?.id}" class="many-to-one" noSelection="['null': '']"/>
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
    </div>

    <g:render template="enhanced_select" contextPath="../templates" />
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs']}" />

    <script language="JavaScript">

      $(document).ready(function(){
      });

    </script>

  </body>
</html>

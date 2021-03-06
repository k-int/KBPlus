<%@ page import="com.k_int.kbplus.Package; com.k_int.kbplus.RefdataCategory;" %>
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
                  <g:relation domain='Package'
                            pk='${packageInstance?.id}'
                            field='packageType'
                            class='refdataedit'
                            id="${RefdataCategory.PKG_TYPE}">${packageInstance?.packageType?.value?:'Not set'}</g:relation>
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

    <r:script language="JavaScript">

      $(document).ready(function(){
         $('div span.refdataedit').editable('<g:createLink controller="ajax" params="${[resultProp:'value']}" action="genericSetRel" />', {
           loadurl: '<g:createLink controller="ajax" params="${[id:"${RefdataCategory.PKG_TYPE}",format:'json']}" action="refdataSearch" />',
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
           }
         });
      });

    </r:script>

  </body>
</html>

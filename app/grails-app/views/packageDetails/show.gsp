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

              <dl>
                <dt>Org Links</dt>
                <dd><g:render template="orgLinks" 
                            contextPath="../templates" 
                            model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',editmode:true]}" /></dd>
              </dl>

              <dl>
                <dt>Package Type</dt>
                <dd>
                  <g:relation domain='Package'
                            pk='${packageInstance?.id}'
                            field='packageType'
                            class='refdataedit'
                            id='PackageType'>${packageInstance?.packageType?.value?:'Not set'}</g:relation>
                </dd>
              </dl>

              <dl>
                <dt>Content Provider</dt>
                <dd>
                  <g:enhancedSelect id="contentProvider"
                                    title="select content provider"
                                    owner="${packageInstance}"
                                    ownerProperty="contentProvider"
                                    refdataProfile="ContentProvider"
                                    filterFields="name">
                    <g:if test="${packageInstance.contentProvider}">${packageInstance.contentProvider?.name}</g:if>
                    <g:else>Not Set</g:else>
                  </g:enhancedSelect>
                </dd>
              </dl>

            </fieldset>
        </fieldset>

             <div class="form-actions">
                <g:form action="uploadTitles">
                  <input type="hidden" name="id" value="${params.id}"/>
                  <input type="file" id="titleFile" name="titleFile"/>
                  Check to Replace, Unchecked to append:<input type="checkbox" id="replace" name="replace"/>
                  <button type="submit" class="btn btn-primary">Upload Titles</button>
                </g:form>
             </div>

      </div>


    <g:render template="enhanced_select" contextPath="../templates" />
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',recip_prop:'pkg']}" />

    <script language="JavaScript">

      $(document).ready(function(){
         $('dd span.refdataedit').editable('<g:createLink controller="ajax" params="${[resultProp:'value']}" action="genericSetRel" />', {
           loadurl: '<g:createLink controller="ajax" params="${[id:'PackageType',format:'json']}" action="refdataSearch" />',
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
           }
         });
      });

    </script>

  </body>
</html>

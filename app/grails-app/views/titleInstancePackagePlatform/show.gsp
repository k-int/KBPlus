
<%@ page import="com.k_int.kbplus.TitleInstancePackagePlatform" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="row-fluid">
      
      <div class="span2">
        <div class="well">
          <ul class="nav nav-list">
            <li class="nav-header">${entityName}</li>
            <li>
              <g:link class="list" action="list">
                <i class="icon-list"></i>
                <g:message code="default.list.label" args="[entityName]" />
              </g:link>
            </li>
<sec:ifAnyGranted roles="ROLE_EDITOR,ROLE_ADMIN">
            <li>
              <g:link class="create" action="create">
                <i class="icon-plus"></i>
                <g:message code="default.create.label" args="[entityName]" />
              </g:link>
            </li>
            </sec:ifAnyGranted>
          </ul>
        </div>
      </div>
      
      <div class="span10">

        <div class="page-header">
          <h1><g:message code="default.show.label" args="[entityName]" /></h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>
        
        
          <g:if test="${titleInstancePackagePlatformInstance?.pkg}">
            <dt><g:message code="titleInstancePackagePlatform.pkg.label" default="Pkg" /></dt>
              <dd><g:link controller="package" action="show" id="${titleInstancePackagePlatformInstance?.pkg?.id}">${titleInstancePackagePlatformInstance?.pkg?.name?.encodeAsHTML()} (id: ${titleInstancePackagePlatformInstance?.pkg?.identifier})</g:link></dd>
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.platform}">
            <dt><g:message code="titleInstancePackagePlatform.platform.label" default="Platform" /></dt>
            
              <dd><g:link controller="platform" action="show" id="${titleInstancePackagePlatformInstance?.platform?.id}">${titleInstancePackagePlatformInstance?.platform?.name?.encodeAsHTML()}</g:link></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.title}">
            <dt><g:message code="titleInstancePackagePlatform.title.label" default="Title" /></dt>
              <dd><g:link controller="titleInstance" action="show" id="${titleInstancePackagePlatformInstance?.title?.id}">${titleInstancePackagePlatformInstance?.title?.title?.encodeAsHTML()}</g:link></dd>
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.hostPlatformURL}">
            <dt><g:message code="titleInstancePackagePlatform.hostPlatformURL.label" default="Host Platform URL" /></dt>
              <dd><a href="${titleInstancePackagePlatformInstance?.hostPlatformURL}" target="new"><g:fieldValue bean="${titleInstancePackagePlatformInstance}" field="hostPlatformURL"/></a></dd>
          </g:if>

         <g:if test="${titleInstancePackagePlatformInstance?.additionalPlatforms}">
            <dt><g:message code="titleInstance.additionalPlatforms.label" default="Additional Platforms" /></dt>
              <g:each in="${titleInstancePackagePlatformInstance?.additionalPlatforms}" var="i">
              <dd>${i.rel} : <g:link controller="Platform" action="show" id="${i.platform.id}">${i.platform.name }</g:link>
                <g:if test="${(i.titleUrl != null ) && ( i.titleUrl.trim().length() > 0)}">( <a href="${i.titleUrl}">${i.titleUrl}</a> )</g:if>
              </dd>
              </g:each>
            
          </g:if>
        

          <g:if test="${titleInstancePackagePlatformInstance?.startDate}">
            <dt><g:message code="titleInstancePackagePlatform.startDate.label" default="Start Date" /></dt>
            
              <dd><g:formatDate format="dd MMMM yyyy" date="${titleInstancePackagePlatformInstance.startDate}" /></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.startVolume}">
            <dt><g:message code="titleInstancePackagePlatform.startVolume.label" default="Start Volume" /></dt>
            
              <dd><g:fieldValue bean="${titleInstancePackagePlatformInstance}" field="startVolume"/></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.startIssue}">
            <dt><g:message code="titleInstancePackagePlatform.startIssue.label" default="Start Issue" /></dt>
            
              <dd><g:fieldValue bean="${titleInstancePackagePlatformInstance}" field="startIssue"/></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.endDate}">
            <dt><g:message code="titleInstancePackagePlatform.endDate.label" default="End Date" /></dt>
            
              <dd><g:formatDate format="dd MMMM yyyy" date="${titleInstancePackagePlatformInstance.endDate}" /></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.endVolume}">
            <dt><g:message code="titleInstancePackagePlatform.endVolume.label" default="End Volume" /></dt>
            
              <dd><g:fieldValue bean="${titleInstancePackagePlatformInstance}" field="endVolume"/></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.endIssue}">
            <dt><g:message code="titleInstancePackagePlatform.endIssue.label" default="End Issue" /></dt>
            
              <dd><g:fieldValue bean="${titleInstancePackagePlatformInstance}" field="endIssue"/></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.embargo}">
            <dt><g:message code="titleInstancePackagePlatform.embargo.label" default="Embargo" /></dt>
            
              <dd><g:fieldValue bean="${titleInstancePackagePlatformInstance}" field="embargo"/></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.coverageDepth}">
            <dt><g:message code="titleInstancePackagePlatform.coverageDepth.label" default="Coverage Depth" /></dt>
            
              <dd><g:fieldValue bean="${titleInstancePackagePlatformInstance}" field="coverageDepth"/></dd>
            
          </g:if>
        
          <g:if test="${titleInstancePackagePlatformInstance?.coverageNote}">
            <dt><g:message code="titleInstancePackagePlatform.coverageNote.label" default="Coverage Note" /></dt>
            
              <dd><g:fieldValue bean="${titleInstancePackagePlatformInstance}" field="coverageNote"/></dd>
            
          </g:if>
        </dl>

        <g:form>
          <sec:ifAnyGranted roles="ROLE_EDITOR,ROLE_ADMIN">
          <g:hiddenField name="id" value="${titleInstancePackagePlatformInstance?.id}" />
          
          <div class="form-actions">
            <g:link class="btn" action="edit" id="${titleInstancePackagePlatformInstance?.id}">
              <i class="icon-pencil"></i>
              <g:message code="default.button.edit.label" default="Edit" />
            </g:link>
            <button class="btn btn-danger" type="submit" name="_action_delete">
              <i class="icon-trash icon-white"></i>
              <g:message code="default.button.delete.label" default="Delete" />
            </button>
          </div>
           </sec:ifAnyGranted>
        </g:form>

      </div>

    </div>
  </body>
</html>


<%@ page import="com.k_int.kbplus.TitleInstance" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'titleInstance.label', default: 'TitleInstance')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
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
          <h1><g:message code="default.show.label" args="[entityName]" /></h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>
        
          <g:if test="${titleInstanceInstance?.title}">
            <dt><g:message code="titleInstance.title.label" default="Title" /></dt>
              <dd><g:fieldValue bean="${titleInstanceInstance}" field="title"/></dd>
          </g:if>

          <g:if test="${titleInstanceInstance?.ids}">
            <dt><g:message code="titleInstance.ids.label" default="Ids" /></dt>
            
              <g:each in="${titleInstanceInstance.ids}" var="i">
              <dd>${i.identifier.ns.ns}:${i.identifier.value}</dd>
              </g:each>
            
          </g:if>
        
          <g:if test="${titleInstanceInstance?.impId}">
            <dt><g:message code="titleInstance.impId.label" default="Imp Id" /></dt>
            
              <dd><g:fieldValue bean="${titleInstanceInstance}" field="impId"/></dd>
            
          </g:if>
        
          <g:if test="${titleInstanceInstance?.tipps}">
            <dt><g:message code="titleInstance.tipps.label" default="Occurences of this title against Packages / Platforms" /></dt>
              <g:each in="${titleInstanceInstance.tipps}" var="t">
              <dd><g:link controller="titleInstancePackagePlatform" action="show" id="${t.id}">${t?.pkg?.name?.encodeAsHTML()} (id: ${t?.pkg?.identifier?.encodeAsHTML()}) / ${t?.platform?.name?.encodeAsHTML()}</g:link></dd>
              </g:each>
          </g:if>
        
        </dl>

        <g:form>
          <g:hiddenField name="id" value="${titleInstanceInstance?.id}" />
          <div class="form-actions">
            <g:link class="btn" action="edit" id="${titleInstanceInstance?.id}">
              <i class="icon-pencil"></i>
              <g:message code="default.button.edit.label" default="Edit" />
            </g:link>
            <button class="btn btn-danger" type="submit" name="_action_delete">
              <i class="icon-trash icon-white"></i>
              <g:message code="default.button.delete.label" default="Delete" />
            </button>
          </div>
        </g:form>

      </div>

    </div>
  </body>
</html>

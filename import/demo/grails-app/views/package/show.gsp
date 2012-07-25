<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
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
          <h1>Package : ${packageInstance?.name} (${packageInstance?.contentProvider?.name})</h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>
        
          <g:if test="${packageInstance?.identifier}">
            <dt><g:message code="package.identifier.label" default="Identifier" /></dt>
            
              <dd><g:fieldValue bean="${packageInstance}" field="identifier"/></dd>
            
          </g:if>

          <g:if test="${packageInstance?.contentProvider}">
            <dt><g:message code="package.contentProvider.label" default="Content Provider" /></dt>
            
              <dd><g:link controller="org" action="show" id="${packageInstance.contentProvider.id}"><g:fieldValue bean="${packageInstance}" field="contentProvider.name"/></g:link></dd>
            
          </g:if>
        
          <g:if test="${packageInstance?.name}">
            <dt><g:message code="package.name.label" default="Name" /></dt>
            
              <dd><g:fieldValue bean="${packageInstance}" field="name"/></dd>
            
          </g:if>
        
            
        

          <dt>Availability of titles in this package by platform</dt>
          <dd>
          <g:set var="counter" value="${1}" />
          <table border="1" cellspacing="5" cellpadding="5">
            <tr>
              <th rowspan="2" style="width: 2%;">#</th>
              <th rowspan="2" style="width: 20%;">Title</th>
              <th rowspan="2" style="width: 10%;">ISSN</th>
              <th rowspan="2" style="width: 10%;">eISSN</th>
              <th colspan="${platforms.size()}">Provided by platform</th>
            </tr>
            <tr>
              <g:each in="${platforms}" var="p">
                <th><g:link controller="platform" action="show" id="${p.id}">${p.name}</g:link></th>
              </g:each>
            </tr>
            <g:each in="${titles}" var="t">
              <tr>
                <td>${counter++}</td>
                <td style="text-align:left;"><g:link controller="titleInstance" action="show" id="${t.title.id}">${t.title.title}</g:link>&nbsp;</td>
                <td>${t?.title?.getIdentifierValue('ISSN')}</td>
                <td>${t?.title?.getIdentifierValue('eISSN')}</td>
                <g:each in="${crosstab[t.position]}" var="tipp">
                  <g:if test="${tipp}">
                    <td>from: <g:formatDate format="dd MMM yyyy" date="${tipp.startDate}"/> 
                          <g:if test="${tipp.startVolume}"> / volume: ${tipp.startVolume} </g:if>
                          <g:if test="${tipp.startIssue}"> / issue: ${tipp.startIssue} </g:if> <br/>
                        to:  <g:formatDate format="dd MMM yyyy" date="${tipp.endDate}"/> 
                          <g:if test="${tipp.endVolume}"> / volume: ${tipp.endVolume}</g:if>
                          <g:if test="${tipp.endIssue}"> / issue: ${tipp.endIssue}</g:if> <br/>
                        coverage Depth: ${tipp.coverageDepth}</br>
                      <g:link controller="titleInstancePackagePlatform" action="show" id="${tipp.id}">Full TIPP Details</g:link>
                    </g:if>
                    <g:else>
                      <td></td>
                    </g:else>
                  </td>
                </g:each>
              </tr>
            </g:each>
          </table>
          </dd>
        </dl>


        <g:form>
          <sec:ifAnyGranted roles="ROLE_EDITOR,ROLE_ADMIN">
          <g:hiddenField name="id" value="${packageInstance?.id}" />
          <div class="form-actions">
            <g:link class="btn" action="edit" id="${packageInstance?.id}">
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

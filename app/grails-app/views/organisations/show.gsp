
<%@ page import="com.k_int.kbplus.Org" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'org.label', default: 'Org')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>

    <div class="container">
      <h1>${orgInstance.name}</h1>
      <g:render template="nav" contextPath="." />
    </div>

    <div class="container">
      

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>
          <g:if test="${orgInstance?.name}">
            <dt><g:message code="org.name.label" default="Name" /></dt>
            
              <dd><g:fieldValue bean="${orgInstance}" field="name"/></dd>
            
          </g:if>
        
          <g:if test="${orgInstance?.address}">
            <dt><g:message code="org.address.label" default="Address" /></dt>
            
              <dd><g:fieldValue bean="${orgInstance}" field="address"/></dd>
            
          </g:if>
        
          <g:if test="${orgInstance?.ipRange}">
            <dt><g:message code="org.ipRange.label" default="Ip Range" /></dt>
            
              <dd><g:fieldValue bean="${orgInstance}" field="ipRange"/></dd>
            
          </g:if>
        
          <g:if test="${orgInstance?.sector}">
            <dt><g:message code="org.sector.label" default="Sector" /></dt>
            
              <dd><g:fieldValue bean="${orgInstance}" field="sector"/></dd>
            
          </g:if>
        
          <g:if test="${orgInstance?.ids}">
            <dt><g:message code="org.ids.label" default="Ids" /></dt>
              <g:each in="${orgInstance.ids}" var="i">
              <dd><g:link controller="identifier" action="show" id="${i.identifier.id}">${i?.identifier?.ns?.ns?.encodeAsHTML()} : ${i?.identifier?.value?.encodeAsHTML()}</g:link></dd>
              </g:each>
          </g:if>

          <g:if test="${orgInstance?.outgoingCombos}">
            <dt><g:message code="org.outgoingCombos.label" default="Outgoing Combos" /></dt>
            <g:each in="${orgInstance.outgoingCombos}" var="i">
              <dd>${i.type?.value} - <g:link controller="org" action="show" id="${i.toOrg.id}">${i.toOrg?.name}</g:link>
                (<g:each in="${i.toOrg?.ids}" var="id">
                  ${id.identifier.ns.ns}:${id.identifier.value} 
                </g:each>)
              </dd>
            </g:each>
          </g:if>

          <g:if test="${orgInstance?.incomingCombos}">
            <dt><g:message code="org.incomingCombos.label" default="Incoming Combos" /></dt>
            <g:each in="${orgInstance.incomingCombos}" var="i">
              <dd>${i.type?.value} - <g:link controller="org" action="show" id="${i.toOrg.id}">${i.fromOrg?.name}</g:link>
                (<g:each in="${i.fromOrg?.ids}" var="id">
                  ${id.identifier.ns.ns}:${id.identifier.value} 
                </g:each>)
              </dd>

            </g:each>
          </g:if>

          <g:if test="${orgInstance?.links}">
            <dt><g:message code="org.links.label" default="Other org links" /></dt>
            <dd><ul>
              <g:each in="${orgInstance.links}" var="i">
                <li>
                  <g:if test="${i.pkg}"><g:link controller="package" action="show" id="${i.pkg.id}">Package: ${i.pkg.name}</g:link></g:if>
                  <g:if test="${i.sub}"><g:link controller="subscription" action="show" id="${i.sub.id}">Subscription: ${i.sub.name}</g:link></g:if>
                  <g:if test="${i.lic}">Licence: ${i.lic.id}</g:if>
                  <g:if test="${i.title}"><g:link controller="titleInstance" action="show" id="${i.title.id}">Title: ${i.title.title}</g:link></g:if>
                  (${i.roleType?.value}) </li>
              </g:each>
            </ui></dd>
          </g:if>
        
          <g:if test="${orgInstance?.impId}">
            <dt><g:message code="org.impId.label" default="Imp Id" /></dt>
            
              <dd><g:fieldValue bean="${orgInstance}" field="impId"/></dd>
            
          </g:if>
        
        
        </dl>

    </div>
  </body>
</html>

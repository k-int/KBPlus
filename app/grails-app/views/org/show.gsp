
<%@ page import="com.k_int.kbplus.Org" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'org.label', default: 'Org')}" />
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
            <sec:ifAnyGranted roles="ROLE_ADMIN">
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
          <h1>${orgInstance.name}</h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

          <div class="inline-lists">

              <g:if test="${orgInstance?.name}">
                  <dl>
                      <dt><g:message code="org.name.label" default="Name" /></dt>
                      <dd><g:fieldValue bean="${orgInstance}" field="name"/></dd>
                  </dl>
              </g:if>

              <g:if test="${orgInstance?.address}">
                  <dl>
                      <dt><g:message code="org.address.label" default="Address" /></dt>
                      <dd><g:fieldValue bean="${orgInstance}" field="address"/></dd>
                  </dl>
              </g:if>

              <g:if test="${orgInstance?.ipRange}">
                  <dl>
                      <dt><g:message code="org.ipRange.label" default="Ip Range" /></dt>
                      <dd><g:fieldValue bean="${orgInstance}" field="ipRange"/></dd>
                  </dl>
              </g:if>

              <dl>
                <dt><g:message code="org.sector.label" default="Sector" /></dt>
                <dd>
                  <g:if test="${editable}"><span id="titleEdit" 
                                          class="xEditableValue"
                                          data-pk="${orgInstance.class.name}:${orgInstance.id}"
                                          data-name="sector" 
                                          data-url='<g:createLink controller="ajax" action="editableSetValue"/>'
                                          data-original-title="${orgInstance.sector}">${orgInstance.sector}</span></g:if>
                  <g:else>
                    <g:fieldValue bean="${orgInstance}" field="sector"/></dd>
                  </g:else>
              </dl>

              <g:if test="${orgInstance?.ids}">
                  <dl>
                      <dt><g:message code="org.ids.label" default="Ids" /></dt>
                      <g:each in="${orgInstance.ids}" var="i">
                          <dd><g:link controller="identifier" action="show" id="${i.identifier.id}">${i?.identifier?.ns?.ns?.encodeAsHTML()} : ${i?.identifier?.value?.encodeAsHTML()}</g:link></dd>
                      </g:each>
                  </dl>
              </g:if>

              <g:if test="${orgInstance?.outgoingCombos}">
                  <dl>
                      <dt><g:message code="org.outgoingCombos.label" default="Outgoing Combos" /></dt>
                      <g:each in="${orgInstance.outgoingCombos}" var="i">
                          <dd>${i.type?.value} - <g:link controller="org" action="show" id="${i.toOrg.id}">${i.toOrg?.name}</g:link>
                          (<g:each in="${i.toOrg?.ids}" var="id">
                              ${id.identifier.ns.ns}:${id.identifier.value} 
                          </g:each>)
                          </dd>
                  </dl>
                  </g:each>
              </g:if>

              <g:if test="${orgInstance?.incomingCombos}">
                  <dl>
                      <dt><g:message code="org.incomingCombos.label" default="Incoming Combos" /></dt>
                      <g:each in="${orgInstance.incomingCombos}" var="i">
                          <dd>${i.type?.value} - <g:link controller="org" action="show" id="${i.toOrg.id}">${i.fromOrg?.name}</g:link>
                          (<g:each in="${i.fromOrg?.ids}" var="id">
                              ${id.identifier.ns.ns}:${id.identifier.value} 
                          </g:each>)
                          </dd>
                  </dl>
                  </g:each>
              </g:if>

              <g:if test="${orgInstance?.links}">
                  <dl>
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
                          </li></dd>
                  </dl>
              </g:if>

              <g:if test="${orgInstance?.impId}">
                  <dl>
                      <dt><g:message code="org.impId.label" default="Imp Id" /></dt>

                      <dd><g:fieldValue bean="${orgInstance}" field="impId"/></dd>
                  </dl>
              </g:if>


          </div>

        <g:form>
                                    <sec:ifAnyGranted roles="ROLE_ADMIN">
          <g:hiddenField name="id" value="${orgInstance?.id}" />
          <div class="form-actions">
            <g:link class="btn" action="edit" id="${orgInstance?.id}">
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

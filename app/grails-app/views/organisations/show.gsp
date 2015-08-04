
<%@ page import="com.k_int.kbplus.Org" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'org.label', default: 'Org')}" />
    <title>KB+ <g:message code="default.show.label" args="[entityName]" /></title>
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
            <dt><g:message code="org.name.label" default="Name" /></dt>
              <dd><g:fieldValue bean="${orgInstance}" field="name"/></dd>
        
            <dt><g:message code="org.address.label" default="Address" /></dt>
              <dd><g:fieldValue bean="${orgInstance}" field="address"/></dd>

            <dt><g:message code="org.type.label" default="Org Type" /></dt>
              <dd>
                <g:xEditableRefData owner="${orgInstance}" field="orgType" config='OrgType'/>
              </dd>
        
            <dt><g:message code="org.ipRange.label" default="Ip Range" /></dt>
            
              <dd><g:fieldValue bean="${orgInstance}" field="ipRange"/></dd>
        
            <dt><g:message code="org.sector.label" default="Sector" /></dt>
            
              <dd><g:fieldValue bean="${orgInstance}" field="sector"/></dd>
            
        
            <dt><g:message code="org.ids.label" default="Ids" /></dt>
            Select an existing identifer using the typedown, or create a new one by entering namespace:value (EG eISSN:2190-9180) then clicking that value in the dropdown to confirm.
            <g:if test="${orgInstance?.ids}">
              <g:each in="${orgInstance.ids}" var="i">
              <dd><g:link controller="identifier" action="show" id="${i.identifier.id}">${i?.identifier?.ns?.ns?.encodeAsHTML()} : ${i?.identifier?.value?.encodeAsHTML()}</g:link></dd>
              </g:each>
            </g:if>

          <g:if test="${editable}">
            <g:form controller="ajax" action="addToCollection" class="form-inline">
              <input type="hidden" name="__context" value="${orgInstance.class.name}:${orgInstance.id}"/>
              <input type="hidden" name="__newObjectClass" value="com.k_int.kbplus.IdentifierOccurrence"/>
              <input type="hidden" name="__recip" value="org"/>
              <input type="hidden" name="identifier" id="addIdentifierSelect"/>
              <input type="submit" value="Add Identifier..." class="btn btn-primary btn-small"/>
            </g:form>
          </g:if>


          <g:if test="${orgInstance?.outgoingCombos}">
            <dt><g:message code="org.outgoingCombos.label" default="Outgoing Combos" /></dt>
            <g:each in="${orgInstance.outgoingCombos}" var="i">
              <dd>${i.type?.value} - <g:link controller="organisations" action="show" id="${i.toOrg.id}">${i.toOrg?.name}</g:link>
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
                  <g:if test="${i.pkg}"><g:link controller="packageDetails" action="show" id="${i.pkg.id}">Package: ${i.pkg.name} (${i.pkg?.packageStatus?.value})</g:link></g:if>
                  <g:if test="${i.sub}"><g:link controller="subscriptionDetails" action="index" id="${i.sub.id}">Subscription: ${i.sub.name} (${i.sub.status?.value})</g:link></g:if>
                  <g:if test="${i.lic}">Licence: ${i.lic.id} (${i.lic.status?.value})</g:if>
                  <g:if test="${i.title}"><g:link controller="titleInstance" action="show" id="${i.title.id}">Title: ${i.title.title} (${i.title.status?.value})</g:link></g:if>
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


  <r:script language="JavaScript">

    $(function(){
      <g:if test="${editable}">
      $("#addIdentifierSelect").select2({
        placeholder: "Search for an identifier...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
          url: "<g:createLink controller='ajax' action='lookup'/>",
          dataType: 'json',
          data: function (term, page) {
              return {
                  q: term, // search term
                  page_limit: 10,
                  baseClass:'com.k_int.kbplus.Identifier'
              };
          },
          results: function (data, page) {
            return {results: data.values};
          }
        },
        createSearchChoice:function(term, data) {
          return {id:'com.k_int.kbplus.Identifier:__new__:'+term,text:term};
        }
      });
    });
    </g:if>
  </r:script>
  
  </body>
</html>

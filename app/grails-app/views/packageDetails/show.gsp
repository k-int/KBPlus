<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
      <div class="container">

        <div class="page-header">
          <h1> <span id="packageNameEdit"
                        class="xEditableValue"
                        data-type="textarea"
                        data-pk="${packageInstance.class.name}:${packageInstance.id}"
                        data-name="name"
                        data-url='<g:createLink controller="ajax" action="editableSetValue"/>'>${packageInstance.name}</span></h1>
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
                <dt>Package Name</dt>
                <dd> <span id="packageNameEdit2"
                        class="xEditableValue"
                        data-type="textarea"
                        data-pk="${packageInstance.class.name}:${packageInstance.id}"
                        data-name="name"
                        data-url='<g:createLink controller="ajax" action="editableSetValue"/>'>${packageInstance.name}</span></dd>
              </dl>

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
          <dt>Titles</dt>
          <dd>
          <table class="table table-bordered">
            <thead>
            <tr>
              <th rowspan="2" style="">Title</th>
              <th rowspan="2" style="">Platform</th>
              <th rowspan="2" style="">Identifiers</th>
              <th rowspan="2" style="">Start</th>
              <th rowspan="2" style="">End</th>
              <th rowspan="2" style="">Coverage Depth</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${packageInstance?.tipps}" var="t">
              <tr>
                <td style="vertical-align:top;">
                   ${t.title.title}
                   <g:link controller="titleDetails" action="edit" id="${t.title.id}">(Title)</g:link>
                   <g:link controller="tipp" action="show" id="${t.id}">(TIPP)</g:link>
                </td>
                <td style="white-space: nowrap;vertical-align:top;">${t.platform?.name}</td>
                <td style="white-space: nowrap;vertical-align:top;">
                  <g:each in="${t.title.ids}" var="id">
                    ${id.identifier.ns.ns}:${id.identifier.value}<br/>
                  </g:each>
                </td>

                <td style="white-space: nowrap">
                  Date: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.startDate}"/>
                        <input id="TitleInstancePackagePlatform:${t.id}:startDate" type="hidden" class="dp1" /><br/>
                  Volume: <g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="startVolume" id="startVolume" class="newipe">${t.startVolume}</g:inPlaceEdit><br/>
                  Issue: <g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="endIssue" id="endIssue" class="newipe">${t.startIssue}</g:inPlaceEdit>                
                </td>

                <td style="white-space: nowrap"> 
                   Date:<g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.endDate}"/>
                    <input id="TitleInstancePackagePlatform:${t.id}:endDate" type="hidden" class="dp1" /><br/>
                   Volume: <g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="endVolume" id="endVolume" class="newipe">${t.endVolume}</g:inPlaceEdit><br/>
                   Issue: <g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="endIssue" id="endIssue" class="newipe">${t.endIssue}</g:inPlaceEdit>
                </td>
                <td><g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="coverageDepth" id="coverageDepth" class="newipe">${t.coverageDepth}</g:inPlaceEdit></td>
              </tr>
            </g:each>
            </tbody>
          </table>
          </dd>
        </dl>


            </fieldset>
        </fieldset>
        
        <h3>Add title to package</h3>
        <g:form controller="ajax" action="addToCollection">
          <input type="hidden" name="__context" value="${packageInstance.class.name}:${packageInstance.id}"/>
          <input type="hidden" name="__newObjectClass" value="com.k_int.kbplus.TitleInstancePackagePlatform"/>
          <input type="hidden" name="__recip" value="pkg"/>
          title: <g:simpleReferenceTypedown class="input-xxlarge" name="title" baseClass="com.k_int.kbplus.TitleInstance"/>
          platform: <g:simpleReferenceTypedown class="input-large" name="platform" baseClass="com.k_int.kbplus.Platform"/>
          <input type="submit" value="Add Title..."/>
         </g:form>


      </div>


    <g:render template="enhanced_select" contextPath="../templates" />
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',recip_prop:'pkg']}" />

    <script language="JavaScript">
      $(function(){
        $.fn.editable.defaults.mode = 'inline';
        $('.xEditableValue').editable();
      });
    </script>

  </body>
</html>

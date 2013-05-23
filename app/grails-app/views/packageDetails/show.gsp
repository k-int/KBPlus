<%@ page import="com.k_int.kbplus.Package" %>
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
                <dd> <g:xEditable owner="${packageInstance}" field="name" /></dd>
              </dl>
              
              <dl>
                <dt>Public?</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="isPublic" config='YN'/>
                </dd>
              </dl> 

                <dl><dt>Start Date</dt><dd>
                    <g:xEditable owner="${packageInstance}" field="startDate" type="date"/>
                </dd>
                </dl>

               <dl>
                    <dt>End Date</dt>
                    <dd>
                       <g:xEditable owner="${package  Instance}" field="endDate" type="date"/>
                    </dd>
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
                  <g:xEditableRefData owner="${packageInstance}" field="packageType" config='PackageType'/>
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
                   (<g:xEditableRefData owner="${t}" field="status" config='TIPPStatus'/>)
                </td>
                <td style="white-space: nowrap;vertical-align:top;">${t.platform?.name}</td>
                <td style="white-space: nowrap;vertical-align:top;">
                  <g:each in="${t.title.ids}" var="id">
                    ${id.identifier.ns.ns}:${id.identifier.value}<br/>
                  </g:each>
                </td>

                <td style="white-space: nowrap">
                  Date: <g:xEditable owner="${t}" type="date" field="startDate" /><br/>
                  Volume: <g:xEditable owner="${t}" field="startVolume" /><br/>
                  Issue: <g:xEditable owner="${t}" field="startIssue" />     
                </td>

                <td style="white-space: nowrap"> 
                   Date: <g:xEditable owner="${t}" type="date" field="endDate" /><br/>
                   Volume: <g:xEditable owner="${t}" field="endVolume" /><br/>
                   Issue: <g:xEditable owner="${t}" field="endIssue" />
                </td>
                <td>
                  <g:xEditable owner="${t}" field="coverageDepth" />
                </td>
              </tr>
            </g:each>
            </tbody>
          </table>
          </dd>
        </dl>


            </fieldset>
        </fieldset>
        
        <g:form controller="ajax" action="addToCollection">
          <fieldset>
            <legend><h3>Add A Title To This Package</h3></legend>
            <input type="hidden" name="__context" value="${packageInstance.class.name}:${packageInstance.id}"/>
            <input type="hidden" name="__newObjectClass" value="com.k_int.kbplus.TitleInstancePackagePlatform"/>
            <input type="hidden" name="__recip" value="pkg"/>
            <label>Title To Add</label>
            <g:simpleReferenceTypedown class="input-xxlarge" style="width:350px;" name="title" baseClass="com.k_int.kbplus.TitleInstance"/><br/>
            <span class="help-block"></span>
            <label>Platform For Added Title</label>
            <g:simpleReferenceTypedown class="input-large" style="width:350px;" name="platform" baseClass="com.k_int.kbplus.Platform"/><br/>
            <span class="help-block"></span>
            <button type="submit" class="btn">Add Title...</button>
          </fieldset>
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

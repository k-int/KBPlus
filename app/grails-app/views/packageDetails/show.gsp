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
      <ul class="breadcrumb">
        <li><g:link controller="packageDetails" action="index">All Packages</g:link><span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="show" id="${packageInstance.id}">${packageInstance.name}</g:link></li>
      </ul>
    </div>
  
   

      <div class="container">

        <div class="page-header">
          <div>
          <h1> <span id="packageNameEdit"
                        class="xEditableValue"
                        data-type="textarea"
                        data-pk="${packageInstance.class.name}:${packageInstance.id}"
                        data-name="name"
                        data-url='<g:createLink controller="ajax" action="editableSetValue"/>'>${packageInstance.name}</span></h1>
          </div>

        </div>
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

    <div class="container">
      <div class="row">
        <div class="span8">
            <h6>Package Information</h6>
            <g:hiddenField name="version" value="${packageInstance?.version}" />
            <fieldset>

              <dl>
                <dt>Package Name</dt>
                <dd> <g:xEditable owner="${packageInstance}" field="name"/></dd>
              </dl>
              
              <dl>
                <dt>Public?</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="isPublic" config='YN'/>
                </dd>
              </dl> 

              <dl>
                <dt>License</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="license" config='Licenses'/>
                </dd>
              </dl>


                <dl><dt>Start Date</dt><dd>
                    <g:xEditable owner="${packageInstance}" field="startDate" type="date"/>
                </dd>
                </dl>

               <dl>
                    <dt>End Date</dt>
                    <dd>
                       <g:xEditable owner="${packageInstance}" field="endDate" type="date"/>
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
          </fieldset>
        </div>
        <div class="span4">
          <div class="well notes">
            <g:if test="${(subscriptionList != null) && (subscriptionList?.size() > 0)}">
              <h5>Add package to institutional subscription:</h5>
              <g:form controller="packageDetails" action="addToSub" id="${packageInstance.id}">
                <select name="subid">
                  <g:each in="${subscriptionList}" var="s">
                    <option value="${s.sub.id}">${s.sub.name ?: "unnamed subscription ${s.sub.id}"} - ${s.org.name}</option>
                  </g:each>
                </select><br/>
                Create Entitlements in Subscripion: <input type="checkbox" name="addEntitlements" value="true"/><br/>
                <input type="submit"/>
              </g:form>
            </g:if>
            <g:else>
              No subscriptions available to link to this package
            </g:else>
          </div>
    
          <g:render template="documents" contextPath="../templates" model="${[doclist:packageInstance.documents, ownobj:packageInstance, owntp:'pkg']}" />
          <g:render template="notes" contextPath="../templates" model="${[doclist:packageInstance.documents, ownobj:packageInstance, owntp:'pkg']}" />
        </div>
      </div>
    </div>

    <div class="container">

        <dl>
          <dt>Titles (${offset+1} to ${lasttipp}  of ${num_tipp_rows})</dt>
          <dd>
          <table class="table table-bordered">
            <thead>
            <tr>
              <th>&nbsp;</th>
              <g:sortableColumn params="${params}" property="tipp.title.title" title="Title" />
              <th style="">Platform</th>
              <th style="">Identifiers</th>
              <th style="">Start</th>
              <th style="">End</th>
              <th style="">Coverage Depth</th>
            </tr>
            </thead>
            <tbody>
            <g:set var="counter" value="${offset+1}" />
            <g:each in="${titlesList}" var="t">
              <tr>
                <td>${counter++}</td>
                <td style="vertical-align:top;">
                   ${t.title.title}
                   <g:link controller="titleDetails" action="show" id="${t.title.id}">(Title)</g:link>
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

        <div class="pagination" style="text-align:center">
          <g:if test="${titlesList}" >
            <bootstrap:paginate  action="show" controller="packageDetails" params="${params}" next="Next" prev="Prev" maxsteps="${max}" total="${num_tipp_rows}" />
          </g:if>
        </div>



        
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

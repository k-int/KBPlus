<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
  </head>
  <body>

    <div class="container">
      <div class="page-header">
        <h1>Track ${item.name}(${item.identifier}) from ${item.source.name}</h1>
      </div>
      <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>
    </div>

    <g:form>
      <div class="container well">
        <h1>Review Tracker</h1>
        <g:if test="${type=='new'}">
          <p>This tracker will create a new local package for "${item.name}" from "${item.source.name}". Set the new package name below.</p>
          <dl>
            <dt>New Package Name</dt>
            <dd><input type="text" name="trackerName" value="${item.name}" class="input-xxlarge"/></dd>
          </dl>
        </g:if>
        <g:else>
          <p>This tracker will synchronize package "<b><em>${item.name}</em></b>" from "<b><em>${item.source.name}</em></b>" with the existing local package <b><em>${localPkg.name}</em></b> </p>
        </g:else>

        <dl>
          <td>Auto accept the following changes</dt>
          <dd>
          <table class="table">
            <tr>
              <td><input type="Checkbox" name="autoAcceptTippAddition"/>TIPP Addition</td>
              <td><input type="Checkbox" name="autoAcceptTippUpdate"/>TIPP Update</td>
              <td><input type="Checkbox" name="autoAcceptTippDelete"/>TIPP Delete</td>
              <td><input type="Checkbox" name="autoAcceptPackageChange"/>Package Changes</td>
            </tr>
          </table>
          </dd>
        </dl>
        <input type="submit"/>
      </div>
    </g:form>

    <div class="container well">
      <h1>Package Sync Impact</h1>
      <table class="table table-striped table-bordered">
        <tr>
          <th>
            <g:if test="${type=='new'}">
              No current package
            </g:if>
            <g:else>
              ${localPkg.name} as now
            </g:else>
          </th>
          <th>Action</th>
          <th>
            <g:if test="${type=='new'}">
              New Package After Processing
            </g:if>
            <g:else>
              ${localPkg.name} after sync
            </g:else>
          </th>
        </tr>
        <g:each in="${impact}" var="i">
          <tr>
            <td width="47%">
              <g:if test="${i.action=='i'}">
              </g:if>
              <g:else>
                ${i.tipp?.title?.name} <br/>
                (<g:each in="${i.tipp.title.identifiers}" var="id">${id.namespace}:${id.value} </g:each>) <br/>
              </g:else>
            </td>
            <td width="6%">${i.action}</td>
            <td width="47%">

              <g:if test="${i.action=='d'}">
              </g:if>
              <g:else>
                ${i.tipp?.title?.name} <br/>
                (<g:each in="${i.tipp.title.identifiers}" var="id">${id.namespace}:${id.value} </g:each>) <br/>
                <g:if test="${i.action=='c'}">
                  ${t.tipp.changes}
                </g:if>
              </g:else>
            </td>
            </td>
          </tr>
        </g:each>
      
      </table>
    </div>

  </body>
</html>

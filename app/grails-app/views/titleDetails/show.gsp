<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>${ti.title}</title>
  </head>
  <body>
      <div class="container">
        <div class="row">
          <div class="span12">

            <div class="page-header">
              <h1>${ti.title}</h1>
            </div>

            <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <g:if test="${flash.error}">
            <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
            </g:if>
          </div>
        </div>

        <div class="row">
          <div class="span6">
            <h3>Identifiers</h3>
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th>ID</td>
                  <th>Identifier Namespace</th>
                  <th>Identifier</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${ti.ids}" var="io">
                  <tr>
                    <td>${io.id}</td>
                    <td>${io.identifier.ns.ns}</td>
                    <td>${io.identifier.value}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>
	  </div>
          <div class="span6">
            <h3>Org Links</h3>
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th>ID</td>
                  <th>Org</th>
                  <th>Role</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${ti.orgs}" var="org">
                  <tr>
                    <td>${org.id}</td>
                    <td>${org.org.name}</td>
                    <td>${org.roleType.value}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </div>
        </div>
        <div class="row">
          <div class="span12">


            <h3>Appears in...</h3>
            <table class="table table-bordered table-striped">
                    <tr>
                        <th>From Date</th><th>From Volume</th><th>From Issue</th>
                        <th>To Date</th><th>To Volume</th><th>To Issue</th><th>Coverage Depth</th>
                        <th>Platform</th><th>Package</th><th>Actions</th>
                    </tr>
                    <g:each in="${ti.tipps}" var="t">
                        <tr>
                            <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.startDate}"/></td>
                        <td>${t.startVolume}</td>
                        <td>${t.startIssue}</td>
                        <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.endDate}"/></td>
                        <td>${t.endVolume}</td>
                        <td>${t.endIssue}</td>
                        <td>${t.coverageDepth}</td>
                        <td><g:link controller="platform" action="show" id="${t.platform.id}">${t.platform.name}</g:link></td>
                        <td><g:link controller="packageDetails" action="show" id="${t.pkg.id}">${t.pkg.name}</g:link></td>
                        <td><g:link controller="tipp" action="show" id="${t.id}">Full TIPP record</g:link></td>
                        </tr>
                    </g:each>
            </table>

          </div>
        </div>
      </div>
  </body>
</html>

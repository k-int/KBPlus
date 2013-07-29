<%@ page import="com.k_int.kbplus.Org" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>${ui.display}</title>
  </head>
  <body>
    <div class="container">
      <div class="row">
        <div class="span12">

          <div class="page-header">
             <h1>${ui.displayName} (${ui.username})</h1>
          </div>

          <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
          </g:if>

          <g:if test="${flash.error}">
            <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
          </g:if>

          <h3>Affiliations</h3>

          <table class="table table-bordered">
            <thead>
              <tr>
                <th>Id</td>
                <th>Org</td>
                <th>Role</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${ui.affiliations}" var="af">
                <tr>
                  <td>${af.id}</td>
                  <td>${af.org.name}</td>
                  <td>${af.formalRole?.authority}</td>
                  <td>${['Pending','Approved','Rejected','Auto Approved'][af.status]}</td>
                </tr>
              </g:each>
            </tbody>
          </table>

          <h3>Roles</h3>

          <table class="table table-bordered">
            <thead>
              <tr>
                <th>Role</td>
              </tr>
            </thead>
            <tbody>
              <g:each in="${ui.roles}" var="rl">
                <tr>
                  <td>${rl.role.authority}</td>
                </tr>
              </g:each>
            </tbody>
          </table>

        </div>
      </div>
    </div>
  </body>
</html>

<%@ page import="com.k_int.kbplus.Org" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>${ui.display}</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>

  </head>
  <body>
    <div class="container">
      <div class="row">
        <div class="span12">

          <div class="page-header">
             <h1><span id="displayEdit" 
                       class="xEditableValue"
                       data-type="textarea" 
                       data-pk="${ui.class.name}:${ui.id}"
                       data-name="display" 
                       data-url='<g:createLink controller="ajax" action="editableSetValue"/>'
                       data-original-title="${ui.display}">${ui.display}</span></h1>
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
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${ui.affiliations}" var="af">
                <tr>
                  <td>${af.id}</td>
                  <td>${af.org.name}</td>
                  <td>${af.formalRole.authority}</td>
                  <td><g:link controller="ajax" action="deleteThrough" params='${[contextOid:"${ui.class.name}:${ui.id}",contextProperty:"affiliations",targetOid:"${af.class.name}:${af.id}"]}'>Delete Affiliation</g:link></td>
                </tr>
              </g:each>
            </tbody>
          </table>

          <h3>Roles</h3>

          <table class="table table-bordered">
            <thead>
              <tr>
                <th>Role</td>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${ui.roles}" var="rl">
                <tr>
                  <td>${rl.role.authority}</td>
                  <td><g:link controller="ajax" action="removeUserRole" params='${[user:"${ui.class.name}:${ui.id}",role:"${rl.role.class.name}:${rl.role.id}"]}'>Delete Role</g:link></td>
                </tr>
              </g:each>
            </tbody>
          </table>

        </div>
      </div>
    </div>


  <script language="JavaScript">

    $(function(){
      $.fn.editable.defaults.mode = 'inline';
      $('.xEditableValue').editable();
    });

  </script>
  </body>
</html>

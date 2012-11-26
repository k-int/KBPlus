  <table class="table table-bordered licence-properties">
    <thead>
      <tr>
        <td>Organisation Name</td>
        <td>Role</td>
        <td>actions</td>
      </tr>
    </thead>
    <g:each in="${roleLinks}" var="role">
      <tr>
        <td><g:link controller="Organisations" action="info" id="${role.org.id}">${role.org.name}</g:link></td>
        <td>${role.roleType.value}</td>
        <td><g:link controller="ajax" action="delOrgRole" id="${role.id}" onclick="return confirm('Really delete this org link?')">Delete</g:link></td>
      </tr>
    </g:each>
  </table>
  <a class="btn" data-toggle="modal" href="#osel_add_modal" >Add Org Link</a>

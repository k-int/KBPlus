<div class="container">
  <table class="table table-striped">
    <g:each in="${roleLinks}" var="role">
      <tr>
        <td><g:link controller="Organisations" action="info" id="${role.org.id}">${role.org.name}</g:link></td>
        <td>${role.roleType.value}</td>
      </tr>
    </g:each>
  </table>

  Add new org links below by entering the organisation name and role, then clicking the add button.</br>
  <input type="text">
  <select name="role"><option value="1">Publisher</option></select><button class="btn">Add</button>
</div>

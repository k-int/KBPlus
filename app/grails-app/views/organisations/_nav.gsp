<ul class="nav nav-pills">
  <li <%='show'== actionName ? ' class="active"' : '' %>><g:link controller="organisations" action="show" params="${[id:params.id]}">Details</g:link></li>
  <li <%='users'== actionName ? ' class="active"' : '' %>><g:link controller="organisations" action="users" params="${[id:params.id]}">Users</g:link></li>
    <li <%='config'== actionName ? ' class="active"' : '' %>><g:link controller="organisations" action="config" params="${[id:params.id]}">Options</g:link></li>
</ul>

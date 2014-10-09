<ul class="nav nav-pills">

  <li <%='show'== actionName ? ' class="active"' : '' %>>
    <g:link controller="titleDetails" action="show" params="${[id:params.id]}">Title Details</g:link>
  </li>

  <li<%='history'== actionName ? ' class="active"' : '' %>>
  <g:link controller="titleDetails"  action="history" params="${[id:params.id]}">History</g:link></li>

</ul>

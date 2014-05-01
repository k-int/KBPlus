<ul class="nav nav-pills">

  <li <%='show'== actionName ? ' class="active"' : '' %>>
    <g:link controller="packageDetails" action="show" params="${[id:params.id]}">Package Details</g:link>
  </li>

  <li <%='current'== actionName ? ' class="active"' : '' %>><g:link controller="packageDetails" 
              action="current" 
              params="${[id:params.id]}">Titles</g:link></li>

  <li<%='expected'== actionName ? ' class="active"' : '' %>><g:link controller="packageDetails" 
              action="expected" 
              params="${[id:params.id]}">Expected Titles</g:link></li>

  <li<%='previous'== actionName ? ' class="active"' : '' %>><g:link controller="packageDetails" 
              action="previous" 
              params="${[id:params.id]}">Previous Titles</g:link></li>
</ul>

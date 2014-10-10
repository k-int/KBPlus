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

    <li <%='documents'== actionName ? ' class="active"' : '' %>><g:link controller="packageDetails"
                                                                        action="documents"
                                                                        params="${[id:params.id]}">Documents</g:link></li>

    <li<%='notes'== actionName ? ' class="active"' : '' %>><g:link controller="packageDetails"
                                                                   action="notes"
                                                                   params="${[id:params.id]}">Notes</g:link></li>

    <li<%='history'== actionName ? ' class="active"' : '' %>><g:link controller="packageDetails"
                                                                   action="history"
                                                                   params="${[id:params.id]}">History</g:link></li>

  <g:if test="${editable}">
    <g:if test="${packageInstance?.consortia != null}">
      <li<%='consortia'== actionName ? ' class="active"' : '' %>><g:link controller="packageDetails" action="consortia" params="${[id:params.id]}">Consortia</g:link></li>
    </g:if>
  </g:if>


</ul>

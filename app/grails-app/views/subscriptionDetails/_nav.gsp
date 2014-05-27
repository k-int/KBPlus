<ul class="nav nav-pills">

  <li <%='index'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails" action="index" params="${[id:params.id]}">Current Entitlements</g:link></li>

  <g:if test="${editable}">
    <li <%='addEntitlements'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails" 
      action="addEntitlements" 
      params="${[id:params.id]}">Add Entitlements</g:link></li>
  </g:if>

  <g:if test="${editable}">
    <li <%='linkPackage'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails" 
                    action="linkPackage" 
                    params="${[id:params.id]}">Link Package</g:link></li>
  </g:if>

  <li <%='documents'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails" 
              action="documents" 
              params="${[id:params.id]}">Documents</g:link></li>

  <li<%='notes'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails" 
              action="notes" 
              params="${[id:params.id]}">Notes</g:link></li>

  <li<%='renewals'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails" 
              action="renewals" 
              params="${[id:params.id]}">Renewals</g:link></li>

  <li <%='additionalInfo'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails" 
                    action="additionalInfo" 
                    params="${[id:params.id]}">Additional Info</g:link></li>

  <li <%='history'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails" 
                    action="history" 
                    params="${[id:params.id]}">History</g:link></li>
    <li <%='previous'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails"
                                                                      action="previous"
                                                                      params="${[id:params.id]}">Previous</g:link></li>
    <li <%='expected'== actionName ? ' class="active"' : '' %>><g:link controller="subscriptionDetails"
                                                                       action="expected"
                                                                       params="${[id:params.id]}">Expected</g:link></li>
</ul>

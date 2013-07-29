<ul class="nav nav-pills">
    <g:set var="licenseId" value="${com.k_int.kbplus.OnixplLicense.get(params.id).license.id}"/>
  <li <%='index'== actionName ? ' class="active"' : '' %>><g:link controller="onixplLicenseDetails"
                                   action="index" 
                                   params="${[id:params.id]}">License Details</g:link></li>

  <li <%='documents'== actionName ? ' class="active"' : '' %>><g:link controller="onixplLicenseDetails"
                    action="documents" 
                    params="${[id:params.id]}">Document</g:link></li>

  <li <%='notes'== actionName ? ' class="active"' : '' %>><g:link controller="onixplLicenseDetails"
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>

  <li <%='history'== actionName ? ' class="active"' : '' %>><g:link controller="onixplLicenseDetails"
                    action="history" 
                    params="${[id:params.id]}">History</g:link></li>

  <li <%='additionalInfo'== actionName ? ' class="active"' : '' %>><g:link controller="onixplLicenseDetails"
                        action="additionalInfo" 
                        params="${[id:params.id]}">Additional Information</g:link></li>
 </ul>

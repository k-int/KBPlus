<ul class="nav nav-pills">
    <li <%='index'== actionName ? ' class="active"' : '' %>><g:link controller="licenseDetails"
                                                                    action="index"
                                                                    params="${[id:params.id]}">License Details</g:link></li>

    <li <%='documents'== actionName ? ' class="active"' : '' %>><g:link controller="licenseDetails"
                                                                        action="documents"
                                                                        params="${[id:params.id]}">Documents</g:link></li>

    <li <%='notes'== actionName ? ' class="active"' : '' %>><g:link controller="licenseDetails"
                                                                    action="notes"
                                                                    params="${[id:params.id]}">Notes</g:link></li>

    <li <%='history'== actionName ? ' class="active"' : '' %>><g:link controller="licenseDetails"
                                                                      action="history"
                                                                      params="${[id:params.id]}">History</g:link></li>



    <li <%='additionalInfo'== actionName ? ' class="active"' : '' %>><g:link controller="licenseDetails"
                                                                             action="additionalInfo"
                                                                             params="${[id:params.id]}">Additional Information</g:link></li>

    <g:if test="${com.k_int.kbplus.License.get(params.id).onixplLicense}">
        <li <%='onixpl'== actionName ? ' class="active"' : '' %>><g:link controller="licenseDetails"
                                                                         action="onixpl"
                                                                         params="${[id: params.id]}">ONIX-PL License</g:link></li>
    </g:if>

</ul>
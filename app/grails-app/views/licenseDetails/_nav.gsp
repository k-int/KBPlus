<g:set var="licence" value="${com.k_int.kbplus.License.get(params.id)}"/>
<ul class="nav nav-pills">
    <li <%='index'== actionName ? ' class="active"' : '' %>><g:link controller="licenseDetails"
                                                                    action="index"
                                                                    params="${[id:params.id]}">Licence Details</g:link></li>

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

    <g:if test="${licence.onixplLicense}">
        <li <%='onixpl'== actionName ? ' class="active"' : '' %>><g:link controller="licenseDetails"
                                                                         action="onixpl"
                                                                         params="${[id: params.id]}">ONIX-PL Licence</g:link></li>
    </g:if>
    <g:if test="${licence.orgLinks.find{it.roleType.value == 'Licensing Consortium' &&
      it.org.hasUserWithRole(user,'INST_ADM') 
      licence.licenseType == 'Template'}}">
      <li <%='consortia'== actionName ? ' class="active"' : '' %>>
      <g:link controller="licenseDetails"action="consortia" params="${[id: params.id]}">Consortia</g:link></li>
    </g:if>

</ul>
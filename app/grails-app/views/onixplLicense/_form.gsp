<%@ page import="com.k_int.kbplus.OnixplLicense" %>



<div class="fieldcontain ${hasErrors(bean: onixplLicenseInstance, field: 'license', 'error')} required">
    <label for="license">
        <g:message code="onixplLicense.licence.label" default="Licence"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="license" name="license.id" from="${com.k_int.kbplus.License.list()}" optionKey="id" required=""
              value="${onixplLicenseInstance?.license?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplLicenseInstance, field: 'doc', 'error')} required">
    <label for="doc">
        <g:message code="onixplLicense.doc.label" default="Doc"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="doc" name="doc.id" from="${com.k_int.kbplus.Doc.list()}" optionKey="id" required=""
              value="${onixplLicenseInstance?.doc?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplLicenseInstance, field: 'lastmod', 'error')} required">
    <label for="lastmod">
        <g:message code="onixplLicense.lastmod.label" default="Lastmod"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field name="lastmod" type="number" value="${onixplLicenseInstance.lastmod}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplLicenseInstance, field: 'licenseText', 'error')} ">
    <label for="licenseText">
        <g:message code="onixplLicense.licenseText.label" default="License Text"/>

    </label>

    <ul class="one-to-many">
        <g:each in="${onixplLicenseInstance?.licenseText ?}" var="l">
            <li><g:link controller="onixplLicenseText" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
        </g:each>
        <li class="add">
            <g:link controller="onixplLicenseText" action="create"
                    params="['onixplLicense.id': onixplLicenseInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText')])}</g:link>
        </li>
    </ul>

</div>

<div class="fieldcontain ${hasErrors(bean: onixplLicenseInstance, field: 'usageTerm', 'error')} ">
    <label for="usageTerm">
        <g:message code="onixplLicense.usageTerm.label" default="Usage Term"/>

    </label>

    <ul class="one-to-many">
        <g:each in="${onixplLicenseInstance?.usageTerm ?}" var="u">
            <li><g:link controller="onixplUsageTerm" action="show" id="${u.id}">${u?.encodeAsHTML()}</g:link></li>
        </g:each>
        <li class="add">
            <g:link controller="onixplUsageTerm" action="create"
                    params="['onixplLicense.id': onixplLicenseInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm')])}</g:link>
        </li>
    </ul>

</div>


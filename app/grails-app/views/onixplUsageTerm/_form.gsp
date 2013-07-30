<%@ page import="com.k_int.kbplus.OnixplUsageTerm" %>



<div class="fieldcontain ${hasErrors(bean: onixplUsageTermInstance, field: 'oplLicense', 'error')} required">
    <label for="oplLicense">
        <g:message code="onixplUsageTerm.oplLicense.label" default="Opl License"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="oplLicense" name="oplLicense.id" from="${com.k_int.kbplus.OnixplLicense.list()}" optionKey="id"
              required="" value="${onixplUsageTermInstance?.oplLicense?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplUsageTermInstance, field: 'usageType', 'error')} required">
    <label for="usageType">
        <g:message code="onixplUsageTerm.usageType.label" default="Usage Type"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="usageType" name="usageType.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id"
              required="" value="${onixplUsageTermInstance?.usageType?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplUsageTermInstance, field: 'usageStatus', 'error')} required">
    <label for="usageStatus">
        <g:message code="onixplUsageTerm.usageStatus.label" default="Usage Status"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="usageStatus" name="usageStatus.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id"
              required="" value="${onixplUsageTermInstance?.usageStatus?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplUsageTermInstance, field: 'usageTermLicenseText', 'error')} ">
    <label for="usageTermLicenseText">
        <g:message code="onixplUsageTerm.usageTermLicenseText.label" default="Usage Term License Text"/>

    </label>

    <ul class="one-to-many">
        <g:each in="${onixplUsageTermInstance?.usageTermLicenseText ?}" var="u">
            <li><g:link controller="onixplUsageTermLicenseText" action="show"
                        id="${u.id}">${u?.encodeAsHTML()}</g:link></li>
        </g:each>
        <li class="add">
            <g:link controller="onixplUsageTermLicenseText" action="create"
                    params="['onixplUsageTerm.id': onixplUsageTermInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText')])}</g:link>
        </li>
    </ul>

</div>


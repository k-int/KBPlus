<%@ page import="com.k_int.kbplus.OnixplUsageTermLicenseText" %>



<div class="fieldcontain ${hasErrors(bean: onixplUsageTermLicenseTextInstance, field: 'usageTerm', 'error')} required">
	<label for="usageTerm">
		<g:message code="onixplUsageTermLicenseText.usageTerm.label" default="Usage Term" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="usageTerm" name="usageTerm.id" from="${com.k_int.kbplus.OnixplUsageTerm.list()}" optionKey="id" required="" value="${onixplUsageTermLicenseTextInstance?.usageTerm?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplUsageTermLicenseTextInstance, field: 'licenseText', 'error')} required">
	<label for="licenseText">
		<g:message code="onixplUsageTermLicenseText.licenseText.label" default="License Text" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="licenseText" name="licenseText.id" from="${com.k_int.kbplus.OnixplLicenseText.list()}" optionKey="id" required="" value="${onixplUsageTermLicenseTextInstance?.licenseText?.id}" class="many-to-one"/>
</div>


<%@ page import="com.k_int.kbplus.OnixplLicenseText" %>



<div class="fieldcontain ${hasErrors(bean: onixplLicenseTextInstance, field: 'displayNum', 'error')} ">
	<label for="displayNum">
		<g:message code="onixplLicenseText.displayNum.label" default="Display Num" />

	</label>
	<g:textField name="displayNum" value="${onixplLicenseTextInstance?.displayNum}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplLicenseTextInstance, field: 'text', 'error')} ">
	<label for="text">
		<g:message code="onixplLicenseText.text.label" default="Text" />

	</label>
	<g:textField name="text" value="${onixplLicenseTextInstance?.text}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplLicenseTextInstance, field: 'elementId', 'error')} ">
	<label for="elementId">
		<g:message code="onixplLicenseText.elementId.label" default="Element Id" />

	</label>
	<g:textField name="elementId" value="${onixplLicenseTextInstance?.elementId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplLicenseTextInstance, field: 'oplLicense', 'error')} required">
	<label for="oplLicense">
		<g:message code="onixplLicenseText.oplLicense.label" default="Opl License" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="oplLicense" name="oplLicense.id" from="${com.k_int.kbplus.OnixplLicense.list()}" optionKey="id" required="" value="${onixplLicenseTextInstance?.oplLicense?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: onixplLicenseTextInstance, field: 'usageTermLicenseText', 'error')} ">
	<label for="usageTermLicenseText">
		<g:message code="onixplLicenseText.usageTermLicenseText.label" default="Usage Term License Text" />

	</label>

<ul class="one-to-many">
<g:each in="${onixplLicenseTextInstance?.usageTermLicenseText?}" var="u">
    <li><g:link controller="onixplUsageTermLicenseText" action="show" id="${u.id}">${u?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="onixplUsageTermLicenseText" action="create" params="['onixplLicenseText.id': onixplLicenseTextInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText')])}</g:link>
</li>
</ul>

</div>


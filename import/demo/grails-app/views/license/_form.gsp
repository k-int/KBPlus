<%@ page import="com.k_int.kbplus.License" %>



<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="license.status.label" default="Status" />
		
	</label>
	<g:select id="status" name="status.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.status?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'type', 'error')} ">
	<label for="type">
		<g:message code="license.type.label" default="Type" />
		
	</label>
	<g:select id="type" name="type.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.type?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'reference', 'error')} ">
	<label for="reference">
		<g:message code="license.reference.label" default="Reference" />
		
	</label>
	<g:textField name="reference" value="${licenseInstance?.reference}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'concurrentUsers', 'error')} ">
	<label for="concurrentUsers">
		<g:message code="license.concurrentUsers.label" default="Concurrent Users" />
		
	</label>
	<g:textField name="concurrentUsers" value="${licenseInstance?.concurrentUsers}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'remoteAccess', 'error')} ">
	<label for="remoteAccess">
		<g:message code="license.remoteAccess.label" default="Remote Access" />
		
	</label>
	<g:textField name="remoteAccess" value="${licenseInstance?.remoteAccess}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'walkinAccess', 'error')} ">
	<label for="walkinAccess">
		<g:message code="license.walkinAccess.label" default="Walkin Access" />
		
	</label>
	<g:textField name="walkinAccess" value="${licenseInstance?.walkinAccess}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'multisiteAccess', 'error')} ">
	<label for="multisiteAccess">
		<g:message code="license.multisiteAccess.label" default="Multisite Access" />
		
	</label>
	<g:textField name="multisiteAccess" value="${licenseInstance?.multisiteAccess}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'partnersAccess', 'error')} ">
	<label for="partnersAccess">
		<g:message code="license.partnersAccess.label" default="Partners Access" />
		
	</label>
	<g:textField name="partnersAccess" value="${licenseInstance?.partnersAccess}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'alumniAccess', 'error')} ">
	<label for="alumniAccess">
		<g:message code="license.alumniAccess.label" default="Alumni Access" />
		
	</label>
	<g:textField name="alumniAccess" value="${licenseInstance?.alumniAccess}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'ill', 'error')} ">
	<label for="ill">
		<g:message code="license.ill.label" default="Ill" />
		
	</label>
	<g:textField name="ill" value="${licenseInstance?.ill}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'coursepack', 'error')} ">
	<label for="coursepack">
		<g:message code="license.coursepack.label" default="Coursepack" />
		
	</label>
	<g:textField name="coursepack" value="${licenseInstance?.coursepack}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'vle', 'error')} ">
	<label for="vle">
		<g:message code="license.vle.label" default="Vle" />
		
	</label>
	<g:textField name="vle" value="${licenseInstance?.vle}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'enterprise', 'error')} ">
	<label for="enterprise">
		<g:message code="license.enterprise.label" default="Enterprise" />
		
	</label>
	<g:textField name="enterprise" value="${licenseInstance?.enterprise}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'pca', 'error')} ">
	<label for="pca">
		<g:message code="license.pca.label" default="Pca" />
		
	</label>
	<g:textField name="pca" value="${licenseInstance?.pca}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'noticePeriod', 'error')} ">
	<label for="noticePeriod">
		<g:message code="license.noticePeriod.label" default="Notice Period" />
		
	</label>
	<g:textField name="noticePeriod" value="${licenseInstance?.noticePeriod}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'licenseUrl', 'error')} ">
	<label for="licenseUrl">
		<g:message code="license.licenseUrl.label" default="License Url" />
		
	</label>
	<g:textField name="licenseUrl" value="${licenseInstance?.licenseUrl}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'licensorRef', 'error')} ">
	<label for="licensorRef">
		<g:message code="license.licensorRef.label" default="Licensor Ref" />
		
	</label>
	<g:textField name="licensorRef" value="${licenseInstance?.licensorRef}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'licenseeRef', 'error')} ">
	<label for="licenseeRef">
		<g:message code="license.licenseeRef.label" default="Licensee Ref" />
		
	</label>
	<g:textField name="licenseeRef" value="${licenseInstance?.licenseeRef}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'licenseType', 'error')} ">
	<label for="licenseType">
		<g:message code="license.licenseType.label" default="License Type" />
		
	</label>
	<g:textField name="licenseType" value="${licenseInstance?.licenseType}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'licenseStatus', 'error')} ">
	<label for="licenseStatus">
		<g:message code="license.licenseStatus.label" default="License Status" />
		
	</label>
	<g:textField name="licenseStatus" value="${licenseInstance?.licenseStatus}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'lastmod', 'error')} required">
	<label for="lastmod">
		<g:message code="license.lastmod.label" default="Lastmod" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="lastmod" required="" value="${fieldValue(bean: licenseInstance, field: 'lastmod')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'subscriptions', 'error')} ">
	<label for="subscriptions">
		<g:message code="license.subscriptions.label" default="Subscriptions" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${licenseInstance?.subscriptions?}" var="s">
    <li><g:link controller="subscription" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="subscription" action="create" params="['license.id': licenseInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'subscription.label', default: 'Subscription')])}</g:link>
</li>
</ul>

</div>


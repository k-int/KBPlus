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
	<g:select id="concurrentUsers" name="concurrentUsers.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.concurrentUsers?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'remoteAccess', 'error')} ">
	<label for="remoteAccess">
		<g:message code="license.remoteAccess.label" default="Remote Access" />
		
	</label>
	<g:select id="remoteAccess" name="remoteAccess.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.remoteAccess?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'walkinAccess', 'error')} ">
	<label for="walkinAccess">
		<g:message code="license.walkinAccess.label" default="Walkin Access" />
		
	</label>
	<g:select id="walkinAccess" name="walkinAccess.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.walkinAccess?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'multisiteAccess', 'error')} ">
	<label for="multisiteAccess">
		<g:message code="license.multisiteAccess.label" default="Multisite Access" />
		
	</label>
	<g:select id="multisiteAccess" name="multisiteAccess.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.multisiteAccess?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'partnersAccess', 'error')} ">
	<label for="partnersAccess">
		<g:message code="license.partnersAccess.label" default="Partners Access" />
		
	</label>
	<g:select id="partnersAccess" name="partnersAccess.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.partnersAccess?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'alumniAccess', 'error')} ">
	<label for="alumniAccess">
		<g:message code="license.alumniAccess.label" default="Alumni Access" />
		
	</label>
	<g:select id="alumniAccess" name="alumniAccess.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.alumniAccess?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'ill', 'error')} ">
	<label for="ill">
		<g:message code="license.ill.label" default="Ill" />
		
	</label>
	<g:select id="ill" name="ill.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.ill?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'coursepack', 'error')} ">
	<label for="coursepack">
		<g:message code="license.coursepack.label" default="Coursepack" />
		
	</label>
	<g:select id="coursepack" name="coursepack.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.coursepack?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'vle', 'error')} ">
	<label for="vle">
		<g:message code="license.vle.label" default="Vle" />
		
	</label>
	<g:select id="vle" name="vle.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.vle?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'enterprise', 'error')} ">
	<label for="enterprise">
		<g:message code="license.enterprise.label" default="Enterprise" />
		
	</label>
	<g:select id="enterprise" name="enterprise.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.enterprise?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'pca', 'error')} ">
	<label for="pca">
		<g:message code="license.pca.label" default="Pca" />
		
	</label>
	<g:select id="pca" name="pca.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${licenseInstance?.pca?.id}" class="many-to-one" noSelection="['null': '']"/>
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
	<g:field type="number" name="lastmod" required="" value="${licenseInstance.lastmod}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'documents', 'error')} ">
	<label for="documents">
		<g:message code="license.documents.label" default="Documents" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${licenseInstance?.documents?}" var="d">
    <li><g:link controller="docContext" action="show" id="${d.id}">${d?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="docContext" action="create" params="['license.id': licenseInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'docContext.label', default: 'DocContext')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: licenseInstance, field: 'orgLinks', 'error')} ">
	<label for="orgLinks">
		<g:message code="license.orgLinks.label" default="Org Links" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${licenseInstance?.orgLinks?}" var="o">
    <li><g:link controller="orgRole" action="show" id="${o.id}">${o?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="orgRole" action="create" params="['license.id': licenseInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'orgRole.label', default: 'OrgRole')])}</g:link>
</li>
</ul>

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


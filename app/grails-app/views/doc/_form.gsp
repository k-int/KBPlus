<%@ page import="com.k_int.kbplus.Doc" %>



<div class="fieldcontain ${hasErrors(bean: docInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="doc.status.label" default="Status" />
		
	</label>
	<g:select id="status" name="status.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${docInstance?.status?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: docInstance, field: 'type', 'error')} ">
	<label for="type">
		<g:message code="doc.type.label" default="Type" />
		
	</label>
	<g:select id="type" name="type.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${docInstance?.type?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: docInstance, field: 'alert', 'error')} ">
	<label for="alert">
		<g:message code="doc.alert.label" default="Alert" />
		
	</label>
	<g:select id="alert" name="alert.id" from="${com.k_int.kbplus.Alert.list()}" optionKey="id" value="${docInstance?.alert?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: docInstance, field: 'content', 'error')} ">
	<label for="content">
		<g:message code="doc.content.label" default="Content" />
		
	</label>
	<g:textField name="content" value="${docInstance?.content}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: docInstance, field: 'uuid', 'error')} ">
	<label for="uuid">
		<g:message code="doc.uuid.label" default="Uuid" />
		
	</label>
	<g:textField name="uuid" value="${docInstance?.uuid}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: docInstance, field: 'contentType', 'error')} ">
	<label for="contentType">
		<g:message code="doc.contentType.label" default="Content Type" />
		
	</label>
	<g:field type="number" name="contentType" value="${docInstance.contentType}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: docInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="doc.title.label" default="Title" />
		
	</label>
	<g:textField name="title" value="${docInstance?.title}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: docInstance, field: 'filename', 'error')} ">
	<label for="filename">
		<g:message code="doc.filename.label" default="Filename" />
		
	</label>
	<g:textField name="filename" value="${docInstance?.filename}"/>
</div>


<%@ page import="com.k_int.kbplus.TitleInstance" %>



<div class="fieldcontain ${hasErrors(bean: titleInstanceInstance, field: 'ids', 'error')} ">
	<label for="ids">
		<g:message code="titleInstance.ids.label" default="Ids" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${titleInstanceInstance?.ids?}" var="i">
    <li><g:link controller="titleSID" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="titleSID" action="create" params="['titleInstance.id': titleInstanceInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'titleSID.label', default: 'TitleSID')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: titleInstanceInstance, field: 'impId', 'error')} ">
	<label for="impId">
		<g:message code="titleInstance.impId.label" default="Imp Id" />
		
	</label>
	<g:textField name="impId" value="${titleInstanceInstance?.impId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstanceInstance, field: 'tipps', 'error')} ">
	<label for="tipps">
		<g:message code="titleInstance.tipps.label" default="Tipps" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${titleInstanceInstance?.tipps?}" var="t">
    <li><g:link controller="titleInstancePackagePlatform" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="titleInstancePackagePlatform" action="create" params="['titleInstance.id': titleInstanceInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: titleInstanceInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="titleInstance.title.label" default="Title" />
		
	</label>
	<g:textField name="title" value="${titleInstanceInstance?.title}"/>
</div>


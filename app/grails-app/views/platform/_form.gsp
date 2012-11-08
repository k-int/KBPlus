<%@ page import="com.k_int.kbplus.Platform" %>



<div class="fieldcontain ${hasErrors(bean: platformInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="platform.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${platformInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: platformInstance, field: 'tipps', 'error')} ">
	<label for="tipps">
		<g:message code="platform.tipps.label" default="Tipps" />
		
	</label>
	<g:select name="tipps" from="${com.k_int.kbplus.TitleInstancePackagePlatform.list()}" multiple="multiple" optionKey="id" size="5" value="${platformInstance?.tipps*.id}" class="many-to-many"/>
</div>


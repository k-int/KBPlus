<%@ page import="com.k_int.kbplus.Package" %>



<div class="fieldcontain ${hasErrors(bean: packageInstance, field: 'identifier', 'error')} ">
	<label for="identifier">
		<g:message code="package.identifier.label" default="Identifier" />
		
	</label>
	<g:textField name="identifier" value="${packageInstance?.identifier}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: packageInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="package.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${packageInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: packageInstance, field: 'tipps', 'error')} ">
	<label for="tipps">
		<g:message code="package.tipps.label" default="Tipps" />
		
	</label>
	<g:select name="tipps" from="${com.k_int.kbplus.TitleInstancePackagePlatform.list()}" multiple="multiple" optionKey="id" size="5" value="${packageInstance?.tipps*.id}" class="many-to-many"/>
</div>


<%@ page import="com.k_int.kbplus.Identifier" %>



<div class="fieldcontain ${hasErrors(bean: identifierInstance, field: 'ns', 'error')} required">
	<label for="ns">
		<g:message code="identifier.ns.label" default="Ns" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="ns" name="ns.id" from="${com.k_int.kbplus.IdentifierNamespace.list()}" optionKey="id" required="" value="${identifierInstance?.ns?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: identifierInstance, field: 'value', 'error')} ">
	<label for="value">
		<g:message code="identifier.value.label" default="Value" />
		
	</label>
	<g:textField name="value" value="${identifierInstance?.value}"/>
</div>


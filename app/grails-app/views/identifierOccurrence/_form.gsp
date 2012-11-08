<%@ page import="com.k_int.kbplus.IdentifierOccurrence" %>



<div class="fieldcontain ${hasErrors(bean: identifierOccurrenceInstance, field: 'org', 'error')} ">
	<label for="org">
		<g:message code="identifierOccurrence.org.label" default="Org" />
		
	</label>
	<g:select id="org" name="org.id" from="${com.k_int.kbplus.Org.list()}" optionKey="id" value="${identifierOccurrenceInstance?.org?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: identifierOccurrenceInstance, field: 'ti', 'error')} ">
	<label for="ti">
		<g:message code="identifierOccurrence.ti.label" default="Ti" />
		
	</label>
	<g:select id="ti" name="ti.id" from="${com.k_int.kbplus.TitleInstance.list()}" optionKey="id" value="${identifierOccurrenceInstance?.ti?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: identifierOccurrenceInstance, field: 'tipp', 'error')} ">
	<label for="tipp">
		<g:message code="identifierOccurrence.tipp.label" default="Tipp" />
		
	</label>
	<g:select id="tipp" name="tipp.id" from="${com.k_int.kbplus.TitleInstancePackagePlatform.list()}" optionKey="id" value="${identifierOccurrenceInstance?.tipp?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: identifierOccurrenceInstance, field: 'identifier', 'error')} required">
	<label for="identifier">
		<g:message code="identifierOccurrence.identifier.label" default="Identifier" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="identifier" name="identifier.id" from="${com.k_int.kbplus.Identifier.list()}" optionKey="id" required="" value="${identifierOccurrenceInstance?.identifier?.id}" class="many-to-one"/>
</div>


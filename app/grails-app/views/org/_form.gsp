<%@ page import="com.k_int.kbplus.Org" %>



<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'address', 'error')} ">
	<label for="address">
		<g:message code="org.address.label" default="Address" />
		
	</label>
	<g:textArea name="address" cols="40" rows="5" maxlength="256" value="${orgInstance?.address}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'ipRange', 'error')} ">
	<label for="ipRange">
		<g:message code="org.ipRange.label" default="Ip Range" />
		
	</label>
	<g:textArea name="ipRange" cols="40" rows="5" maxlength="256" value="${orgInstance?.ipRange}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'sector', 'error')} ">
	<label for="sector">
		<g:message code="org.sector.label" default="Sector" />
		
	</label>
	<g:textField name="sector" maxlength="128" value="${orgInstance?.sector}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'ids', 'error')} ">
	<label for="ids">
		<g:message code="org.ids.label" default="Ids" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${orgInstance?.ids?}" var="i">
    <li><g:link controller="identifierOccurrence" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="identifierOccurrence" action="create" params="['org.id': orgInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'impId', 'error')} ">
	<label for="impId">
		<g:message code="org.impId.label" default="Imp Id" />
		
	</label>
	<g:textField name="impId" value="${orgInstance?.impId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="org.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${orgInstance?.name}"/>
</div>


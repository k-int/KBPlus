<%@ page import="com.k_int.kbplus.Subscription" %>



<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="subscription.status.label" default="Status" />
		
	</label>
	<g:select id="status" name="status.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${subscriptionInstance?.status?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'type', 'error')} ">
	<label for="type">
		<g:message code="subscription.type.label" default="Type" />
		
	</label>
	<g:select id="type" name="type.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${subscriptionInstance?.type?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'owner', 'error')} ">
	<label for="owner">
		<g:message code="subscription.owner.label" default="Owner" />
		
	</label>
	<g:select id="owner" name="owner.id" from="${com.k_int.kbplus.License.list()}" optionKey="id" value="${subscriptionInstance?.owner?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'impId', 'error')} ">
	<label for="impId">
		<g:message code="subscription.impId.label" default="Imp Id" />
		
	</label>
	<g:textField name="impId" value="${subscriptionInstance?.impId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'startDate', 'error')} ">
	<label for="startDate">
		<g:message code="subscription.startDate.label" default="Start Date" />
		
	</label>
	<g:datePicker name="startDate" precision="day"  value="${subscriptionInstance?.startDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'endDate', 'error')} ">
	<label for="endDate">
		<g:message code="subscription.endDate.label" default="End Date" />
		
	</label>
	<g:datePicker name="endDate" precision="day"  value="${subscriptionInstance?.endDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'instanceOf', 'error')} ">
	<label for="instanceOf">
		<g:message code="subscription.instanceOf.label" default="Instance Of" />
		
	</label>
	<g:select id="instanceOf" name="instanceOf.id" from="${com.k_int.kbplus.Subscription.list()}" optionKey="id" value="${subscriptionInstance?.instanceOf?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'identifier', 'error')} ">
	<label for="identifier">
		<g:message code="subscription.identifier.label" default="Identifier" />
		
	</label>
	<g:textField name="identifier" value="${subscriptionInstance?.identifier}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="subscription.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${subscriptionInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'packages', 'error')} ">
	<label for="packages">
		<g:message code="subscription.packages.label" default="Packages" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${subscriptionInstance?.packages?}" var="p">
    <li><g:link controller="subscriptionPackage" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="subscriptionPackage" action="create" params="['subscription.id': subscriptionInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'subscriptionPackage.label', default: 'SubscriptionPackage')])}</g:link>
</li>
</ul>

</div>


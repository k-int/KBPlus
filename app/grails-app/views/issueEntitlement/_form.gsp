<%@ page import="com.k_int.kbplus.IssueEntitlement" %>



<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="issueEntitlement.status.label" default="Status" />
		
	</label>
	<g:select id="status" name="status.id" from="${com.k_int.kbplus.RefdataValue.list()}" optionKey="id" value="${issueEntitlementInstance?.status?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'subscription', 'error')} ">
	<label for="subscription">
		<g:message code="issueEntitlement.subscription.label" default="Subscription" />
		
	</label>
	<g:select id="subscription" name="subscription.id" from="${com.k_int.kbplus.Subscription.list()}" optionKey="id" value="${issueEntitlementInstance?.subscription?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'tipp', 'error')} ">
	<label for="tipp">
		<g:message code="issueEntitlement.tipp.label" default="Tipp" />
		
	</label>
	<g:select id="tipp" name="tipp.id" from="${com.k_int.kbplus.TitleInstancePackagePlatform.list()}" optionKey="id" value="${issueEntitlementInstance?.tipp?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'startDate', 'error')} ">
	<label for="startDate">
		<g:message code="issueEntitlement.startDate.label" default="Start Date" />
		
	</label>
	<g:datePicker name="startDate" precision="day"  value="${issueEntitlementInstance?.startDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'startVolume', 'error')} ">
	<label for="startVolume">
		<g:message code="issueEntitlement.startVolume.label" default="Start Volume" />
		
	</label>
	<g:textField name="startVolume" value="${issueEntitlementInstance?.startVolume}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'startIssue', 'error')} ">
	<label for="startIssue">
		<g:message code="issueEntitlement.startIssue.label" default="Start Issue" />
		
	</label>
	<g:textField name="startIssue" value="${issueEntitlementInstance?.startIssue}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'endDate', 'error')} ">
	<label for="endDate">
		<g:message code="issueEntitlement.endDate.label" default="End Date" />
		
	</label>
	<g:datePicker name="endDate" precision="day"  value="${issueEntitlementInstance?.endDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'endVolume', 'error')} ">
	<label for="endVolume">
		<g:message code="issueEntitlement.endVolume.label" default="End Volume" />
		
	</label>
	<g:textField name="endVolume" value="${issueEntitlementInstance?.endVolume}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'endIssue', 'error')} ">
	<label for="endIssue">
		<g:message code="issueEntitlement.endIssue.label" default="End Issue" />
		
	</label>
	<g:textField name="endIssue" value="${issueEntitlementInstance?.endIssue}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'embargo', 'error')} ">
	<label for="embargo">
		<g:message code="issueEntitlement.embargo.label" default="Embargo" />
		
	</label>
	<g:textField name="embargo" value="${issueEntitlementInstance?.embargo}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'coverageDepth', 'error')} ">
	<label for="coverageDepth">
		<g:message code="issueEntitlement.coverageDepth.label" default="Coverage Depth" />
		
	</label>
	<g:textField name="coverageDepth" value="${issueEntitlementInstance?.coverageDepth}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'coverageNote', 'error')} ">
	<label for="coverageNote">
		<g:message code="issueEntitlement.coverageNote.label" default="Coverage Note" />
		
	</label>
	<g:textField name="coverageNote" value="${issueEntitlementInstance?.coverageNote}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'coreTitle', 'error')} ">
	<label for="coreTitle">
		<g:message code="issueEntitlement.coreTitle.label" default="Core Title" />
		
	</label>
	<g:checkBox name="coreTitle" value="${issueEntitlementInstance?.coreTitle}" />
</div>

<div class="fieldcontain ${hasErrors(bean: issueEntitlementInstance, field: 'ieReason', 'error')} ">
	<label for="ieReason">
		<g:message code="issueEntitlement.ieReason.label" default="Ie Reason" />
		
	</label>
	<g:textField name="ieReason" value="${issueEntitlementInstance?.ieReason}"/>
</div>


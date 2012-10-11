
<%@ page import="com.k_int.kbplus.IssueEntitlement" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'issueEntitlement.label', default: 'IssueEntitlement')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li>
							<g:link class="list" action="list">
								<i class="icon-list"></i>
								<g:message code="default.list.label" args="[entityName]" />
							</g:link>
						</li>
						<li>
							<g:link class="create" action="create">
								<i class="icon-plus"></i>
								<g:message code="default.create.label" args="[entityName]" />
							</g:link>
						</li>
					</ul>
				</div>
			</div>
			
			<div class="span9">

				<div class="page-header">
					<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>

				<dl>
				
					<g:if test="${issueEntitlementInstance?.status}">
						<dt><g:message code="issueEntitlement.status.label" default="Status" /></dt>
						
							<dd><g:link controller="refdataValue" action="show" id="${issueEntitlementInstance?.status?.id}">${issueEntitlementInstance?.status?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.subscription}">
						<dt><g:message code="issueEntitlement.subscription.label" default="Subscription" /></dt>
						
							<dd><g:link controller="subscription" action="show" id="${issueEntitlementInstance?.subscription?.id}">${issueEntitlementInstance?.subscription?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.tipp}">
						<dt><g:message code="issueEntitlement.tipp.label" default="Tipp" /></dt>
						
							<dd><g:link controller="titleInstancePackagePlatform" action="show" id="${issueEntitlementInstance?.tipp?.id}">${issueEntitlementInstance?.tipp?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.startDate}">
						<dt><g:message code="issueEntitlement.startDate.label" default="Start Date" /></dt>
						
							<dd><g:formatDate date="${issueEntitlementInstance?.startDate}" /></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.startVolume}">
						<dt><g:message code="issueEntitlement.startVolume.label" default="Start Volume" /></dt>
						
							<dd><g:fieldValue bean="${issueEntitlementInstance}" field="startVolume"/></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.startIssue}">
						<dt><g:message code="issueEntitlement.startIssue.label" default="Start Issue" /></dt>
						
							<dd><g:fieldValue bean="${issueEntitlementInstance}" field="startIssue"/></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.endDate}">
						<dt><g:message code="issueEntitlement.endDate.label" default="End Date" /></dt>
						
							<dd><g:formatDate date="${issueEntitlementInstance?.endDate}" /></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.endVolume}">
						<dt><g:message code="issueEntitlement.endVolume.label" default="End Volume" /></dt>
						
							<dd><g:fieldValue bean="${issueEntitlementInstance}" field="endVolume"/></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.endIssue}">
						<dt><g:message code="issueEntitlement.endIssue.label" default="End Issue" /></dt>
						
							<dd><g:fieldValue bean="${issueEntitlementInstance}" field="endIssue"/></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.embargo}">
						<dt><g:message code="issueEntitlement.embargo.label" default="Embargo" /></dt>
						
							<dd><g:fieldValue bean="${issueEntitlementInstance}" field="embargo"/></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.coverageDepth}">
						<dt><g:message code="issueEntitlement.coverageDepth.label" default="Coverage Depth" /></dt>
						
							<dd><g:fieldValue bean="${issueEntitlementInstance}" field="coverageDepth"/></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.coverageNote}">
						<dt><g:message code="issueEntitlement.coverageNote.label" default="Coverage Note" /></dt>
						
							<dd><g:fieldValue bean="${issueEntitlementInstance}" field="coverageNote"/></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.coreTitle}">
						<dt><g:message code="issueEntitlement.coreTitle.label" default="Core Title" /></dt>
						
							<dd><g:formatBoolean boolean="${issueEntitlementInstance?.coreTitle}" /></dd>
						
					</g:if>
				
					<g:if test="${issueEntitlementInstance?.ieReason}">
						<dt><g:message code="issueEntitlement.ieReason.label" default="Ie Reason" /></dt>
						
							<dd><g:fieldValue bean="${issueEntitlementInstance}" field="ieReason"/></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${issueEntitlementInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${issueEntitlementInstance?.id}">
							<i class="icon-pencil"></i>
							<g:message code="default.button.edit.label" default="Edit" />
						</g:link>
						<button class="btn btn-danger" type="submit" name="_action_delete">
							<i class="icon-trash icon-white"></i>
							<g:message code="default.button.delete.label" default="Delete" />
						</button>
					</div>
				</g:form>

			</div>

		</div>
	</body>
</html>

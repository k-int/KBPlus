
<%@ page import="com.k_int.kbplus.IssueEntitlement" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'issueEntitlement.label', default: 'IssueEntitlement')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li class="active">
							<g:link class="list" action="list">
								<i class="icon-list icon-white"></i>
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
					<h1><g:message code="default.list.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				
				<table class="table table-striped">
					<thead>
						<tr>
						
							<th class="header"><g:message code="issueEntitlement.status.label" default="Status" /></th>
						
							<th class="header"><g:message code="issueEntitlement.subscription.label" default="Subscription" /></th>
						
							<th class="header"><g:message code="issueEntitlement.tipp.label" default="Tipp" /></th>
						
							<g:sortableColumn property="startDate" title="${message(code: 'issueEntitlement.startDate.label', default: 'Start Date')}" />
						
							<g:sortableColumn property="startVolume" title="${message(code: 'issueEntitlement.startVolume.label', default: 'Start Volume')}" />
						
							<g:sortableColumn property="startIssue" title="${message(code: 'issueEntitlement.startIssue.label', default: 'Start Issue')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${issueEntitlementInstanceList}" var="issueEntitlementInstance">
						<tr>
						
							<td>${fieldValue(bean: issueEntitlementInstance, field: "status")}</td>
						
							<td>${fieldValue(bean: issueEntitlementInstance, field: "subscription")}</td>
						
							<td>${fieldValue(bean: issueEntitlementInstance, field: "tipp")}</td>
						
							<td><g:formatDate date="${issueEntitlementInstance.startDate}" /></td>
						
							<td>${fieldValue(bean: issueEntitlementInstance, field: "startVolume")}</td>
						
							<td>${fieldValue(bean: issueEntitlementInstance, field: "startIssue")}</td>
						
							<td class="link">
								<g:link action="show" id="${issueEntitlementInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${issueEntitlementInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

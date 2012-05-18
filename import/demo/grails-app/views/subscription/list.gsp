
<%@ page import="com.k_int.kbplus.Subscription" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'subscription.label', default: 'Subscription')}" />
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
						
							<th class="header"><g:message code="subscription.status.label" default="Status" /></th>
						
							<th class="header"><g:message code="subscription.type.label" default="Type" /></th>
						
							<th class="header"><g:message code="subscription.owner.label" default="Owner" /></th>
						
							<g:sortableColumn property="impId" title="${message(code: 'subscription.impId.label', default: 'Imp Id')}" />
						
							<g:sortableColumn property="startDate" title="${message(code: 'subscription.startDate.label', default: 'Start Date')}" />
						
							<g:sortableColumn property="endDate" title="${message(code: 'subscription.endDate.label', default: 'End Date')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${subscriptionInstanceList}" var="subscriptionInstance">
						<tr>
						
							<td>${fieldValue(bean: subscriptionInstance, field: "status")}</td>
						
							<td>${fieldValue(bean: subscriptionInstance, field: "type")}</td>
						
							<td>${fieldValue(bean: subscriptionInstance, field: "owner")}</td>
						
							<td>${fieldValue(bean: subscriptionInstance, field: "impId")}</td>
						
							<td><g:formatDate date="${subscriptionInstance.startDate}" /></td>
						
							<td><g:formatDate date="${subscriptionInstance.endDate}" /></td>
						
							<td class="link">
								<g:link action="show" id="${subscriptionInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${subscriptionInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

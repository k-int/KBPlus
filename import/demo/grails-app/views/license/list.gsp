
<%@ page import="com.k_int.kbplus.License" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'license.label', default: 'License')}" />
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
                                                <sec:ifLoggedIn>
						<li>
							<g:link class="create" action="create">
								<i class="icon-plus"></i>
								<g:message code="default.create.label" args="[entityName]" />
							</g:link>
						</li>
                                                </sec:ifLoggedIn>
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
						
							<th class="header"><g:message code="license.status.label" default="Status" /></th>
						
							<th class="header"><g:message code="license.type.label" default="Type" /></th>
						
							<g:sortableColumn property="reference" title="${message(code: 'license.reference.label', default: 'Reference')}" />
						
							<g:sortableColumn property="concurrentUsers" title="${message(code: 'license.concurrentUsers.label', default: 'Concurrent Users')}" />
						
							<g:sortableColumn property="remoteAccess" title="${message(code: 'license.remoteAccess.label', default: 'Remote Access')}" />
						
							<g:sortableColumn property="walkinAccess" title="${message(code: 'license.walkinAccess.label', default: 'Walkin Access')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${licenseInstanceList}" var="licenseInstance">
						<tr>
						
							<td>${fieldValue(bean: licenseInstance, field: "status")}</td>
						
							<td>${fieldValue(bean: licenseInstance, field: "type")}</td>
						
							<td>${fieldValue(bean: licenseInstance, field: "reference")}</td>
						
							<td>${fieldValue(bean: licenseInstance, field: "concurrentUsers")}</td>
						
							<td>${fieldValue(bean: licenseInstance, field: "remoteAccess")}</td>
						
							<td>${fieldValue(bean: licenseInstance, field: "walkinAccess")}</td>
						
							<td class="link">
								<g:link action="show" id="${licenseInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${licenseInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

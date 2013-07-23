
<%@ page import="com.k_int.kbplus.OnixplUsageTermLicenseText" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'onixplUsageTermLicenseText.label', default: 'OnixplUsageTermLicenseText')}" />
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
						
							<th class="header"><g:message code="onixplUsageTermLicenseText.usageTerm.label" default="Usage Term" /></th>
						
							<th class="header"><g:message code="onixplUsageTermLicenseText.licenseText.label" default="License Text" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${onixplUsageTermLicenseTextInstanceList}" var="onixplUsageTermLicenseTextInstance">
						<tr>
						
							<td>${fieldValue(bean: onixplUsageTermLicenseTextInstance, field: "usageTerm")}</td>
						
							<td>${fieldValue(bean: onixplUsageTermLicenseTextInstance, field: "licenseText")}</td>
						
							<td class="link">
								<g:link action="show" id="${onixplUsageTermLicenseTextInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${onixplUsageTermLicenseTextInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

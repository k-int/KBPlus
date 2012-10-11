
<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span2">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li class="active">
							<g:link class="list" action="list">
								<i class="icon-list icon-white"></i>
								<g:message code="default.list.label" args="[entityName]" />
							</g:link>
						</li>
                                                <sec:ifAnyGranted roles="ROLE_EDITOR,ROLE_ADMIN">
						<li>
							<g:link class="create" action="create">
								<i class="icon-plus"></i>
								<g:message code="default.create.label" args="[entityName]" />
							</g:link>
						</li>
                                                </sec:ifAnyGranted>
					</ul>
				</div>
			</div>

			<div class="span10">
				
				<div class="page-header">
					<h1><g:message code="default.list.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				
				<table class="table table-striped">
					<thead>
						<tr>
						
							<g:sortableColumn property="identifier" title="${message(code: 'package.identifier.label', default: 'Identifier')}" />
						
							<g:sortableColumn property="name" title="${message(code: 'package.name.label', default: 'Name')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${packageInstanceList}" var="packageInstance">
						<tr>
						
							<td>${fieldValue(bean: packageInstance, field: "identifier")}</td>
						
							<td>${fieldValue(bean: packageInstance, field: "name")} (${packageInstance?.contentProvider?.name})</td>
						
							<td class="link">
								<g:link action="show" id="${packageInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${packageInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

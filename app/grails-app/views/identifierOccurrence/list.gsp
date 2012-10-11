
<%@ page import="com.k_int.kbplus.IdentifierOccurrence" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'identifierOccurrence.label', default: 'IdentifierOccurrence')}" />
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
						
							<th class="header"><g:message code="identifierOccurrence.org.label" default="Org" /></th>
						
							<th class="header"><g:message code="identifierOccurrence.ti.label" default="Ti" /></th>
						
							<th class="header"><g:message code="identifierOccurrence.tipp.label" default="Tipp" /></th>
						
							<th class="header"><g:message code="identifierOccurrence.identifier.label" default="Identifier" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${identifierOccurrenceInstanceList}" var="identifierOccurrenceInstance">
						<tr>
						
							<td>${fieldValue(bean: identifierOccurrenceInstance, field: "org")}</td>
						
							<td>${fieldValue(bean: identifierOccurrenceInstance, field: "ti")}</td>
						
							<td>${fieldValue(bean: identifierOccurrenceInstance, field: "tipp")}</td>
						
							<td>${fieldValue(bean: identifierOccurrenceInstance, field: "identifier")}</td>
						
							<td class="link">
								<g:link action="show" id="${identifierOccurrenceInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${identifierOccurrenceInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

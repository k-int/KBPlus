
<%@ page import="com.k_int.kbplus.TitleInstancePackagePlatform" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'titleInstancePackagePlatform.label', default: 'TitleInstancePackagePlatform')}" />
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
						
							<g:sortableColumn property="startDate" title="${message(code: 'titleInstancePackagePlatform.startDate.label', default: 'Start Date')}" />
						
							<g:sortableColumn property="startVolume" title="${message(code: 'titleInstancePackagePlatform.startVolume.label', default: 'Start Volume')}" />
						
							<g:sortableColumn property="startIssue" title="${message(code: 'titleInstancePackagePlatform.startIssue.label', default: 'Start Issue')}" />
						
							<g:sortableColumn property="endDate" title="${message(code: 'titleInstancePackagePlatform.endDate.label', default: 'End Date')}" />
						
							<g:sortableColumn property="endVolume" title="${message(code: 'titleInstancePackagePlatform.endVolume.label', default: 'End Volume')}" />
						
							<g:sortableColumn property="endIssue" title="${message(code: 'titleInstancePackagePlatform.endIssue.label', default: 'End Issue')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${titleInstancePackagePlatformInstanceList}" var="titleInstancePackagePlatformInstance">
						<tr>
						
							<td>${fieldValue(bean: titleInstancePackagePlatformInstance, field: "startDate")}</td>
						
							<td>${fieldValue(bean: titleInstancePackagePlatformInstance, field: "startVolume")}</td>
						
							<td>${fieldValue(bean: titleInstancePackagePlatformInstance, field: "startIssue")}</td>
						
							<td>${fieldValue(bean: titleInstancePackagePlatformInstance, field: "endDate")}</td>
						
							<td>${fieldValue(bean: titleInstancePackagePlatformInstance, field: "endVolume")}</td>
						
							<td>${fieldValue(bean: titleInstancePackagePlatformInstance, field: "endIssue")}</td>
						
							<td class="link">
								<g:link action="show" id="${titleInstancePackagePlatformInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${titleInstancePackagePlatformInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

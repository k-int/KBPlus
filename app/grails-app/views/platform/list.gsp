
<%@ page import="com.k_int.kbplus.Platform" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'platform.label', default: 'Platform')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="container">
			<div class="span12">
				
				<div class="page-header">
					<h1><g:message code="default.list.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>

				<div class="container" style="text-align:left">
					<g:form action="list" method="get" class="form-inline">
						<label>Search text : </label> <input type="text" name="q" placeholder="enter search term..." value="${params.q?.encodeAsHTML()}"  /> &nbsp;
						<input type="submit" class="btn btn-primary" value="Search" />
					</g:form><br/>
				</div>

				<table class="table table-bordered table-striped">
					<thead>
						<tr>
						
							<g:sortableColumn property="name" title="${message(code: 'platform.name.label', default: 'Name')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${platformInstanceList}" var="platformInstance">
						<tr>
						
							<td>${fieldValue(bean: platformInstance, field: "name")}</td>
						
							<td class="link">
								<g:link action="show" id="${platformInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate  action="list" controller="platform" params="${params}" next="Next" prev="Prev" max="${max}" total="${platformInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

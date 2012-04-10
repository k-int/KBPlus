
<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
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
				
					<g:if test="${packageInstance?.identifier}">
						<dt><g:message code="package.identifier.label" default="Identifier" /></dt>
						
							<dd><g:fieldValue bean="${packageInstance}" field="identifier"/></dd>
						
					</g:if>
				
					<g:if test="${packageInstance?.name}">
						<dt><g:message code="package.name.label" default="Name" /></dt>
						
							<dd><g:fieldValue bean="${packageInstance}" field="name"/></dd>
						
					</g:if>
				
					<g:if test="${packageInstance?.tipps}">
						<dt><g:message code="package.tipps.label" default="Tipps" /></dt>
						
							<g:each in="${packageInstance.tipps}" var="t">
							<dd><g:link controller="titleInstancePackagePlatform" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${packageInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${packageInstance?.id}">
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

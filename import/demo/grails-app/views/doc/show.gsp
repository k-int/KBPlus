
<%@ page import="com.k_int.kbplus.Doc" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'doc.label', default: 'Doc')}" />
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
				
					<g:if test="${docInstance?.status}">
						<dt><g:message code="doc.status.label" default="Status" /></dt>
						
							<dd><g:link controller="refdataValue" action="show" id="${docInstance?.status?.id}">${docInstance?.status?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.type}">
						<dt><g:message code="doc.type.label" default="Type" /></dt>
						
							<dd><g:link controller="refdataValue" action="show" id="${docInstance?.type?.id}">${docInstance?.type?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.alert}">
						<dt><g:message code="doc.alert.label" default="Alert" /></dt>
						
							<dd><g:link controller="alert" action="show" id="${docInstance?.alert?.id}">${docInstance?.alert?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.content}">
						<dt><g:message code="doc.content.label" default="Content" /></dt>
						
							<dd><g:fieldValue bean="${docInstance}" field="content"/></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.uuid}">
						<dt><g:message code="doc.uuid.label" default="Uuid" /></dt>
						
							<dd><g:fieldValue bean="${docInstance}" field="uuid"/></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.contentType}">
						<dt><g:message code="doc.contentType.label" default="Content Type" /></dt>
						
							<dd><g:fieldValue bean="${docInstance}" field="contentType"/></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.title}">
						<dt><g:message code="doc.title.label" default="Title" /></dt>
						
							<dd><g:fieldValue bean="${docInstance}" field="title"/></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.filename}">
						<dt><g:message code="doc.filename.label" default="Filename" /></dt>
						
							<dd><g:fieldValue bean="${docInstance}" field="filename"/></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.dateCreated}">
						<dt><g:message code="doc.dateCreated.label" default="Date Created" /></dt>
						
							<dd><g:formatDate date="${docInstance?.dateCreated}" /></dd>
						
					</g:if>
				
					<g:if test="${docInstance?.lastUpdated}">
						<dt><g:message code="doc.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd><g:formatDate date="${docInstance?.lastUpdated}" /></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${docInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${docInstance?.id}">
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

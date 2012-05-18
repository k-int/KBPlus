
<%@ page import="com.k_int.kbplus.Subscription" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'subscription.label', default: 'Subscription')}" />
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
				
					<g:if test="${subscriptionInstance?.status}">
						<dt><g:message code="subscription.status.label" default="Status" /></dt>
						
							<dd><g:link controller="refdataValue" action="show" id="${subscriptionInstance?.status?.id}">${subscriptionInstance?.status?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.type}">
						<dt><g:message code="subscription.type.label" default="Type" /></dt>
						
							<dd><g:link controller="refdataValue" action="show" id="${subscriptionInstance?.type?.id}">${subscriptionInstance?.type?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.owner}">
						<dt><g:message code="subscription.owner.label" default="Owner" /></dt>
						
							<dd><g:link controller="license" action="show" id="${subscriptionInstance?.owner?.id}">${subscriptionInstance?.owner?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.impId}">
						<dt><g:message code="subscription.impId.label" default="Imp Id" /></dt>
						
							<dd><g:fieldValue bean="${subscriptionInstance}" field="impId"/></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.startDate}">
						<dt><g:message code="subscription.startDate.label" default="Start Date" /></dt>
						
							<dd><g:formatDate date="${subscriptionInstance?.startDate}" /></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.endDate}">
						<dt><g:message code="subscription.endDate.label" default="End Date" /></dt>
						
							<dd><g:formatDate date="${subscriptionInstance?.endDate}" /></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.instanceOf}">
						<dt><g:message code="subscription.instanceOf.label" default="Instance Of" /></dt>
						
							<dd><g:link controller="subscription" action="show" id="${subscriptionInstance?.instanceOf?.id}">${subscriptionInstance?.instanceOf?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.identifier}">
						<dt><g:message code="subscription.identifier.label" default="Identifier" /></dt>
						
							<dd><g:fieldValue bean="${subscriptionInstance}" field="identifier"/></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.name}">
						<dt><g:message code="subscription.name.label" default="Name" /></dt>
						
							<dd><g:fieldValue bean="${subscriptionInstance}" field="name"/></dd>
						
					</g:if>
				
					<g:if test="${subscriptionInstance?.packages}">
						<dt><g:message code="subscription.packages.label" default="Packages" /></dt>
						
							<g:each in="${subscriptionInstance.packages}" var="p">
							<dd><g:link controller="subscriptionPackage" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${subscriptionInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${subscriptionInstance?.id}">
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

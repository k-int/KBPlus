
<%@ page import="com.k_int.kbplus.License" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'license.label', default: 'Licence')}" />
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
				
					<g:if test="${licenseInstance?.status}">
						<dt><g:message code="license.status.label" default="Status" /></dt>
						
							<dd>${licenseInstance?.status?.value?.encodeAsHTML()}</dd>
      
						
					</g:if>
				
					<g:if test="${licenseInstance?.type}">
						<dt><g:message code="license.type.label" default="Type" /></dt>
						
							<dd>${licenseInstance?.type?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.reference}">
						<dt><g:message code="license.reference.label" default="Reference" /></dt>
						
							<dd><g:fieldValue bean="${licenseInstance}" field="reference"/></dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.concurrentUsers}">
						<dt><g:message code="license.concurrentUsers.label" default="Concurrent Users" /></dt>
						
							<dd>${licenseInstance?.concurrentUsers?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.remoteAccess}">
						<dt><g:message code="license.remoteAccess.label" default="Remote Access" /></dt>
						
							<dd>${licenseInstance?.remoteAccess?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.walkinAccess}">
						<dt><g:message code="license.walkinAccess.label" default="Walkin Access" /></dt>
						
							<dd>${licenseInstance?.walkinAccess?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.multisiteAccess}">
						<dt><g:message code="license.multisiteAccess.label" default="Multisite Access" /></dt>
						
							<dd>${licenseInstance?.multisiteAccess?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.partnersAccess}">
						<dt><g:message code="license.partnersAccess.label" default="Partners Access" /></dt>
						
							<dd>${licenseInstance?.partnersAccess?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.alumniAccess}">
						<dt><g:message code="license.alumniAccess.label" default="Alumni Access" /></dt>
						
							<dd>${licenseInstance?.alumniAccess?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.ill}">
						<dt><g:message code="license.ill.label" default="Ill" /></dt>
						
							<dd>${licenseInstance?.ill?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.coursepack}">
						<dt><g:message code="license.coursepack.label" default="Coursepack" /></dt>
						
							<dd>${licenseInstance?.coursepack?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.vle}">
						<dt><g:message code="license.vle.label" default="Vle" /></dt>
						
							<dd>${licenseInstance?.vle?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.enterprise}">
						<dt><g:message code="license.enterprise.label" default="Enterprise" /></dt>
						
							<dd>${licenseInstance?.enterprise?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.pca}">
						<dt><g:message code="license.pca.label" default="Pca" /></dt>
						
							<dd>${licenseInstance?.pca?.value?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.noticePeriod}">
						<dt><g:message code="license.noticePeriod.label" default="Notice Period" /></dt>
						
							<dd><g:fieldValue bean="${licenseInstance}" field="noticePeriod"/></dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.licenseUrl}">
						<dt><g:message code="license.licenseUrl.label" default="Licence Url" /></dt>
						
							<dd><g:fieldValue bean="${licenseInstance}" field="licenseUrl"/></dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.licensorRef}">
						<dt><g:message code="license.licensorRef.label" default="Licensor Ref" /></dt>
						
							<dd><g:fieldValue bean="${licenseInstance}" field="licensorRef"/></dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.licenseeRef}">
						<dt><g:message code="license.licenseeRef.label" default="Licencee Ref" /></dt>
						
							<dd><g:fieldValue bean="${licenseInstance}" field="licenseeRef"/></dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.licenseType}">
						<dt><g:message code="license.licenseType.label" default="Licence Type" /></dt>
						
							<dd><g:fieldValue bean="${licenseInstance}" field="licenseType"/></dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.licenseStatus}">
						<dt><g:message code="license.licenseStatus.label" default="Licence Status" /></dt>
						
							<dd><g:fieldValue bean="${licenseInstance}" field="licenseStatus"/></dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.lastmod}">
						<dt><g:message code="license.lastmod.label" default="Lastmod" /></dt>
						
							<dd><g:fieldValue bean="${licenseInstance}" field="lastmod"/></dd>
						
					</g:if>
				
					<g:if test="${licenseInstance?.documents}">
						<dt><g:message code="license.documents.label" default="Documents" /></dt>
						
							<g:each in="${licenseInstance.documents}" var="d">
							<dd><g:link controller="docContext" action="show" id="${d.id}">${d?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${licenseInstance?.orgLinks}">
						<dt><g:message code="license.orgLinks.label" default="Org Links" /></dt>
						
							<g:each in="${licenseInstance.orgLinks}" var="o">
							<dd><g:link controller="orgRole" action="show" id="${o.id}">${o?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${licenseInstance?.subscriptions}">
						<dt><g:message code="license.subscriptions.label" default="Subscriptions" /></dt>
						
							<g:each in="${licenseInstance.subscriptions}" var="s">
							<dd><g:link controller="subscription" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${licenseInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${licenseInstance?.id}">
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

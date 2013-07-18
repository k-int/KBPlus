
<%@ page import="com.k_int.kbplus.OnixplLicense" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'onixplLicense.label', default: 'OnixplLicense')}" />
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
						
							<th class="header"><g:message code="onixplLicense.license.label" default="KB+ License" /></th>

                            <g:sortableColumn property="Type" title="${message(code: 'onixplLicense.type.label', default: 'Type')}" />

                            <g:sortableColumn property="Status" title="${message(code: 'onixplLicense.status.label', default: 'Status')}" />

							<g:sortableColumn property="Document" title="${message(code: 'onixplLicense.document.label', default: 'Document')}" />
						
							<g:sortableColumn property="License Text" title="${message(code: 'onixplLicense.licenseText.label', default: 'License Text')}" />
						
							<g:sortableColumn property="Usage Term" title="${message(code: 'onixplLicense.usageTerm.label', default: 'Usage Term')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${onixplLicenseInstanceList}" var="onixplLicenseInstance">
						<tr>
						
							<td>
                                <g:link controller="licenseDetails" action="index" id="${onixplLicenseInstance.license.id}">${onixplLicenseInstance.license}</g:link>
                            </td>

                            <td>
                                ${onixplLicenseInstance.license.type.value}
                            </td>

                            <td>
                                ${onixplLicenseInstance.license.status.value}
                            </td>
						
							<td>
                                <g:link controller="doc" action="show" id="${onixplLicenseInstance.doc.id}">${onixplLicenseInstance.doc.title}</g:link>
                            </td>
						
							<td>
                                <ul>
                                <g:each in="${onixplLicenseInstance.licenseText}">
                                    <li>
                                        <g:link controller="onixplLicenseText" action="show" id="${it.id}">${it.text}</g:link>
                                    </li>
                                </g:each>
                                </ul>
                            </td>
						
							<td>
                                <ul>
                                <g:each in="${onixplLicenseInstance.usageTerm.sort {it.usageType.value}}">
                                    <li>
                                        <g:link controller="onixplUsageTerm" action="show" id="${it.id}">${it.usageType.value} | ${it.usageStatus.value}</g:link>
                                    </li>
                                </g:each>
                                </ul>
                            </td>
						
							<td class="link">
								<g:link action="index" id="${onixplLicenseInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${onixplLicenseInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>

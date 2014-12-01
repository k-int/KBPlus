
<%@ page import="com.k_int.kbplus.OnixplLicense" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'onixplLicence.label', default: 'OnixplLicence')}" />
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
						
							<th class="header"><g:message code="onixplLicense.title.label" default="Title" /></th>

                            <th class="header"><g:message code="onixplLicense.licenses.label" default="KB+ ${message(code:'licence.plural')}" /></th>

                            <g:sortableColumn property="Type" title="${message(code: 'onixplLicence.type.label', default: 'Type')}" />

                            <g:sortableColumn property="Status" title="${message(code: 'onixplLicence.status.label', default: 'Status')}" />

                            <g:sortableColumn property="Document" title="${message(code: 'onixplLicence.document.label', default: 'Document')}" />

                            <th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${onixplLicenseInstanceList}" var="onixplLicenseInstance">
						<tr>

                            <td>
                                ${onixplLicenseInstance.title}
                            </td>
						
							<td>
                                <ul>
                                    <g:each in="${onixplLicenseInstance.licenses}">
                                        <li>
                                            <g:link controller="licenseDetails" action="index" id="${it.id}">${it}</g:link>
                                        </li>
                                    </g:each>
                                </ul>
                            </td>

                            <td>
                                <g:each in="${onixplLicenseInstance.licenses}">
                                <li>
                                    ${it.type?.value}
                                </li>
                            </g:each>
                            </td>

                            <td>
                                <g:each in="${onixplLicenseInstance.licenses}">
                                    <li>
                                        ${it.status?.value}
                                    </li>
                                </g:each>
                            </td>
						
							<td>
                                <g:link controller="doc" action="show" id="${onixplLicenseInstance.doc.id}">${onixplLicenseInstance.doc?.title}</g:link>
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

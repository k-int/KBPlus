
<%@ page import="com.k_int.kbplus.Identifier" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'identifier.label', default: 'Identifier')}" />
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
				
					<g:if test="${identifierInstance?.ns}">
						<dt><g:message code="identifier.ns.label" default="Namespace / Identifier Type" /></dt>						
							<dd>${identifierInstance?.ns?.ns?.encodeAsHTML()}</dd>
						
					</g:if>
				
					<g:if test="${identifierInstance?.value}">
						<dt><g:message code="identifier.value.label" default="Value" /></dt>
						
							<dd><g:fieldValue bean="${identifierInstance}" field="value"/></dd>
						
					</g:if>

					<g:if test="${identifierInstance?.occurrences}">
						<dt><g:message code="identifier.occurrences.label" default="This identifier appears in" /></dt>
						
 				<dd><ul><g:each in="${identifierInstance.occurrences}" var="io">
                                                   <li>
                       <g:if test="${io.org}">Oganisation <g:link controller="org" action="show" id="${io.org.id}">${io.org.name}</g:link></g:if>
                       <g:if test="${io.ti}">Title Instance <g:link controller="titleInstance" action="show" id="${io.ti.id}">${io.ti.title}</g:link></g:if>
                       <g:if test="${io.tipp}">tipp <g:link controller="titleInstancePackagePlatform" action="show" id="${io.tipp.id}">${io.tipp.title.title}</g:link></g:if>

                                                          </li>
                                          </g:each></ul></dd>	
				</g:if>

				
				</dl>
			</div>

		</div>
	</body>
</html>

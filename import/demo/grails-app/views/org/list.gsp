
<%@ page import="com.k_int.kbplus.*" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'org.label', default: 'Org')}" />
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
<sec:ifAnyGranted roles="ROLE_EDITOR,ROLE_ADMIN">
						<li>
							<g:link class="create" action="create">
								<i class="icon-plus"></i>
								<g:message code="default.create.label" args="[entityName]" />
							</g:link>
						</li>
                                                </sec:ifAnyGranted>
					</ul>
				</div>
			</div>

			<div class="span9">
				
				<div class="page-header">
				<h1>Organisations</h1>
				</div>

          <g:form action="list" method="get">
            Org Name Contains: <input type="text" name="orgNameContains" value="${params.orgNameContains}"/> Restrict to orgs who are 
            <g:select name="orgRole" noSelection="${['':'Select One...']}" from="${RefdataValue.findAllByOwner(RefdataCategory.get(2))}" value="${params.orgRole}" optionKey="id" optionValue="value"/>
            <input type="submit" value="GO ->"/> (${orgInstanceTotal} Matches)
          </g:form>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				
				<table class="table table-striped">
					<thead>
						<tr>
							<g:sortableColumn property="name" title="${message(code: 'org.name.label', default: 'Name')}" />
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${orgInstanceList}" var="orgInstance">
						<tr>
							<td>${fieldValue(bean: orgInstance, field: "name")}</td>
						
							<td class="link">
								<g:link action="show" id="${orgInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${orgInstanceTotal}" params="${params}" />
				</div>
			</div>

		</div>
	</body>
</html>

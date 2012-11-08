<%@ page import="com.k_int.kbplus.*" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'org.label', default: 'Org')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="container">
      
        
        <div class="page-header">
          <h1>Organisations</h1>
        </div>

        <div class="well">
          <g:form action="list" method="get">
            Org Name Contains: <input type="text" name="orgNameContains" value="${params.orgNameContains}"/> Restrict to orgs who are 
            <g:select name="orgRole" noSelection="${['':'Select One...']}" from="${RefdataValue.findAllByOwner(RefdataCategory.get(2))}" value="${params.orgRole}" optionKey="id" optionValue="value"/>
            <input type="submit" value="GO ->" class="btn btn-primary"/> (${orgInstanceTotal} Matches)
          </g:form>
        </div>

        <g:if test="${flash.message}">
          <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>
        
        <table class="table table-striped">
          <thead>
            <tr>
              <g:sortableColumn property="name" title="${message(code: 'org.name.label', default: 'Name')}" />
              <g:sortableColumn property="shortcode" title="${message(code: 'org.shortcode.label', default: 'Short Code')}" />
              <g:sortableColumn property="sector" title="${message(code: 'org.sector.label', default: 'Sector')}" />
              <g:sortableColumn property="scope" title="${message(code: 'org.scope.label', default: 'Scope')}" />
            </tr>
          </thead>
          <tbody>
          <g:each in="${orgInstanceList}" var="orgInstance">
            <tr>
              <td><g:link  action="show" id="${orgInstance.id}">${fieldValue(bean: orgInstance, field: "name")}</g:link></td>
              <td>${fieldValue(bean: orgInstance, field: "shortcode")}</td>
              <td>${fieldValue(bean: orgInstance, field: "sector")}</td>
              <td>${fieldValue(bean: orgInstance, field: "scope")}</td>
            </tr>
          </g:each>
          </tbody>
        </table>

        <div class="pagination">
          <bootstrap:paginate total="${orgInstanceTotal}" params="${params}" />
        </div>

    </div>
  </body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: ioannis
  Date: 15/05/2014
  Time: 14:00
--%>

<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>

</head>

<body>

<div class="container">
    <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="index">All Packages</g:link><span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="show"
                    id="${packageInstance.id}">${packageInstance.name}</g:link></li>

        <li class="dropdown pull-right">
            <a class="dropdown-toggle badge" id="export-menu" role="button" data-toggle="dropdown" data-target="#"
               href="">Exports<b class="caret"></b></a>

            <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
                <li><g:link action="show" params="${params + [format: 'json']}">Json Export</g:link></li>
                <li><g:link action="show" params="${params + [format: 'xml']}">XML Export</g:link></li>
                <g:each in="${transforms}" var="transkey,transval">
                    <li><g:link action="show" id="${params.id}"
                                params="${[format: 'xml', transformId: transkey]}">${transval.name}</g:link></li>
                </g:each>
            </ul>
        </li>

        <li class="pull-right">
            View:
            <div class="btn-group" data-toggle="buttons-radio">
                <g:link controller="packageDetails" action="show" params="${params + ['mode': 'basic']}"
                        class="btn btn-primary btn-mini ${((params.mode == 'basic') || (params.mode == null)) ? 'active' : ''}">Basic</g:link>
                <g:link controller="packageDetails" action="show" params="${params + ['mode': 'advanced']}"
                        class="btn btn-primary btn-mini ${params.mode == 'advanced' ? 'active' : ''}">Advanced</g:link>
            </div>
            &nbsp;
        </li>

    </ul>
</div>
<g:if test="${flash.message}">
    <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
</g:if>

<g:if test="${flash.error}">
    <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
</g:if>
<div class="container">
    <h1>${packageInstance?.name}</h1>
    <g:render template="nav" />
</div>
<div class="container">

    <g:render template="/templates/documents_table"
              model="${[instance:packageInstance,context:'pkg',redirect:'documents']}" />

</div>

<g:render template="/templates/addDocument"
          model="${[doclist: packageInstance.documents, ownobj: packageInstance, owntp: 'pkg']}"/>

</body>

</html>
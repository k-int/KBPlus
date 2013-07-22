<%@ page import="com.k_int.kbplus.OnixplLicense" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'onixplLicense.label', default: 'OnixplLicense')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
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
                        <g:message code="default.list.label" args="[entityName]"/>
                    </g:link>
                </li>
                <li>
                    <g:link class="create" action="create">
                        <i class="icon-plus"></i>
                        <g:message code="default.create.label" args="[entityName]"/>
                    </g:link>
                </li>
            </ul>
        </div>
    </div>

    <div class="span9">

        <div class="page-header">
            <h1><g:message code="default.list.label" args="[entityName]"/></h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <table class="table table-striped">
            <thead>
            <tr>

                <th class="header"><g:message code="onixplLicense.license.label" default="License"/></th>

                <th class="header"><g:message code="onixplLicense.doc.label" default="Doc"/></th>

                <g:sortableColumn property="lastmod"
                                  title="${message(code: 'onixplLicense.lastmod.label', default: 'Lastmod')}"/>

                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${onixplLicenseInstanceList}" var="onixplLicenseInstance">
                <tr>

                    <td>${fieldValue(bean: onixplLicenseInstance, field: "license")}</td>

                    <td>${fieldValue(bean: onixplLicenseInstance, field: "doc")}</td>

                    <td>${fieldValue(bean: onixplLicenseInstance, field: "lastmod")}</td>

                    <td class="link">
                        <g:link action="show" id="${onixplLicenseInstance.id}"
                                class="btn btn-small">Show &raquo;</g:link>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <div class="pagination">
            <bootstrap:paginate total="${onixplLicenseInstanceTotal}"/>
        </div>
    </div>

</div>
</body>
</html>

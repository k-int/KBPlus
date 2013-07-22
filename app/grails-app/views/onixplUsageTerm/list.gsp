<%@ page import="com.k_int.kbplus.OnixplUsageTerm" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm')}"/>
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

                <th class="header"><g:message code="onixplUsageTerm.oplLicense.label" default="Opl License"/></th>

                <th class="header"><g:message code="onixplUsageTerm.usageType.label" default="Usage Type"/></th>

                <th class="header"><g:message code="onixplUsageTerm.usageStatus.label" default="Usage Status"/></th>

                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${onixplUsageTermInstanceList}" var="onixplUsageTermInstance">
                <tr>

                    <td>${fieldValue(bean: onixplUsageTermInstance, field: "oplLicense")}</td>

                    <td>${fieldValue(bean: onixplUsageTermInstance, field: "usageType")}</td>

                    <td>${fieldValue(bean: onixplUsageTermInstance, field: "usageStatus")}</td>

                    <td class="link">
                        <g:link action="show" id="${onixplUsageTermInstance.id}"
                                class="btn btn-small">Show &raquo;</g:link>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <div class="pagination">
            <bootstrap:paginate total="${onixplUsageTermInstanceTotal}"/>
        </div>
    </div>

</div>
</body>
</html>

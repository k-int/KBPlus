<%@ page import="com.k_int.kbplus.OnixplLicenseText" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText')}"/>
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

                <g:sortableColumn property="displayNum"
                                  title="${message(code: 'onixplLicenseText.displayNum.label', default: 'Display Num')}"/>

                <g:sortableColumn property="text"
                                  title="${message(code: 'onixplLicenseText.text.label', default: 'Text')}"/>

                <g:sortableColumn property="elementId"
                                  title="${message(code: 'onixplLicenseText.elementId.label', default: 'Element Id')}"/>

                <th class="header"><g:message code="onixplLicenseText.oplLicense.label" default="Opl License"/></th>

                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${onixplLicenseTextInstanceList}" var="onixplLicenseTextInstance">
                <tr>

                    <td>${fieldValue(bean: onixplLicenseTextInstance, field: "displayNum")}</td>

                    <td>${fieldValue(bean: onixplLicenseTextInstance, field: "text")}</td>

                    <td>${fieldValue(bean: onixplLicenseTextInstance, field: "elementId")}</td>

                    <td>${fieldValue(bean: onixplLicenseTextInstance, field: "oplLicense")}</td>

                    <td class="link">
                        <g:link action="show" id="${onixplLicenseTextInstance.id}"
                                class="btn btn-small">Show &raquo;</g:link>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <div class="pagination">
            <bootstrap:paginate total="${onixplLicenseTextInstanceTotal}"/>
        </div>
    </div>

</div>
</body>
</html>

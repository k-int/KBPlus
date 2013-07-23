<%@ page import="com.k_int.kbplus.OnixplUsageTerm" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'onixplUsageTerm.label', default: 'OnixplUsageTerm')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
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
            <h1><g:message code="default.show.label" args="[entityName]"/></h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>

            <g:if test="${onixplUsageTermInstance?.oplLicense}">
                <dt><g:message code="onixplUsageTerm.oplLicense.label" default="Opl License"/></dt>

                <dd><g:link controller="onixplLicense" action="show"
                            id="${onixplUsageTermInstance?.oplLicense?.id}">${onixplUsageTermInstance?.oplLicense?.encodeAsHTML()}</g:link></dd>

            </g:if>

            <g:if test="${onixplUsageTermInstance?.usageType}">
                <dt><g:message code="onixplUsageTerm.usageType.label" default="Usage Type"/></dt>

                <dd><g:link controller="refdataValue" action="show"
                            id="${onixplUsageTermInstance?.usageType?.id}">${onixplUsageTermInstance?.usageType?.encodeAsHTML()}</g:link></dd>

            </g:if>

            <g:if test="${onixplUsageTermInstance?.usageStatus}">
                <dt><g:message code="onixplUsageTerm.usageStatus.label" default="Usage Status"/></dt>

                <dd><g:link controller="refdataValue" action="show"
                            id="${onixplUsageTermInstance?.usageStatus?.id}">${onixplUsageTermInstance?.usageStatus?.encodeAsHTML()}</g:link></dd>

            </g:if>

            <g:if test="${onixplUsageTermInstance?.usageTermLicenseText}">
                <dt><g:message code="onixplUsageTerm.usageTermLicenseText.label"
                               default="Usage Term License Text"/></dt>

                <g:each in="${onixplUsageTermInstance.usageTermLicenseText}" var="u">
                    <dd><g:link controller="onixplUsageTermLicenseText" action="show"
                                id="${u.id}">${u?.encodeAsHTML()}</g:link></dd>
                </g:each>

            </g:if>

        </dl>

        <g:form>
            <g:hiddenField name="id" value="${onixplUsageTermInstance?.id}"/>
            <div class="form-actions">
                <g:link class="btn" action="edit" id="${onixplUsageTermInstance?.id}">
                    <i class="icon-pencil"></i>
                    <g:message code="default.button.edit.label" default="Edit"/>
                </g:link>
                <button class="btn btn-danger" type="submit" name="_action_delete">
                    <i class="icon-trash icon-white"></i>
                    <g:message code="default.button.delete.label" default="Delete"/>
                </button>
            </div>
        </g:form>

    </div>

</div>
</body>
</html>

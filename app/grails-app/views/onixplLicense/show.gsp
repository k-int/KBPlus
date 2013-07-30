<%@ page import="com.k_int.kbplus.OnixplLicense" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'onixplLicense.label', default: 'OnixplLicense')}"/>
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

            <g:if test="${onixplLicenseInstance?.license}">
                <dt><g:message code="onixplLicense.license.label" default="License"/></dt>

                <dd><g:link controller="license" action="show"
                            id="${onixplLicenseInstance?.license?.id}">${onixplLicenseInstance?.license?.encodeAsHTML()}</g:link></dd>

            </g:if>

            <g:if test="${onixplLicenseInstance?.doc}">
                <dt><g:message code="onixplLicense.doc.label" default="Doc"/></dt>

                <dd><g:link controller="doc" action="show"
                            id="${onixplLicenseInstance?.doc?.id}">${onixplLicenseInstance?.doc?.encodeAsHTML()}</g:link></dd>

            </g:if>

            <g:if test="${onixplLicenseInstance?.lastmod}">
                <dt><g:message code="onixplLicense.lastmod.label" default="Lastmod"/></dt>

                <dd><g:fieldValue bean="${onixplLicenseInstance}" field="lastmod"/></dd>

            </g:if>

            <g:if test="${onixplLicenseInstance?.licenseText}">
                <dt><g:message code="onixplLicense.licenseText.label" default="License Text"/></dt>

                <g:each in="${onixplLicenseInstance.licenseText}" var="l">
                    <dd><g:link controller="onixplLicenseText" action="show"
                                id="${l.id}">${l?.encodeAsHTML()}</g:link></dd>
                </g:each>

            </g:if>

            <g:if test="${onixplLicenseInstance?.usageTerm}">
                <dt><g:message code="onixplLicense.usageTerm.label" default="Usage Term"/></dt>

                <g:each in="${onixplLicenseInstance.usageTerm}" var="u">
                    <dd><g:link controller="onixplUsageTerm" action="show"
                                id="${u.id}">${u?.encodeAsHTML()}</g:link></dd>
                </g:each>

            </g:if>

        </dl>

        <g:form>
            <g:hiddenField name="id" value="${onixplLicenseInstance?.id}"/>
            <div class="form-actions">
                <g:link class="btn" action="edit" id="${onixplLicenseInstance?.id}">
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

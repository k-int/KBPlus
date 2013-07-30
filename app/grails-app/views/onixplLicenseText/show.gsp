<%@ page import="com.k_int.kbplus.OnixplLicenseText" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'onixplLicenseText.label', default: 'OnixplLicenseText')}"/>
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

            <g:if test="${onixplLicenseTextInstance?.displayNum}">
                <dt><g:message code="onixplLicenseText.displayNum.label" default="Display Num"/></dt>

                <dd><g:fieldValue bean="${onixplLicenseTextInstance}" field="displayNum"/></dd>

            </g:if>

            <g:if test="${onixplLicenseTextInstance?.text}">
                <dt><g:message code="onixplLicenseText.text.label" default="Text"/></dt>

                <dd><g:fieldValue bean="${onixplLicenseTextInstance}" field="text"/></dd>

            </g:if>

            <g:if test="${onixplLicenseTextInstance?.elementId}">
                <dt><g:message code="onixplLicenseText.elementId.label" default="Element Id"/></dt>

                <dd><g:fieldValue bean="${onixplLicenseTextInstance}" field="elementId"/></dd>

            </g:if>

            <g:if test="${onixplLicenseTextInstance?.oplLicense}">
                <dt><g:message code="onixplLicenseText.oplLicense.label" default="Opl License"/></dt>

                <dd><g:link controller="onixplLicense" action="show"
                            id="${onixplLicenseTextInstance?.oplLicense?.id}">${onixplLicenseTextInstance?.oplLicense?.encodeAsHTML()}</g:link></dd>

            </g:if>

            <g:if test="${onixplLicenseTextInstance?.usageTermLicenseText.licenseText}">
                <dt><g:message code="onixplLicenseText.usageTermLicenseText.label"
                               default="Usage Term License Text"/></dt>

                <g:each in="${onixplLicenseTextInstance.usageTermLicenseText.licenseText}" var="u">
                    <dd><g:link controller="onixplUsageTermLicenseText" action="show"
                                id="${u.id}">${u?.encodeAsHTML()}</g:link></dd>
                </g:each>

            </g:if>

        </dl>

        <g:form>
            <g:hiddenField name="id" value="${onixplLicenseTextInstance?.id}"/>
            <div class="form-actions">
                <g:link class="btn" action="edit" id="${onixplLicenseTextInstance?.id}">
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

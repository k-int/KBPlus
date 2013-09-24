<%--
  Created by IntelliJ IDEA.
  User: rwincewicz
  Date: 06/09/2013
  Time: 16:34
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="com.k_int.kbplus.OnixplLicenseCompareController; com.k_int.kbplus.OnixplUsageTerm; com.k_int.kbplus.RefdataCategory" contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
</head>

<body>

<div class="container">
    <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <li>ONIX-PL Usage Term</li>
    </ul>
</div>

<div class="container">
    <h1>ONIX-PL Usage Term</h1>
</div>

<div class="container">
    <div class="row">
        <div class="span8">

            <div class="inline-lists">

                <g:if test="${flash.message}">
                    <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
                </g:if>

                <dl>

                    <g:if test="${usageTerm?.oplLicense}">
                        <dt><g:message code="onixplUsageTerm.oplLicense.label" default="Opl License"/></dt>

                        <dd><g:link controller="onixplLicenseDetails" action="index"
                                    id="${usageTerm?.oplLicense?.id}">${usageTerm?.oplLicense?.title.encodeAsHTML()}</g:link></dd>

                    </g:if>

                    <g:if test="${usageTerm?.usageType}">
                        <dt><g:message code="onixplUsageTerm.usageType.label" default="Usage Type"/></dt>

                        <dd>${usageTerm?.usageType?.value.encodeAsHTML()}</dd>

                    </g:if>

                    <g:if test="${usageTerm?.usageStatus}">
                        <dt><g:message code="onixplUsageTerm.usageStatus.label" default="Usage Status"/></dt>

                        <dd>${usageTerm?.usageStatus?.value.encodeAsHTML()}</dd>

                    </g:if>

                <g:if test="${usageTerm?.user}">
                    <dt><g:message code="onixplUsageTerm.usageStatus.label" default="Users"/></dt>
                    <dd><ul>
                    <g:each in="${usageTerm.user}">
                        <li>${it.value.encodeAsHTML()}</li>
                    </g:each>
                    </ul></dd>
                </g:if>

                <g:if test="${usageTerm?.usedResource}">
                    <dt><g:message code="onixplUsageTerm.usageStatus.label" default="Used Resource"/></dt>
                    <dd><ul>
                    <g:each in="${usageTerm.usedResource}">
                        <li>${it.value.encodeAsHTML()}</li>
                    </g:each>
                    </ul></dd>
                </g:if>

                    <g:if test="${usageTerm?.usageTermLicenseText}">
                        <dt><g:message code="onixplUsageTerm.usageTermLicenseText.label"
                                       default="Usage Term License Text"/></dt>

                        <dd>
                        <g:each in="${usageTerm.usageTermLicenseText.sort {it.licenseText.text}}" var="u">
                            ${u.licenseText.displayNum} ${u.licenseText?.text.encodeAsHTML()}<br>
                        </g:each>
                        </dd>
                    </g:if>

                </dl>
            </div>
        </div>
    </div>
</div>

</body>
</html>
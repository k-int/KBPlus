<%--
  Created by IntelliJ IDEA.
  User: rwincewicz
  Date: 10/07/2013
  Time: 09:11
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
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
        <li>License Text</li>
    </ul>
</div>

<div class="container">
    <h1>License Text</h1>
</div>

<div class="container">
    <div class="row">
        <div class="span8">

            <h6>ONIX-PL Licence Properties</h6>

            <div class="inline-lists">
                <dl>
                    <dt><label class="control-label" for="oplLicense">ONIX-PL License</label></dt>
                    <dd>
                        <g:link name="oplLicense" controller="onixplLicenseDetails" action="index"
                                id="${onixplLicenseText.oplLicense.id}">${onixplLicenseText.oplLicense.title}</g:link>
                    </dd>
                </dl>
                <dl>
                    <dt><span class="control-label">Element Id</span></dt>
                    <dd>
                        ${onixplLicenseText.elementId}
                    </dd>
                </dl>
                <dl>
                    <dt><span class="control-label">Usage Term</span></dt>
                    <dd>
                        <g:each in="${onixplLicenseText.usageTermLicenseText.usageTerm}">
                            <g:link controller="onixplUsageTermsDetails" action="index" id="${it.id}">${it.usageType.value} - ${it.usageStatus.value}</g:link>
                        </g:each>
                    </dd>
                </dl>
                <dl>
                    <dt><span class="control-label">Text</span></dt>
                    <dd>
                        ${onixplLicenseText.text}
                    </dd>
                </dl>
            </div>

        </div>
    </div>
</div>

</body>
</html>
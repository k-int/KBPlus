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
        <li><g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span></li>
        <li>Usage Term</li>
    </ul>
</div>

<div class="container">
    <h1>Usage Term</h1>
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
                                id="${onixplUsageTerm.oplLicense.id}">${onixplUsageTerm.oplLicense.title}</g:link>
                    </dd>
                </dl>
                <dl>
                    <dt><span class="control-label">Usage Type</span></dt>
                    <dd>
                        ${onixplUsageTerm.usageType.value}
                    </dd>
                </dl>
                <dl>
                    <dt><span class="control-label">Usage Value</span></dt>
                    <dd>
                        ${onixplUsageTerm.usageStatus.value}
                    </dd>
                </dl>
                <dl>
                    <dt><span class="control-label">Text</span></dt>
                    <dd>
                        <g:each in="${onixplUsageTerm.usageTermLicenseText.sort { it.licenseText.elementId }}">
                            ${it.licenseText.elementId} - <g:link controller="onixplLicenseTextDetails" action="index"
                                                                  id="${it.licenseText.id}">${it.licenseText.text}</g:link><br>
                        </g:each>
                    </dd>
                </dl>
            </div>

        </div>
    </div>
</div>

</body>
</html>
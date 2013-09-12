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
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}">ONIX-PL License Details</g:link> </li>
        <g:if test="${editable}">
            <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
</ul>
    </div>

<div class="container">
    <h1>ONIX-PL Licence : ${onixplLicense?.title}</h1>
</div>

<div class="container">
    <div class="row">
        <div class="span8">

            <h6>KB+ License Information</h6>

            <g:if test="${!onixplLicense}">
            Could not find ONIX-PL license
            </g:if>
            <g:else>
            <div class="inline-lists">
                <dl>
                    <dt><label class="control-label" for="license">Reference</label></dt>
                    <dd>
                        <g:each in="${onixplLicense.licenses}">
                            <g:link name="license" controller="licenseDetails" action="index" id="${it.id}">${it.reference}</g:link>
                        </g:each>
                    </dd>
                </dl>
                </div>

            <h6>ONIX-PL Licence Properties</h6>

            <table class="table table-bordered licence-properties">
                <thead>
                <tr>
                    <th>Property</th>
                    <th>Status</th>
                    <th>Notes</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${onixplLicense.usageTerm.sort {it.usageType.value}}">
                    <tr>
                        <td><g:link controller="onixplUsageTermDetails" action="index" id="${it.id}">${it.usageType.value}</g:link></td>
                        <td><g:refdataValue cat="UsageStatus" owner="${onixplLicense}" val="${it.usageStatus.value}" /></td>
                        <td><g:each in="${it.usageTermLicenseText.licenseText.sort {it.elementId}}">
                            ${it.elementId} - <g:link controller="onixplLicenseTextDetails" action="index" id="${it.id}">${it.text}</g:link><br>
                        </g:each>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
            </g:else>
        </div>
    </div>
</div>

</body>
</html>
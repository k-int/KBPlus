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
        <g:if test="${onixplLicense.license.licensee}">
            <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:onixplLicense.license.licensee.shortcode]}"> ${onixplLicense.license.licensee.name} Current Licenses</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}">License Details</g:link> <span class="divider">/</span></li>
        <li> <g:link controller="licenseDetails" action="onixpl" id="${params.id}">ONIX-PL License</g:link> </li>
        <g:if test="${editable}">
            <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
</ul>
    </div>

<div class="container">
    <h1>${onixplLicense.license.licensee?.name} ${onixplLicense.license.type?.value} Licence : <g:xEditable owner="${onixplLicense.license}" field="reference" id="reference"/></h1>
    <g:render template="nav" contextPath="." />
</div>

<div class="container">
    <div class="row">
        <div class="span8">

            <h6>Information</h6>

            <div class="inline-lists">
                <dl>
                    <dt>ONIX-PL License</dt>
                    <dd>
                        <g:link controller="onixplLicenseDetails" action="index" id="${onixplLicense.id}">${onixplLicense}</g:link>
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
                    <tr><td><g:link controller="onixplUsageTermsDetails" action="index" id="${it.id}">${it.usageType.value}</g:link></td>
                    <td><g:refdataValue cat="UsageStatus" val="${it.usageStatus.value}" /></td>
                    <td><g:each in="${it.usageTermLicenseText.licenseText.sort {it.elementId}}">
                        ${it.elementId} - <g:link controller="onixplLicenseTextDetails" action="index" id="${it.id}">${it.text}</g:link><br>
                        </g:each>
                    </td></tr>

                </g:each>
                </tbody>
            </table>

        </div>
        <div class="span4">
            <g:render template="documents" contextPath="../templates" model="${[doclist:onixplLicense.license.documents, ownobj:onixplLicense.license, owntp:'license']}" />
            <g:render template="notes" contextPath="../templates" model="${[doclist:onixplLicense.license.documents, ownobj:onixplLicense.license, owntp:'license']}" />
        </div>
    </div>
</div>

</body>
</html>
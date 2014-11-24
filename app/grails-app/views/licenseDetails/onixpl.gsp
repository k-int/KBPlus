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
    <title>KB+ <g:message code="licence" default="Licence"/></title>
</head>
<body>

<div class="container">
    <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${license?.licensee}">
            <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:license.licensee.shortcode]}"> ${license.licensee.name} <g:message code="current.licenses" default="Licence"/></g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}"><g:message code="licence.details" default="Licence"/></g:link> <span class="divider">/</span></li>
        <li> <g:link controller="licenseDetails" action="onixpl" id="${params.id}"><g:message code="onix.licence" default="Licence"/></g:link> </li>
        <g:if test="${editable}">
            <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
</ul>
    </div>

<div class="container">
    <g:if test="${license}">
        <h1>${license?.licensee?.name} ${license?.type?.value} <g:message code="licence" default="Licence"/> : <g:xEditable owner="${license}" field="reference" id="reference"/></h1>
        <g:render template="nav" />
    </g:if>
</div>

<div class="container">
    <div class="row">
        <div class="span8">

            <h6>Information</h6>

            <div class="inline-lists">
                <dl>
                    <dt>ONIX-PL Licence</dt>
                    <dd>
                        <g:link controller="onixplLicenseDetails" action="index" id="${onixplLicense?.id}">${onixplLicense?.title}</g:link>
                    </dd>
                </dl>
                </div>

            <h6>ONIX-PL Licence Properties</h6>

            <table class="table table-bordered licence-properties">
                <thead>
                <tr>
                    <th>Property</th>
                    <th>Status</th>
                    <th>Licence Text</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${onixplLicense?.usageTerm?.sort {it.usageType.value}}">
                    <tr>
                        <td><g:link controller="onixplUsageTermDetails" action="index" id="${it.id}">${it.usageType.value}</g:link></td>
                        <td><g:refdataValue cat="UsageStatus" val="${it.usageStatus.value}" /></td>
                        <td>
                            <g:each in="${it.usageTermLicenseText.sort {it.licenseText.text}}" var="u">
                                ${u.licenseText.displayNum} ${u.licenseText?.text.encodeAsHTML()}<br>
                            </g:each>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

        </div>
        <g:if test="${license}">
        <div class="span4">
            <g:render template="documents" contextPath="../templates" model="${[doclist:license?.documents, ownobj:license, owntp:'license']}" />
            <g:render template="notes" contextPath="../templates" model="${[doclist:license?.documents, ownobj:license, owntp:'license']}" />
        </div>
        </g:if>
    </div>
</div>

</body>
</html>

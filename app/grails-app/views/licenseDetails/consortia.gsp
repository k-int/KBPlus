<%@ page import="com.k_int.kbplus.License" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'license.label', default: 'Licence')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>

<div class="container">
    <ul class="breadcrumb">
       <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${licence.licensee}">
          <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:licence.licensee.shortcode]}"> ${licence.licensee.name} <g:message code="current.licenses" default="Licence"/></g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}"><g:message code="license.details" default="Licence"/></g:link> </li>
    </ul>
</div>
<g:if test="${flash.message}">
    <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
</g:if>

<g:if test="${flash.error}">
    <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
</g:if>

<div class="container">
    <h1>${licence?.reference}</h1>
    <g:render template="nav"/>
</div>

<div class="container">
<g:if test="${consortia}">
<h3> Institutions for ${consortia.name} consortia </h3>
<br><p> The following list displays all members of ${consortia.name} consortia. To create child licences
    select the desired checkboxes and click 'Create child licences'</p><br>
<g:form action="generateSlaveLicences" controller="licenseDetails" method="POST">
<input type="hidden" name="baselicense" value="${licence.id}"/>
<input type="hidden" name="id" value="${id}"/>
<table class="table table-bordered"> 
<thead>
    <tr>
        <th>Organisation</th>
        <th>Contains  Licence Copy </th>
        <th>Create Child Licence</th>
    </tr>
</thead>
<tbody>
    <g:each in="${consortiaInstsWithStatus}" var="pair">
        <tr>
            <td>${pair.getKey().name}</td>
            <td><g:refdataValue cat="YNO" val="${pair.getValue()}" /></td>
            <td><g:if test="${editable}"><input type="checkbox" name="_create.${pair.getKey().id}" value="true"/>
                    </g:if></td>
        </tr>
    </g:each>
</tbody>
</table>
<dl>
<dt>Licence name: <input type="text" name="lic_name" 
    value="Child licence for ${licence?.reference}"/></dt>
<dd><input type="submit" class="btn btn-primary" value="Create child licences"/></dd>
</dl>
</g:form>
</g:if>
</div>
</body>
</html>

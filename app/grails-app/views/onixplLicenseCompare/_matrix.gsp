<%--
  Created by IntelliJ IDEA.
  User: rwincewicz
  Date: 05/09/2013
  Time: 08:53
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="com.k_int.kbplus.OnixplLicenseCompareController; com.k_int.kbplus.OnixplLicense; com.k_int.kbplus.OnixplUsageTerm; com.k_int.kbplus.RefdataCategory" contentType="text/html;charset=UTF-8" %>
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
        <li>ONIX-PL License Comparison</li>

        <li class="dropdown pull-right">
            <a class="dropdown-toggle" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">
                Exports<b class="caret"></b>
            </a>
            <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
                <li>
                    <% def ps_json = [:]; ps_json.putAll(params); ps_json.format = 'json'; %>
                    <g:link action="export" params="${params}">TSV Export</g:link>
                </li>
            </ul>
        </li>
    </ul>
</div>

<div class="container">
    <h1>ONIX-PL Licence Comparison</h1>
</div>

<div class="container">
    <div class="row">
        <div class="span10">
            <g:if test="${flash.message}">
                <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>
        <g:if test="${!license}">
            No license could be found!
        </g:if>
            <g:elseif test="${!list}">
                No matching licenses could be found!
            </g:elseif>
            <g:else>
                <g:if test="${!"all".equals(section) && !"".equals(match)}">${OnixplLicenseCompareController.getSectionMessage(match, section)}</g:if>
                <div class="pagination">
                    ${params.setProperty("action", "matrix")}
                    <bootstrap:paginate total="${total}" params="${params}" />
                </div>
            <table class="table table-bordered">
                <thead>
                <th>Usage term</th>
                <th><g:link controller="onixplLicenseDetails" action="index" id="${license.id}">${license.title}</g:link></th>
                <g:each in="${list}">
                    <th><g:link controller="onixplLicenseDetails" action="index" id="${it.id}">${it.title}</g:link></th>
                </g:each>
                </thead>
                <tbody>
                <tr>
                    <td>Match?</td>
                    <td></td>
                    <g:each in="${list}" var="llist">
                        <td>${license.compare(llist, null).toString()}</td>
                    </g:each>
                </tr>
                <g:each in="${termList}" var="it" status="i">
                    <g:set var="ltermList"
                           value="${com.k_int.kbplus.OnixplLicenseCompareController.getUsageTermList(license, it)}"/>
                    <tr>
                        <td>${it.value}</td>
                        <td>
                            <g:if test="${OnixplUsageTerm.findAllByOplLicenseAndUsageType(license, it)}">
                                <g:each in="${ltermList}" var="lterm">
                                    <div class="matrix-cell">
                                        <g:link controller="onixplUsageTermDetails" action="index" id="${lterm.id}" title="${lterm.getLicenseText()}"><g:refdataValue cat="UsageStatus" owner="${license}"
                                                        val="${lterm.usageStatus.value}"/></g:link>
                                    </div>
                                </g:each>
                            </g:if>
                            <g:else>
                                No value
                            </g:else>
                        </td>
                        <g:each in="${list}" var="j">
                                <g:if test="${OnixplUsageTerm.findAllByOplLicenseAndUsageType(j, it)}">
                                    <g:set var="l2termList" value="${com.k_int.kbplus.OnixplLicenseCompareController.getUsageTermList(j, it)}"/>
                                    <g:if test="${l2termList.size() == ltermList.size()}">
                                        <g:set var="match" value="${true}"/>
                                        <g:each in="${ltermList}" var="u1" status="k">
                                            <g:if test="${!u1.compare(l2termList.get(k))}">
                                                <g:set var="match" value="${false}"/>
                                            </g:if>
                                        </g:each>
                                        <g:if test="${OnixplUsageTerm.findAllByOplLicenseAndUsageType(license, it).asBoolean() != OnixplUsageTerm.findAllByOplLicenseAndUsageType(j, it).asBoolean()}">
                                            <g:set var="match" value="${false}"/>
                                        </g:if>
                                    </g:if>
                                    <g:else>
                                        <g:set var="match" value="${false}"/>
                                    </g:else>
                                    <g:if test="${match}">
                                        <td class="matrix-match">
                                    </g:if>
                                    <g:else>
                                        <td class="matrix-no-match">
                                    </g:else>
                                    <g:each in="${l2termList}" var="l2term">
                                        <div class="matrix-cell">
                                            <g:link controller="onixplUsageTermDetails" action="index" id="${l2term.id}" title="${OnixplLicenseCompareController.getLicenseText(l2term)}"><g:refdataValue cat="UsageStatus" owner="${j}"
                                                            val="${l2term.usageStatus.value}"/></g:link>
                                        </div>
                                    </g:each>
                                    </td>
                                </g:if>
                                <g:else>
                                    <td class="matrix-no-value">No value</td>
                                </g:else>
                        </g:each>
                    </tr>
                </g:each>
                </tbody>
            </table>
                <div class="pagination">
                    <bootstrap:paginate total="${total}" params="${params}" />
                </div>
            </g:else>
        </div>
    </div>
</div>

</body>
</html>

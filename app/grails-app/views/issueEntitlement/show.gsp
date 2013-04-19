<%@ page import="com.k_int.kbplus.IssueEntitlement" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'issueEntitlement.label', default: 'IssueEntitlement')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
</head>
<body>

   <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${issueEntitlementInstance?.subscription.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:issueEntitlementInstance?.subscription.subscriber.shortcode]}"> ${issueEntitlementInstance?.subscription.subscriber.name} Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${issueEntitlementInstance?.subscription.id}">${issueEntitlementInstance?.subscription.name}</g:link>  <span class="divider">/</span> </li>
        <li> <g:link controller="issueEntitlement" action="show" id="${issueEntitlementInstance?.id}">${issueEntitlementInstance?.tipp.title.title}</g:link> </li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

    <div class="container">

        <div class="page-header">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <div class="inline-lists">

            <dl>
                <g:if test="${issueEntitlementInstance?.status}">
                    <dt><g:message code="issueEntitlement.status.label" default="Status" /></dt>
                    <dd><g:link controller="refdataValue" action="show" id="${issueEntitlementInstance?.status?.id}">${issueEntitlementInstance?.status?.value.encodeAsHTML()}</g:link></dd>
                </g:if>
            </dl>

            <dl>
                <g:if test="${issueEntitlementInstance?.subscription}">
                    <dt><g:message code="issueEntitlement.subscription.label" default="Subscription" /></dt>

                    <dd><g:link controller="subscription" action="show" id="${issueEntitlementInstance?.subscription?.id}">${issueEntitlementInstance?.subscription?.name.encodeAsHTML()}</g:link></dd>

                </g:if>
            </dl>

            <dl>
                <g:if test="${issueEntitlementInstance?.tipp}">
                    <dt><g:message code="issueEntitlement.tipp.title.label" default="Title" /></dt>
                    <dd>${issueEntitlementInstance?.tipp?.title.title.encodeAsHTML()}</dd>
                </g:if>
            </dl>

            <dl>
                <g:if test="${issueEntitlementInstance?.tipp.title?.ids}">
                    <dt>Title Identfiers</dt>

                    <g:each in="${issueEntitlementInstance?.tipp.title?.ids}" var="i">
                        <dd>${i.identifier.ns.ns}:${i.identifier.value}</dd>
                    </g:each>

                </g:if>
            </dl>

            <dl>
                <g:if test="${issueEntitlementInstance?.coreStatus}">
                    <dt>Core Status</dt>
                    <dd>${issueEntitlementInstance?.coreStatus.value}</dd>
                </g:if>
            </dl>

            <dl>
                <g:if test="${issueEntitlementInstance?.tipp.hostPlatformURL}">
                    <dt>Host Platform URL</dt>
                    <dd> <a href="${issueEntitlementInstance.tipp?.hostPlatformURL}" TITLE="${issueEntitlementInstance.tipp?.hostPlatformURL}">${issueEntitlementInstance.tipp.platform.name}</a></dd>
                </g:if>
            </dl>

            <br/>
            
            <h6>Issue Entitlement details under subscription : ${issueEntitlementInstance.subscription.name}</h6>

            <table class="table table-bordered table-striped">
                <tr>
                    <th>From Date</th><th>From Volume</th><th>From Issue</th>
                    <th>To Date</th><th>To Volume</th><th>To Issue</th>
                </tr>
                <tr>
                  <td><g:xEditable owner="${issueEntitlementInstance}" field="startDate" type="date"/></td>
                  <td><g:xEditable owner="${issueEntitlementInstance}" field="startVolume"/></td>
                  <td><g:xEditable owner="${issueEntitlementInstance}" field="startIssue"/></td>
                  <td><g:xEditable owner="${issueEntitlementInstance}" field="endDate" type="date"/></td>
                  <td><g:xEditable owner="${issueEntitlementInstance}" field="endVolume"/></td>
                  <td><g:xEditable owner="${issueEntitlementInstance}" field="endIssue"/></td>
                </tr>
            </table>

            <dl>
                <dt>Embargo</dt>
                <dd><g:xEditable owner="${issueEntitlementInstance}" field="embargo"/></dd>
            </dl>

            <dl>
                <dt>Coverage</dt>
                <dd>${issueEntitlementInstance.coverageDepth}</dd>
            </dl>

            <dl>
                <dt>Coverage Note</dt>
                <dd>${issueEntitlementInstance.coverageNote}</dd>
            </dl>

            <br/>
            
            <h6>Defaults from package : ${issueEntitlementInstance.tipp.pkg.name}</h6>

            <table class="table table-bordered table-striped">
                <tr>
                    <th>From Date</th><th>From Volume</th><th>From Issue</th>
                    <th>To Date</th><th>To Volume</th><th>To Issue</th>
                </tr>
                
                <tr>
                  <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${issueEntitlementInstance.tipp.startDate}"/></td>
                  <td>${issueEntitlementInstance.tipp.startVolume}</td>
                  <td>${issueEntitlementInstance.tipp.startIssue}</td>
                  <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${issueEntitlementInstance.tipp.endDate}"/></td>
                  <td>${issueEntitlementInstance.tipp.endVolume}</td>
                  <td>${issueEntitlementInstance.tipp.endIssue}</td>
                </tr>
            </table>

            <dl>
                <dt>Embargo (tipp)</dt>
                <dd>${issueEntitlementInstance.tipp.embargo}</dd>
            </dl>

            <dl>
                <dt>Coverage</dt>
                <dd>${issueEntitlementInstance.tipp.coverageDepth}</dd>
            </dl>

            <dl>
                <dt>Coverage Note</dt>
                <dd>${issueEntitlementInstance.tipp.coverageNote}</dd>
            </dl>

            <g:if test="${issueEntitlementInstance.tipp.title?.tipps}">

                <br/>
                
                <h6><g:message code="titleInstance.tipps.label" default="Occurences of this title against Packages / Platforms" /><g:message code="titleInstance.tipps.label" default="Occurences of this title against Packages / Platforms" /></h6>

                <table class="table table-bordered table-striped">
                    <tr>
                        <th>From Date</th><th>From Volume</th><th>From Issue</th>
                        <th>To Date</th><th>To Volume</th><th>To Issue</th><th>Coverage Depth</th>
                        <th>Platform</th><th>Package</th><th>Actions</th>
                    </tr>
                    <g:each in="${issueEntitlementInstance.tipp.title.tipps}" var="t">
                        <tr>
                            <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.startDate}"/></td>
                        <td>${t.startVolume}</td>
                        <td>${t.startIssue}</td>
                        <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.endDate}"/></td>
                        <td>${t.endVolume}</td>
                        <td>${t.endIssue}</td>
                        <td>${t.coverageDepth}</td>
                        <td><g:link controller="platform" action="show" id="${t.platform.id}">${t.platform.name}</g:link></td>
                        <td><g:link controller="package" action="show" id="${t.pkg.id}">${t.pkg.name} (${t.pkg.contentProvider?.name})</g:link></td>
                        <td><g:link controller="titleInstancePackagePlatform" action="show" id="${t.id}">Full TIPP record</g:link></td>
                        </tr>
                    </g:each>
                </table>
            </g:if>
        </div>
    </div>
</body>
</html>

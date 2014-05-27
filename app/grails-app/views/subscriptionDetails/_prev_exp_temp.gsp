<%@ page import="com.k_int.kbplus.Subscription" %>
<r:require module="annotations" />

<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
</head>
<body>

<div class="container">
    <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
            <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Details</g:link> </li>
</div>

<g:if test="${flash.message}">
    <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
</g:if>

<g:if test="${flash.error}">
    <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
</g:if>

<div class="container">
    ${institution?.name} ${subscriptionInstance?.type?.value}
    <h1>${subscriptionInstance.name}</h1>
    <g:render template="nav"  />
</div>

<div class="container">

    <dl>
        <g:if test="${num_ie_rows > max}">
            <dt>Titles (${offset+1} to ${lastie}  of ${num_ie_rows}) </dt>
        </g:if>
        <g:set var="counter" value="${offset+1}" />

        <dd>

            <table  class="table table-striped table-bordered">
                <thead>

                <tr>
                    <th rowspan="2">#</th>
                    <g:sortableColumn params="${params}" property="tipp.title.title" title="Title" />
                    <th>ISSN</th>
                    <g:sortableColumn params="${params}" property="coreStatus" title="Core" />
                    <g:sortableColumn params="${params}" property="startDate" title="Coverage Start Date" />
                    <g:sortableColumn params="${params}" property="coreStatusStart" title="Core Start Date" />
                    <th rowspan="2">Actions</th>
                </tr>

                <tr>
                    <th>Access Dates</th>
                    <th>eISSN</th>
                    <th></th>
                    <g:sortableColumn params="${params}" property="endDate" title="Coverage End Date" />
                    <g:sortableColumn params="${params}" property="coreStatusEnd" title="Core End Date"  />
                </tr>


                </thead>
                <tbody>

                <g:if test="${titlesList}">
                    <g:each in="${titlesList}" var="ie">
                        <tr>
                            <td>${counter++}</td>
                            <td>
                                <g:link controller="issueEntitlement" id="${ie.id}" action="show">${ie.tipp.title.title}</g:link>
                                <g:if test="${ie.tipp?.hostPlatformURL}">( <a href="${ie.tipp?.hostPlatformURL}" TITLE="${ie.tipp?.hostPlatformURL}">Host Link</a>
                                    <a href="${ie.tipp?.hostPlatformURL}" TITLE="${ie.tipp?.hostPlatformURL} (In new window)" target="_blank"><i class="icon-share-alt"></i></a>)</g:if> <br/>
                                Access: ${ie.availabilityStatus?.value}
                                <g:if test="${ie.availabilityStatus?.value=='Expected'}">
                                    on <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.accessStartDate}"/>
                                </g:if>
                                <g:if test="${ie.availabilityStatus?.value=='Expired'}">
                                    on <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.accessEndDate}"/>
                                </g:if>
                                <br/> Record Status: ${ie.status}
                                <br/> Access Start: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.accessStartDate}"/>
                                <br/> Access End: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.accessEndDate}"/>

                            </td>
                            <td>
                                ${ie?.tipp?.title?.getIdentifierValue('ISSN')}<br/>
                                ${ie?.tipp?.title?.getIdentifierValue('eISSN')}
                            </td>
                            <td>
                                ${ie.coreStatus}
                            <td>
                                <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.startDate}"/> <br/>
                                <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.endDate}"/>
                            </td>
                            <td>
                                <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.coreStatusStart}"/> <br/>
                                <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.coreStatusEnd}"/>
                            </td>
                            <td>
                                <g:if test="${institutional_usage_identifier}">
                                    <g:if test="${ie?.tipp?.title?.getIdentifierValue('ISSN')}">
                                        | <a href="https://www.jusp.mimas.ac.uk/secure/v2/ijsu/?id=${institutional_usage_identifier.value}&issn=${ie?.tipp?.title?.getIdentifierValue('ISSN')}">ISSN Usage</a>
                                    </g:if>
                                    <g:if test="${ie?.tipp?.title?.getIdentifierValue('eISSN')}">
                                        | <a href="https://www.jusp.mimas.ac.uk/secure/v2/ijsu/?id=${institutional_usage_identifier.value}&issn=${ie?.tipp?.title?.getIdentifierValue('eISSN')}">eISSN Usage</a>
                                    </g:if>
                                </g:if>
                            </td>
                        </tr>
                    </g:each>
                </g:if>
                </tbody>
            </table>
        </dd>
    </dl>

    <div class="pagination" style="text-align:center">
        <g:if test="${titlesList}" >
            <bootstrap:paginate  action="${screen}" controller="subscriptionDetails" params="${params}" next="Next" prev="Prev" maxsteps="${max}" total="${num_ie_rows}" />
        </g:if>
    </div>

</div>
</body>

</html>
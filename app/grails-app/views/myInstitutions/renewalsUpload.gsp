<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Renewals Upload</title>
  </head>

  <body>
    <div class="container">
      <g:form action="renewalsUpload" method="post" enctype="multipart/form-data" params="${params}">
        <input type="file" id="renewalsWorksheet" name="renewalsWorksheet"/>
        <button type="submit" class="btn btn-primary">Upload Renewals Worksheet</button>
      </g:form>
    </div>

    <g:if test="${(errors && (errors.size() > 0))}">
      <div class="container">
        <ul>
          <g:each in="${errors}" var="e">
            <li>${e}</li>
          </g:each>
        </ul>
      </div>
    </g:if>

    <g:set var="counter" value="${-1}" />

    <g:if test="${base_subscription}">
      <form  action="processRenewal" method="post" params="${params}">
        <input type="hidden" name="baseSubscription" value="${base_subscription.id}"/>
        <div class="container">
        <hr/>
          Uploaded worksheet will create a new subscription taken for ${institution.name} based on subscription offered 
          <g:link controller="subscriptionDetails" action="index" id="${base_subscription.id}">${base_subscription.id} - ${base_subscription.name}</g:link><br/>
          The uploaded worksheet will generate the following issue entitlements:<br/>
  
          <table class="table table-bordered">
            <thead>
              <tr>
                <td>Title</td>
                <td>ISSN</td>
                <td>eISSN</td>
                <td>Start Date</td>
                <td>Start Volume</td>
                <td>Start Issue</td>
                <td>End Date</td>
                <td>End Volume</td>
                <td>End Issue</td>
                <td>Core?</td>
                <td>Core Start Date</td>
                <td>Core End Date</td>
              </tr>
            </thead>
            <tbody>
              <g:each in="${entitlements}" var="e">
                <tr>
                  <td><input type="hidden" name="entitlements.${++counter}.tipp_id" value="${e.base_entitlement.tipp.id}"/>
		      <input type="hidden" name="entitlements.${counter}.entitlement_id" value="${e.base_entitlement.id}"/>
                      <input type="hidden" name="entitlements.${counter}.is_core" value="${e.core}"/>
                      ${e.base_entitlement.tipp.title.title}</td>
                  <td>${e.base_entitlement.tipp.title.getIdentifierValue('ISSN')}</td>
                  <td>${e.base_entitlement.tipp.title.getIdentifierValue('eISSN')}</td>
                  <td><g:formatDate format="dd MMMM yyyy" date="${e.base_entitlement.startDate}"/></td>
                  <td>${e.base_entitlement.startVolume}</td>
                  <td>${e.base_entitlement.startIssue}</td>
                  <td><g:formatDate format="dd MMMM yyyy" date="${e.base_entitlement.endDate}"/></td>
                  <td>${e.base_entitlement.endVolume}</td>
                  <td>${e.base_entitlement.endIssue}</td>
                  <td>${e.core}<input type="hidden" name="entitlements.${counter}.iscore" value="${e.core}"/></td>
                  <td>${e.core_start_date}<input type="hidden" name="entitlements.${counter}.core_start" value="${e.core_start_date}"/></td>
                  <td>${e.core_end_date}<input type="hidden" name="entitlements.${counter}.core_end" value="${e.core_end_date}"/></td>
                </tr>
              </g:each>
            </tbody>
          </table>
          <input type="hidden" name="ecount" value="${counter}"/>

          <div class="pull-right">
            <button type="submit" class="btn btn-primary">Accept and Process</button>
          </div>
        </div>
      </form>
    </g:if>

  </body>
</html>

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

    <g:if test="${flash.message}">
      <div class="container">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </div>
    </g:if>

    <g:if test="${flash.error}">
      <div class="container">
        <bootstrap:alert class="error-info">${flash.error}</bootstrap:alert>
      </div>
    </g:if>

    <g:set var="counter" value="${-1}" />

      <g:form action="processRenewal" method="post" enctype="multipart/form-data" params="${params}">


        <div class="container">
        <hr/>
          Uploaded worksheet will create a new subscription taken for ${institution.name} based on the following rows...<br/>
          <table class="table table-bordered">
            <tbody>
            <input type="hidden" name="subscription.start_date" value="${additionalInfo?.sub_startDate}"/>
            <input type="hidden" name="subscription.end_date" value="${additionalInfo?.sub_endDate}"/>
            <input type="hidden" name="subscription.copy_docs" value="${additionalInfo?.sub_id}"/>

              <tr><th>Select</th><th >Subscription Properties</th><th>Value</th></tr>
              <tr>
                <th><g:checkBox name="subscription.copyStart" value="${true}" /></th>
                <th>Start Date</th>
                <td>${additionalInfo?.sub_startDate}</td>
              </tr>
              <tr>
                <th><g:checkBox name="subscription.copyEnd" value="${true}" /></th>
                <th>End Date</th>
                <td>${additionalInfo?.sub_endDate}</td>
              </tr>
              <tr>
                <th><g:checkBox name="subscription.copyDocs" value="${true}" /></th>
                <th>Copy Documents and Notes from Subscription</th>
                <td>${additionalInfo?.sub_name}</td>
              </tr>
            </tbody>
          </table>
          <table class="table table-bordered">
            <thead>
              <tr>
                <td>Title</td>
                <td>From Pkg</td>
                <td>ISSN</td>
                <td>eISSN</td>
                <td>Start Date</td>
                <td>Start Volume</td>
                <td>Start Issue</td>
                <td>End Date</td>
                <td>End Volume</td>
                <td>End Issue</td>
                <td>Core Medium</td>
              </tr>
            </thead>
            <tbody>
              <g:each in="${entitlements}" var="e">
                <tr>
                  <td><input type="hidden" name="entitlements.${++counter}.tipp_id" value="${e.base_entitlement.id}"/>
                      <input type="hidden" name="entitlements.${counter}.core_status" value="${e.core_status}"/>
                      <input type="hidden" name="entitlements.${counter}.start_date" value="${e.start_date}"/>
                      <input type="hidden" name="entitlements.${counter}.end_date" value="${e.end_date}"/>
                      <input type="hidden" name="entitlements.${counter}.coverage" value="${e.coverage}"/>
                      <input type="hidden" name="entitlements.${counter}.coverage_note" value="${e.coverage_note}"/>
                      ${e.base_entitlement.title.title}</td>
                  <td><g:link controller="packageDetails" action="show" id="${e.base_entitlement.pkg.id}">${e.base_entitlement.pkg.name}(${e.base_entitlement.pkg.id})</g:link></td>
                  <td>${e.base_entitlement.title.getIdentifierValue('ISSN')}</td>
                  <td>${e.base_entitlement.title.getIdentifierValue('eISSN')}</td>
                  <td>${e.start_date} (Default:<g:formatDate format="dd MMMM yyyy" date="${e.base_entitlement.startDate}"/>)</td>
                  <td>${e.base_entitlement.startVolume}</td>
                  <td>${e.base_entitlement.startIssue}</td>
                  <td>${e.end_date} (Default:<g:formatDate format="dd MMMM yyyy" date="${e.base_entitlement.endDate}"/>)</td>
                  <td>${e.base_entitlement.endVolume}</td>
                  <td>${e.base_entitlement.endIssue}</td>
                  <td>${e.core_status?:'N'}</td>
                </tr>
              </g:each>
            </tbody>
          </table>

          <div class="pull-right">
            <button type="submit" class="btn btn-primary">Accept and Process</button>
          </div>
        </div>
        <input type="hidden" name="ecount" value="${counter}"/>
      </g:form>

  </body>
</html>

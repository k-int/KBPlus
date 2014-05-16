<%@ page import="com.k_int.kbplus.TitleInstance" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>${titleInstanceInstance?.title} in ${tipp.pkg.name} on ${tipp.platform.name}</title>
  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="packageDetails" action="show" id="${tipp.pkg.id}">${tipp.pkg.name} [package]</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="tipp" action="show" id="${tipp.id}">${tipp.title.title}</g:link> [title]</li>

        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

    <div class="container inline-lists">
      
        <div class="page-header">
          <h1>"${titleInstanceInstance?.title}" in "${tipp.pkg.name}" on ${tipp.platform.name}</h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>
          <g:if test="${titleInstanceInstance?.ids}">
            <dt><g:message code="titleInstance.ids.label" default="Ids" /></dt>
            
              <dd><g:each in="${titleInstanceInstance.ids}" var="i">
              ${i.identifier.ns.ns}:${i.identifier.value}<br/>
              </g:each>
              </dd>
            
          </g:if>
        </dl>
        <dl>
          <dt>Availability Status</dt>
          <dd> <span title="${tipp.availabilityStatusExplanation}">${tipp.availabilityStatus?.value}</span></dd>
        </dl>
        <dl>
          <dt>Access Start Date (Enters Package)</dt>
          <dd><g:xEditable owner="${tipp}" type="date" field="accessStartDate" /></dd>
        </dl>
        <dl>
          <dt>Access End Date (Leaves Package)</dt>
          <dd><g:xEditable owner="${tipp}" type="date" field="accessEndDate" /></dd>
        </dl>
        <dl>
          <dt>TIPP Start Date</dt>
          <dd><g:xEditable owner="${tipp}" type="date" field="startDate"/></dd>
        </dl>
        <dl>
          <dt>TIPP Start Volume</dt>
          <dd><g:xEditable owner="${tipp}" field="startVolume"/></dd>
        </dl>
        <dl>
          <dt>TIPP Start Issue</dt>
          <dd><g:xEditable owner="${tipp}" field="startIssue"/></dd>
        </dl>
        <dl>

          <dt>TIPP End Date</dt>
          <dd><g:xEditable owner="${tipp}"  type="date" field="endDate"/></dd>
        </dl>
        <dl>

          <dt>TIPP End Volume</dt>
          <dd><g:xEditable owner="${tipp}" field="endVolume"/></dd>
        </dl>
        <dl>

          <dt>TIPP End Issue</dt>
          <dd><g:xEditable owner="${tipp}" field="endIssue"/></dd>
        </dl>
        <dl>

          <dt>Coverage Depth</dt>
          <dd><g:xEditable owner="${tipp}" field="coverageDepth"/></dd>
        </dl>
        <dl>

          <dt>Coverage Note</dt>
          <dd><g:xEditable owner="${tipp}" field="coverageNote"/></dd>
        </dl>
        <dl>

          <dt>Embargo</dt>
          <dd><g:xEditable owner="${tipp}" field="embargo"/></dd>
        </dl>
        <dl>

          <dt>Host URL</dt>
          <dd><g:xEditable type="text" owner="${tipp}" field="hostPlatformURL"/></dd>
        </dl>
        <dl>

          <dt>Status</dt>
          <dd><g:xEditableRefData owner="${tipp}" field="status" config='TIPPStatus'/><dd>
        </dl>
        <dl>

          <dt>Status Reason</dt>
          <dd><g:xEditableRefData owner="${tipp}" field="statusReason" config='TIPPStatusReason'/><dd>
        </dl>

        <dl>
          <dt>Delayed OA</dt>
          <dd><g:xEditableRefData owner="${tipp}" field="delayedOA" config='TIPPDelayedOA'/><dd>
        </dl>

        <dl>
          <dt>Hybrid OA</dt>
          <dd><g:xEditableRefData owner="${tipp}" field="hybridOA" config='TIPPHybridOA'/><dd>
        </dl>

        <dl>
          <dt>Payment</dt>
          <dd><g:xEditableRefData owner="${tipp}" field="payment" config='TIPPPaymentType'/><dd>
        </dl>

        <dl>
          <dt>Host Platform</dt>
          <dd>${tipp.platform.name}</dd>
        </dl>
        <dl>
          <dt>Additional Platforms</td>
          <dd>
            <table class="table">
              <thead>
                <tr><th>Relation</th><th>Platform Name</th><th>Primary URL</th></tr>
              </thead>
              <tbody>
                <g:each in="${tipp.additionalPlatforms}" var="ap">
                  <tr>
                    <td>${ap.rel}</td>
                    <td>${ap.platform.name}</td>
                    <td>${ap.platform.primaryUrl}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </dd>
        </dl>



          <g:if test="${titleInstanceInstance?.tipps}">
            <dl>
            <dt><g:message code="titleInstance.tipps.label" default="Occurences of this title against Packages / Platforms" /></dt>
            <dd>

               <g:form action="show" params="${params}" method="get" class="form-inline">
                  <input type="hidden" name="sort" value="${params.sort}">
                  <input type="hidden" name="order" value="${params.order}">
                  <label>Filters - Package Name:</label> <input name="filter" value="${params.filter}"/> &nbsp;
                  &nbsp; <label>Starts Before: </label>
                  <g:simpleHiddenValue id="startsBefore" name="startsBefore" type="date" value="${params.startsBefore}"/>
                  &nbsp; <label>Ends After: </label>
                  <g:simpleHiddenValue id="endsAfter" name="endsAfter" type="date" value="${params.endsAfter}"/>
                  <input type="submit" class="btn btn-primary">
                </g:form>

            <table class="table">
              <tr>
                <th>From Date</th><th>From Volume</th><th>From Issue</th>
                <th>To Date</th><th>To Volume</th><th>To Issue</th><th>Coverage Depth</th>
                <th>Platform</th><th>Package</th><th>Actions</th>
              </tr>
              <g:each in="${tippList}" var="t">
                <tr>
                  <td><g:formatDate format="dd MMM yyyy" date="${t.startDate}"/></td>
                  <td>${t.startVolume}</td>
                  <td>${t.startIssue}</td>
                  <td><g:formatDate format="dd MMM yyyy" date="${t.endDate}"/></td>
                  <td>${t.endVolume}</td>
                  <td>${t.endIssue}</td>
                  <td>${t.coverageDepth}</td>
                  <td><g:link controller="platform" action="show" id="${t.platform.id}">${t.platform.name}</g:link></td>
                  <td><g:link controller="packageDetails" action="show" id="${t.pkg.id}">${t.pkg.name} (${t.pkg.contentProvider?.name})</g:link></td>
                  <td></td>
                </tr>
              </g:each>

            </table>
            </dd>
            </dl>
          </g:if>
    </div>
  </body>
</html>

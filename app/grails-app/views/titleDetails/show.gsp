<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'titleInstance.label', default: 'Title Instance')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="titleDetails" action="show" id="${ti.id}">Title ${ti.title}</g:link> </li>

        <li class="dropdown pull-right">

        <g:if test="${editable}">
          <li class="pull-right"><span class="badge badge-warning">Editable</span>&nbsp;</li>
        </g:if>
      </ul>
    </div>

      <div class="container">
        <div class="row">
          <div class="span12">

            <div class="page-header">
              <h1>${ti.title}</h1>
            </div>         

              <g:render template="nav" />

            <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <g:if test="${flash.error}">
            <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
            </g:if>
          </div>
        </div>

        <div class="row">
          <div class="span6">

            <h3>Identifiers</h3>

              <g:each in="${duplicates}" var="entry">

                 <bootstrap:alert class="alert-info">
                 Identifier ${entry.key} used in multiple titles:
                 <ul>
                 <g:each in ="${entry.value}" var="dup_title">
                 <li><g:link controller='titleDetails' action='show' id="${dup_title.id}">${dup_title.title}</g:link></li>
                 </g:each>
                 </ul>
                 </bootstrap:alert>
              </g:each>

            <table class="table table-bordered">
              <thead>
                <tr>
                  <th>ID</td>
                  <th>Identifier Namespace</th>
                  <th>Identifier</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${ti.ids}" var="io">
                  <tr>
                    <td>${io.id}</td>
                    <td>${io.identifier.ns.ns}</td>
                    <td>${io.identifier.value}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>
	  </div>
          <div class="span6">
            <h3>Org Links</h3>
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th>ID</td>
                  <th>Org</th>
                  <th>Role</th>
                  <th>From</th>
                  <th>To</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${ti.orgs}" var="org">
                  <tr>
                    <td>${org.id}</td>
                    <td>${org.org.name}</td>
                    <td>${org?.roleType?.value}</td>
                    <td>
                      <g:xEditable owner="${org}" type="date" field="startDate"/>
                    </td>
                    <td>
                      <g:xEditable owner="${org}" type="date" field="endDate"/>
                    </td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </div>
        </div>


        <div class="row">
          <div class="span12">
            <h3>Bibliographic Title History</h3>
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>From</th>
                  <th>To</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${titleHistory}" var="th">
                  <tr>
                    <td><g:formatDate date="${th.eventDate}" format="yyyy-MM-dd"/></td>
                    <td>
                      <g:each in="${th.participants}" var="p">
                        <g:if test="${p.participantRole=='from'}">
                          <g:link controller="titleDetails" action="show" id="${p.participant.id}">${p.participant.title}</g:link><br/>
                        </g:if>
                      </g:each>
                    </td>
                    <td>
                      <g:each in="${th.participants}" var="p">
                        <g:if test="${p.participantRole=='to'}">
                          <g:link controller="titleDetails" action="show" id="${p.participant.id}">${p.participant.title}</g:link><br/>
                        </g:if>
                      </g:each>
                    </td>
                  </tr>
                </g:each>
              </tbody>
            </table>
            <g:if test="${ti.getIdentifierValue('originediturl') != null}">
              <span class="pull-right">
                Title history can be edited in gokb here:: <a href="${ti.getIdentifierValue('originediturl')}">Here</a>
              </span>
            </g:if>
          </div>
        </div>

        <div class="row">
          <div class="span12">

            <h3>Appears in...</h3>
            <g:form id="${params.id}" controller="titleDetails" action="batchUpdate">
              <table class="table table-bordered table-striped">
                <tr>
                  <th rowspan="2"></th>
                  <th>Platform</th><th>Package</th>
                  <th>Start</th>
                  <th>End</th>
                  <th>Coverage Depth</th>
                  <th>Actions</th>
                </tr>
                <tr>
                  <th colspan="6">Coverage Note</th>
                </tr>

                <g:if test="${editable}">
                  <tr>
                    <td rowspan="2"><input type="checkbox" name="checkall" onClick="javascript:$('.bulkcheck').attr('checked', true);"/></td>
                    <td colspan="2"><button class="btn btn-primary" type="submit" value="Go" name="BatchEdit">Apply Batch Changes</button></td>
                    <td>Date:<g:simpleHiddenValue id="bulk_start_date" name="bulk_start_date" type="date"/>
                       - <input type="checkbox" name="clear_start_date"/> (clear)
                        <br/>
                        Volume:<g:simpleHiddenValue id="bulk_start_volume" name="bulk_start_volume"/>
                       - <input type="checkbox" name="clear_start_volume"/> (clear)
                        <br/>
                        Issue:<g:simpleHiddenValue id="bulk_start_issue" name="bulk_start_issue"/>
                       - <input type="checkbox" name="clear_start_issue"/> (clear)

                    </td>
                    <td>Date:<g:simpleHiddenValue id="bulk_end_date" name="bulk_end_date" type="date"/>
                       - <input type="checkbox" name="clear_end_date"/> (clear)
                        <br/>
                        Volume: <g:simpleHiddenValue id="bulk_end_volume" name="bulk_end_volume"/>
                       - <input type="checkbox" name="clear_end_volume"/> (clear)
                        <br/>
                        Issue: <g:simpleHiddenValue id="bulk_end_issue" name="bulk_end_issue"/>
                       - <input type="checkbox" name="clear_end_issue"/> (clear)

                    </td>
                    <td><g:simpleHiddenValue id="bulk_coverage_depth" name="bulk_coverage_depth"/>
                        - <input type="checkbox" name="clear_coverage_depth"/> (clear)
                    </td>
                    <td/>
                  </tr>
                  <tr>
                    <td colspan="6">
                      Bulk coverage note change: <g:simpleHiddenValue id="bulk_coverage_note" name="bulk_coverage_note"/>
                       - <input type="checkbox" name="clear_coverage_note"/> (clear) <br/>
                      Bulk Host Platform URL change: <g:simpleHiddenValue id="bulk_hostPlatformURL" name="bulk_hostPlatformURL"/>
                       - <input type="checkbox" name="clear_hostPlatformURL"/> (clear) <br/>
                    </td>
                  </tr>
                </g:if>
  
                <g:each in="${ti.tipps}" var="t">
                  <tr>
                    <td rowspan="2"><g:if test="${editable}"><input type="checkbox" name="_bulkflag.${t.id}" class="bulkcheck"/></g:if></td>
                    <td><g:link controller="platform" action="show" id="${t.platform.id}">${t.platform.name}</g:link></td>
                    <td><g:link controller="packageDetails" action="show" id="${t.pkg.id}">${t.pkg.name}</g:link></td>
  
                    <td>Date: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.startDate}"/><br/>
                    Volume: ${t.startVolume}<br/>
                    Issue: ${t.startIssue}</td>
                    <td>Date: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.endDate}"/><br/>
                    Volume: ${t.endVolume}<br/>
                    Issue: ${t.endIssue}</td>
                    <td>${t.coverageDepth}</td>
                    <td><g:link controller="tipp" action="show" id="${t.id}">Full TIPP record</g:link></td>
                  </tr>
                  <tr>
                    <td colspan="6">Coverage Note: ${t.coverageNote?:'No coverage note'}<br/>
                                    Host Platform URL: ${t.hostPlatformURL?:'No Host Platform URL'}</td>
                  </tr>
                </g:each>
              </table>
            </g:form>

          </div>
        </div>
      </div>
  </body>
</html>

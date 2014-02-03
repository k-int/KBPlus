<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>${ti.title}</title>
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
                </tr>
              </thead>
              <tbody>
                <g:each in="${ti.orgs}" var="org">
                  <tr>
                    <td>${org.id}</td>
                    <td>${org.org.name}</td>
                    <td>${org.roleType.value}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </div>
        </div>
        <div class="row">
          <div class="span12">


            <h3>Appears in...</h3>
            <g:form id="${params.id}" controller="titleDetails" action="batchUpdate">
              <table class="table table-bordered table-striped">
                <tr>
                  <th></th>
                  <th>Platform</th><th>Package</th>
                  <th>Start</th>
                  <th>End</th>
                  <th>Coverage Depth</th>
                  <th>Actions</th>
                </tr>

                <g:if test="${editable}">
                  <tr>
                    <td><input type="checkbox" name="checkall" onClick="javascript:$('.bulkcheck').attr('checked', true);"/></td>
                    <td colspan="2"><button class="btn btn-primary" type="submit" value="Go" name="BatchEdit">Apply Batch Changes</button></td>
                    <td>Date:<g:simpleHiddenValue id="bulk_start_date" name="bulk_start_date" type="date"/><br/>
                        Volume:<g:simpleHiddenValue id="bulk_start_volume" name="bulk_start_volume"/><br/>
                        Issue:<g:simpleHiddenValue id="bulk_start_issue" name="bulk_start_issue"/>
                    </td>
                    <td>Date:<g:simpleHiddenValue id="bulk_end_date" name="bulk_end_date" type="date"/><br/>
                        Volume: <g:simpleHiddenValue id="bulk_end_volume" name="bulk_end_volume"/><br/>
                        Issue: <g:simpleHiddenValue id="bulk_end_issue" name="bulk_end_issue"/>
                    </td>
                    <td><g:simpleHiddenValue id="bulk_coverage_depth" name="bulk_coverage_depth"/>
                    </td>
                    <td/>
                  </tr>
                </g:if>
  
                <g:each in="${ti.tipps}" var="t">
                  <tr>
                    <td><g:if test="${editable}"><input type="checkbox" name="_bulkflag.${t.id}" class="bulkcheck"/></g:if></td>
                    <td><g:link controller="platform" action="show" id="${t.platform.id}">${t.platform.name}</g:link></td>
                    <td><g:link controller="packageDetails" action="show" id="${t.pkg.id}">${t.pkg.name}</g:link></td>
  
                    <td>Date: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.startDate}"/><br/>
                    Volume: ${t.startVolume}<br/>
                    Issue: ${t.startIssue}</td>
                    <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.endDate}"/><br/>
                    Volume: ${t.endVolume}<br/>
                    Issue: ${t.endIssue}</td>
                    <td>${t.coverageDepth}</td>
                    <td><g:link controller="tipp" action="show" id="${t.id}">Full TIPP record</g:link></td>
                  </tr>
                </g:each>
              </table>
            </g:form>

          </div>
        </div>
      </div>
  </body>
</html>

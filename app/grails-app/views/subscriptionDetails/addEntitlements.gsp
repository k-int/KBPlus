<%@ page import="com.k_int.kbplus.Subscription" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="addEntitlements" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Add Entitlements</g:link> </li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

    <div class="container">

      ${institution?.name} Subscription Taken
      <h1><g:inPlaceEdit domain="Subscription" pk="${subscriptionInstance.id}" field="name" id="name" class="newipe">${subscriptionInstance?.name}</g:inPlaceEdit></h1>

      <g:render template="nav" contextPath="." />

    </div>

    <g:set var="counter" value="${offset+1}" />

    <div class="container">

      <dl>
        <dt>Available Titles ( ${offset+1} to ${offset+(tipps?.size())} of ${num_tipp_rows} )
          <g:form action="addEntitlements" params="${params}" method="get" class="form-inline">
            <input type="hidden" name="sort" value="${params.sort}">
            <input type="hidden" name="order" value="${params.order}">
            <label>Filter:</label> <input name="filter" value="${params.filter}"/> &nbsp;
            <label>From Package:</label> <select name="pkgfilter">
                               <option value="">All</option>
                               <g:each in="${subscriptionInstance.packages}" var="sp">
                                 <option value="${sp.pkg.id}" ${sp.pkg.id.toString()==params.pkgfilter?'selected=true':''}>${sp.pkg.name}</option>
                               </g:each>
                            </select> &nbsp;
            <br/>
            <label>Starts Before (YYYY/MM/DD)</label> <input name="startsBefore" type="text" value="${params.startsBefore}"/>
            <label>Ends After (YYYY/MM/DD)</label> <input name="endsAfter" type="text" value="${params.endsAfter}"/>

            <input type="submit" class="btn btn-primary">
          </g:form>
        </dt>
        <dd>
          <g:form action="processAddEntitlements">
            <input type="hidden" name="siid" value="${subscriptionInstance.id}"/>
            <table  class="table table-striped table-bordered columns10">
              <thead>
                <tr>
                  <th>
                    <g:if test="${editable}"><input type="checkbox" name="chkall" onClick="javascript:selectAll();"/></g:if>
                  </th>
                  <th>#</th>
                  <g:sortableColumn params="${params}" property="tipp.title.title" title="Title" />
                  <th>ISSN</th>
                  <th>eISSN</th>
                  <g:sortableColumn params="${params}" property="startDate" title="Start Date" />
                  <g:sortableColumn params="${params}" property="endDate" title="End Date" />
                  <th>Embargo</th>
                  <th>Coverage Depth</th>
                  <th>Coverage Note</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${tipps}" var="tipp">
                  <tr>
                    <td><input type="checkbox" name="_bulkflag.${tipp.id}" class="bulkcheck"/></td>
                    <td>${counter++}</td>
                    <td>
                      <g:link controller="tipp" id="${tipp.id}" action="show">${tipp.title.title}</g:link>
                      <g:if test="${tipp?.hostPlatformURL}">( <a href="${tipp?.hostPlatformURL}" TITLE="${tipp?.hostPlatformURL}">Host Link</a>
                            <a href="${tipp?.hostPlatformURL}" TITLE="${tipp?.hostPlatformURL} (In new window)" target="_blank"><i class="icon-share-alt"></i></a>)</g:if>
                    </td>
                    <td>${tipp?.title?.getIdentifierValue('ISSN')}</td>
                    <td>${tipp?.title?.getIdentifierValue('eISSN')}</td>
                    <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${tipp.startDate}"/></td>
                    <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${tipp.endDate}"/></td>
                    <td>${tipp.embargo}</td>
                    <td>${tipp.coverageDepth}</td>
                    <td>${tipp.coverageNote}</td>
                  </tr>
                </g:each>
              </tbody>
            </table>

            <div class="paginateButtons" style="text-align:center">
              <input type="submit" value="Add Selected Entitlements" class="btn btn-primary"/>
            </div>


            <div class="pagination" style="text-align:center">
              <g:if test="${tipps}" >
                <bootstrap:paginate controller="subscriptionDetails" 
                                  action="addEntitlements" 
                                  params="${params}" next="Next" prev="Prev" 
                                  max="${max}" 
                                  total="${num_tipp_rows}" />
              </g:if>
            </div>
          </g:form>
        </dd>
      </dl>
    </div>

    <script language="JavaScript">
      $(document).ready(function() {
        $('span.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
          type      : 'textarea',
          cancel    : 'Cancel',
          submit    : 'OK',
          id        : 'elementid',
          rows      : 3,
          tooltip   : 'Click to edit...'
        });
      });

      function selectAll() {
        $('.bulkcheck').attr('checked', true);
      }
    </script>
  </body>
</html>

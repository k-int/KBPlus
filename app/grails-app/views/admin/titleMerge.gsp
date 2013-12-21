<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::Title Merge</title>
  </head>

  <body>
    <div class="container">
      <div class="span12">
        <h1>Title Merge</h1>
        <g:form action="titleMerge" method="get">
          <p>Add the appropriate ID's below. Detailed information and confirmation will be presented before proceeding</p>
          <dl>
            <div class="control-group">
              <dt>Database ID of Title To Deprecate</dt>
              <dd>
                <input type="text" name="titleIdToDeprecate" value="${params.titleIdToDeprecate}" />
                <g:if test="${title_to_deprecate != null}">
                   <h3>Title To Deprecate: <strong>${title_to_deprecate.title}</strong></h3>
                   <p>The following TIPPs will be updated to point at the authorized title</p>
                   <table class="table table-striped">
                     <thead>
                       <th>Internal Id</th>
                       <th>Package</th>
                       <th>Platform</th>
                       <th>Start</th>
                       <th>End</th>
                       <th>Coverage</th>
                     </thead>
                     <tbody>
                       <g:each in="${title_to_deprecate.tipps}" var="tipp">
                         <tr>
                           <td>${tipp.id}</td>
                           <td><g:link controller="packageDetails" action="show" id="${tipp.pkg.id}">${tipp.pkg.name}</g:link></td>
                           <td>${tipp.platform.name}</td>

                           <td style="white-space: nowrap">
                             Date: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${tipp.startDate}"/><br/>
                             Volume: ${tipp.startVolume}<br/>
                             Issue: ${tipp.startIssue}
                           </td>

                           <td style="white-space: nowrap">
                              Date: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${tipp.endDate}"/><br/>
                              Volume: ${tipp.endVolume}<br/>
                              Issue: ${tipp.endIssue}
                           </td>
                           <td>${tipp.coverageDepth}
                         </tr>
                       </g:each>
                     </tbody>
                   </table>
                </g:if>
              </dd>
            </div>

            <div class="control-group">
              <dt>Database ID of Correct Title </dt>
              <dd>
                <input type="text" name="correctTitleId" value="${params.correctTitleId}"/>
                <g:if test="${correct_title != null}">
                   <br/>Authorized Title:${correct_title.title}
                </g:if>
              </dd>
            </div>
 
            <g:if test="${correct_title != null && title_to_deprecate != null}">
              <button name="MergeButton" type="submit" value="Go">**MERGE**</button>
            </g:if>
            <button name="LookupButton" type="submit" value="Go">Look Up Title Info...</button>
          </dl>
        </g:form>
      </div>
    </div>
  </body>
</html>

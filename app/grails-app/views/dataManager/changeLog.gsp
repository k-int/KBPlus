<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data Manager Dashboard</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="dataManager" action="index">Data Manager Dashboard</g:link> </li>
      </ul>
    </div>

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

    <div class="container">
      <h2>Data Manager Dashboard</h2>
      <h6>Change Log <span class="pull-right">${num_hl} changes</span></h6>
      <g:form action="changeLog" controller="dataManager" method="get">
        From Date: <input name="startDate" type="date" value="${params.startDate}"/>
        To Date: <input name="endDate" type="date" value="${params.endDate}"/><br/>
        Package Changes: <input type="checkbox" name="packages" value="Y" ${params.packages=='Y'?'checked':''}/>
        License Changes: <input type="checkbox" name="licenses" value="Y" ${params.licenses=='Y'?'checked':''}/>
        New Items: <input type="checkbox" name="creates" value="Y" ${params.creates=='Y'?'checked':''}/>
        Updates: <input type="checkbox" name="updates" value="Y" ${params.updates=='Y'?'checked':''}/>
        <input type="submit"/>
      </g:form>
    </div>

    <g:if test="${formattedHistoryLines?.size() > 0}">

      <div class="container alert-warn">
        <table class="table table-bordered">
          <thead>
            <tr>
              <td>Name</td>
              <td>Actor</td>
              <td>Event name</td>
              <td>Property</td>
              <td>Old</td>
              <td>New</td>
              <td>date</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${formattedHistoryLines}" var="hl">
              <tr>
                <td><a href="${hl.link}">${hl.name}</a></td>
                <td><g:link controller="userDetails" action="edit" id="${hl.actor?.id}">${hl.actor?.displayName}</g:link></td>
                <td>${hl.eventName}</td>
                <td>${hl.propertyName}</td>
                <td>${hl.oldValue}</td>
                <td>${hl.newValue}</td>
                <td><g:formatDate date="${hl.lastUpdated}"/></td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>

      ${num_hl} ${max}
      <div class="pagination" style="text-align:center">
        <g:if test="${historyLines != null}" >
          <bootstrap:paginate  action="changeLog" controller="dataManager" params="${params}" next="Next" prev="Prev" maxsteps="${max}" total="${num_hl}" />
        </g:if>
      </div>

    </g:if>
    <g:else>
      <div class="container alert-warn">
      </div>
    </g:else>
  </body>
</html>

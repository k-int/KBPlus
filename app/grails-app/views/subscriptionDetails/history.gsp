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
            <li>Subscriptions</li>
        </ul>
    </div>

    <div class="container">
        <h1>${subscription.name}</h1>
        <g:render template="nav" contextPath="." />
    </div>

    <div class="container">
Subscription history
      <table  class="table table-striped table-bordered">
        <tr>
          <th>Event ID</th>
          <th>Person</th>
          <th>Date</th>
          <th>Event</th>
          <th>Field</th>
          <th>Old Value</th>
          <th>New Value</th>
        </tr>
        <g:if test="${historyLines}">
          <g:each in="${historyLines}" var="hl">
            <tr>
              <td>${hl.id}</td>
              <td style="white-space:nowrap;">${hl.actor}</td>
              <td style="white-space:nowrap;">${hl.dateCreated}</td>
              <td style="white-space:nowrap;">${hl.eventName}</td>
              <td style="white-space:nowrap;">${hl.propertyName}</td>
              <td>${hl.oldValue}</td>
              <td>${hl.newValue}</td>
            </tr>
          </g:each>
        </g:if>
      </table>
    </div>

</body>
</html>

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
            <li>Subscriptions</li>
        </ul>
    </div>

    <div class="container">
        <h1>${subscription.name}</h1>
        <g:render template="nav" contextPath="." />
    </div>

    <div class="container">

      <h3>ToDo History</h3>

      <table  class="table table-striped table-bordered">
        <tr>
          <th>ToDo Description</th>
          <th>Outcome</th>
          <th>Date</th>
          <th>Action</th>
        </tr>
        <g:if test="${todoHistoryLines}">
          <g:each in="${todoHistoryLines}" var="hl">
            <tr>
              <td>${hl.desc}</td>
              <td>${hl.status?.value?:'Pending'}
                <g:if test="${((hl.status?.value=='Accepted')||(hl.status?.value=='Rejected'))}">
                  By ${hl.user?.display?:hl.user?.username} on <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${hl.actionDate}"/>
                </g:if>
              </td>
              <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${hl.ts}"/></td>
              <td>
                <g:if test="${((hl.status==null)||(hl.status.value=='Pending'))}">
                  <g:link controller="pendingChange" action="accept" id="${hl.id}" class="btn btn-success"><i class="icon-white icon-ok"></i>Accept</g:link>
                  <g:link controller="pendingChange" action="reject" id="${hl.id}" class="btn btn-danger"><i class="icon-white icon-remove"></i>Reject</g:link>
                </g:if>
              </td>
            </tr>
          </g:each>
        </g:if>
    </div>


    <div class="container">
      <h3>Subscription history</h3>
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

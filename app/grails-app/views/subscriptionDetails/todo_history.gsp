<!doctype html>
<html>
    <head>
        <meta name="layout" content="mmbootstrap"/>
        <title>KB+ Subscription</title>
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
            </tr>
          </g:each>
        </g:if>
      </table>
       <div class="pagination">
        <bootstrap:paginate  action="todo_history" controller="subscriptionDetails" params="${params}" next="Next" prev="Prev" max="${max}" total="${todoHistoryLinesTotal}" />
      </div>


 

</body>
</html>

<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>


    <div class="container">
        <ul class="breadcrumb">
        <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller='admin' action='index'>Admin</g:link> <span class="divider">/</span> </li>
        <li class="active">Manage Affiliation Requests</li>
      </ul>
    </div>

    <div class="container">

    <g:if test="${flash.error}">
       <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
    </g:if>

    <g:if test="${flash.message}">
       <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>

        <div class="well">
          <h2>Manage Pending Membership Requests</h2>

          <table class="table table-striped table-bordered table-condensed">
            <thead>
              <tr>
                <th>User</th>
                <th>Display Name</th>
                <th>Email</th>
                <th>Organisation</th>
                <th>Role</th>
                <th>Status</th>
                <th>Date Requested</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${pendingRequests}" var="req">
                <tr>
                  <td>${req.user.username}</td>
                  <td>${req.user.displayName}</td>
                  <td>${req.user.email}</td>
                  <td>${req.org.name}</td>
                  <td>${req.role}</td>
                  <td><g:message code="cv.membership.status.${req.status}"/></td>
                  <td><g:formatDate format="dd MMMM yyyy" date="${req.dateRequested}"/></td>
                  <td><g:link controller="admin" action="actionAffiliationRequest" params="${[req:req.id, act:'approve']}" class="btn" >Approve</g:link>
                      <g:link controller="admin" action="actionAffiliationRequest" params="${[req:req.id, act:'deny']}" class="btn" >Deny</g:link></td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
    </div>




  </body>
</html>

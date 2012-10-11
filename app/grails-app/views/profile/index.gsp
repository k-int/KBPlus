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
        <li> <g:link controller='myInstitutions' action='index'>My Institutions</g:link> <span class="divider">/</span> </li>
        <li class="active">Manage Affiliations</li>
      </ul>
    </div>

    <g:if test="${flash.error}">
       <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
    </g:if>

    <g:if test="${flash.message}">
       <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>

    <div class="container">
      <div class="span12">
        <h1>User Profile</h1>
      </div>
    </div>

    <div class="container">
      <div class="span12">
        <g:form action="updateProfile">
          <dl>
            <dd>Your Display Name (Appears top right)</dd>
            <dt><input type="text" name="userDispName" value="${user.displayName}"/></dt>
          </dl>
          <input type="submit" value="Update Profile"/>
        </g:form>
      </div>
    </div>


    <div class="container">
      <div class="span12">
        <h1>Administrative memberships</h1>
      </div>
    </div>

    <div class="container"><div class="row-fluid">
      <div class="span6">
        <div class="well">
          <h2>Existing Memberships</h2>

          <table class="table table-striped table-bordered table-condensed">
            <thead>
              <tr>
                <th>Organisation</th>
                <th>Role</th>
                <th>Status</th>
                <th>Date Requested / Actioned</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${user.affiliations}" var="assoc">
                <tr>
                  <td>${assoc.org.name}</td>
                  <td>${assoc.role}</td>
                  <td><g:message code="cv.membership.status.${assoc.status}"/></td>
                  <td><g:formatDate format="dd MMMM yyyy" date="${assoc.dateRequested}"/> / <g:formatDate format="dd MMMM yyyy" date="${assoc.dateActioned}"/></td>
                  <td><!--<button class="btn">Remove</button>--></td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
      </div>

      <div class="span6">
        <div class="well">
          <h2>Request new membership</h2>
          <p>Select an organisation and a role below. Requests to join existing
             organisations will be referred to the administrative users of that organisation. If you feel you should be the administrator of an organisation
             please contact the KBPlus team for suppot.</p>
  
          <g:form controller="profile" action="processJoinRequest" form class="form-search">
            <g:select name="org"
                      from="${com.k_int.kbplus.Org.findAllBySector('Higher Education')}"
                      optionKey="id"
                      optionValue="name"
                      class="input-medium"
                      value="${params.req}">
            </g:select>
            <g:select name="role" from="${['Staff', 'Administrator']}"/>
            <button class="btn" data-complete-text="Request Membership" type="submit">Request Membership</button>
          </g:form>
          <form>
        </div>
      </div>
    </div></div>

  </body>
</html>

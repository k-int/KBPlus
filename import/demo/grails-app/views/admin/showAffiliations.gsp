<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>

    <g:if test="${flash.error}">
       <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
    </g:if>

    <g:if test="${flash.message}">
       <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>


    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller='admin' action='index'>Admin</g:link> <span class="divider">/</span> </li>
        <li class="active">Manage Affiliation Requests</li>
      </ul>
    </div>

    <div class="container">
      <div class="well">
        <h2>Affiliations</h2>
        <table class="table table-striped">
          <tr>
            <th>Username</th><th>Affiliations</th>
          </tr>
          <g:each in="${users}" var="u">
            <tr>
              <td>${u.displayName} / ${u.username}</td>
              <td>
                <ul>
                  <g:each in="${u.affiliations}" var="ua">
                    <li>${ua.org.shortcode}:${ua.status}:${ua.role}</li>
                  </g:each>
                </ul>
              </td>
          </g:each>
        </table>
      </div>


    </div>




  </body>
</html>

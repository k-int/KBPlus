<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>

    <div class="container home-page">
      <h1>Org Name</h1>
      <ul>
        <li>Licenses</li>
        <li>Subscriptions</li>
        <li>Titles</li>
        <li>Generate Renewals Worksheet</li>
        <li>Import Renewals</li>
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

    <div class="container home-page">
      <div class="row">
        <div class="span4">
          <div class="well">
            <h6>ToDo</h6>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Announcements</h6>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Latest Discussions</h6>
          </div>
        </div>
      </div>
    </div>

  </body>
</html>

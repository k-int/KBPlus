<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>
    <h2>${institution?.name} - A License</h2>
    <hr/>
    <div class="row">
      <div class="span12">
        <div class="well">
          Main
        </div>
      </div>
      <div class="span2">
        <g:render template="documents" contextPath="../templates"/>
        <g:render template="notes" contextPath="../templates"/>
        <g:render template="links" contextPath="../templates"/>
      </div>
    </div>
  </body>
</html>

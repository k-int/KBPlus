<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Forbidden</title>
  </head>

  <body>
    <g:if test="${flash.message}">
      <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
    </g:if>

    <g:if test="${flash.error}">
      <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
    </g:if>


    <div class="container">
    You do not have permission to view the requested page. Please check your profile page and request appropriate roles from the relevant institution to rectify this.
    </div>
  </body>
</html>

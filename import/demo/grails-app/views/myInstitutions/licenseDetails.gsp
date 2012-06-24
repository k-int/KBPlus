<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>
    <div class="row-fluid">

      <h2>${institution?.name} - Licenses</h2>
      <hr/>
      <div class="well">
        <g:form action="newLicense"
                controller="myInstitutions" 
                params="${[shortcode:params.shortcode]}">

        </g:form>
      </div>

    </div>
  </body>
</html>

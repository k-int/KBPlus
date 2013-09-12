<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="todo" params="${[shortcode:params.shortcode]}">${institution.name} ToDo List</g:link> </li>
      </ul>
    </div>


    <div class="container home-page">
    </div>

  </body>
</html>

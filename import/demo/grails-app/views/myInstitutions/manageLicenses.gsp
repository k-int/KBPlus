<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="row-fluid">
      <p>
        You are seeing this page because you have editor level access to more than one institution. Please
        select from the list below.
      </p>
      <ul>
        <g:each in="${orgs}" var="o">
          <li>${o.name} | <g:link action="manageLicenses">Manage Licenses</g:link></li>
        </g:each>
      </ul>
    </div>
  </body>
</html>

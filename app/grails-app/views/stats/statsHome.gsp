<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="container">
      <div class="row">

        <h1>Accounts by institutions</h1>
        <table class="table table-bordered">
          <thead>
            <tr>
              <th>Institution</th>
              <th>Num accounts</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${instStats}" var="is">
              <tr>
                <td>${is[0].name}</td>
                <td>${is[1]}</td>
              </tr>
            </g:each>
          </tbody>
        </table>

        <h1>Subscriptions by institutions</h1>
        <table class="table table-bordered">
          <thead>
            <tr>
              <th>Institution</th>
              <th>Num Subscriptions</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${soStats}" var="is">
              <tr>
                <td>${is[0].name}</td>
                <td>${is[1]}</td>
              </tr>
            </g:each>
          </tbody>
        </table>

        <h1>Licenses by institutions</h1>
        <table class="table table-bordered">
          <thead>
            <tr>
              <th>Institution</th>
              <th>Num Licenses</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${lStats}" var="is">
              <tr>
                <td>${is[0].name}</td>
                <td>${is[1]}</td>
              </tr>
            </g:each>
          </tbody>
        </table>

      </div>
    </div>
  </body>
</html>

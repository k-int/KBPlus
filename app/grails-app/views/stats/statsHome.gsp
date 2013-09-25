<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="container">
      <div class="row">

        <h1>Org Info</h1>
        <table class="table table-bordered">
          <thead>
            <tr>
              <th>Institution</th>
              <th>Affiliated Users</th>
              <th>Total subscriptions</th>
              <th>Current subscriptions</th>
              <th>Total licenses</th>
              <th>Current licenses</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${orginfo}" var="is">
              <tr>
                <td>${is.key.name}</td>
                <td>${is.value['userCount']}</td>
                <td>${is.value['subCount']}</td>
                <td>${is.value['currentSoCount']}</td>
                <td>${is.value['licCount']}</td>
                <td>${is.value['currentLicCount']}</td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>

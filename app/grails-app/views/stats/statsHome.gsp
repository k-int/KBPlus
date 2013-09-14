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
              <th>Number of accounts</th>
              <th>Number of subscriptions</th>
              <th>Number of licenses</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${orginfo}" var="is">
              <tr>
                <td>${is.key.name}</td>
                <td>${is.value['userCount']}</td>
                <td>${is.value['subCount']}</td>
                <td>${is.value['licCount']}</td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>

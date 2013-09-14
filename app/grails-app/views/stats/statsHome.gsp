<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="container">
      <div class="row">
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
      </div>
    </div>
  </body>
</html>

<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin Settings</title>
  </head>

  <body>
    <div class="container">
      <table class="table table-bordered">
        <thead>
          <tr>
            <td>Setting</td>
            <td>Value</td>
          </tr>
        </thead>
        <tbody>
          <g:each in="${settings}" var="s">
            <tr>
              <td>${s.name}</td>
              <td>
                <g:if test="${s.tp==1}">
                  <g:link controller="admin" action="toggleBoolSetting" params="${[setting:s.name]}">${s.value}</g:link>
                </g:if>
              </td>
            </tr>
          </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>

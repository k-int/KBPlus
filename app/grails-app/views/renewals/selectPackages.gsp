<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Package Planning - Select Candidate Packages</title>
  </head>

  <body>
    <div class="container">
      This page current lists all packages with any title overlap for entitlements in the current subscription
      <table>
        <g:each in="${candidates}" var="c">
          <tr>
            <td>-${c}</td>
          </tr>
        </g:each>
      </table>
    </div>
  </body>
</html>

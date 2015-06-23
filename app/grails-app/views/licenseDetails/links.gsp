<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Licence</title>
  </head>

  <body>

    <div class="container">
      <g:render template="breadcrumb" model="${[ license:license, params:params ]}"/>
    </div>

    <div class="container">
      <h1>${license.licensee?.name} ${license.type?.value} Licence : <span id="reference" style="padding-top: 5px;">${license.reference}</span></h1>

      <g:render template="nav"/>

    </div>

    <div class="container">

      <table class="table table-striped table-bordered table-condensed">
        <thead>
          <tr>
            <td>Link Type</td>
            <td>Linked Object</td>
          </tr>
        </thead>
        <tbody>
          <g:each in="${license.outgoinglinks}" var="links">
            <tr>
              <td>Outgoing</td>
              <td>${links.linkSource.genericLabel}</td>
            </tr>
          </g:each>
          <g:each in="${license.incomingLinks}" var="links">
            <tr>
              <td>Incoming</td>
              <td>${links.linkTarget.genericLabel}</td>
            </tr>
          </g:each>
        </tbody>
      </table>
    </div>
    
  </body>
</html>

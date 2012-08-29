<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <li>Licences</li>
      </ul>
    </div>

    <div class="container">
      <h1>${license.licensee?.name} ${license.type?.value} Licence : <span id="reference" class="ipe" style="padding-top: 5px;">${license.reference}</span></h1>

      <ul class="nav nav-pills">
        <li><g:link controller="licenseDetails" 
                                   action="index" 
                                   params="${[id:params.id]}">License Details</g:link></li>

        <li><g:link controller="licenseDetails" 
                    action="documents" 
                    params="${[id:params.id]}">Documents</g:link></li>

        <li class="active"><g:link controller="licenseDetails" 
                    action="links" 
                    params="${[id:params.id]}">Links</g:link></li>

        <li><g:link controller="licenseDetails" 
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>
      </ul>

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
    
    <script language="JavaScript">
      $(document).ready(function() {

       });
    </script>

  </body>
</html>

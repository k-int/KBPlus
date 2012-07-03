<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>


    <div class="row-fluid">
        <ul class="breadcrumb">
        <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller='admin' action='index'>Admin</g:link> <span class="divider">/</span> </li>
        <li class="active">Manage Affiliation Requests</li>
      </ul>
    </div>


    <g:if test="${flash.error}">
       <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
    </g:if>

    <g:if test="${flash.message}">
       <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>


    <div class="row-fluid">
      <div class="well">
        <h2>Data Reconciliation</h2>
        <g:if test="${recon_status.active}">
          Data reconciliation currently in stage : ${recon_status.stage} (nn%)
        </g:if>
        <g:else>
          Data reconciliation not currently active. <g:link action="startReconciliation">Start</g:link>
        </g:else>

        <g:if test="${stats}">
          <table>
            <g:each in="${stats}" var="s">
              <tr><td>${s.key}</td><td>${s.value}</td></tr>
            </g:each>
          </table>
        </g:if>
      </div>


    </div>




  </body>
</html>

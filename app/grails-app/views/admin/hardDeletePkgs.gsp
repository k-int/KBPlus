<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::Package Delete</title>
  </head>

  <body>
    <h1>Hard Delete Packages</h1>

      <div id="osel_add_modal" class="modal hide">
      Hello there
      </div>    
    <g:if test="${pkg}">

      <script>
      console.log('hello');
      $("#osel_add_modal").show();
      </script>
    </g:if>
    
      <g:if test="${flash.message}">
      <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>
        <g:if test="${flash.error}">
        <bootstrap:alert class="alert alert-error">${flash.error}</bootstrap:alert>
      </g:if>

        <table class="table table-bordered table-striped">
          <thead>
            <tr>
              <g:sortableColumn property="name" title="${message(code: 'package.name.label', default: 'Name')}" />
              <th></th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${pkgs}" var="packageInstance">
            <tr>
                        
              <td>${fieldValue(bean: packageInstance, field: "name")} (${packageInstance?.contentProvider?.name})</td>            
              <td class="link">
                <g:link action="hardDeletePkgs" id="${packageInstance.id}" class="btn btn-small">Prepare Delete</g:link>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>

  </body>
</html>	    
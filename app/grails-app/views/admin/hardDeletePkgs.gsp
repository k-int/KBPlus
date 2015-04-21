<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::Package Delete</title>
  </head>

  <body>
    <h1>Hard Delete Packages</h1>

  
    
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
                <button onclick="showDetails(${packageInstance.id});" class="btn btn-small">Prepare Delete</button>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
        <div id="packageDetails_div">
        </div>
        <g:javascript>
        function showDetails(id){
          jQuery.ajax({type:'get',data:jQuery(this).serialize(), url:"${createLink(controller:'admin', action:'hardDeletePkgs')}"+"/"+id,success:function(data,textStatus){jQuery('#packageDetails_div').html(data);$("#pkg_details_modal").modal("show")},error:function(XMLHttpRequest,textStatus,errorThrown){}
        });
        }
        </g:javascript>

  </body>
</html>     
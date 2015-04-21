<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::TIP View</title>
  </head>

  <body>
    <h1>TIP View</h1>

  
    
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
              <th>Institution</th>
              <th>Provider</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${tips}" var="tip">
            <tr>
                        
              <td>${tip?.title?.title}</td>   
              <td>${tip?.institution?.name}</td>   
              <td>${tip?.provider?.name}</td>   
              <td class="link">
                <button onclick="showDetails(${tip.id});" class="btn btn-small">Edit Dates</button>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
        <div id="magicArea">
        </div>

        <g:javascript>
        function showDetails(id){
          console.log(id);
          jQuery.ajax({type:'get',data:jQuery(this).serialize(), url:"${createLink(controller:'ajax', action:'getTipCoreDates')}"+"/"+id,success:function(data,textStatus){jQuery('#magicArea').html(data);$('div[name=coreAssertionEdit]').modal("show")},error:function(XMLHttpRequest,textStatus,errorThrown){}
        });
        }

         function hideModal(){
          $("[name='coreAssertionEdit']").modal('hide');
         }

        function showCoreAssertionModal(){
          $("input.datepicker-class").datepicker({
            format:"${session.sessionPreferences?.globalDatepickerFormat}"
          });
          $("[name='coreAssertionEdit']").modal('show');
          $('.xEditableValue').editable();
        }
        </g:javascript>

  </body>
</html>     
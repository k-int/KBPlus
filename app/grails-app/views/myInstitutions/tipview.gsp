<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} - Edit Core Titles</title>
  </head>

  <body>
    <div class="container">

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="dashboard" params="${[shortcode:params.shortcode]}">${institution.name} - Dashboard</g:link> <span class="divider">/</span>  </li>
        <li> <g:link controller="myInstitutions" action="tipview" params="${[shortcode:params.shortcode]}"> Edit Core Titles (JUSP & KB+) </g:link> </li>

      </ul>
    </div>

    </div>
      <div class="container">


      <g:if test="${flash.message}">
      <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>
        <g:if test="${flash.error}">
        <bootstrap:alert class="alert alert-error">${flash.error}</bootstrap:alert>
      </g:if>

      <ul class="nav nav-pills">
          <g:set var="nparams" value="${params.clone()}"/>
          <g:set var="active_filter" value="${nparams.remove('filter')}"/>

          <li class="${(active_filter=='core' || active_filter == null)?'active':''}">
            <g:link action="tipview" params="${nparams + [filter:'core']}">Core</g:link>
          </li>
          <li class="${active_filter=='not'?'active':''}"><g:link action="tipview" params="${nparams + [filter:'not']}">Not Core</g:link></li>
          <li class="${active_filter=='all'?'active':''}"><g:link action="tipview" params="${nparams + [filter:'all']}">All</g:link></li>

      </ul>
      <div class="row">
        <div class="span12">
          <g:form action="tipview" method="get" params="${[shortcode:params.shortcode]}">

          <div class="well form-horizontal">
            Search For: <select name="search_for">
                                <option ${params.search_for=='title' ? 'selected' : ''} value="title">Title</option>
                    <option ${params.search_for=='provider' ? 'selected' : ''} value="provider">Provider</option>
                    </select>
            Name: <input name="search_str" placeholder="Partial terms accepted" value="${params.search_str}"/>
            Sort: <select name="sort">
                    <option ${params.sort=='title-title' ? 'selected' : ''} value="title-title">Title</option>
                    <option ${params.sort=='provider-name' ? 'selected' : ''} value="provider-name">Provider</option>
                  </select>
            Order: <select name="order" value="${params.order}">
                    <option ${params.order=='asc' ? 'selected' : ''} value="asc">Ascending</option>
                    <option ${params.order=='desc' ? 'selected' : ''} value="desc">Descending</option>
                  </select>
                  <input type="hidden" name="filter" value="${params.filter}"/>
            <button type="submit" name="search">Search</button>
          </div>
          </g:form>
        </div>
      </div>


        <table class="table table-bordered table-striped">
          <thead>
            <tr>
              <th>Title</th>
              <th>Provider</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${tips}" var="tip">
            <tr>

              <td>
              <g:link controller="myInstitutions" action="tip" params="${[shortcode:params.shortcode]}" id="${tip.id}">${tip?.title?.title}</g:link>
              (title: <g:link controller="titleDetails" action="show" id="${tip?.title?.id}">${tip?.title?.title}</g:link>)
              </td>
              <td>
              <g:link controller="org" action="show" id="${tip?.provider?.id}">${tip?.provider?.name}</g:link>
              </td>
              <td class="link">

                <g:set var="coreStatus" value="${tip?.coreStatus(null)}"/>
                <a href="#" class="editable-click" onclick="showDetails(${tip.id});">${coreStatus?'True(Now)':coreStatus==null?'False(Never)':'False(Now)'}</a>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
          <div class="pagination" style="text-align:center">
            <span><bootstrap:paginate action="tipview" max="${user?.defaultPageSize?:10}" params="${[:]+params}" next="Next" prev="Prev" total="${tips.totalCount}" /></span>
          </div>
        <div id="magicArea">
        </div>
        </div>

        <g:javascript>
        function showDetails(id){
          console.log(${editable});
          jQuery.ajax({type:'get', url:"${createLink(controller:'ajax', action:'getTipCoreDates')}?editable="+${editable}+"&tipID="+id,success:function(data,textStatus){jQuery('#magicArea').html(data);$('div[name=coreAssertionEdit]').modal("show")},error:function(XMLHttpRequest,textStatus,errorThrown){}
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

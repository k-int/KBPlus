<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} ToDo List</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="todo" params="${[shortcode:params.shortcode]}">${institution.name} ToDo List</g:link> </li>
      </ul>
    </div>

    <div class="container home-page">
            <table class="table">
              <g:each in="${todos}" var="todo">
                <tr>
                  <td>
                    <span class="badge badge-warning">${todo.num_changes}</span> <em>${todo.item_with_changes.toString()}</em> </br>
                    <span class="pull-right">Changes between <g:formatDate date="${todo.earliest}" format="yyyy-MM-dd hh:mm a"/></span><br/>
                    <span class="pull-right">and <g:formatDate date="${todo.latest}" format="yyyy-MM-dd hh:mm a"/></span><br/>
                    <strong class="pull-right">
                      <g:if test="${todo.item_with_changes instanceof com.k_int.kbplus.Subscription}">
                        <g:link controller="subscriptionDetails" action="index" id="${todo.item_with_changes.id}">View Subscription</g:link>
                      </g:if>
                      <g:else>
                        <g:link controller="licenseDetails" action="index" id="${todo.item_with_changes.id}">View License</g:link>
                      </g:else>
                    </strong>
                  </td>
                </tr>
              </g:each>
            </table>

      <div class="pagination" style="text-align:center">
        <g:if test="${todos!=null}" >
          <bootstrap:paginate  action="todos" controller="myInstitutions" params="${params}" next="Next" prev="Prev" max="${max}" total="${num_todos}" />
        </g:if>
      </div>
    </div>


  </body>
</html>

<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} ToDo List</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions"
                     action="instdash"
                     params="${[shortcode:params.shortcode]}">${institution.name} Dashboard</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="todo" params="${[shortcode:params.shortcode]}">ToDo List</g:link> </li>
      </ul>
    </div>

    <div class="container home-page">

      <div class="pagination" style="text-align:center">
        <g:if test="${todos!=null}" >
          <bootstrap:paginate  action="todo" controller="myInstitutions" params="${params}" next="Next" prev="Prev" max="${max}" total="${num_todos}" />
        </g:if>
      </div>

            <table class="table">
              <g:each in="${todos}" var="todo">
                <tr>
                  <td>
                    <strong>
                      <g:if test="${todo.item_with_changes instanceof com.k_int.kbplus.Subscription}">
                        <g:link controller="subscriptionDetails" action="index" id="${todo.item_with_changes.id}">Subscription: ${todo.item_with_changes.toString()}</g:link>
                      </g:if>
                      <g:else>
                        <g:link controller="licenseDetails" action="index" id="${todo.item_with_changes.id}">License: ${todo.item_with_changes.toString()}</g:link>
                      </g:else>
                    </strong><br/>
                    <span class="badge badge-warning">${todo.num_changes}</span> 
                    <span>Change(s) between <g:formatDate date="${todo.earliest}" format="yyyy-MM-dd hh:mm a"/></span>
                    <span>and <g:formatDate date="${todo.latest}" format="yyyy-MM-dd hh:mm a"/></span><br/>
                  </td>
                </tr>
              </g:each>
            </table>

      <div class="pagination" style="text-align:center">
        <g:if test="${todos!=null}" >
          <bootstrap:paginate  action="todo" controller="myInstitutions" params="${params}" next="Next" prev="Prev" max="${max}" total="${num_todos}" />
        </g:if>
      </div>

    </div>


  </body>
</html>

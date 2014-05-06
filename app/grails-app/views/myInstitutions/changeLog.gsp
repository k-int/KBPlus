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
        <li> <g:link controller="myInstitutions" action="todo" params="${[shortcode:params.shortcode]}">${institution.name} Change Log</g:link> </li>
      </ul>
    </div>

    <div class="container home-page">
      <table class="table table-striped">
        <g:each in="${changes}" var="chg">
          <tr>
            <td>
              ${chg.ts}
            </td>
            <td>
              <g:if test="${chg.subscription != null}">Change to subscription ${chg.subscription.id}</g:if>
              <g:if test="${chg.license != null}">Change to license ${chg.license.id}</g:if>
              <g:if test="${chg.pkg != null}">Change to package ${chg.package.id}</g:if>
            </td>
            <td>
              ${chg.desc}
              ${chg.status} on ${chg.actionDate} by ${chg.user}
            </td>
          </tr>
        </g:each>
      </table>

      <div class="pagination" style="text-align:center">
        <g:if test="${todos!=null}" >
          <bootstrap:paginate  action="todo" controller="myInstitutions" params="${params}" next="Next" prev="Prev" max="${max}" total="${num_changes}" />
        </g:if>
      </div>
    </div>


  </body>
</html>

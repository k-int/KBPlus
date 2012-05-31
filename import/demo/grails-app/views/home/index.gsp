<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="row-fluid">
      <p>
        JISC KB+ data explorer. Use the links above to navigate the data items imported by the KB+ import process and validate the data.
      </p>
      <p>
        Browse using the categories above, or search over organisations and titles below.
      </p>
      <p>

        <div class="container">
          <g:form action="index" method="get">
            Search Text: <input type="text" class="search-query" placeholder="Search" name="q">
          </g:form>
        </div>

        <div >
          <g:if test="${hits}">
            <div id="resultsarea">
              <table cellpadding="5" cellspacing="5">
                <tr><th>Type</th><th>Title/Name</th></tr>
                <g:each in="${hits}" var="hit">
                  <tr>
                    <td>
                      <g:if test="${hit.type=='com.k_int.kbplus.Org'}"><span class="label label-info">Organisation</span></g:if> 
                      <g:else><span class="label label-info">Title Instance</span></g:else>
                    </td>
                    <td>
                      <g:if test="${hit.type=='com.k_int.kbplus.Org'}">
                        <g:link controller="org" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link>
                      </g:if> 
                      <g:else>
                        <g:link controller="titleInstance" action="show" id="${hit.source.dbId}">${hit.source.title}</g:link>
                      </g:else>
                    </td>
                  </tr>
                </g:each>
              </table>
            </div>
          </g:if>
        </div>
      </p>
    </div>
  </body>
</html>

<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Renewals Generation - Search</title>
  </head>

  <body>
    <div class="container">
      <div class="row">
        <div class="span12">
          <div class="well">
            <g:form action="search">
              Package Name: <input name="pkgname" value="${params.pkgname}"/><br/>
              ${params.pkgname}
            </g:form>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="span2">
          <div class="well">
              <ul>
              <g:each in="${facets}" var="facet">
                <li> ${facet.key}
                  <ul>
                    <g:each in="${facet.value}" var="fe">
                      <li>${fe.display} (${fe.count})</li>
                    </g:each>
                  </ul>
                </li>
              </g:each>
              </ul>
          </div>
        </div>
        <div class="span8">
          <div class="well">
             <g:if test="${hits}" >
                <div class="paginateButtons" style="text-align:center">
                  <g:if test="${params.int('offset')}">
                   Showing Results ${params.int('offset') + 1} - ${hits.totalHits < (params.int('max') + params.int('offset')) ? hits.totalHits : (params.int('max') + params.int('offset'))} of ${hits.totalHits}
                  </g:if>
                  <g:elseif test="${hits.totalHits && hits.totalHits > 0}">
                    Showing Results 1 - ${hits.totalHits < params.int('max') ? hits.totalHits : params.int('max')} of ${hits.totalHits}
                  </g:elseif>
                  <g:else>
                    Showing ${hits.totalHits} Results
                  </g:else>
                </div>

                <div id="resultsarea">
                  <table cellpadding="5" cellspacing="5">
                    <tr><th>Type</th><th>Title/Name</th><th>Additional Info</th></tr>
                    <g:each in="${hits}" var="hit">
                      <tr>
                        <td>
                          ${hit.source.subtype}
                        </td>
                        <td><g:link controller="subscription" action="show" id="${hit.source.dbId}">${hit.source.name} (${hit.source.type})</g:link></td>
                        <td>${hit.source.identifier}</td>
                      </tr>
                    </g:each>
                  </table>
                </div>
             </g:if>
             <div class="paginateButtons" style="text-align:center">
                <g:if test="${hits}" >
                  <span><g:paginate controller="renewals" action="search" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${hits.totalHits}" /></span>
                </g:if>
              </div>
          </div>
        </div>
        <div class="span2">
          <div class="well">
          Basket
          </div>
        </div>

      </div>
    </div>
  </body>
</html>

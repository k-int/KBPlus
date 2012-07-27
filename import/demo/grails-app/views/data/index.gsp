<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>

    <div class="container">
        <ul class="breadcrumb">
        <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
        <li class="active">Search</li>
      </ul>
    </div>

    <div class="container">
      <p>
        JISC KB+ data explorer. Use the links above to navigate the data items imported by the KB+ import process and validate the data.
      </p>
      <p>
        Browse using the categories above, or search over organisations and titles below.
      </p>
      <div>

        <div class="container" style="text-align:center">
          <g:form action="index" method="get">
            Search Text: <input type="text" class="search-query" placeholder="Search" name="q" value="${params.q?.encodeAsHTML()}">
          </g:form>
        </div>

        <div class="row">

          <div class="span2">
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

          <div class="span8">
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
                        <g:if test="${hit.type=='com.k_int.kbplus.Org'}"><span class="label label-info">Organisation</span></g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.TitleInstance'}"><span class="label label-info">Title Instance</span></g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.Package'}"><span class="label label-info">Package</span></g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.Platform'}"><span class="label label-info">Platform</span></g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.Subscription'}"><span class="label label-info">Subscription</span></g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.License'}"><span class="label label-info">License</span></g:if> 
                      </td>
                      <g:if test="${hit.type=='com.k_int.kbplus.Org'}">
                          <td><g:link controller="org" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link></td>
                      </g:if> 
                      <g:if test="${hit.type=='com.k_int.kbplus.TitleInstance'}">
                        <td><g:link controller="titleInstance" action="show" id="${hit.source.dbId}">${hit.source.title}</g:link></td>
                        <td>
                          <g:each in="${hit.source.identifiers}" var="id">
                            ${id.type}:${id.value} &nbsp;
                          </g:each>
                        </td>
                      </g:if>
                      <g:if test="${hit.type=='com.k_int.kbplus.Package'}">
                        <td><g:link controller="package" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link></td>
                      </g:if>
                      <g:if test="${hit.type=='com.k_int.kbplus.Platform'}">
                        <td><g:link controller="platform" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link></td>
                      </g:if>
                      <g:if test="${hit.type=='com.k_int.kbplus.Subscription'}">
                        <td><g:link controller="subscription" action="show" id="${hit.source.dbId}">${hit.source.name} (${hit.source.type})</g:link></td>
                      </g:if>
                      <g:if test="${hit.type=='com.k_int.kbplus.License'}">
                        <td><g:link controller="license" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link></td>
                      </g:if>
                    </tr>
                  </g:each>
                </table>
              </div>
            </g:if>
  
            <div class="paginateButtons" style="text-align:center">
              <g:if test="${hits}" >
                <span><g:paginate controller="home" action="index" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${hits.totalHits}" /></span>
              </g:if>
            </div>
          </div>

          <div class="span2">
          </div>

        </div><!-- end row-->
      </div>
    </div>
  </body>
</html>

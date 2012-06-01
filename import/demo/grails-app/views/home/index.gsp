<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>

    <style>
.paginateButtons {
    margin: 3px 0px 3px 0px;
}

.paginateButtons a {
    padding: 2px 4px 2px 4px;
    background-color: #A4A4A4;
    border: 1px solid #EEEEEE;
    text-decoration: none;
    font-size: 10pt;
    font-variant: small-caps;
    color: #EEEEEE;
}

.paginateButtons a:hover {
    text-decoration: underline;
    background-color: #888888;
    border: 1px solid #AA4444;
    color: #FFFFFF;
}
    </style>
  </head>

  <body>
    <div class="row-fluid">
      <p>
        JISC KB+ data explorer. Use the links above to navigate the data items imported by the KB+ import process and validate the data.
      </p>
      <p>
        Browse using the categories above, or search over organisations and titles below.
      </p>
      <div>

        <div class="container" style="text-align:center">
          <g:form action="index" method="get">
            Search Text: <input type="text" class="search-query" placeholder="Search" name="q">
          </g:form>
        </div>

        <div class="row">

          <div class="span2">
            Facets
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
                  <tr><th>Type</th><th>Title/Name</th></tr>
                  <g:each in="${hits}" var="hit">
                    <tr>
                      <td>
                        <g:if test="${hit.type=='com.k_int.kbplus.Org'}"><span class="label label-info">Organisation</span></g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.TitleInstance'}"><span class="label label-info">Title Instance</span></g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.Package'}"><span class="label label-info">Package</span></g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.Platform'}"><span class="label label-info">Platform</span></g:if> 
                      </td>
                      <td>
                        <g:if test="${hit.type=='com.k_int.kbplus.Org'}">
                          <g:link controller="org" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link>
                        </g:if> 
                        <g:if test="${hit.type=='com.k_int.kbplus.TitleInstance'}">
                          <g:link controller="titleInstance" action="show" id="${hit.source.dbId}">${hit.source.title}</g:link>
                        </g:if>
                        <g:if test="${hit.type=='com.k_int.kbplus.Package'}">
                          <g:link controller="package" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link>
                        </g:if>
                        <g:if test="${hit.type=='com.k_int.kbplus.Platform'}">
                          <g:link controller="platform" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link>
                        </g:if>
                      </td>
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

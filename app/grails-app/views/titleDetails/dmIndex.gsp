<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Titles - Search</title>
  </head>

  <body>


    <div class="container">
      <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <li><g:link controller="titleDetails" action="dmIndex">Data Manager Titles View</g:link></li>
      </ul>
    </div>

    <div class="container">
      <g:form action="dmIndex" method="get" params="${params}" role="form" class="form-inline">
      <input type="hidden" name="offset" value="${params.offset}"/>

      <div class="row">
        <div class="span12">
          <div class="well container">
            Title : <input name="q" placeholder="Add &quot;&quot; for exact match" value="${params.q}"/> (Search on title text and identifiers)
            Status : <select name="status">
              <option value="">All</option>
              <option value="Deleted" ${params.status=='Deleted'?'selected':''}>Deleted</option>
            </select>
            <button type="submit" name="search" value="yes">Search</button>
            <div class="pull-right">
            </div>
          </div>
        </div>
      </div>


      <div class="row">

        <div class="span12">
          <div class="well">
             <g:if test="${hits}" >
                <div class="paginateButtons" style="text-align:center">
                  <g:if test="${params.int('offset')}">
                   Showing Results ${params.int('offset') + 1} - ${totalHits < (params.int('max') + params.int('offset')) ? totalHits : (params.int('max') + params.int('offset'))} of ${totalHits}
                  </g:if>
                  <g:elseif test="${totalHits && totalHits > 0}">
                    Showing Results 1 - ${totalHits < params.int('max') ? totalHits : params.int('max')} of ${totalHits}
                  </g:elseif>
                  <g:else>
                    Showing ${totalHits} Results
                  </g:else>
                </div>

                <div id="resultsarea">
                  <table class="table table-bordered table-striped">
                    <thead>
                      <tr>
                      <th style="white-space:nowrap">Title</th>
                      <th style="white-space:nowrap">Publisher</th>
                      <th style="white-space:nowrap">Identifiers</th>
                      <th style="white-space:nowrap">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <g:each in="${hits}" var="hit">
                        <tr>
                          <td>
                            <g:link controller="titleDetails" action="show" id="${hit.id}">${hit.title}</g:link>
                            <g:if test="${editable}">
                              <g:link controller="titleDetails" action="edit" id="${hit.id}">(Edit)</g:link>
                            </g:if>
                          </td>
                          <td>
                          </td>
                          <td>
                          </td>
                          <td>
                            ${hit.status?.value}
                          </td>
                        </tr>
                      </g:each>
                    </tbody>
                  </table>
                </div>
             </g:if>
             <div class="paginateButtons" style="text-align:center">
                <g:if test="${hits}" >
                  <span><g:paginate controller="titleDetails" action="dmIndex" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${totalHits}" /></span>
                </g:if>
              </div>
          </div>
        </div>
      </div>
      </g:form>
    </div>
    <!-- ES Query: ${es_query} -->
  </body>
</html>

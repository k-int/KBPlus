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
        <li><g:link controller="titleDetails" action="index">All Titles</g:link></li>
      </ul>
    </div>

    <div class="container">
      <div class="well">
      <g:form action="index" role="form" class="form-inline" method="get" params="${params}">

        <input type="hidden" name="offset" value="${params.offset}"/>

        <label for="q" class="control-label">Search :</label>   
        <input id="q" type="text" name="q" placeholder="Add &quot;&quot; for exact match" value="${params.q}"/>
       
        <label for="filter" class="control-label">Search in :</label>
        <g:select id="filter" name="filter" from="${[[key:'title',value:'Title'],[key:'publisher',value:'Publisher'],[key:'',value:'All']   ]}" optionKey="key" optionValue="value" value="${params.filter}"/>
       
        <button type="submit" name="search" value="yes">Search</button>
      </g:form>
      </div>
    </div>
    
    <div class="container">
      <div class="row">

        <div class="span12">
          <div class="well">
             <g:if test="${hits}" >
                <div class="paginateButtons" style="text-align:center">
                  <g:if test="${params.int('offset')}">
                   Showing Results ${params.int('offset') + 1} - ${resultsTotal < (params.int('max') + params.int('offset')) ? resultsTotal : (params.int('max') + params.int('offset'))} of ${resultsTotal}
                  </g:if>
                  <g:elseif test="${resultsTotal && resultsTotal > 0}">
                    Showing Results 1 - ${resultsTotal < params.int('max') ? resultsTotal : params.int('max')} of ${resultsTotal}
                  </g:elseif>
                  <g:else>
                    Showing ${resultsTotal} Results
                  </g:else>
                </div>

                <div id="resultsarea">
                  <table class="table table-bordered table-striped">
                    <thead>
                      <tr>
                      <g:sortableColumn property="sortTitle" title="Title" params="${params}" />
                      <g:sortableColumn property="publisher" title="Publisher" params="${params}" />
                      <th style="white-space:nowrap">Identifiers</th>
                      </tr>
                    </thead>
                    <tbody>
                      <g:each in="${hits}" var="hit">
                        <tr>
                          <td>
                            <g:link controller="titleDetails" action="show" id="${hit.getSource().dbId}">${hit.getSource().title}</g:link>
                            <g:if test="${editable}">
                              <g:link controller="titleDetails" action="edit" id="${hit.getSource().dbId}">(Edit)</g:link>
                            </g:if>
                          </td>
                          <td>
                            ${hit.getSource().publisher?:''}
                          </td>
                          <td>
                            <g:each in="${hit.getSource().identifiers}" var="id">
                              ${id.type}:${id.value}<br/>
                            </g:each>
                          </td>
                        </tr>
                      </g:each>
                    </tbody>
                  </table>
                </div>
             </g:if>
             <div class="paginateButtons" style="text-align:center">
                <g:if test="${hits}" >
                  <span><g:paginate controller="titleDetails" action="index" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${resultsTotal}" /></span>
                </g:if>
              </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>

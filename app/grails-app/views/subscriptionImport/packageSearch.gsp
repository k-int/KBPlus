<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Subscription Generation - Find base packages</title>
  </head>

  <body>
    <div class="container">
      <g:form action="generateImportWorksheet" method="get" params="${params}">
      <input type="hidden" name="offset" value="${params.offset}"/>
      <div class="row">
        <div class="span12">
          <div class="well">
            Package Name: <input name="pkgname" value="${params.pkgname}"/>
            <button type="submit" name="search" value="yes">Search</button>
            <div class="pull-right">
            <button type="submit" name="clearBasket" value="yes">Clear Basket</button>
            <button type="submit" name="generate" value="yes">Generate Subscription Sheet</button>
            </div>
           
          </div>
        </div>
      </div>
      <div class="row">
        <div class="span2">
          <div class="well">
              <g:each in="${facets}" var="facet">
                <g:if test="${facet.key != 'type'}">
                <h5><g:message code="facet.so.${facet.key}" default="${facet.key}" /></h5>
                <g:each in="${facet.value}" var="fe">
                    <g:set var="facetname" value="fct:${facet.key}:${fe.display}" />
                    <div><g:checkBox class="pull-right" name="${facetname}" value="${params[facetname]}" />${fe.display} (${fe.count})</div>
                </g:each>
                </g:if>
                </li>
              </g:each>
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
                  <table class="table table-bordered table-striped">
                    <thead>
                      <tr><th>Package Name</th><th>Consortium</th><th>Additional Info</th></tr>
                    </thead>
                    <tbody>
                      <g:each in="${hits}" var="hit">
                        <tr>
                          <td><g:link controller="packageDetails" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link></td>
                          <td>${hit.source.consortiaName}</td>
                          <td><button type="submit" class="btn" name="addBtn" value="${hit.source.dbId}">Add to<br/>basket</button></td>
                        </tr>
                      </g:each>
                    </tbody>
                  </table>
                </div>
             </g:if>
             <div class="paginateButtons" style="text-align:center">
                <g:if test="${hits}" >
                  <span><g:paginate controller="subscriptionImport" action="generateImportWorksheet" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${hits.totalHits}" /></span>
                </g:if>
              </div>
          </div>
        </div>
        <div class="span2">
          <div class="well">
            <h5>Basket</h5>
            <g:each in="${basket}" var="itm">
              <div>
                <hr/>
                ${itm.name}
              </div>
            </g:each>
          </div>
        </div>

      </div>
      </g:form>
    </div>
  </body>
</html>

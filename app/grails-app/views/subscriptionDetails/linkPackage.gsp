<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Notes</g:link> </li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

    <div class="container">
      ${institution?.name} ${subscriptionInstance?.type?.value}
       <h1>${subscriptionInstance.name} : Link Subscription to Packages</h1>
       <g:render template="nav" contextPath="." />
    </div>

    <div class="container">
      <g:form name="LinkPackageForm" action="linkPackage" method="get" params="${params}">
      <input type="hidden" name="offset" value="${params.offset}"/>
      <input type="hidden" name="id" value="${params.id}"/>
      <div class="row">
        <div class="span12">
          <div class="well">
            Package Name: <input name="pkgname" value="${params.pkgname}"/><button type="submit" name="search" value="yes">Search</button>           
          </div>
        </div>
      </div>
      <div class="row">
        <div class="span2">
          <div class="well">
              <g:each in="${facets}" var="facet">
                <h5><g:message code="facet.so.${facet.key}" default="${facet.key}" /></h5>
                    <g:each in="${facet.value}" var="fe">
                      <g:set var="facetname" value="fct:${facet.key}:${fe.display}" />
                      <div><g:checkBox class="pull-right" name="${facetname}" value="${params[facetname]}" />${fe.display} (${fe.count})</div>
                    </g:each>
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
                          <td><g:link action="linkPackage" 
                                 id="${params.id}"
                                 params="${[addId:hit.source.dbId,addType:'Without']}"
                                 onClick="return confirm('Are you sure you want to add without entitlements?');">Link (no Entitlements)</g:link><br/>
                              <g:link action="linkPackage" 
                                 id="${params.id}" 
                                 params="${[addId:hit.source.dbId,addType:'With']}"
                                 onClick="return confirm('Are you sure you want to add with entitlements?');">Link (with Entitlements)</g:link></td>
                        </tr>
                      </g:each>
                    </tbody>
                  </table>
                </div>
             </g:if>
             <div class="paginateButtons" style="text-align:center">
                <g:if test="${hits}" >
                  <span><g:paginate controller="subscriptionDetails" action="linkPackage" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${hits.totalHits}" /></span>
                </g:if>
              </div>
          </div>
        </div>
        <div class="span2">
          <div class="well">
            <h4>Current Links</h4>
            <hr/>
            <g:each in="${subscriptionInstance.packages}" var="sp">
              <g:link controller="packageDetails" action="show" id="${sp.pkg.id}">${sp.pkg.name}</g:link><br/>
            </g:each>
          </div>
        </div>
      </div>
      </g:form>
    </div>    
    <!-- ES Query String: ${es_query} -->
  </body>
</html>

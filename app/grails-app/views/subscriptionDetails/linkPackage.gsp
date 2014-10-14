<!doctype html>
<%
  def addFacet = { params, facet, val ->
    def newparams = [:]
    newparams.putAll(params)
    def current = newparams[facet]
    if ( current == null ) {
      newparams[facet] = val
    }
    else if ( current instanceof String[] ) {
      newparams.remove(current)
      newparams[facet] = current as List
      newparams[facet].add(val);
    }
    else {
      newparams[facet] = [ current, val ]
    }
    newparams
  }

  def removeFacet = { params, facet, val ->
    def newparams = [:]
    newparams.putAll(params)
    def current = newparams[facet]
    if ( current == null ) {
    }
    else if ( current instanceof String[] ) {
      newparams.remove(current)
      newparams[facet] = current as List
      newparams[facet].remove(val);
    }
    else if ( current?.equals(val.toString()) ) {
      newparams.remove(facet)
    }
    newparams
  }

%>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Subscription</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
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
      <p>
          <g:each in="${['type','endYear','startYear','consortiaName','cpname']}" var="facet">
            <g:each in="${params.list(facet)}" var="fv">
              <span class="badge alert-info">${facet}:${fv} &nbsp; <g:link controller="${controller}" action="linkPackage" params="${removeFacet(params,facet,fv)}"><i class="icon-remove icon-white"></i></g:link></span>
            </g:each>
          </g:each>
      </p>

        <div class="facetFilter span2">
          <g:each in="${facets}" var="facet">
            <div class="panel panel-default">
              <div class="panel-heading">
                <h3><g:message code="facet.so.${facet.key}" default="${facet.key}" /></h3>
              </div>
              <div class="panel-body">
                <ul>
                  <g:each in="${facet.value}" var="v">
                    <li>
                      <g:set var="fname" value="facet:${facet.key+':'+v.term}"/>

                      <g:if test="${params.list(facet.key).contains(v.term.toString())}">
                        ${v.display} (${v.count})
                      </g:if>
                      <g:else>
                        <g:link controller="${controller}" action="linkPackage" params="${addFacet(params,facet.key,v.term)}">${v.display}</g:link> (${v.count})
                      </g:else>
                    </li>
                  </g:each>
                </ul>
              </div>
            </div>
          </g:each>
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
                      <tr><th>Package Name</th><th>Consortium</th><th>Action</th></tr>
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

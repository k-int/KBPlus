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
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="content"><div class="row-fluid">
      <div class="row well text-center">
        <g:form role="form" action="search" controller="home" method="get" class="form-inline">
          <fieldset>

            <g:each in="${['collections','categories','subjects']}" var="facet">
              <g:each in="${params.list(facet)}" var="selected_facet_value"><input type="hidden" name="${facet}" value="${selected_facet_value}"/></g:each>
            </g:each>

            <label>Search Text : </label>
            <input id="intext" type="text" class="form-control" placeholder="Search Text" name="q" value="${params.q}"/>
            <!--
            <label>Keywords : </label>
            <input id="kwin" type="text" class="form-control" placeholder="Keyword" name="q" value="${params.q}"/>
            -->
            <button name="search" type="submit" value="true" class="btn btn-default">Search</button>
          </fieldset>
        </g:form>
        <p>
          <g:each in="${['collections','categories','subjects']}" var="facet">
            <g:each in="${params.list(facet)}" var="fv">
              <span class="badge alert-info">${fv} &nbsp; <g:link controller="home" action="search" params="${removeFacet(params,facet,fv)}"><span class="glyphicon glyphicon-remove"></span></g:link></span>
            </g:each>
          </g:each>
        </p>
        <g:if test="${hits!=null}">
          <p>
            Your search found ${hits?.totalHits} records
          </p>
        </g:if>
      </div></div>


      <div class="container-fluid">
        <div class="facetFilter col-lg-3">
          <g:each in="${facets}" var="facet">
            <div class="panel panel-default">
              <div class="panel-heading">
                <h3 class="panel-title">${facet.key}</h3>
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
                        <g:link controller="home" action="search" params="${addFacet(params,facet.key,v.term)}">${v.display}</g:link> (${v.count})
                      </g:else>
                    </li>
                  </g:each>
                </ul>
              </div>
            </div>
          </g:each>
        </div>
        <div class="col-lg-9">
        </div>
      </div>
    </div>
  </body>
</html>

%{-- This is not working for some reason, cant figure how to make it work.
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
%> --}

      <p>
          <g:each in="${['type','endYear','startYear','consortiaName','cpname']}" var="facet">
            <g:each in="${params.list(facet)}" var="fv">
              <span class="badge alert-info">${facet}:${fv} &nbsp; <g:link controller="packageDetails" action="index" params="${removeFacet(params,facet,fv)}"><i class="icon-remove icon-white"></i></g:link></span>
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
                <g:link controller="${controller}" action="${action}" params="${addFacet(params,facet.key,v.term)}">${v.display}</g:link> (${v.count})
              </g:else>
            </li>
          </g:each>
        </ul>
      </div>
    </div>
  </g:each>
</div>
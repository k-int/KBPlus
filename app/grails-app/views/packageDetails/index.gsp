<!doctype html>
<%@ page import="java.text.SimpleDateFormat"%>
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

  def dateFormater = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ss.SSS'Z'")
%>

<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Packages</title>
  </head>

  <body>


    <div class="container">
      <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="index">All Packages</g:link></li>
      </ul>
    </div>



    <div class="container">
      <g:form action="index" method="get" params="${params}">
      <input type="hidden" name="offset" value="${params.offset}"/>

      <div class="row">
        <div class="span12">
          <div class="well form-horizontal">
            Package Name: <input name="q" placeholder="Add &quot;&quot; for exact match" value="${params.q}"/>
            Sort: <select name="sort">
                    <option ${params.sort=='sortname' ? 'selected' : ''} value="sortname">Package Name</option>
                    <option ${params.sort=='_score' ? 'selected' : ''} value="_score">Score</option>
                    <option ${params.sort=='lastModified' ? 'selected' : ''} value="lastModified">Last Modified</option>
                  </select>
            Order: <select name="order" value="${params.order}">
                    <option ${params.order=='asc' ? 'selected' : ''} value="asc">Ascending</option>
                    <option ${params.order=='desc' ? 'selected' : ''} value="desc">Descending</option>
                  </select>
            <button type="submit" name="search" value="yes">Search</button>
          </div>
        </div>
      </div>
      </g:form>

      <p>
          <g:each in="${['type','endYear','startYear','consortiaName','cpname']}" var="facet">
            <g:each in="${params.list(facet)}" var="fv">
              <span class="badge alert-info">${facet}:${fv} &nbsp; <g:link controller="packageDetails" action="index" params="${removeFacet(params,facet,fv)}"><i class="icon-remove icon-white"></i></g:link></span>
            </g:each>
          </g:each>
        </p>

      <div class="row">

  
        <div class="facetFilter span2">
          <g:each in="${facets}" var="facet">
            <g:if test="${facet.key != 'type'}">
            <div class="panel panel-default">
              <div class="panel-heading">
                <h5><g:message code="facet.so.${facet.key}" default="${facet.key}" /></h5>
              </div>
              <div class="panel-body">
                <ul>
                  <g:each in="${facet.value.sort{it.display}}" var="v">
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
            </g:if>
          </g:each>
        </div>


        <div class="span10">
          <div class="well">
             <g:if test="${hits}" >
                <div class="paginateButtons" style="text-align:center">
                    <g:if test=" ${params.int('offset')}">
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
                      <tr><th>Package Name</th><th>Consortium</th>
                          <th>Start Date</th>
                          <th>End Date</th>
                          <th>Last Modified</th></tr>
                    </thead>
                    <tbody>
                      <g:each in="${hits}" var="hit">
                        <tr>
                          <td><g:link controller="packageDetails" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link>
                              <!--(${hit.score})-->
                              <span>(${hit.source.titleCount?:'Unknown number of'} titles)</span>
                              </td>
                          <td>${hit.source.consortiaName}</td>
                          <td>
                          <g:formatDate formatName="default.date.format.notime" date='${hit.source.startDate?dateFormater.parse(hit.source.startDate):null}'/>
                          </td>
                          <td>
                          <g:formatDate formatName="default.date.format.notime" date='${hit.source.endDate?
                            dateFormater.parse(hit.source.endDate):null}'/>
                          </td>
                          <td>${hit.source.lastModified}</td>
                        </tr>
                      </g:each>
                    </tbody>
                  </table>
                </div>
                <div class="paginateButtons" style="text-align:center">
                  <span><g:paginate controller="packageDetails" action="index" params="${params}" next="Next" prev="Prev" total="${hits.totalHits}" /></span>
            </g:if>
          </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>

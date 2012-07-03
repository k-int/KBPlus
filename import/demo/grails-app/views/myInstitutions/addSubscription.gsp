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

      <h2>${institution?.name} - Subscriptions</h2>
      <hr/>

      <ul class="nav nav-pills">
        <li><g:link controller="myInstitutions" 
                                   action="currentSubscriptions" 
                                   params="${[shortcode:params.shortcode]}">Current Subscriptions</g:link></li>
        <li class="active"><g:link controller="myInstitutions" 
                               action="addSubscription" 
                               params="${[shortcode:params.shortcode]}">Subscriptions Offered / Add New</g:link></li>
      </ul>
    </div>


    <div class="container" style="text-align:center">
      <g:form action="addSubscription" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="get">
        Search Text: <input type="text" class="search-query" placeholder="Search" name="q" value="${params.q?.encodeAsHTML()}">
      </g:form>
    </div>

    <div class="row-fluid">
      <div class="span2">
        &nbsp;
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
  
            <table class="table table-striped table-bordered table-condensed" >
              <tr><th></th><th>Subscription Offered</th></tr>
              <g:each in="${hits}" var="hit">
                <tr>
                  <td><input type="radio" name="select"/></td>
                  <td>
                    ${hit.source.name} (${hit.source.type})
                  </td>
                </tr>
              </g:each>
            </table>

         <div class="paginateButtons" style="text-align:center">
           New subscription name: <input type="text" name="newSubName"> <button>Create Subscription -></button>
         </div>
        </g:if>
  
        <div class="paginateButtons" style="text-align:center">
          <g:if test="${hits}" >
            <span><g:paginate  action="addSubscription" controller="myInstitutions" params="${params}" next="Next" prev="Prev" maxsteps="10" total="${hits.totalHits}" /></span>
          </g:if>
        </div>
      </div>

      <div class="span2">
      </div>

    </div>
  </body>
</html>

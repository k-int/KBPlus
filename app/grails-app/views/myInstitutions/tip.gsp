<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: ${tip?.title?.title} via ${tip?.provider?.name}</title>
  </head>

  <body>
    <div class="container">

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link>
          <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="dashboard" params="${[shortcode:params.shortcode]}">${institution.name} - Dashboard</g:link>
          <span class="divider">/</span>  </li>
        <li> <g:link controller="myInstitutions" action="tipview" params="${[shortcode:params.shortcode]}"> Titles </g:link>
          <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="tip" params="${[shortcode:params.shortcode, id:id]}"> ${tip?.title?.title} via ${tip?.provider?.name} </g:link>
           </li>

      </ul>
    </div>

    </div>
      <div class="container">


      <g:if test="${flash.message}">
      <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>

        <g:if test="${flash.error}">
        <bootstrap:alert class="alert alert-error">${flash.error}</bootstrap:alert>
      </g:if>

      <div class="row">
        <div class="span12">
          <h3>Core dates</h3>
          <ul>
            <g:each in="${tip.coreDates}" var="cd">
              <li>${cd}</li>
            </g:each>
          </ul>
          <g:if test="${tip.coreDates == null || tip.coreDates.size() == 0}">
            No Core Dates Currently
          </g:if>

          <h3>Usage Records</h3>
          <g:if test="${usage && usage.size() > 0 }">
            <ul>
              <g:each in="${usage}" var="u">
                <li>
                  usage:${u}
                </li>
              </g:each>
            </ul>
          </g:if>
          <g:else>
            No usage currently
          </g:else>

          <h4>Add usage information</h4>
          <g:form action="tip" params="${[shortcode:params.shortcode]}" id="${params.id}">
            Usage Date : <input type="date" name="usageDate"/><br/>
            Usage Record : <input type="text" name="usageValue"/><br/>
            <button type="submit">Add Usage</button>
          </g:form>
        </div>
      </div>


  </body>
</html>

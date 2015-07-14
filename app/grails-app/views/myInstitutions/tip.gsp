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
          <table class="table table-bordered">
          <thead>
            <tr>
              <th>Start</th>
              <th>End</th>
              <th>Reporting Year</th>
              <th>Reporting Month</th>
              <th>Type</th>
              <th>Value</th>
            </tr>
          </thead>
          <tbody>
              <g:if test="${usage && usage.size() > 0 }">
                <g:each in="${usage}" var="u">
                  <tr>
                    <td>${u.factFrom}</td>
                    <td>${u.factTo}</td>
                    <td>${u.reportingYear}</td>
                    <td>${u.reportingMonth}</td>
                    <td>${u.factType?.value}</td>
                    <td>${u.factValue}</td>
                  </tr>
                </g:each>
              </g:if>
              <g:else>
                <tr><td colspan="6">No usage currently</td></tr>
              </g:else>
            </tbody>
          </table>

          <h4>Add usage information</h4>
          <g:form action="tip" params="${[shortcode:params.shortcode]}" id="${params.id}">
            Usage Date : <input type="date" name="usageDate"/><br/>
            Usage Record : <input type="text" name="usageValue"/><br/>
            Usage Type :
            <g:select name='factType'
    from='${com.k_int.kbplus.RefdataValue.executeQuery('select o from RefdataValue as o where o.owner.desc=?',['FactType'])}'
    optionKey="id" optionValue="value"></g:select><br/>

            <button type="submit">Add Usage</button>
          </g:form>
        </div>
      </div>


  </body>
</html>

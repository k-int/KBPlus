<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data Manager Change Log</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="dataManager" action="index">Data Manager Change Log</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="dataManager" action="changeLog">DM Change log</g:link> </li>

        <li class="dropdown pull-right">
          <a class="dropdown-toggle badge" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">Exports<b class="caret"></b></a>
          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
            <li><g:link controller="dataManager" action="changeLog" params="${params+[format:'csv']}">CSV Export</g:link></li>
          </ul>
        </li>

      </ul>
    </div>

    <g:if test="${flash.message}">
      <div class="container">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </div>
    </g:if>

    <g:if test="${flash.error}">
      <div class="container">
        <bootstrap:alert class="error-info">${flash.error}</bootstrap:alert>
      </div>
    </g:if>

    <div class="container">
      <h2>Data Manager Change Log</h2>
      <h6>Change Log <span class="pull-right">${num_hl} changes</span></h6>
      <g:form action="changeLog" controller="dataManager" method="get">
        From Date: <input name="startDate" type="date" value="${params.startDate}"/>
        To Date: <input name="endDate" type="date" value="${params.endDate}"/>
        <div class="dropdown">
        Actor : 
        <a class="dropdown-toggle btn" data-toggle="dropdown" href="#">
            Select Actors
            <b class="caret"></b>
        </a>
        <ul class="dropdown-menu dropdown-checkboxes" role="menu">
            <li>
                <label class="checkbox">
                    <input type="checkbox" name="change_actor_PEOPLE" value="Y"
                    ${params.change_actor_PEOPLE == "Y" ? 'checked' : ''} >
                    ALL (Real Users)
                </label>
            </li>
            <li>
                <label class="checkbox">
                    <input type="checkbox" name="change_actor_ALL" value="Y"
                    ${params.change_actor_ALL == "Y" ? 'checked' : ''} >
                    ALL (Including system)
                </label>
            </li>
            <g:each in="${actors}" var="a">

               <li>
                  <label class="checkbox">
                      <input type="checkbox" name="change_actor_${a[0]}" value="Y"
                        ${params."change_actor_${a[0]}" == "Y" ? 'checked' : ''} >
                        ${a[1]}
                  </label>                
              </li>
            </g:each>
        </ul>
      </div>

        <br/>

        Whats Changed :
        <input type="checkbox" name="packages" value="Y" ${params.packages=='Y'?'checked':''}/> Packages &nbsp;
        <input type="checkbox" name="licenses" value="Y" ${params.licenses=='Y'?'checked':''}/> Licenses &nbsp;
        <input type="checkbox" name="titles" value="Y" ${params.titles=='Y'?'checked':''}/> Titles &nbsp;
        <input type="checkbox" name="tipps" value="Y" ${params.tipps=='Y'?'checked':''}/> TIPPs &nbsp; <br/>
        How has it changed :
        <input type="checkbox" name="creates" value="Y" ${params.creates=='Y'?'checked':''}/> New Items &nbsp;
        <input type="checkbox" name="updates" value="Y" ${params.updates=='Y'?'checked':''}/> Updates to existing items&nbsp;
        <input type="submit"/>
      </g:form>
    </div>

    <g:set var="counter" value="${offset+1}" />

    <g:if test="${formattedHistoryLines?.size() > 0}">

      <div class="container alert-warn">
        <table class="table table-bordered">
          <thead>
            <tr>
              <td></td>
              <td>Name</td>
              <td>Actor</td>
              <td>Event name</td>
              <td>Property</td>
              <td>Old</td>
              <td>New</td>
              <td>date</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${formattedHistoryLines}" var="hl">
              <tr>
                <td>${counter++}</td>
                <td><a href="${hl.link}">${hl.name}</a></td>
                <td>
                  <g:link controller="userDetails" action="edit" id="${hl.actor?.id}">${hl.actor?.displayName}</g:link>
                </td>
                <td>${hl.eventName}</td>
                <td>${hl.propertyName}</td>
                <td>${hl.oldValue}</td>
                <td>${hl.newValue}</td>
                <td><g:formatDate format="yyyy-MM-dd" date="${hl.lastUpdated}"/></td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>


      <div class="pagination" style="text-align:center">
        <g:if test="${historyLines != null}" >
          <bootstrap:paginate  action="changeLog" controller="dataManager" params="${params}" next="Next" prev="Prev" maxsteps="${max}" total="${num_hl}" />
        </g:if>
      </div>

    </g:if>
    <g:else>
      <div class="container alert-warn">
      </div>
    </g:else>
    <r:script language="JavaScript">
      $('.dropdown-menu').on('click', function(e) {
      if($(this).hasClass('dropdown-checkboxes')) {
          e.stopPropagation();
      }});
  </r:script>
  </body>
</html>

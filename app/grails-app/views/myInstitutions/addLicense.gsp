<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>

    <div class="container">
        <ul class="breadcrumb">
            <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
           <li> <g:link controller="myInstitutions" action="addLicense" params="${[shortcode:params.shortcode]}">${institution.name} Copy from Template</g:link> </li>
        </ul>
    </div>

    <div class="container">
      <h1>${institution?.name} - Licences</h1>
      <ul class="nav nav-pills">
       <li><g:link controller="myInstitutions" 
                   action="currentLicenses" 
                   params="${[shortcode:params.shortcode]}">Current Licences</g:link></li>

       <li class="active"><g:link controller="myInstitutions" 
                                  action="addLicense" 
                                  params="${[shortcode:params.shortcode]}">Copy from Template</g:link></li>

        <g:if test="${is_admin}">
          <li><g:link controller="myInstitutions" 
                                     action="cleanLicense" 
                                     params="${[shortcode:params.shortcode]}">Add Blank License</g:link></li>
        </g:if>
      </ul>

    </div>

    <div class="container licence-searches">
        <div class="row">
            <div class="span6">&nbsp;
                <!--
                <input type="text" name="keyword-search" placeholder="enter search term..." />
                <input type="submit" class="btn btn-primary" value="Search" />
                -->
            </div>
            <div class="span6">
            </div>
        </div>
    </div>

      <div class="container">
        <g:form action="addLicense" params="${params}" method="get" class="form-inline">
          <input type="hidden" name="sort" value="${params.sort}">
          <input type="hidden" name="order" value="${params.order}">
          <label>Filters - License Name:</label> <input name="filter" value="${params.filter}"/> &nbsp;
          <input type="submit" class="btn btn-primary">
        </g:form>
      </div>

      <div class="container">
          <div class="well licence-options">
            <g:if test="${is_admin}">
              <input type="submit" name="copy-licence" value="Copy Selected" class="btn btn-warning" />
            </g:if>
            <g:else>Sorry, you must have editor role to be able to add licences</g:else>
          </div>
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


      <g:if test="${licenses?.size() > 0}">
        <div class="container licence-results">
          <table class="table table-bordered table-striped">
            <thead>
              <tr>
                <g:sortableColumn params="${params}" property="reference" title="License Name" />
                <th>Licensor</th>
                <g:sortableColumn params="${params}" property="startDate" title="Start Date" />
                <g:sortableColumn params="${params}" property="endDate" title="End Date" />
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${licenses}" var="l">
                <tr>
                  <td><g:link action="index"
                              controller="licenseDetails" 
                              id="${l.id}">${l.reference?:"License ${l.id} - no reference set"}</g:link>
                    <g:if test="${l.subscriptions && ( l.subscriptions.size() > 0 )}">
                      <ul>
                        <g:each in="${l.subscriptions}" var="sub">
                          <li><g:link controller="subscriptionDetails" action="index" id="${sub.id}">${sub.id} (${sub.name})</g:link><br/></li>
                        </g:each>
                      </ul>
                    </g:if>
                    <g:else>
                      <br/>No linked packages.
                    </g:else>
                  </td>
                  <td>${l.licensor?.name}</td>
                  <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${l.startDate}"/></td>
                  <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${l.endDate}"/></td>
                  <td><g:link controller="myInstitutions" action="actionLicenses" params="${[shortcode:params.shortcode,baselicense:l.id,'copy-licence':'Y']}" class="btn btn-success">Copy</g:link></td>
                </tr>
              </g:each>
            </tbody>
          </table>

          <div class="pagination" style="text-align:center">
            <g:if test="${licenses}" >
              <bootstrap:paginate  action="addLicense" controller="myInstitutions" params="${params}" next="Next" prev="Prev" max="${max}" total="${numLicenses}" />
            </g:if>
          </div>
        </div>

      </g:if>

    <r:script type="text/javascript">
        $('.licence-results input[type="radio"]').click(function () {
            $('.licence-options').slideDown('fast');
        });

        $('.licence-options .delete-licence').click(function () {
            $('.licence-results input:checked').each(function () {
                $(this).parent().parent().fadeOut('slow');
                $('.licence-options').slideUp('fast');
            })
        })
    </r:script>

  </body>
</html>

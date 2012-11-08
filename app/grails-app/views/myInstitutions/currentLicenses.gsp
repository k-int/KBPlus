<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>

    <div class="container">
        <ul class="breadcrumb">
            <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
           <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:params.shortcode]}">${institution.name} Current Licenses</g:link> </li>
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
      <h1>${institution?.name} - Licences</h1>

     <ul class="nav nav-pills">
       <li class="active"><g:link controller="myInstitutions" 
                                  action="currentLicenses" 
                                  params="${[shortcode:params.shortcode]}">Current Licences</g:link></li>

        <li><g:link controller="myInstitutions" 
                                action="addLicense" 
                                params="${[shortcode:params.shortcode]}">Add Licence</g:link></li>
      </ul>

    </div>

    <div class="container licence-searches">
        <div class="row">
            <div class="span6">
                <input type="text" name="keyword-search" placeholder="enter search term..." />
                <input type="submit" class="btn btn-primary" value="Search" />
            </div>
            <div class="span6">
                <div class="pull-right">
                    <g:link controller="myInstitutions" action="cleanLicense" params="${[shortcode:params.shortcode]}" class="btn btn-primary">Create New Licence</g:link>
                </div>
            </div>
        </div>
    </div>

    <g:form action="actionLicenses"
            controller="myInstitutions" 
            params="${[shortcode:params.shortcode]}">

      <div class="container">
          <div class="well licence-options">
              <input type="submit" name="delete-licence" value="Delete Selected" class="btn btn-danger delete-licence" />
          </div>
      </div>

      <g:if test="${licenses?.size() > 0}">
        <div class="container licence-results">
          <table class="table table-bordered">
            <thead>
              <tr>
                <td>Select</td>
                <g:sortableColumn params="${params}" property="reference" title="Reference Description" />
                <td>Licensor</td>
                <td>Licensee</td>
                <g:sortableColumn params="${params}" property="status.value" title="Status" />
                <g:sortableColumn params="${params}" property="type.value" title="Type" />
              </tr>
            </thead>
            <tbody>
              <g:each in="${licenses}" var="l">
                <tr>
                  <td><input type="radio" name="baselicense" value="${l.id}"/></td>
                  <td><g:link action="index"
                              controller="licenseDetails" 
                              id="${l.id}">${l.reference?:message(code:'missingLicenseReference', default:'** No Licence Reference Set **')}</g:link></td>
                  <td>${l.licensor?.name}</td>
                  <td>${l.licensee?.name}</td>
                  <td>${l.status?.value}</td>
                  <td>${l.type?.value}</td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
      </g:if>
    </g:form>
    <script type="text/javascript">
        $('.licence-results input[type="radio"]').click(function () {
            $('.licence-options').slideDown('fast');
        });

        $('.licence-options .delete-licence').click(function () {
            $('.licence-results input:checked').each(function () {
                $(this).parent().parent().fadeOut('slow');
                $('.licence-options').slideUp('fast');
            })
        })
    </script>


  </body>
</html>

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
        <li> <g:link controller="myInstitutions" action="instdash" params="${[shortcode:params.shortcode]}">${institution.name} Dashboard</g:link> </li>
      </ul>
    </div>


    <div class="container home-page">
      <h1>${institution.name} Dashboard</h1>
      <ul class="inline">
        <li><g:link controller="myInstitutions" 
                                       action="currentLicenses" 
                                       params="${[shortcode:params.shortcode]}">Licences</g:link></li>
        <li><g:link controller="myInstitutions" 
                                       action="currentSubscriptions" 
                                       params="${[shortcode:params.shortcode]}">Subscriptions</g:link></li>
        <li><g:link controller="myInstitutions" 
                                       action="currentTitles" 
                                       params="${[shortcode:params.shortcode]}">Titles</g:link></li>
        <li><g:link controller="myInstitutions" 
                                       action="renewalsSearch" 
                                       params="${[shortcode:params.shortcode]}">Generate Renewals Worksheet</g:link></li>
        <li><g:link controller="myInstitutions" 
                                       action="renewalsUpload" 
                                       params="${[shortcode:params.shortcode]}">Import Renewals</g:link></li>
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

    <div class="container home-page">
      <div class="row">
        <div class="span4">
          <div class="well">
            <h6>ToDo</h6>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Announcements</h6>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Latest Discussions</h6>
          </div>
        </div>
      </div>
    </div>

  </body>
</html>

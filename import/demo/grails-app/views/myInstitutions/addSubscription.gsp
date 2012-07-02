<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
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
  </body>
</html>

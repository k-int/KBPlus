<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Subscription</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Notes</g:link> </li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

   <div class="container">

      ${institution?.name} ${subscriptionInstance?.type?.value} Subscription Taken

       <h1>${subscriptionInstance?.name}</h1>

       <g:render template="nav" />

    </div>

    <div class="container">
        <g:render template="/templates/notes_table" model="${[instance: subscriptionInstance, redirect: 'notes']}"/>
    </div>
  <g:render template="/templates/addNote"
            model="${[doclist: subscriptionInstance.documents, ownobj: subscriptionInstance, owntp: 'subscription']}"/>
    
  </body>
</html>

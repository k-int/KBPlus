<%@ page import="com.k_int.kbplus.Subscription" %>
<r:require module="annotations" />

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
            <g:if test="${subscription.subscriber}">
                <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscription.subscriber.shortcode]}"> ${subscription.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
            </g:if>
            <li> <g:link controller="subscriptionDetails" action="index" id="${subscription.id}">Subscription ${subscription.id} Details</g:link> <span class="divider">/</span> </li>
            <li> <g:link controller="subscriptionDetails" action="costPerUse" id="${subscription.id}">Subscription ${subscription.id} Cost Per Use</g:link> </li>

        </div>

    <g:if test="${flash.message}">
        <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
    </g:if>

    <g:if test="${flash.error}">
        <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
    </g:if>

    <div class="container">
        <h1>Cost Per Use :: ${subscription.name}</h1>
        <g:render template="nav"  />
    </div>

    <div class="container">
    </div>

  </body>
</html>

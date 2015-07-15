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
        </ul>
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
      <g:if test="${costItems && costItems.size() > 0}">
        <table class="table table-bordered">
          <thead>
            <tr>
              <th>Invoice Number</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Invoice Total</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${costItems}" var="ci">
              <tr>
                <td>${ci.invoice.invoiceNumber}</td>
                <td><g:formatDate date="${ci.invoice.startDate}" format="yyyy-MM-dd"/></td>
                <td><g:formatDate date="${ci.invoice.endDate}" format="yyyy-MM-dd"/></td>
                <td><span class="pull-right">${ci.total}</span></td>
              </tr>
              <tr>
                <td colspan="4">Total usage for this invoice period: ${ci.total_usage_for_sub} gives an overall cost per use of 
                       <strong><g:formatNumber number="${ci.overall_cost_per_use}" format="#,###,###.##" /></strong></td>
              </tr>
              <g:each in="${ci.usage}" var="u">
                <tr>
                  <td colspan="3"><span class="pull-right">Apportionment for usage period ${u[0]}/${u[1]}</span></td>
                  <td><span class="pull-right">${u[2]} @ <g:formatNumber number="${ci.overall_cost_per_use}" format="#,###,###.##" />
                       = <g:formatNumber number="${ci.overall_cost_per_use * Integer.parseInt(u[2])}" format="#,###,###.##" /></span></td>

                </tr>
              </g:each>
            </g:each>
          </tbody>
        </table>
      </g:if>
      <g:else>
        Unable to locate any invoices against this subscription
      </g:else>
    </div>

  </body>
</html>

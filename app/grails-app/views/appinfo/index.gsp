<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ App Info</title>
  </head>

  <body>
    <div class="container">
      <h1>Application Info</h1>
      <table class="table table-bordered">
        <tr><td>Build Number</td><td> <g:meta name="app.buildNumber"/></td></tr>
        <tr><td>Build Profile</td><td> <g:meta name="app.buildProfile"/></td></tr>
        <tr><td>App version</td><td> <g:meta name="app.version"/></td></tr>
        <tr><td>Grails version</td><td> <g:meta name="app.grails.version"/></td></tr>
        <tr><td>Groovy version</td><td> ${GroovySystem.getVersion()}</td></tr>
        <tr><td>JVM version</td><td> ${System.getProperty('java.version')}</td></tr>
        <tr><td>Reloading active</td><td> ${grails.util.Environment.reloadingAgentEnabled}</td></tr>
        <tr><td>Build Date</td><td> <g:meta name="app.buildDate"/></td></tr>
      </table>
  
  
      <h1>Background task status</h1>
      <h2>JUSP Sync Service</h2>
      <table class="table table-bordered">
        <tr><td>Currently Running</td><td>${juspSyncService.running}</td></tr>
        <tr><td>Completed Count</td><td>${juspSyncService.completedCount}</td></tr>
        <tr><td>New Fact Count</td><td>${juspSyncService.newFactCount}</td></tr>
        <tr><td>Total Time (All Threads)</td><td>${juspSyncService.totalTime} (ms)</td></tr>
        <tr><td>Total Time Elapsed</td><td>${juspSyncService.syncElapsed} (ms)</td></tr>
        <tr><td>Thread Pool Size</td><td>${juspSyncService.threads}</td></tr>
        <tr><td>Last Start Time</td>
            <td>
              <g:if test="${juspSyncService.syncStartTime != 0}">
                <g:formatDate date="${new Date(juspSyncService.syncStartTime)}" format="yyyy-MM-dd hh:mm"/>
              </g:if>
              <g:else>
                Not started yet
              </g:else>
          </tr>
        <tr><td>Initial Query Time</td><td>${juspSyncService.queryTime} (ms)</td></tr>

        <g:if test="${((juspSyncService.completedCount != 0) && (juspSyncService.totalTime != 0))}">
          <tr><td>Average Time Per JUSP Triple (Current/Last Run)</td><td>${juspSyncService.totalTime/juspSyncService.completedCount} (ms)</td></tr>
        </g:if>
        <tr><td>Activity Histogram</td>
            <td>
              <g:each in="${juspSyncService.activityHistogram}" var="ah">
                ${ah.key}:${ah.value}<br/>
              </g:each>
            </td></tr>
      </table>

      <ul>
        <g:each in="${request.getAttributeNames()}" var="an">
          <li>${an} = ${request.getAttribute(an)}</li>
        </g:each>
        <ul> authenticationMethodObject = ${request.getAttribute('Shib-Authentication-Method')}</ul>
        <ul> identityProviderObject = ${request.getAttribute('Shib-Identity-Provider')}</ul>
        <ul> principalUsernameObject = (${grailsApplication.config.grails.plugins.springsecurity.shibboleth.principalUsername.attribute}) 
                                       ${request.getAttribute(grailsApplication.config.grails.plugins.springsecurity.shibboleth.principalUsername.attribute)}</ul>
        <ul> authenticationInstantObject = ${request.getAttribute('Shib-Authentication-Instant')}</ul>
        <ul> usernameObject = (EPPN) ${request.getAttribute('EPPN')}</ul>
        <ul> eduPersonPrincipalName = ${request.getAttribute('eduPersonPrincipalName')}</ul>
        <ul> eduPersonScopedAffiliation = ${request.getAttribute('eduPersonScopedAffiliation')}</ul>
        <ul> eduPersonPrincipalName = ${request.getAttribute('eduPersonPrincipalName')}</ul>
        <ul> eduPersonEntitlement = ${request.getAttribute('eduPersonEntitlement')}</ul>
        <ul> uid = ${request.getAttribute('uid')}</ul>
        <ul> mail = ${request.getAttribute('mail')}</ul>
        <ul> affiliation = ${request.getAttribute('affiliation')}</ul>
        <ul> entitlement = ${request.getAttribute('entitlement')}</ul>
        <ul> persistent-id = ${request.getAttribute('persistent-id')}</ul>
      </ul>
    </div>
  </body>
</html>

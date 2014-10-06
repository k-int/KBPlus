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
        <tr><td>Last Quartz Heartbeat</td><td>${grailsApplication.config.quartzHeartbeat}</td></tr>
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
        <li> authenticationMethodObject = ${request.getAttribute('Shib-Authentication-Method')}</li>
        <li> identityProviderObject = ${request.getAttribute('Shib-Identity-Provider')}</li>
        <li> principalUsernameObject = (${grailsApplication.config.grails.plugins.springsecurity.shibboleth.principalUsername.attribute}) 
                                       ${request.getAttribute(grailsApplication.config.grails.plugins.springsecurity.shibboleth.principalUsername.attribute)}</li>
        <li> authenticationInstantObject = ${request.getAttribute('Shib-Authentication-Instant')}</li>
        <li> usernameObject = (EPPN) ${request.getAttribute('EPPN')}</li>
        <li> eduPersonPrincipalName = ${request.getAttribute('eduPersonPrincipalName')}</li>
        <li> eduPersonScopedAffiliation = ${request.getAttribute('eduPersonScopedAffiliation')}</li>
        <li> eduPersonPrincipalName = ${request.getAttribute('eduPersonPrincipalName')}</li>
        <li> eduPersonEntitlement = ${request.getAttribute('eduPersonEntitlement')}</li>
        <li> uid = ${request.getAttribute('uid')}</li>
        <li> mail = ${request.getAttribute('mail')}</li>
        <li> affiliation = ${request.getAttribute('affiliation')}</li>
        <li> entitlement = ${request.getAttribute('entitlement')}</li>
        <li> persistent-id = ${request.getAttribute('persistent-id')}</li>
        <li> authInstitutionName = ${request.getAttribute('authInstitutionName')}</li>
        <li> eduPersonTargetedID = ${request.getAttribute('eduPersonTargetedID')}</li>
        <li> authInstitutionAddress = ${request.getAttribute('authInstitutionAddress')}</li>
        <li> targeted-id = ${request.getAttribute('targeted-id')}</li>
        <li> uid = ${request.getAttribute('uid')}</li>
        <li> REMOTE_USER = ${request.getAttribute('REMOTE_USER')}</li>
        <li> REMOTE_USER (fm) = ${request.getRemoteUser()}</li>
      </ul>
    </div>
  </body>
</html>

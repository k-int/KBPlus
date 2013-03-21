<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="container">

      <g:if test="${staticAlerts.size() > 0}">
        <table class="table table-bordered">
          <tr><th>System Alert</th></tr>

          <g:each in="${staticAlerts}" var="sa">
            <tr>
              <td>
                <g:if test="${sa.controller}">
                  <g:link controller="${sa.controller}" action="${sa.action}">${message(code:sa.message)}</g:link>
                </g:if>
                <g:else>
                  ${message(sa.message)}
                </g:else>
              </td>
            </tr>
          </g:each>
        </table>
      </g:if>

      <table class="table table-bordered">
        <tr>
          <th colspan="3">Note attached to</th>
        </tr>
        <tr>
          <th>Note</th>
        </tr>
        <g:each in="${userAlerts}" var="ua">
          <tr>
            <td colspan="3">
            <g:if test="${ua.rootObj.class.name=='com.k_int.kbplus.License'}">
              <span class="label label-info">License</span>
              <em><g:link action="index"
                      controller="licenseDetails" 
                      id="${ua.rootObj.id}">${ua.rootObj.reference}</g:link></em>
            </g:if>
            <g:elseif test="${ua.rootObj.class.name=='com.k_int.kbplus.Subscription'}">
             <span class="label label-info">Subscription</span>
              <em><g:link action="index"
                      controller="subscriptionDetails" 
                      id="${ua.rootObj.id}">${ua.rootObj.name}</g:link></em>
            </g:elseif>
            <g:else>
              Unhandled object type attached to alert: ${ua.rootObj.class.name}:${ua.rootObj.id}
            </g:else>
            </td>
          </tr>
          <g:each in="${ua.notes}" var="n">
            <tr>
              <td>
                  ${n.owner.content}<br/>
                  <div class="pull-right"><i>${n.owner.type?.value} (
                    <g:if test="${n.alert.sharingLevel==2}">Shared with KB+ Community</g:if>
                    <g:elseif test="${n.alert.sharingLevel==1}">JC Only</g:elseif>
                    <g:else>Private</g:else>
) By ${n.owner.user?.displayName} on <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${n.alert.createTime}" /></i></div>
              </td>
            </tr>
          </g:each>
        </g:each>
      </table>
    </div>
  </body>
</html>

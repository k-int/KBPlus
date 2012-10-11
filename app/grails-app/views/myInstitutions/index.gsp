<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="container">
      <table class="table table-bordered">
        <tr>
          <th colspan="3">Note attached to</th>
        </tr>
        <tr>
          <th>Date</th>
          <th>Sharing</th>
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
              <td><g:formatDate format="dd MMMM yyyy" date="${n.alert.createTime}" /></td>
              <td>
                <g:if test="${n.alert.sharingLevel==2}">- Shared with KB+ Community -</g:if>
                <g:elseif test="${n.alert.sharingLevel==1}">- JC Only -</g:elseif>
                <g:else>- Private -</g:else>
              </td>
              <td>
                  ${n.owner.content}
              </td>
            </tr>
          </g:each>
        </g:each>
      </table>
    </div>
  </body>
</html>

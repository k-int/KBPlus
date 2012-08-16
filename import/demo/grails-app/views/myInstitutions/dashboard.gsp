<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>

    <div class="container home-page">
      <div class="row">
        <div class="span4">
          <div class="well">
            <h6>Licences</h6>
            <dl>
              <g:each in="${user.authorizedAffiliations}" var="ua">
                <dd><g:link controller="myInstitutions" 
                                     action="currentLicenses" 
                                     params="${[shortcode:ua.org.shortcode]}">${ua.org.name}</g:link></dd>
              </g:each>
            </dl>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Subscriptions</h6>
            <dl>
              <g:each in="${user.authorizedAffiliations}" var="ua">
                <dd><g:link controller="myInstitutions" 
                                     action="currentSubscriptions" 
                                     params="${[shortcode:ua.org.shortcode]}">${ua.org.name}</g:link></dd>
              </g:each>
            </dl>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Reports</h6>
            <dl>
              <dd><a href="#">View reports</a><dd>
            </dl>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <table class="table table-bordered">
        <tr>
          <th colspan="5">Note attached to</th>
        </tr>
        <tr>
          <th>Type</th>
          <th>Date</th>
          <th>Author</th>
          <th>Sharing</th>
          <th>Note</th>
        </tr>
        <g:each in="${userAlerts}" var="ua">
          <tr>
            <td colspan="5">
              <g:if test="${ua.rootObj.class.name=='com.k_int.kbplus.License'}">
                <span class="label label-info">License</span>
                <em><g:link action="licenseDetails"
                        controller="myInstitutions" 
                        id="${ua.rootObj.id}"
                        params="${[shortcode:ua.rootObj.licensee.shortcode]}">${ua.rootObj.reference}</g:link></em>
              </g:if>
              <g:else>
                Unhandled object type attached to alert: ${ua.rootObj.class.name}:${ua.rootObj.id}
              </g:else>
            </td>
          </tr>
          <g:each in="${ua.notes}" var="n">
            <tr>
              <td>Type</td>
              <td><g:formatDate format="dd MMMM yyyy" date="${n.alert.createTime}" /></td>
              <td>Author</td>
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

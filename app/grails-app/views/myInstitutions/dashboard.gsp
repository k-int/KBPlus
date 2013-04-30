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
              <g:each in="${user.authorizedOrgs}" var="org">
                <dd><g:link controller="myInstitutions" 
                                     action="currentLicenses" 
                                     params="${[shortcode:org?.shortcode]}">${org.name}</g:link></dd>
              </g:each>
            </dl>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Subscriptions</h6>
            <dl>
              <g:each in="${user.authorizedOrgs}" var="org">
                <dd><g:link controller="myInstitutions" 
                                     action="currentSubscriptions" 
                                     params="${[shortcode:org?.shortcode]}">${org.name}</g:link></dd>
              </g:each>
            </dl>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Reports</h6>
            <dl>
              <dd>Currently unavailable<dd>
            </dl>
          </div>
        </div>
      </div>
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

    <g:if test="${staticAlerts.size() > 0}">
      <div class="container">
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
      </div>
    </g:if>

    <div class="container">
      <table class="table table-bordered">
          <thead>
              <tr>
                  <th colspan="6">Alerted item</th>
              </tr>
          </thead>
        <tr class="no-background">
          <th>Note</th>
          <th>Comments</th>
        </tr>
        <g:each in="${userAlerts}" var="ua">
          <tr>
            <td colspan="2">
              <g:if test="${ua.rootObj.class.name=='com.k_int.kbplus.License'}">
                <span class="label label-info">Licence</span>
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
              <td>
                <input type="submit" 
                       class="btn btn-primary announce" 
                       value="${n.alert?.comments != null ? n.alert?.comments?.size() : 0} Comment(s)" 
                       data-id="${n.alert.id}" 
                       />
              </td>
            </tr>
          </g:each>
        </g:each>
      </table>
    </div>

    <!-- Lightbox modal for creating a note taken from licenceNotes.html -->
    <div class="modal hide fade" id="modalComments">
    </div>

    <script type="text/javascript">
      // http://stackoverflow.com/questions/10626885/passing-data-to-a-bootstrap-modal
      $(document).ready(function() {
         $(".announce").click(function(){ 
           var id = $(this).data('id');
           $('#modalComments').load('<g:createLink controller="alert" action="commentsFragment" />/'+id);
           $('#modalComments').modal('show');
         });
      });
    </script>
  </body>
</html>

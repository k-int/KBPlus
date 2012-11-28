<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Permissions</g:link> </li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

    <div class="container">

      <h1>${subscriptionInstance?.name} Permissions against Current User</h1>

      <ul class="nav nav-pills">
        <li><g:link controller="subscriptionDetails" 
                    action="index" 
                    params="${[id:params.id]}">Current Entitlements</g:link></li>

        <g:if test="${editable}">
          <li><g:link controller="subscriptionDetails" 
                      action="addEntitlements" 
                      params="${[id:params.id]}">Add Entitlements</g:link></li>
        </g:if>

        <li><g:link controller="subscriptionDetails" 
                    action="documents" 
                    params="${[id:params.id]}">Documents</g:link></li>

        <li><g:link controller="subscriptionDetails" 
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>

        <li class="active"><g:link controller="subscriptionDetails" 
                    action="additionalInfo" 
                    params="${[id:params.id]}">Additional Info</g:link></li>

      </ul>
    </div>


   <div class="container">

      <h2>The following organisations are granted the listed permissions from this licence</h2>
      <table  class="table table-striped table-bordered">
        <tr>
          <th>Organisation</th><th>Roles and Permissions</th>
        </tr>
        <g:each in="${subscriptionInstance.orgRelations}" var="ol">
          <tr>
            <td>${ol.org.name}</td>
            <td>
              Connected to this license through role ${ol.roleType.value}.<br/>
              This role grants the following permissions to members of that org whose membership role also includes the permission<br/>
              <ul>
                <g:each in="${ol.roleType?.sharedPermissions}" var="sp">
                  <li>${sp.perm.code}
                      <g:if test="${subscriptionInstance.checkPermissions(sp.perm.code,user)}">
                        [Granted]
                      </g:if>
                      <g:else>
                        [Not granted]
                      </g:else>

                  </li>
                </g:each>
              </ul>
            </td>
          </tr>
        </g:each>
      </table>

      <h2>Logged in user permissions</h2>
      <table  class="table table-striped table-bordered">
        <tr>
          <th>Affiliated via Role</th><th>Permissions</th>
        </tr>
        <g:each in="${user.affiliations}" var="ol">
          <g:if test="${((ol.status==1)||(ol.status==3))}">
            <tr>
              <td>Affiliated to ${ol.org?.name} with role <g:message code="cv.roles.${ol.formalRole?.authority}"/> (${ol.status})</td>
              <td>
                <ul>
                  <g:each in="${ol.formalRole.grantedPermissions}" var="gp">
                    <li>${gp.perm.code}</li>
                  </g:each>
                </ul>
              </td>
            </tr>
            <g:each in="${ol.org.outgoingCombos}" var="oc">
              <tr>
                <td> --&gt; This org is related to ${oc.toOrg.name} ( ${oc.type.value} )</td>
                <td>
                  <ul>
                    <g:each in="${oc.type.sharedPermissions}" var="gp">
                      <li>${gp.perm.code}</li>
                    </g:each>
                  </ul>
                </td>
              </tr>
            </g:each>
          </g:if>
        </g:each>
      </table>



    </div>
    
  </body>
</html>

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
            <li>Licences</li>
        </ul>
    </div>

    <div class="container">
        <h1>${license.licensee?.name} ${license.type?.value} Licence : <span id="reference" class="ipe" style="padding-top: 5px;">${license.reference}</span></h1>

        <ul class="nav nav-pills">
            <li><g:link controller="licenseDetails"
                        action="index"
                        params="${[id:params.id]}">License Details</g:link></li>

            <li><g:link controller="licenseDetails"
                        action="documents"
                        params="${[id:params.id]}">Documents</g:link></li>

            <li><g:link controller="licenseDetails"
                        action="links"
                        params="${[id:params.id]}">Links</g:link></li>

            <li><g:link controller="licenseDetails"
                        action="notes"
                        params="${[id:params.id]}">Notes</g:link></li>

            <li><g:link controller="licenseDetails" 
                        action="history" 
                        params="${[id:params.id]}">History</g:link></li>

            <li class="active"><g:link controller="additionalInfo" 
                        action="additionalInfo" 
                        params="${[id:params.id]}">Additional Information</g:link></li>

        </ul>

    </div>



    <div class="container">
      <h2>Permissions for user</h2>
      <table  class="table table-striped table-bordered">
      </table>

      <h2>The following organisations are granted the listed permissions from this licence</h2>
      <table  class="table table-striped table-bordered">
        <tr>
          <th>Organisation</th><th>Roles and Permissions</th>
        </tr>
        <g:each in="${license.orgLinks}" var="ol">
          <tr>
            <td>${ol.org.name}</td>
            <td>
              Connected to this license through role ${ol.roleType.value}.<br/>
              This role grants the following permissions to members of that org whose membership role also includes the permission<br/>
              <ul>
                <g:each in="${ol.roleType?.sharedPermissions}" var="sp">
                  <li>${sp.perm.code} 
                      <g:if test="${license.checkPermissions(sp.perm.code,user)}">
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
          <g:if test="${((ol.status==1) || (ol.status==3))}">
            <tr>
              <td>Affiliated to ${ol.org?.name} with role <g:message code="cv.roles.${ol.formalRole?.authority}"/></td>
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

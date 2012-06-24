<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>
    <div class="row-fluid">

      <h2>${institution?.name} - Licenses</h2>
      <hr/>
      <div class="well">
        <g:form action="newLicense"
                controller="myInstitutions" 
                params="${[shortcode:params.shortcode]}">

          <g:if test="${licenses?.size() > 0}">
            <p>
              <table class="table table-striped table-bordered table-condensed">
                <tr>
                  <th>Select</th>
                  <th>Reference Description</th>
                  <th>Licensor</th>
                  <th>Licensee</th>
                  <th>Type</th>
                </tr>
              <g:each in="${licenses}" var="l">
                <tr>
                  <td><input type="radio" name="baselicense" value="${l.id}"/></td>
                  <td><g:link action="licenseDetails"
                              controller="myInstitutions" 
                              id="${l.id}"
                              params="${[shortcode:params.shortcode]}">${l.reference}</g:link></td>
                  <td>${l.licensor?.name}</td>
                  <td>${l.licensee?.name}</td>
                  <td>${l.type?.value}</td>
                </tr>
              </g:each>
              </table>
      
            </p>
          </g:if>
          <p>
            <g:if test="${licenses?.size() > 0}">
              If you wish to add a new license for this organisation, please enter a new reference below and cick the create button. Select a license from the list above to copy values from the selected
              license into your new license<br/>
            </g:if>
            <g:else>
              There are no licenses currently attached to ${institution.name}. To create a new one, please enter a reference name for the new
              license and press the create new license button below.<br/>
            </g:else>
  
            New license reference name: <input type="text" name="new_license_ref_name"/><input type="submit" value="Create new license"/>
          </p>
        </g:form>
      </div>

    </div>
  </body>
</html>

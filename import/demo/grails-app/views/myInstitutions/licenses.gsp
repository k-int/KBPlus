<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>
    <div class="row-fluid">

      <g:if test="${licenses?.size() > 0}">
        <div class="well">
          <p>
            Table of current licenses for org safdk adfja slkdjfas kdjaslk; jlkasj fdl;kjasdfkljasdflkjasdlfkj as;ljf<br/>
  
            <table>
            <g:each in="${licenses}" var="or">
              <tr>
                <td>${or.lic.reference}</td>
                <td>${or.lic.reference}</td>
                <td>${or.lic.reference}</td>
              </tr>
            </g:each>
            </table>
    
          </p>
        </div>
      </g:if>

      <div class="well">
        <p>
          <g:if test="${licenses?.size() > 0}">
            If you wish to add a new license for this organisation, please enter a new reference below and cick the create button<br/>
          </g:if>
          <g:else>
            There are no licenses currently attached to ${institution.name}. To create a new one, please enter a reference name for the new
            license and press the create new license button below.<br/>
          </g:else>

          <g:form action="newLicense"
                  controller="myInstitutions" 
                  params="${[shortcode:params.shortcode]}">
            New license reference name: <input type="text" name="new_license_ref_name"/><input type="submit" value="Create new license"/>
          </g:form>
       </p>
      </div>

    </div>
  </body>
</html>

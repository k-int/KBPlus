<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="row-fluid">
      <p>
        JISC KB+ data explorer. Use the links above to navigate the data items imported by the KB+ import process and validate the data.
      </p>
      <p>
        Currently, you can review:

        <ul class="nav">              
          <li><g:link controller="package">Packages</g:link></li>
          <li><g:link controller="org">Organisations</g:link></li>
          <li><g:link controller="platform">Platforms</g:link></li>
          <li><g:link controller="titleInstance">Title Instances</g:link></li>
          <li><g:link controller="titleInstancePackagePlatform">Title Instance Package Platform Links</g:link></li>
          <li><g:link controller="subscription">Subscriptions</g:link></li>
          <li><g:link controller="license">Licenses</g:link></li>
        </ul>
      </p>
      <p>

        <g:form action="index" method="get">
          Query:<input type="text" name="q" value=""/><br/>
          <input type="submit"/>
        </g:form>

        <g:if test="${hits}">
          <div id="resultsarea">
            <table>
              <tr><th>#</th><th>Type</th><th>Title/Name</th></tr>
              <g:each in="${hits}" var="hit">
                <tr>
                  <td></td>
                  <td>
                    <g:if test="1==2">
                      Organisation
                    </g:if> 
                    <g:else>
                    </g:else>
                  </td>
                  <td>${hit}</td>
                </tr>
              </g:each>
            </table>
          </div>
        </g:if>

      </p>
    </div>
  </body>
</html>

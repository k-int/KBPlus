<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>

    <div class="container">
        <ul class="breadcrumb">
           <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
           <li>Licences</li>
        </ul>
    </div>

    <div class="container">
      <h1>${institution?.name} - Licenses</h1>
    </div>

    <div class="container licence-searches">
        <div class="row">
            <div class="span6">
                <input type="text" name="keyword-search" placeholder="enter keyword..."
                />
                <input type="submit" class="btn btn-primary" value="Search" />
            </div>
            <div class="span6">
                <div class="pull-right">
                    <a class="btn btn-primary" href="licenceCreate.html">Create New Licence</a>
                </div>
            </div>
        </div>
    </div>

    <g:form action="newLicense"
                controller="myInstitutions" 
                params="${[shortcode:params.shortcode]}">

      <div class="container">
          <div class="well licence-options">
              <input type="submit" name="copy-licence" value="Copy Selected" class="btn btn-warning" />
              <!-- <input type="submit" name="delete-licence" value="Delete Selected" class="btn btn-danger delete-licence" /> -->
          </div>
      </div>


      <g:if test="${licenses?.size() > 0}">
        <div class="container licence-results">
          <table class="table table-bordered">
            <thead>
              <tr>
                <th>Select</th>
                <th>Reference Description</th>
                <th>Licensor</th>
                <th>Licensee</th>
                <th>Status</th>
                <th>Type</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${licenses}" var="l">
                <tr>
                  <td><input type="radio" name="baselicense" value="${l.id}"/></td>
                  <td><g:link action="licenseDetails"
                              controller="myInstitutions" 
                              id="${l.id}"
                              params="${[shortcode:params.shortcode]}">${l.reference}</g:link></td>
                  <td>${l.licensor?.name}</td>
                  <td>${l.licensee?.name}</td>
                  <td>${l.status?.value}</td>
                  <td>${l.type?.value}</td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
      </g:if>
      <!--
        <p>New license reference name: <input type="text" name="new_license_ref_name"/><input type="submit" value="Create new license"/></p>
      -->
    </g:form>

  </body>
</html>

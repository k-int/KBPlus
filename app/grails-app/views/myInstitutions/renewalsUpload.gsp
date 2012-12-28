<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Renewals Upload</title>
  </head>

  <body>
    <div class="container">
      <g:form action="renewalsUpload" method="post" enctype="multipart/form-data" params="${params}">
        <input type="file" id="renewalsWorksheet" name="renewalsWorksheet"/>
        <button type="submit" class="btn btn-primary">Upload Renewals Worksheet</button>
      </g:form>
    </div>

    <g:if test="${(errors && (errors.size() > 0))}">
      <div class="container">
        <ul>
          <g:each in="${errors}" var="e">
            <li>${e}</li>
          </g:each>
        </ul>
      </div>
    </g:if>

    <g:if test="${base_subscription}">
      <div class="container">
      <hr/>
        Uploaded worksheet will create a new subscription taken for ${institution.name} based on subscription offered 
        <g:link controller="subscriptionDetails" action="index" id="${base_subscription.id}">${base_subscription.id} - ${base_subscription.name}</g:link><br/>
        The uploaded worksheet will generate the following issue entitlements:<br/>

        <table class="table table-bordered">
          <thead>
            <tr>
              <td>Title</td>
              <td>Subscribe?</td>
              <td>Core?</td>
              <td>Core Start Date</td>
              <td>Core End Date</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${entitlements}" var="e">
              <tr>
                <td>${e.title_id}</td>
                <td>${e.subscribe}</td>
                <td>${e.core}</td>
                <td>${e.core_start_date}</td>
                <td>${e.core_end_date}</td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
    </g:if>

  </body>
</html>

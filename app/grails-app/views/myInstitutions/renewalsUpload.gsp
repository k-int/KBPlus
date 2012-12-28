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
  </body>
</html>

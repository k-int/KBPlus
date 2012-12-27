<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>Upload Renewals Worksheet</title>
    <!-- r:require modules="bootstrap-typeahead"-->
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
      <div class="container">

        <div class="page-header">
          <h1>Upload Renewals Worksheet</h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <g:if test="${flash.error}">
        <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
        </g:if>

        <g:form action="upload" method="post" enctype="multipart/form-data">
            <input type="file" id="renewalFile" name="soFile"/>
            <button type="submit" class="btn btn-primary">Upload Renewal Worksheet</button>
        </g:form>

      </div>

  </body>
</html>


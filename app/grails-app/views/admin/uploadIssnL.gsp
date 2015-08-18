<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::Identifier Same-As Upload</title>
  </head>

  <body>
    <div class="container">
      <div class="span12">
        <h1>Import Identifier Same-As Relations</h1>
      <g:if test="${complete}">
        <div class="container">
           <bootstrap:alert id="procesing_alert" class="alert-info"> CSV Processing is complete</bootstrap:alert>
        </div>
      </g:if>
        <p>UPload a file of tab separated equivalent identifiers. By default, the assumption is ISSN -&gt; ISSNL mappings</p>
           
        <g:form action="uploadIssnL" method="post" enctype="multipart/form-data">
          <dl>
            <div class="control-group">
              <dt>Upload ISSN to ISSNL mapping file</dt>
              <dd>
                <input type="file" name="sameasfile" />
              </dd>
            </div>
            <button name="load" type="submit" value="Go">Upload...</button>
          </dl>
        </g:form>
      </div>
    </div>
  </body>
</html>

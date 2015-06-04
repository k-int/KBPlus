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
        <p>UPload a file of tab separated equivalent identifiers. By default, the assumption is ISSN -&gt; ISSNL mappings</p>
           
        <g:form action="grails-app/views/admin/uploadIssnL.gsp" method="post" enctype="multipart/form-data">
          <dl>
            <div class="control-group">
              <dt>Upload ISSN to ISSNL mapping file</dt>
              <dd>
                <input type="file" name="issnl_file" />
              </dd>
            </div>
            <button name="load" type="submit" value="Go">Upload...</button>
          </dl>
        </g:form>
      </div>
    </div>
  </body>
</html>

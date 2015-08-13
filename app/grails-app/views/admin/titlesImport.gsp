<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::Orgs Import</title>
  </head>

  <body>
    <div class="container">
      <div class="span12">
        <h1>Titles Import - Create/Add Identifiers</h1>
        <p>
          This form allows an administrator to upload a CSV of titles containing one or more identifiers and titles. If a title can be uniquely matched using any/all of the identifiers then any missing identifiers will be added. If multiple identifiers match different titles no update will be made. If the identifiers do not match any existing titles, and the file includes a 'title.title' column, a new journal title record is created.

        </p>
        <p>Currently there is no error reporting for this upload.</p>
        <p>At the end, a bad file is available for corrections and re-ingest</p>
        <g:form action="titlesImport" method="post" enctype="multipart/form-data">
          <p>
            Upload a .csv file with any combination of the following columns. For titles to be created the 'title.title' column must be present. If there is no 'title.title' column, new titles will not be created, and only identifier enrichment (for matched titles) will be processed.<br/>
            
            <p>The columns supported are: title.title,title.id,title.id.namespace<br/></p>
            The last of these can be repeated for each type of identifier in the file, with the 'namespace' being replaced by the type of identifier (e.g. title.id.ISSN, title.id.eISSN)<br/>
            N.B. The 'namespace' must be exactly the same as the namespace already used in the system for identifiers to match successfully. This is case-sensitive.<br/>
            Example header "title.id","title.title","title.id.Ringold","title.id.Ingenta",'title.id.ISSN','title.id.eISSN','title.id.jusp','title.id.zdb'


          </p>
          <dl>
            <div class="control-group">
              <dt>Titles CSV File</dt>
              <dd>
                <input type="file" name="titles_file" />
              </dd>
            </div>
            <button name="load" type="submit" value="Go">Load...</button>
          </dl>
        </g:form>
      </div>
    </div>
  </body>
</html>

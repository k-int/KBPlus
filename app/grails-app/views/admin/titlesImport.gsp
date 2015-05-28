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
        <p>This form allows an administrator to upload a CSV of titles containing one or more identifiers and titles. If
           a title can be uniquely matched using any/all of the identifiers then any missing identifiers will be added.
           Alternatively, titles can be created. If multiple identifiers match different titles the row is flagged as problematic</p>
        <p>The upload is a two stage process - with a review page and a dry-run before executing the update properly</p>
        <p>At the end, a bad file is available for corrections and re-ingest</p>
        <g:form action="titlesImport" method="post" enctype="multipart/form-data">
          <p>
            Upload a .csv file with any combination of the following columns. For titles to be crrated the title column must be present.
            Without a title column, only identifier enrichment will be processed<br/>
            title.title,title.id,title.id.namespace,title.id.namespace,title.id.namespace.<br/>
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

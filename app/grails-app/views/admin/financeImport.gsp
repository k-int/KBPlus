<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::Identifier Same-As Upload</title>
  </head>

  <body>
    <div class="container-fluid">
      <div class="span12">
        <h1>Import Financials Data</h1>
        <g:if test="${loaderResult==null}">
          <p>This service allows administrators to bulk load cost item records. It understands the following column mappings in the uploaded .tsv file</p>
          <table class="table table-striped">
            <thead>
              <tr>
                <th>tsv column name</th>
                <th>Description</th>
                <th>maps to</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${grailsApplication.config.financialImportTSVLoaderMappings.cols}" var="mpg">
                <tr>
                  <td>${mpg.colname}</td>
                  <td>${mpg.desc}
                      <g:if test="${mpg.type=='vocab'}">
                        <br/>Must be one of : <ul>
                          <g:each in="${mpg.mapping}" var="m,k">
                            <li>${m}</li>
                          </g:each>
                        </ul>
                      </g:if>
                  </td>
                  <td></td>
                </tr>
              </g:each>
            </tbody>
          </table>

          <g:form action="financeImport" method="post" enctype="multipart/form-data">
            <dl>
              <div class="control-group">
                <dt>Upload TSV File according to the column definitions above</dt>
                <dd>
                  <input type="file" name="tsvfile" />
                </dd>
              </div>
              <div class="control-group">
                <dt>Dry Run</dt>
                <dd>
                  <input type="checkbox" name="dryRun" checked value="Y" />
                </dd>
              </div>
              <button name="load" type="submit" value="Go">Upload...</button>
            </dl>
          </g:form>
        </g:if>
      </div>

      <g:if test="${loaderResult}">
        <table class="table table-striped">
          <thead>
            <tr>
              <td></td>
              <g:each in="${loaderResult.columns}" var="c">
                <td>${c}</td>
              </g:each>
            </tr>
          </thead>
          <tbody>
            <g:each in="${loaderResult.log}" var="logEntry">
              <tr>
                <td rowspan="2">${logEntry.rownum}</td>
                <g:each in="${logEntry.rawValues}" var="v">
                  <td>${v}</td>
                </g:each>
              </tr>
              <tr ${logEntry.error?'style="background-color:red;"':''}>
                <td colspan="${loaderResult.columns.size()}">
                  ${logEntry.error?'Row ERROR':'Row OK'}
                  <ul>
                    <g:each in="${logEntry.messages}" var="m">
                      <li>${m}</li>
                    </g:each>
                  </ul>
                </td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </g:if>
    </div>
  </body>
</html>

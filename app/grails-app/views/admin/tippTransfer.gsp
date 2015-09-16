<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::TIPP Transfer</title>
  </head>
  <body>
    <div class="container">
    <h1>TIPP Transfer</h1>

        <g:each in="${error}" var="err">
          <bootstrap:alert class="alert-danger">${err}</bootstrap:alert>
        </g:each>

        <g:if test="${success}">
          <bootstrap:alert class="alert-info">Transfer Sucessful</bootstrap:alert>
        </g:if>

        <g:form action="tippTransfer" method="get">
          <p>Add the appropriate ID's below. All IssueEntitlements of source will be removed and transfered to target. Detailed information and confirmation will be presented before proceeding</p>
          <dl>
            <div class="control-group">
              <dt>Database ID of TIPP</dt>
              <dd>
                <input type="text" name="sourceTIPP" value="${params.sourceTIPP}" />

              </dd>
            </div>

            <div class="control-group">
              <dt>Database ID of target TitleInstance</dt>
              <dd>
                <input type="text" name="targetTI" value="${params.targetTI}"/>
              </dd>
            </div>
    
              <button onclick="return confirm('Any existing TIs on TIPP will be replaced. Continue?')" class="btn-success" type="submit">Transfer</button>
          </dl>
        </g:form>
      </div>
    </div>

  </body>
</html>
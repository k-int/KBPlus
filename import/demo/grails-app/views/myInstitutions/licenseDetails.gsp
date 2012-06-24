<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>
    <h2>${institution?.name} - A License</h2>
    <hr/>
    <div class="row">
      <div class="span12">
        <div class="well">


          <g:form class="form-horizontal">  
            <fieldset>  
              <legend>Fieldset</legend>  
              <div class="control-group">  
                <label class="control-label" for="reference">Reference</label>  
                <div class="controls">  
                  <g:textField name="reference" value="${license.reference}" class="input-xlarge" id="input01"/>  
                  <p class="help-block">Descriptive reference for this license</p>  
                </div>  
              </div>  
            </fieldset>
            <div class="form-actions">  
              <g:actionSubmit value="Update" class="btn btn-primary"/>
            </div>  
           </g:form>
        </div>
      </div>
      <div class="span2">
        <g:render template="documents" contextPath="../templates"/>
        <g:render template="notes" contextPath="../templates"/>
        <g:render template="links" contextPath="../templates"/>
      </div>
    </div>
  </body>
</html>

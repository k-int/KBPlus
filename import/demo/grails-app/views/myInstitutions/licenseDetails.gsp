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
              <legend>Licence Information</legend>  
              <div class="control-group">  
                <label class="control-label" for="reference">Reference</label>  
                <div class="controls">  
                  <g:textField name="reference" value="${license.reference}" class="input-xlarge" id="input01"/>  
                  <p class="help-block">Descriptive reference for this license</p>  
                </div>  
              </div>  

              <div class="control-group">
                <label class="control-label" for="reference">Notice Period</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.noticePeriod}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Notice Period</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">License Url</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.licenseUrl}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">Licenseor Ref</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.licensorRef}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">Licensee Ref</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.licenseeRef}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <legend>Licens Properties</legend>  
              <div class="control-group">
                <label class="control-label" for="reference">Concurrent Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.concurrentUsers}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>


              <div class="control-group">
                <label class="control-label" for="reference">Remote Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.remoteAccess}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">Walk In Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.walkinAccess}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">Multi Site Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.multisiteAccess}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">Partners Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.partnersAccess}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">Alumni Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.alumniAccess}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">ILL Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.ill}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">Coursepack Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.coursepack}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">VLE Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.vle}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">Enterprise Access</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.enterprise}" class="input-xlarge" id="input01"/>
                  <p class="help-block">Descriptive reference for this license</p>
                </div>
              </div>

              <div class="control-group">
                <label class="control-label" for="reference">PCA</label>
                <div class="controls">
                  <g:textField name="reference" value="${license.pca}" class="input-xlarge" id="input01"/>
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
        <g:render template="documents" contextPath="../templates" model="${[doclist:license.documents, owner:license,property:'documents']}" />
        <g:render template="notes" contextPath="../templates"/>
        <g:render template="links" contextPath="../templates"/>
      </div>
    </div>
  </body>
</html>

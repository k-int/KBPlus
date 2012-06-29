<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+</title>
    <r:require modules="jeditable"/>
  </head>
  <body>
    <h2>${institution?.name} - A License</h2>
    <hr/>
    <div class="tabbable"> <!-- Only required for left/right tabs -->
      <ul class="nav nav-tabs">
        <li class="active"><a href="#detailtab" data-toggle="tab">License Detail</a></li>
        <li><a href="#docstab" data-toggle="tab">Documents</a></li>
        <li><a href="#linkstab" data-toggle="tab">Links</a></li>
        <li><a href="#notestab" data-toggle="tab">Notes</a></li>
      </ul>
      <div class="tab-content">
        <div class="tab-pane active" id="detailtab">
          <div class="row-fluid">
            <div class="span8 form-horizontal">
              <h3>Information</h3>
              <fieldset>  

                <div class="control-group">
                    <label class="control-label" for="licref">Reference</label>
                    <div class="controls">
                      <p id="licref" class="ipe" style="padding-top: 5px;">${license.reference}</p>
                    </div>
                </div>
    
                <div class="control-group">
                    <label class="control-label" for="reference">Notice Period</label>
                    <div class="controls">
                      <g:textField name="noticeperiod" value="${license.noticePeriod}" class="input-xlarge" id="input01"/>
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
              </fieldset>
            </div>
            <div class="span4">
              <g:render template="documents" contextPath="../templates" model="${[doclist:license.documents, owner:license,property:'documents']}" />
              <g:render template="notes" contextPath="../templates"/>
              <g:render template="links" contextPath="../templates"/>
            </div>
          </div>
        </div>
        <div class="tab-pane" id="docstab">
          <div class="row-fluid">
            <div class="span12">
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
            </div>
          </div>
        </div>
        <div class="tab-pane" id="linkstab">
          <div class="row-fluid">
            <div class="span12">
              Tab3
            </div>
          </div>
        </div>
        <div class="tab-pane" id="notestab">
          <div class="row-fluid">
            <div class="span12">
              Tab4
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <script language="JavaScript">
      $(document).ready(function() {
         $('.ipe').editable('<g:createLink controller="ajax" params="${[type:'License']}" id="${params.id}" action="inPlaceSave" absolute="true"/>', { 
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           tooltip   : 'Click to edit...'
         });
       });
    </script>

  </body>
</html>

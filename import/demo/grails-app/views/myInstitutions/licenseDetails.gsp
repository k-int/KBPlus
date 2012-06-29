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
                    <label class="control-label" for="reference">Reference</label>
                    <div class="controls">
                      <p id="reference" class="ipe" style="padding-top: 5px;">${license.reference}</p>
                    </div>
                </div>
    
                <div class="control-group">
                    <label class="control-label" for="noticePeriod">Notice Period</label>
                    <div class="controls">
                      <p id="noticePeriod" class="ipe" style="padding-top: 5px;">${license.noticePeriod}</p>
                    </div>
                </div>
    
                <div class="control-group">
                    <label class="control-label" for="licenseUrl">License Url</label>
                    <div class="controls">
                      <p id="licenseUrl" class="ipe" style="padding-top: 5px;">${license.licenseUrl}</p>
                    </div>
                </div>
    
                <div class="control-group">
                    <label class="control-label" for="licensorRef">Licenseor Ref</label>
                    <div class="controls">
                      <p id="licensorRef" class="ipe" style="padding-top: 5px;">${license.licensorRef}</p>
                    </div>
                </div>
    
                <div class="control-group">
                    <label class="control-label" for="licenseeRef">Licensee Ref</label>
                    <div class="controls">
                      <p id="licenseeRef" class="ipe" style="padding-top: 5px;">${license.licenseeRef}</p>
                    </div>
                </div>

                <div class="control-group">
                  <label class="control-label" for="concurrentUsers">Concurrent Access</label>
                  <div class="controls">
                    <p id="concurrentUsers" class="ipe" style="padding-top: 5px;">${license.concurrentUsers}</p>
                  </div>
                </div>
    
    
                <div class="control-group">
                  <label class="control-label" for="remoteAccess">Remote Access</label>
                  <div class="controls">
                    <p id="remoteAccess" class="ipe" style="padding-top: 5px;">${license.remoteAccess}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="walkinAccess">Walk In Access</label>
                  <div class="controls">
                    <p id="walkinAccess" class="ipe" style="padding-top: 5px;">${license.walkinAccess}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="multisiteAccess">Multi Site Access</label>
                  <div class="controls">
                    <p id="multisiteAccess" class="ipe" style="padding-top: 5px;">${license.multisiteAccess}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="partnersAccess">Partners Access</label>
                  <div class="controls">
                    <p id="partnersAccess" class="ipe" style="padding-top: 5px;">${license.partnersAccess}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="alumniAccess">Alumni Access</label>
                  <div class="controls">
                    <p id="alumniAccess" class="ipe" style="padding-top: 5px;">${license.alumniAccess}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="ill">ILL Access</label>
                  <div class="controls">
                    <p id="ill" class="ipe" style="padding-top: 5px;">${license.ill}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="coursepack}">Coursepack Access</label>
                  <div class="controls">
                    <p id="coursepack" class="ipe" style="padding-top: 5px;">${license.coursepack}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="vle">VLE Access</label>
                  <div class="controls">
                    <p id="coursepack" class="vle" style="padding-top: 5px;">${license.vle}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="enterprise">Enterprise Access</label>
                  <div class="controls">
                    <p id="enterprise" class="vle" style="padding-top: 5px;">${license.enterprise}</p>
                  </div>
                </div>
    
                <div class="control-group">
                  <label class="control-label" for="pca">PCA</label>
                  <div class="controls">
                    <p id="enterprise" class="pca" style="padding-top: 5px;">${license.pca}</p>
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
           id        : 'elementid',
           tooltip   : 'Click to edit...'
         });
       });
    </script>

  </body>
</html>

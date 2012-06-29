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
                      <div id="licenseeRef" class="ipe" style="padding-top: 5px;">${license.licenseeRef}</div>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="concurrentUsers">Concurrent Access</label>
                    <div class="controls">
                      <div id="concurrentUsers" class="ipe" style="padding-top: 5px;">${license.concurrentUsers}</div>
                    </div>
                </div>

          <table class="table table-striped table-bordered table-condensed">
            <thead>
              <tr>
                <th>Property</th>
                <th>Status</th>
                <th>Notes</th>
              </tr>
            </thead>
            <tbody>
              <tr><td>Remote Access</td><td><p id="remoteAccess" class="ipe"><img src="<g:message code="refdata.yno.${license.remoteAccess}.icon"/>"/>${license.remoteAccess}</p></td><td></td></tr>
              <tr><td>Walk In Access</td><td><p id="walkinAccess" class="ipe"><img src="<g:message code="refdata.yno.${license.walkinAccess}.icon"/>"/>${license.walkinAccess}</p></td><td></td></tr>
              <tr><td>Multi Site Access</td><td><p id="multisiteAccess" class="ipe"><img src="<g:message code="refdata.yno.${license.multisiteAccess}.icon"/>"/>${license.multisiteAccess}</p></td><td></td></tr>
              <tr><td>Partners Access</td><td><p id="partnersAccess" class="ipe"><img src="<g:message code="refdata.yno.${license.partnersAccess}.icon"/>"/>${license.partnersAccess}</p></td><td></td></tr>
              <tr><td>Alumni Access</td><td><p id="alumniAccess" class="ipe"><img src="<g:message code="refdata.yno.${license.alumniAccess}.icon"/>"/>${license.alumniAccess}</p></td><td></td></tr>
              <tr><td>ILL Access</td><td><p id="ill" class="ipe"><img src="<g:message code="refdata.yno.${license.ill}.icon"/>"/>${license.ill}</p></td><td></td></tr>
              <tr><td>Coursepack Access</td><td><p id="coursepack" class="ipe"><img src="<g:message code="refdata.yno.${license.coursepack}.icon"/>"/>${license.coursepack}</p></td><td></td></tr>
              <tr><td>VLE Access</td><td><p id="vle" class="ipe"><img src="<g:message code="refdata.yno.${license.vle}.icon"/>"/>${license.vle}</p></td><td></td></tr>
              <tr><td>Enterprise Access</td><td><p id="enterprise" class="ipe"><img src="<g:message code="refdata.yno.${license.enterprise}.icon"/>"/>${license.enterprise}</p></td><td></td></tr>
              <tr><td>PCA Access</td><td><p id="pca" class="ipe"><img src="<g:message code="refdata.yno.${license.pca}.icon"/>"/>${license.pca}</p></td><td></td></tr>
            </tbody>
          </table>

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

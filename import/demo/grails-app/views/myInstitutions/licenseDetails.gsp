<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
    <h2>${institution?.name} License : <span id="reference" class="ipe" style="padding-top: 5px;">${license.reference}</span></h2>
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

          <table class="table table-striped table-bordered table-condensed">
            <thead>
              <tr>
                <th width="20%">Property</th>
                <th width="25%">Status</th>
                <th width="55%">Notes</th>
              </tr>
            </thead>
            <tbody>
              <tr><td>Concurrent Access</td>
                  <td>
                       <g:refdataValue val="${license.concurrentUsers?.value}" propname="concurrentUsers" refdataCat='Concurrent Access' class="cuedit"/>
                       <span id="cucwrap">
                       (<span id="concurrentUserCount" class="intedit" style="padding-top: 5px;">${license.concurrentUserCount}</span>)
                       </span>
                  </td>
                  <td><g:singleValueFieldNote domain="concurrentUsers" value="${license.getNote('concurrentUsers')}" class="fieldNote"/></td></tr>

              <tr><td>Remote Access</td>
                  <td><g:refdataValue val="${license.remoteAccess?.value}" propname="remoteAccess" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="remoteAccess" value="${license.getNote('remoteAccess')}" class="fieldNote"/></td></tr>

              <tr><td>Walk In Access</td>
                  <td><g:refdataValue val="${license.walkinAccess?.value}" propname="walkinAccess" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="walkinAccess" value="${license.getNote('walkinAccess')}" class="fieldNote"/></td></tr>
              <tr><td>Multi Site Access</td>
                  <td><g:refdataValue val="${license.multisiteAccess?.value}" propname="multisiteAccess" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="multisiteAccess" value="${license.getNote('multisiteAccess')}" class="fieldNote"/></td></tr>
              <tr><td>Partners Access</td>
                  <td><g:refdataValue val="${license.partnersAccess?.value}" propname="partnersAccess" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="partnersAccess" value="${license.getNote('partnersAccess')}" class="fieldNote"/></td></tr>
              <tr><td>Alumni Access</td>
                  <td><g:refdataValue val="${license.alumniAccess?.value}" propname="alumniAccess" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="alumniAccess" value="${license.getNote('alumniAccess')}" class="fieldNote"/></td></tr>
              <tr><td>ILL Access</td>
                  <td><g:refdataValue val="${license.ill?.value}" propname="ill" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="ill" value="${license.getNote('ill')}" class="fieldNote"/></td></tr>
              <tr><td>Coursepack Access</td>
                  <td><g:refdataValue val="${license.coursepack?.value}" propname="coursepack" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="coursepack" value="${license.getNote('coursepack')}" class="fieldNote"/></td></tr>
              <tr><td>VLE Access</td>
                  <td><g:refdataValue val="${license.vle?.value}" propname="vle" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="vle" value="${license.getNote('vle')}" class="fieldNote"/></td></tr>
              <tr><td>Enterprise Access</td>
                  <td><g:refdataValue val="${license.enterprise?.value}" propname="enterprise" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="enterprise" value="${license.getNote('enterprise')}" class="fieldNote"/></td></tr>
              <tr><td>PCA Access</td>
                  <td><g:refdataValue val="${license.pca?.value}" propname="pca" refdataCat='YNO' class="refdataedit"/></td>
                  <td><g:singleValueFieldNote domain="pca" value="${license.getNote('pca')}" class="fieldNote"/></td></tr>
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

              <button id="attach-doc">Attach New Document</button>

              <table class="table table-striped table-bordered table-condensed">
                <thead>
                  <tr>
                    <th>Select</th>
                    <th>Title</th>
                    <th>File Name</th>
                    <th>Download Link</th>
                    <th>Creator</th>
                    <th>Type</th>
                    <th>Doc Store ID</th>
                    <th>Linked Here</th>
                  </tr>
                </thead>
                <tbody>
                  <g:each in="${license.documents}" var="docctx">
                    <tr>
                      <td><input type="checkbox" value="${docctx.id}"/></td>
                      <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="title" id="doctitle" class="newipe">${docctx.owner.title}</g:inPlaceEdit></td>
                      <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="filename" id="docfilename" class="newipe">${docctx.owner.filename}</g:inPlaceEdit></td>
                      <td>
                        <g:if test="docctx.owner?.uuid">
                          <a href="http://knowplus.edina.ac.uk/oledocstore/document?uuid=${docctx.owner?.uuid}">Download Doc</a>
                        </g:if>
                      </td>
                      <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="creator" id="docCreator" class="newipe">${docctx.owner.creator}</g:inPlaceEdit></td>
                      <td>${docctx.owner?.type?.value}</td>
                      <td><g:if test="${docctx.owner?.uuid}">${docctx.owner?.uuid}</g:if></td>
                      <td>Links</td>
                    </tr>
                  </g:each>
                </tbody>
              </table>
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

         if ( '${license.concurrentUsers?.value}'==='Specified' ) {
           $('#cucwrap').show();
         }
         else {
           $('#cucwrap').hide();
         }

         $('.ipe').editable('<g:createLink controller="ajax" params="${[type:'License']}" id="${params.id}" action="setValue" absolute="true"/>', { 
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...'
         });

         $('.intedit').editable('<g:createLink controller="ajax" params="${[type:'License']}" id="${params.id}" action="setValue" absolute="true"/>', {
           type      : 'text',
           cols      : '5',
           width     : '30',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           tooltip   : 'Click to edit...'
         });


         $('.refdataedit').editable('<g:createLink controller="ajax" params="${[type:'License',cat:'YNO']}" id="${params.id}" action="setRef" absolute="true"/>', {
           data   : {'Yes':'Yes', 'No':'No','Other':'Other'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...'
         });

         $('.cuedit').editable('<g:createLink controller="ajax" params="${[type:'License',cat:'Concurrent Access']}" id="${params.id}" action="setRef" absolute="true"/>', {
           data   : {'No limit':'No limit', 'Specified':'Specified','Not Specified':'Not Specified', 'Other':'Other'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
             if ( value==='Specified' ) {
               $('#cucwrap').show();
             }
             else {
               $('#cucwrap').hide();
             }
           }
         });

         $('.fieldNote').editable('<g:createLink controller="ajax" params="${[type:'License']}" id="${params.id}" action="setFieldNote" absolute="true"/>', {
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...'
         });

         $('.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" absolute="true"/>', {
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...'
         });

         $( "#dialog-form" ).dialog({
           autoOpen: false,
           height: 300,
           width: 350,
           modal: true,
           buttons: {
             Save: function() {
               $( this ).dialog( "close" );
             },
             Cancel: function() {
               $( this ).dialog( "close" );
             }
           },
           close: function() {
             allFields.val( "" ).removeClass( "ui-state-error" );
           }
         });

         $( "#attach-doc" )
             .button()
             .click(function() {
               $( "#dialog-form" ).dialog( "open" );
             });

       });

    </script>

    <div id="dialog-form" title="Attach New Document">
      <g:form controller="myInstitutions" action="uploadDocument" method="post" enctype="multipart/form-data">
        <fieldset>
          <label for="upload_title">Title</label>
          <input type="text" name="upload_title" id="upload_title" value="" class="text ui-widget-content ui-corner-all" />
          <label for="upload_file">File:</label>
          <input type="file" name="upload_file" id="upload_file" class="text ui-widget-content ui-corner-all" />
        </fieldset>
      </g:form>
    </div>

  </body>
</html>

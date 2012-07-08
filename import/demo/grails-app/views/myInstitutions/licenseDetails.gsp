<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
        <li>Licences</li>
      </ul>
    </div>

    <div class="container">
      <h1>${institution?.name} License : <span id="reference" class="ipe" style="padding-top: 5px;">${license.reference}</span></h1>
    </div>


    <div class="container">
      <div class="tabbable"> <!-- Only required for left/right tabs -->
        <ul class="nav nav-tabs">
          <li class="active"><a href="#detailtab" data-toggle="tab">License Detail</a></li>
          <li><a href="#docstab" data-toggle="tab">Documents</a></li>
          <li><a href="#linkstab" data-toggle="tab">Links</a></li>
          <li><a href="#notestab" data-toggle="tab">Notes</a></li>
        </ul>
      </div>
    </div>

    <div class="container">
      <div class="tabbable"> <!-- Only required for left/right tabs -->
        <div class="tab-content">
          <div class="tab-pane active" id="detailtab">
            <div class="row">
              <div class="span8">
  
                <h6>Information</h6>

                <div class="licence-info">

                <g:if test="${flash.message}">
                  <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
                </g:if>
  
                <g:hasErrors bean="${titleInstanceInstance}">
                  <bootstrap:alert class="alert-error">
                  <ul>
                    <g:eachError bean="${titleInstanceInstance}" var="error">
                      <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                  </ul>
                  </bootstrap:alert>
                </g:hasErrors>
  
  
                  <dl>
                      <dt><label class="control-label" for="reference">Reference</label></td>
                      <dd>
                        <p id="reference" class="ipe" >${license.reference}</p>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="noticePeriod">Notice Period</label></td>
                      <dd>
                        <p id="noticePeriod" class="ipe" >${license.noticePeriod}</p>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licenseUrl">License Url</label></dt></td>
                      <dd>
                        <p id="licenseUrl" class="ipe">${license.licenseUrl}</p>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licensorRef">Licenseor Ref</label></td>
                      <dd>
                        <p id="licensorRef" class="ipe">${license.licensorRef}</p>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licenseeRef">Licensee Ref</label></td>
                      <dd>
                        <div id="licenseeRef" class="ipe" >${license.licenseeRef}</div>
                      </dd>
                  </dl>
                  <div class="clearfix"></div>
                  </div>
  
            <h6>licence Properties</h6>

            <table class="table table-bordered licence-properties">
              <thead>
                <tr>
                  <td>Property</td>
                  <td>Status</td>
                  <td>Notes</td>
                </tr>
              </thead>
              <tbody>
                <tr><td>Concurrent Access</td>
                    <td>
                         <g:refdataValue val="${license.concurrentUsers?.value}" domain="License" pk="${license.id}" field="concurrentUsers" cat='Concurrent Access' class="cuedit"/>
                         <span id="cucwrap">
                         (<span id="concurrentUserCount" class="intedit" style="padding-top: 5px;">${license.concurrentUserCount}</span>)
                         </span>
                    </td>
                    <td><g:singleValueFieldNote domain="concurrentUsers" value="${license.getNote('concurrentUsers')}" class="fieldNote"/></td></tr>
  
                <tr><td>Remote Access</td>
                    <td><g:refdataValue val="${license.remoteAccess?.value}" domain="License" pk="${license.id}" field="remoteAccess" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="remoteAccess" value="${license.getNote('remoteAccess')}" class="fieldNote"/></td></tr>
  
                <tr><td>Walk In Access</td>
                    <td><g:refdataValue val="${license.walkinAccess?.value}" domain="License" pk="${license.id}" field="walkinAccess" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="walkinAccess" value="${license.getNote('walkinAccess')}" class="fieldNote"/></td></tr>
                <tr><td>Multi Site Access</td>
                    <td><g:refdataValue val="${license.multisiteAccess?.value}" domain="License" pk="${license.id}" field="multisiteAccess" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="multisiteAccess" value="${license.getNote('multisiteAccess')}" class="fieldNote"/></td></tr>
                <tr><td>Partners Access</td>
                    <td><g:refdataValue val="${license.partnersAccess?.value}" domain="License" pk="${license.id}" field="partnersAccess" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="partnersAccess" value="${license.getNote('partnersAccess')}" class="fieldNote"/></td></tr>
                <tr><td>Alumni Access</td>
                    <td><g:refdataValue val="${license.alumniAccess?.value}" domain="License" pk="${license.id}" field="alumniAccess" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="alumniAccess" value="${license.getNote('alumniAccess')}" class="fieldNote"/></td></tr>
                <tr><td>ILL Access</td>
                    <td><g:refdataValue val="${license.ill?.value}" domain="License" pk="${license.id}" field="ill" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="ill" value="${license.getNote('ill')}" class="fieldNote"/></td></tr>
                <tr><td>Coursepack Access</td>
                    <td><g:refdataValue val="${license.coursepack?.value}" domain="License" pk="${license.id}" field="coursepack" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="coursepack" value="${license.getNote('coursepack')}" class="fieldNote"/></td></tr>
                <tr><td>VLE Access</td>
                    <td><g:refdataValue val="${license.vle?.value}" domain="License" pk="${license.id}" field="vle" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="vle" value="${license.getNote('vle')}" class="fieldNote"/></td></tr>
                <tr><td>Enterprise Access</td>
                    <td><g:refdataValue val="${license.enterprise?.value}" domain="License" pk="${license.id}" field="enterprise" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="enterprise" value="${license.getNote('enterprise')}" class="fieldNote"/></td></tr>
                <tr><td>PCA Access</td>
                    <td><g:refdataValue val="${license.pca?.value}" domain="License" pk="${license.id}" field="pca" cat='YNO' class="refdataedit"/></td>
                    <td><g:singleValueFieldNote domain="pca" value="${license.getNote('pca')}" class="fieldNote"/></td></tr>
              </tbody>
            </table>
  
              </div>
              <div class="span4">
                <g:render template="documents" contextPath="../templates" model="${[doclist:license.documents, 
                                                                                    license:license,
                                                                                    property:'documents',
                                                                                    shortcode:params.shortcode]}" />
                <g:render template="notes" contextPath="../templates" model="${[doclist:license.documents, 
                                                                                    license:license,
                                                                                    property:'documents',
                                                                                    shortcode:params.shortcode]}" />
                <g:render template="links" contextPath="../templates"/>
              </div>
            </div>
          </div>
          <div class="tab-pane" id="docstab">
            <div class="row-fluid">
              <div class="span12">
  
                <button id="delete-doc">Delete Selected Documents</button>&nbsp;
                <input type="submit" class="btn btn-primary" value="Add new document" data-toggle="modal" href="#modalCreateDocument" />
  
                <g:form id="delete_doc_form" url="[controller:'myInstitutions',action:'deleteDocuments']" method="post">
                  <input type="hidden" name="licid" value="${params.id}"/>
                  <input type="hidden" name="shortcode" value="${params.shortcode}"/>
  
                <table class="table table-striped table-bordered table-condensed">
                  <thead>
                    <tr>
                      <td>Select</td>
                      <td>Title</td>
                      <td>File Name</td>
                      <td>Download Link</td>
                      <td>Creator</td>
                      <td>Type</td>
                      <td>Doc Store ID</td>
                      <td>Linked Here</td>
                    </tr>
                  </thead>
                  <tbody>
                    <g:each in="${license.documents}" var="docctx">
                      <tr>
                        <td><input type="checkbox" name="_deleteflag.${docctx.id}" value="true"/></td>
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
                </g:form>
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
    </div>
    
    <script language="JavaScript">
      $(document).ready(function() {

         var checkEmptyEditable = function() {
           $('.ipe, .refdataedit, .fieldNote').each(function() {
             if($(this).text().length == 0) {
               $(this).addClass('editableEmpty');
             } else {
               $(this).removeClass('editableEmpty');
             }
           });
         }

         checkEmptyEditable();

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


         $('.refdataedit').editable('<g:createLink controller="ajax" action="genericSetRef" absolute="true"/>', {
           data   : {'Yes':'Yes', 'No':'No','Other':'Other'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...'
         });

         $('.cuedit').editable('<g:createLink controller="ajax" action="genericSetRef" absolute="true"/>', {
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
               $( "#upload_new_doc_form" ).submit();
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

         $( "#delete-doc" )
             .button()
             .click(function() {
               $( "#delete_doc_form" ).submit();
             });

         url = document.location.href.split('#');
         if(url[1] != undefined) {
           $('[href=#'+url[1]+']').tab('show');
         }

       });

    </script>

<!-- Lightbox modal for creating a document taken from licenceDocuments.html -->
<div class="modal hide" id="modalCreateDocument">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Create New Document</h3>
  </div>
  <g:form id="upload_new_doc_form" url="[controller:'myInstitutions',action:'uploadDocument']" method="post" enctype="multipart/form-data">
    <input type="hidden" name="licid" value="${license.id}"/>
    <input type="hidden" name="shortcode" value="${params.shortcode}"/>
    <div class="modal-body">
      <dl>
        <dt>
          <label>Document Name:</label>
        </dt>
        <dd>
          <input type="text" name="upload_title">
        </dd>
      </dl>
      <dl>
        <dt>
          <label>File:</label>
        </dt>
        <dd>
          <input type="file" name="upload_file" />
        </dd>
      </dl>
    </div>
    <div class="modal-footer">
      <a href="#" class="btn" data-dismiss="modal">Close</a>
      <input type="submit" class="btn btn-primary" value="Save Changes">
    </div>
  </g:form>
</div>
<!-- End lightbox modal -->

<!-- Lightbox modal for creating a note taken from licenceNotes.html -->
<div class="modal hide" id="modalCreateNote">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Create New Note</h3>
  </div>
  <g:form id="create_license_note" url="[controller:'myInstitutions',action:'uploadNewNote']" method="post">
    <input type="hidden" name="licid" value="${license.id}"/>
    <input type="hidden" name="shortcode" value="${params.shortcode}"/>
    <div class="modal-body">
      <dl>
        <dt>
          <label>Note:</label>
        </dt>
        <dd>
          <textarea name="licenceNote"></textarea>
        </dd>
      </dl>
      <dl>
        <dt>
          <label>Shared:</label>
        </dt>
        <dd>
          <select name="licenceNoteShared">
            <option value="0">Not Shared</option>
            <option value="1">JISC Collections</option>
            <option value="2">Community</option>
          </select>
        </dd>
      </dl>
    </div>
    <div class="modal-footer">
      <a href="#" class="btn" data-dismiss="modal">Close</a>
      <input type="submit" class="btn btn-primary" value="Save Changes">
    </div>
  </g:form>
</div>

  </body>
</html>

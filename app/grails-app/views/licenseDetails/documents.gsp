<!doctype html>
<html>
    <head>
        <meta name="layout" content="mmbootstrap"/>
        <title>KB+</title>
</head>

<body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${license.licensee}">
          <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:license.licensee.shortcode]}"> ${license.licensee.name} Current Licenses</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}">License Details</g:link> <span class="divider">/</span></li>
        <li> <g:link controller="licenseDetails" action="documents" id="${params.id}">License Documents</g:link> </li>

        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>

      </ul>
    </div>

    <div class="container">
        <h1>${license.licensee?.name} ${license.type?.value} Licence : <span id="reference" style="padding-top: 5px;">${license.reference}</span></h1>

<g:render template="nav" contextPath="." />

    </div>

    <div class="container">
      <g:form id="delete_doc_form" url="[controller:'licenseDetails',action:'deleteDocuments']" method="post">
        <div class="well hide licence-documents-options">
            <button class="btn btn-danger" id="delete-doc">Delete Selected Documents</button>&nbsp;
            <input type="submit" class="btn btn-primary" value="Add new document" data-toggle="modal" href="#modalCreateDocument" />

                <input type="hidden" name="licid" value="${params.id}"/>
        </div>

        <table class="table table-striped table-bordered table-condensed licence-documents">
            <thead>
                <tr>
                    <th></th>
                    <th>Title</th>
                    <th>File Name</th>
                    <th>Download</th>
                    <th>Creator</th>
                    <th>Type</th>
                </tr>
            </thead>
            <tbody>
            <g:each in="${license.documents}" var="docctx">
                <g:if test="${(((docctx.owner?.contentType==1)||(docctx.owner?.contentType==3)) && ( docctx.status?.value!='Deleted'))}">
                    <tr>
                      <td><input type="checkbox" name="_deleteflag.${docctx.id}" value="true"/></td>
                      <td style="max-width: 300px;overflow: hidden;text-overflow: ellipsis;">
                        <g:xEditable owner="${docctx.owner}" field="title" id="title"/>
                      </td>
                      <td style="max-width: 300px;overflow: hidden;text-overflow: ellipsis;">
                        <g:xEditable owner="${docctx.owner}" field="filename" id="filename"/>
                      </td>
                      <td>
                      <g:if test="${((docctx.owner?.contentType==1)||(docctx.owner?.contentType==3))}">
                          <g:link controller="docstore" id="${docctx.owner.uuid}">Download Doc</g:link>
                      </g:if>
                      </td>
                      <td>
                        <g:xEditable owner="${docctx.owner}" field="creator" id="creator"/>
                       </td>
                      <td>${docctx.owner?.type?.value}</td>
                      <!--${docctx.owner?.uuid}-->
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
      </g:form>
    </div>

<!-- Lightbox modal for creating a document taken from licenceDocuments.html -->
<div class="modal hide" id="modalCreateDocument">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Create New Document</h3>
    </div>
    <g:form id="upload_new_doc_form" url="[controller:'licenseDetails',action:'uploadDocument']" method="post" enctype="multipart/form-data">
        <input type="hidden" name="licid" value="${license.id}"/>
        <div class="modal-body">
            <div class="inline-lists">
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
                <dl>
                    <dt>
                    <label>Document Type:</label>
                    </dt>
                    <dd>
                        <select name="doctype">
                            <option value="License">License</option>
                            <option value="General">General</option>
                            <option value="General">Addendum</option>
                        </select>
                    </dd>
                </dl>
            </div>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal">Close</a>
            <input type="submit" class="btn btn-primary" value="Save Changes">
        </div>
    </g:form>
</div>
<!-- End lightbox modal -->

<r:script language="JavaScript">
  $(document).ready(function() {
      
      var checkEmptyEditable = function() {
           $('.fieldNote').each(function() {
             if($(this).text().length == 0) {
               $(this).addClass('editableEmpty');
             } else {
               $(this).removeClass('editableEmpty');
             }
           });
         }

         checkEmptyEditable();

        $('.fieldNote').click(function() {
            // Hide edit icon with overwriting style.
            $(this).addClass('clicked');  	
         	
            var e = $(this);
         	
            var removeClicked = function() {
                setTimeout(function() {
                    e.removeClass('clicked');
         			
                    if(iconStyle) {
                        e.parent().find('.select-icon').show();
                    }
                }, 1);
            }
         	
            setTimeout(function() {
                e.find('form button').click(function() {
                    removeClicked();
                });
                e.keydown(function(event) {
                    if(event.keyCode == 27) {
                        removeClicked();
                    }
                });
            }, 1);
         });
         
     $('.fieldNote').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
       type      : 'textarea',
       cancel    : 'Cancel',
       submit    : 'OK',
       id        : 'elementid',
       rows      : 3,
       tooltip   : 'Click to edit...',
       onblur	 : 'ignore'
     });
   });
</r:script>

<!-- JS for licence documents -->
<r:script type="text/javascript">
    $('.licence-documents input[type="checkbox"]').click(function () {
        if ($('.licence-documents input:checked').length > 0) {
            $('.licence-documents-options').slideDown('fast');
        } else {
            $('.licence-documents-options').slideUp('fast');
        }
    });

    $('.licence-documents-options .delete-document').click(function () {
        if (!confirm('Are you sure you wish to delete these documents?')) {
            $('.licence-documents input:checked').attr('checked', false);
            return false;
        }
        $('.licence-documents input:checked').each(function () {
            $(this).parent().parent().fadeOut('slow');
            $('.licence-documents-options').slideUp('fast');
        });
    })
</r:script>


</body>
</html>

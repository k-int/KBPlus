<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
</head>

<body>

<div class="container">
    <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <g:if test="${onixplLicense.license.licensee}">
            <li><g:link controller="myInstitutions" action="currentLicenses"
                        params="${[shortcode: onixplLicense.license.licensee.shortcode]}">${onixplLicense.license.licensee.name} Current Licences</g:link> <span
                    class="divider">/</span></li>
        </g:if>
        <li><g:link controller="onixplLicenseDetails" action="index"
                    id="${params.id}">ONIX-PL License Details</g:link> <span class="divider">/</span></li>
        <li><g:link controller="onixplLicenseDetails" action="documents"
                    id="${params.id}">License Documents</g:link></li>

        <g:if test="${editable}">
            <li class="pull-right"><span class="badge badge-warning">Editable</span>&nbsp;</li>
        </g:if>

    </ul>
</div>

<div class="container">
    <h1>${onixplLicense.license.licensee?.name} ${onixplLicense.license.type?.value} Licence : <span id="reference"
                                                                                                     style="padding-top: 5px;">${onixplLicense.license.reference}</span>
    </h1>

    <g:render template="nav" contextPath="."/>

</div>

<div class="container">
    <g:form id="delete_doc_form" url="[controller: 'licenseDetails', action: 'deleteDocuments']" method="post">
        <div class="well hide licence-documents-options">
            <button class="btn btn-danger" id="delete-doc">Delete Selected Documents</button>&nbsp;
            <input type="submit" class="btn btn-primary" value="Add new document" data-toggle="modal"
                   href="#modalCreateDocument"/>

            <input type="hidden" name="licid" value="${params.id}"/>
        </div>

        <div class="row">
        <div class="span8">
        <g:if test="${onixplLicense.doc}">
            <h6>Document Details</h6>

            <div class="inline-lists">
                <dl>
                    <dt>Title</dt>
                    <dd>${onixplLicense?.doc?.title}</dd>
                </dl>
                <dl>
                    <dt>Filename</dt>
                    <dd>${onixplLicense?.doc?.filename}</dd>
                </dl>
                <dl>
                    <dt>Type</dt>
                    <dd>${onixplLicense?.doc?.type?.value}</dd>
                </dl>
                <dl>
                    <dt>Status</dt>
                    <dd>${onixplLicense?.doc?.status?.value}</dd>
                </dl>
                <dl>
                    <dt>Creator</dt>
                    <dd>${onixplLicense?.doc?.creator}</dd>
                </dl>
                <dl>
                    <dt>User</dt>
                    <dd>${onixplLicense?.doc?.user?.display}</dd>
                </dl>
                <dl>
                    <dt>Created</dt>
                    <dd>${onixplLicense?.doc?.dateCreated}</dd>
                </dl>
                <dl>
                    <dt>Last Modified</dt>
                    <dd>${onixplLicense?.doc?.lastUpdated}</dd>
                </dl>
                <dl>
                    <dt>Content</dt>
                    <dd>${onixplLicense?.doc?.content}</dd>
                </dl>
            </div>
        </g:if>
        <g:else>
            No document available
        </g:else>

    </g:form>
</div>
</div>
</div>

<!-- Lightbox modal for creating a document taken from licenceDocuments.html -->
<div class="modal hide" id="modalCreateDocument">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3>Create New Document</h3>
    </div>
    <g:form id="upload_new_doc_form" url="[controller: 'licenseDetails', action: 'uploadDocument']" method="post"
            enctype="multipart/form-data">
        <input type="hidden" name="licid" value="${onixplLicense.license.id}"/>

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
                        <input type="file" name="upload_file"/>
                    </dd>
                </dl>
                <dl>
                    <dt>
                        <label>Document Type:</label>
                    </dt>
                    <dd>
                        <select name="doctype">
                            <option value="License">Licence</option>
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
    $(document).ready(function () {

        var checkEmptyEditable = function () {
            $('.fieldNote').each(function () {
                if ($(this).text().length == 0) {
                    $(this).addClass('editableEmpty');
                } else {
                    $(this).removeClass('editableEmpty');
                }
            });
        }

        checkEmptyEditable();

        $('.fieldNote').click(function () {
            // Hide edit icon with overwriting style.
            $(this).addClass('clicked');

            var e = $(this);

            var removeClicked = function () {
                setTimeout(function () {
                    e.removeClass('clicked');

                    if (iconStyle) {
                        e.parent().find('.select-icon').show();
                    }
                }, 1);
            }

            setTimeout(function () {
                e.find('form button').click(function () {
                    removeClicked();
                });
                e.keydown(function (event) {
                    if (event.keyCode == 27) {
                        removeClicked();
                    }
                });
            }, 1);
        });

        $('.fieldNote').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
            type: 'textarea',
            cancel: 'Cancel',
            submit: 'OK',
            id: 'elementid',
            rows: 3,
            tooltip: 'Click to edit...',
            onblur: 'ignore'
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
